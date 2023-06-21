package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanLegsMode extends ICanHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.LEGSMODE) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedLegsMode(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldLegsMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedLegsMode(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onPressedLegsMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onHeldLegsMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedLegsMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}