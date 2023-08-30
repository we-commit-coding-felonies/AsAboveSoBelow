package com.quartzshard.aasb.api.item;

import com.quartzshard.aasb.api.item.bind.ICanItemFunc1;
import com.quartzshard.aasb.api.item.bind.ICanItemFunc2;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.util.PlayerHelper;

import net.minecraft.world.InteractionHand;

public interface ITrinket extends ICanItemFunc1, ICanItemFunc2 {
	@Override
	default boolean handle(PressContext ctx) {
		if ( !PlayerHelper.onCooldown(ctx.player(), ctx.stack().getItem())
				&& (ICanItemFunc1.super.handle(ctx) || ICanItemFunc2.super.handle(ctx)) ) {
			InteractionHand hand = ctx.player().getItemInHand(InteractionHand.OFF_HAND) == ctx.stack() ?
					InteractionHand.OFF_HAND :
					InteractionHand.MAIN_HAND;
			PlayerHelper.swingArm(ctx.player(), ctx.level(), hand);
			return true;
		}
		return false;
	}
}
