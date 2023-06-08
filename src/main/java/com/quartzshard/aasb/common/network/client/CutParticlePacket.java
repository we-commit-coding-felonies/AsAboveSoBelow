package com.quartzshard.aasb.common.network.client;

import java.util.Random;
import java.util.function.Supplier;

import com.quartzshard.aasb.init.EffectInit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

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

	public static CutParticlePacket dec(FriendlyByteBuf buffer) {
		return new CutParticlePacket(buffer.readInt(),
				new AABB(buffer.readDouble(),buffer.readDouble(),buffer.readDouble(),
						buffer.readDouble(),buffer.readDouble(),buffer.readDouble()));
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
        	@SuppressWarnings("resource")
			ClientLevel level = Minecraft.getInstance().level;
        	Random r = level.random;
        	for (int i = 0; i < amount; i++) {
        		double x1 = r.nextDouble(area.minX, area.maxX);
        		double y1 = r.nextDouble(area.minY, area.maxY);
        		double z1 = r.nextDouble(area.minZ, area.maxZ);
        		double x2 = r.nextDouble(area.minX, area.maxX);
        		double y2 = r.nextDouble(area.minY, area.maxY);
        		double z2 = r.nextDouble(area.minZ, area.maxZ);
				level.addParticle(EffectInit.Particles.CUT_PARTICLE.get(), x1,y1,z1, x2,y2,z2);
        	}
        });
        return true;
    }

}
