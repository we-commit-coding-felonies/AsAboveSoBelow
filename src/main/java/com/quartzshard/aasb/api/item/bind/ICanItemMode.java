package com.quartzshard.aasb.api.item.bind;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ICanItemMode extends ICanHandleKeybind {
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.ITEMMODE) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedItemMode(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldItemMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedItemMode(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onPressedItemMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onHeldItemMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onReleasedItemMode(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
