package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanItemFunc1 extends ICanHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.ITEMFUNC_1) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedFunc1(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldFunc1(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedFunc1(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onHeldFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
