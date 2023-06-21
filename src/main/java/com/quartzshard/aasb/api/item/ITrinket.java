package com.quartzshard.aasb.api.item;

import com.quartzshard.aasb.api.item.bind.ICanItemFunc1;
import com.quartzshard.aasb.api.item.bind.ICanItemFunc2;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;

public interface ITrinket extends ICanItemFunc1, ICanItemFunc2 {
	@Override
	default boolean handle(PressContext ctx) {
		return ICanItemFunc1.super.handle(ctx)
				|| ICanItemFunc2.super.handle(ctx);
	}
}
