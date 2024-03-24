package com.quartzshard.aasb.net.client;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.Phil;
import com.quartzshard.aasb.init.FxInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public record MapperPacket(CompoundTag nbt) {

	public void enc(FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}

	public static MapperPacket dec(@NotNull FriendlyByteBuf buffer) {
		return new MapperPacket(buffer.readNbt());
	}

	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			Phil.deserializeMap(nbt);
		});
		return true;
	}
}
