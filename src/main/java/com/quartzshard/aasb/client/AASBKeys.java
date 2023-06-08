package com.quartzshard.aasb.client;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.common.network.server.KeyPressPacket;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;
import com.quartzshard.aasb.data.AASBLang;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID, value = Dist.CLIENT)
public class AASBKeys {
	public static final String CATEGORY = "key.categories."+AsAboveSoBelow.MODID;
	
	public enum Bind {
		HEADMODE(new KeyMapping(AASBLang.KEY_HEADMODE, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_H), CATEGORY)),
		CHESTMODE(new KeyMapping(AASBLang.KEY_CHESTMODE, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_J), CATEGORY)),
		LEGSMODE(new KeyMapping(AASBLang.KEY_LEGSMODE, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_K), CATEGORY)),
		FEETMODE(new KeyMapping(AASBLang.KEY_FEETMODE, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_L), CATEGORY)),
		ITEMMODE(new KeyMapping(AASBLang.KEY_ITEMMODE, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_G), CATEGORY)),
		ITEMFUNC_1(new KeyMapping(AASBLang.KEY_ITEMFUNC_1, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_R), CATEGORY)),
		ITEMFUNC_2(new KeyMapping(AASBLang.KEY_ITEMFUNC_2, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_C), CATEGORY)),
		EMPOWER(new KeyMapping(AASBLang.KEY_EMPOWER, KeyConflictContext.IN_GAME, keyById(GLFW.GLFW_KEY_V), CATEGORY));
		
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
			return new TranslatableComponent("[%s]", key);
		}
		
		/**
		 * gets the server-friendly version of this (no client-only code references)
		 * @return corresponding ServerBind
		 */
		public ServerBind packetFriendly() {
			return valueOf(ServerBind.class, this.name());
		}
	}
	
	private static Map<Bind, Boolean> keyTracker = new EnumMap<>(Bind.class);
	public static void register() {
		for (Bind bind : Bind.values()) {
			ClientRegistry.registerKeyBinding(bind.getKey());
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
			if (key.isDown()) {
				state = wasDown ? BindState.HELD : BindState.PRESSED;
			} else if (wasDown) {
				state = BindState.RELEASED;
			} else continue;
			
			int toSend = 1;
			switch (state) {
			case PRESSED:
				toSend = 0;
				while (key.consumeClick()) {
					toSend++;
				}
				toSend = Math.max(1, toSend);
				break;
			case HELD:
				break;
			case RELEASED:
				while (key.consumeClick()) {
					// resets the click count to 0
				}
				break;
			}
			KeyPressPacket packet = new KeyPressPacket(bind.getKey().packetFriendly(), state);
			for (; toSend > 0; toSend--) {
				AASBNet.toServer(packet);
			}
		}
	}
}
