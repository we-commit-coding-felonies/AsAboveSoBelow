package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanHeadMode extends ICanHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.HEADMODE) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedHeadMode(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldHeadMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedHeadMode(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onPressedHeadMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onHeldHeadMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedHeadMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
