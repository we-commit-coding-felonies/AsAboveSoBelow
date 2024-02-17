package com.quartzshard.aasb.net.server;

import java.util.function.Supplier;

import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.api.item.bind.IHandleKeybind;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.SlotContext;

/**
 * keybinds client -> server, except this time without the spam
 * @author solunareclipse1
 */
public record KeybindPacket(ServerBind bind, BindState state) {
	
	/**
	 * exists to prevent client-code from being called on the server
	 * @author solunareclipse1
	 */
	public enum ServerBind {
		HEAD,
		CHEST,
		LEGS,
		FEET,
		
		ITEMMODE,
		ITEMFUNC_1,
		ITEMFUNC_2,
		EMPOWER,
		
		HANDSWAP,
		GLOVE,
		BRACELET,
		CHARM;
	}
	public enum BindState {
		RELEASED,
		PRESSED
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer
			.writeEnum(bind)
			.writeEnum(state);
	}

	public static KeybindPacket dec(FriendlyByteBuf buffer) {
		return new KeybindPacket(buffer.readEnum(ServerBind.class), buffer.readEnum(BindState.class));
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		Logger.debug("KeybindPacket.handle()", "HandlingBind", bind.toString() + " was " + state.toString());
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			ServerPlayer player = ctx.getSender();
			if (player != null) {
				ServerLevel level = (ServerLevel) player.level();
				switch (bind) {
					case HEAD:
						passBindToVanillaSlots(player, level, EquipmentSlot.HEAD);
						break;
					case CHEST:
						passBindToVanillaSlots(player, level, EquipmentSlot.CHEST);
						break;
					case LEGS:
						passBindToVanillaSlots(player, level, EquipmentSlot.LEGS);
						break;
					case FEET:
						passBindToVanillaSlots(player, level, EquipmentSlot.FEET);
						break;
						
					case ITEMMODE:
					case ITEMFUNC_1:
					case ITEMFUNC_2:
					case EMPOWER:
						passBindToVanillaSlots(player, level, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
						break;
					
					case HANDSWAP:
						if (state == BindState.PRESSED)
							player.getCapability(PlayerUtil.PlayerSelectedHandProvider.PLAYER_SELECTED_HAND).ifPresent(cap -> {
								cap.swapHand();
							});
						break;
					case GLOVE:
						player.getCapability(PlayerUtil.PlayerSelectedHandProvider.PLAYER_SELECTED_HAND).ifPresent(cap -> {
							int idx = cap.getHand() == InteractionHand.MAIN_HAND ? 0 : 1;
							Tuple<ItemStack,SlotContext> curio = PlayerUtil.getCurio(player, "hands", idx);
							if (curio != null) {
								ItemStack stack = curio.getA();
								if (stack.getItem() instanceof IRuneable item && !PlayerUtil.onCooldown(player, stack.getItem())) {
									if (item.handle(new PressContext(bind, state, stack, player, level))) {
										PlayerUtil.swingArm(player, level, cap.getHand());
									}
								}
							}
						});
						break;
					case BRACELET:
						player.getCapability(PlayerUtil.PlayerSelectedHandProvider.PLAYER_SELECTED_HAND).ifPresent(cap -> {
							int idx = cap.getHand() == InteractionHand.MAIN_HAND ? 0 : 1;
							Tuple<ItemStack,SlotContext> curio = PlayerUtil.getCurio(player, "bracelet", idx);
							if (curio != null) {
								ItemStack stack = curio.getA();
								if (stack.getItem() instanceof IRuneable item && !PlayerUtil.onCooldown(player, stack.getItem())) {
									if (item.handle(new PressContext(bind, state, stack, player, level))) {
										PlayerUtil.swingArm(player, level, cap.getHand());
									}
								}
							}
						});
						break;
					case CHARM:
						Tuple<ItemStack,SlotContext> curio = PlayerUtil.getCurio(player, "charm", 0);
						if (curio != null) {
							ItemStack stack = curio.getA();
							if (stack.getItem() instanceof IRuneable item && !PlayerUtil.onCooldown(player, stack.getItem())) {
								item.handle(new PressContext(bind, state, stack, player, level));
							}
						}
						break;
						
					default:
						Logger.warn("KeybindPacket.handle()", "UnknownBind", "Don't know how to handle bind " + bind.toString() +", ignoring!");
						break;
				}
			}
		});
		return true;
	}
	
	/**
	 * @param player
	 * @param level
	 * @param slots
	 * @return true if the bind was handled
	 */
	private boolean passBindToVanillaSlots(ServerPlayer player, ServerLevel level, EquipmentSlot... slots) {
		boolean didDo = false;
		for (EquipmentSlot slot : slots) {
			ItemStack stack = player.getItemBySlot(slot);
			if (stack != null && !stack.isEmpty() && stack.getItem() instanceof IHandleKeybind item) {
				didDo = item.handle(new PressContext(bind, state, stack, player, level));
				if (didDo) break;
			}
		}
		return didDo;
	}
	
	public record PressContext(ServerBind bind, BindState state, ItemStack stack, ServerPlayer player, ServerLevel level) {}
}



