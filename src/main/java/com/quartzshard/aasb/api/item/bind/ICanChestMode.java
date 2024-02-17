package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.net.server.KeybindPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanChestMode extends IHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.CHEST) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedChestMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedChestMode(ctx.stack(), ctx.player(), ctx.level());
			default:
				break;
			}
		}
		return false;
	}
	
	default boolean onPressedChestMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedChestMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
