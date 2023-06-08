package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanFeetMode extends ICanHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.FEETMODE) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedFeetMode(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldFeetMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedFeetMode(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onPressedFeetMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onHeldFeetMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedFeetMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
