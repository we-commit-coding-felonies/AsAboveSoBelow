package com.quartzshard.aasb.client;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.net.server.KeybindPacket;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.net.server.KeybindPacket.ServerBind;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AASB.MODID, value = Dist.CLIENT)
public class Keybinds {
	public static final String CATEGORY = "key.categories."+AASB.MODID;
	
	public static Component fLoc(KeyMapping key) {
		return Component.translatable("[%s]", key.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.AQUA));
	}
	
	public enum Bind {
		HEAD(new KeyMapping(LangData.KEY_HEAD, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_H), CATEGORY)),
		CHEST(new KeyMapping(LangData.KEY_CHEST, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_J), CATEGORY)),
		LEGS(new KeyMapping(LangData.KEY_LEGS, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_K), CATEGORY)),
		FEET(new KeyMapping(LangData.KEY_FEET, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_L), CATEGORY)),
		
		ITEMMODE(new KeyMapping(LangData.KEY_MODE, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_G), CATEGORY)),
		ITEMFUNC_1(new KeyMapping(LangData.KEY_FUNC_1, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_R), CATEGORY)),
		ITEMFUNC_2(new KeyMapping(LangData.KEY_FUNC_2, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_C), CATEGORY)),
		EMPOWER(new KeyMapping(LangData.KEY_EMPOWER, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_V), CATEGORY)),
		
		HANDSWAP(new KeyMapping(LangData.KEY_HANDSWAP, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_GRAVE_ACCENT), CATEGORY)),
		GLOVE(new KeyMapping(LangData.KEY_GLOVE, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_Z), CATEGORY)),
		BRACELET(new KeyMapping(LangData.KEY_BRACELET, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_X), CATEGORY)),
		CHARM(new KeyMapping(LangData.KEY_CHARM, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_B), CATEGORY))
		;
		
		private final KeyMapping key;
		private Bind(KeyMapping key) {
			this.key = key;
		}
		
		public KeyMapping getKey() {
			return this.key;
		}
		
		public Component loc() {
			return this.getKey().getTranslatedKeyMessage();
		}
		
		public Component fLoc() {
			Component key = loc().copy().withStyle(ChatFormatting.AQUA);
			return Component.translatable("[%s]", key);
		}
		
		/**
		 * gets the server-friendly version of this (no client-only code references)
		 * @return corresponding ServerBind
		 */
		public ServerBind packetFriendly() {
			return valueOf(ServerBind.class, this.name());
		}
	}
	
	private static Map<@NotNull Bind, Boolean> keyTracker = new EnumMap<>(Bind.class);
	
	public static void register(RegisterKeyMappingsEvent event) {
		for (Bind bind : Bind.values()) {
			event.register(bind.getKey());
			//ClientRegistry.registerKeyBinding(bind.getKey());
			keyTracker.put(bind, false);
		}
	}

	private static InputConstants.Key keyById(int id) {
		return InputConstants.Type.KEYSYM.getOrCreate(id);
	}
	
	@SubscribeEvent
	public static void checkKeyPress(TickEvent.ClientTickEvent event) {
		for (Entry<Bind,Boolean> bind : keyTracker.entrySet()) {
			KeyMapping key = bind.getKey().getKey();
			boolean wasDown = keyTracker.put(bind.getKey(), key.isDown());
			BindState state;
			if (key.isDown() && !wasDown) {
				state = /*wasDown ? BindState.HELD :*/ BindState.PRESSED;
			} else if (!key.isDown() && wasDown) {
				state = BindState.RELEASED;
			} else continue;
			
			if (state == BindState.PRESSED && AstralProjection.isEnabled()) { // Only disable on press, so that freecam is still a toggle and not a hold
				// Do not send keybinds if we are freecam, instead just disable freecam
				AstralProjection.toggle();
			} else {
				int toSend = 1;
				switch (state) {
				case PRESSED:
					toSend = 0;
					while (key.consumeClick()) {
						toSend++;
					}
					toSend = Math.max(1, toSend);
					break;
				default:
					break;
				case RELEASED:
					while (key.consumeClick()) {
						// resets the click count to 0
					}
					break;
				}
				KeybindPacket packet = new KeybindPacket(bind.getKey().packetFriendly(), state);
				for (; toSend > 0; toSend--) {
					NetInit.toServer(packet);
				}
			}
		}
	}
}
