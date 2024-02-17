package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.net.server.KeybindPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanFeetMode extends IHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.FEET) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedFeetMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedFeetMode(ctx.stack(), ctx.player(), ctx.level());
			default:
				break;
			}
		}
		return false;
	}
	
	default boolean onPressedFeetMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedFeetMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
