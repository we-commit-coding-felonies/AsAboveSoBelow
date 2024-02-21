package com.quartzshard.aasb.net.client;

import java.util.function.Supplier;

import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection.FreeCamera;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record FreecamPacket(boolean newState) {
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeBoolean(newState);
	}

	public static FreecamPacket dec(FriendlyByteBuf buffer) {
		return new FreecamPacket(buffer.readBoolean());
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			AstralProjection.toggle();
		});
		return true;
	}
}
