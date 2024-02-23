package com.quartzshard.aasb.net.client;

import java.util.Random;
import java.util.function.Supplier;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.init.FxInit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

public record CutParticlePacket(int amount, AABB area) {	
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeInt(amount);
		buffer.writeDouble(area.minX);
		buffer.writeDouble(area.minY);
		buffer.writeDouble(area.minZ);
		buffer.writeDouble(area.maxX);
		buffer.writeDouble(area.maxY);
		buffer.writeDouble(area.maxZ);
	}

	public static CutParticlePacket dec(@NotNull FriendlyByteBuf buffer) {
		return new CutParticlePacket(buffer.readInt(),
				new AABB(buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
						buffer.readDouble(),buffer.readDouble(),buffer.readDouble()));
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			Random r = AASB.RNG;
			for (int i = 0; i < amount; i++) {
				double x1 = r.nextDouble(area.minX, area.maxX);
				double y1 = r.nextDouble(area.minY, area.maxY);
				double z1 = r.nextDouble(area.minZ, area.maxZ);
				double x2 = r.nextDouble(area.minX, area.maxX);
				double y2 = r.nextDouble(area.minY, area.maxY);
				double z2 = r.nextDouble(area.minZ, area.maxZ);
				level.addParticle(FxInit.PTC_CUT.get(), x1,y1,z1, x2,y2,z2);
			}
        });
        return true;
	}

}
