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
				return onEmpowerPressed(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onEmpowerHeld(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onEmpowerReleased(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	default boolean onEmpowerPressed(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onEmpowerHeld(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
	default boolean onEmpowerReleased(ItemStack stack, ServerPlayer player, ServerLevel level) {return false;}
}
