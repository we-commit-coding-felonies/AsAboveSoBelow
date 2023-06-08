package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanChestMode extends ICanHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.CHESTMODE) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedChestMode(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldChestMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedChestMode(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onPressedChestMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onHeldChestMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedChestMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
