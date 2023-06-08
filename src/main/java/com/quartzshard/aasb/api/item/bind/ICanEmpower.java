package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanEmpower extends ICanHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.EMPOWER) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedEmpower(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldEmpower(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedEmpower(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onPressedEmpower(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onHeldEmpower(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedEmpower(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
