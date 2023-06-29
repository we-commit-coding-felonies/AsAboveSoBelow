package com.quartzshard.aasb.common.network.client;

import java.util.Random;
import java.util.function.Supplier;

import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.ClientHelper;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

/**
 * preset multi-particle-type effects
 */
public record PresetParticlePacket(FXPreset preset, Vec3 pos) {
	
	public enum FXPreset {
		MUSTANG
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeEnum(preset);
		buffer.writeDouble(pos.x);
		buffer.writeDouble(pos.y);
		buffer.writeDouble(pos.z);
	}

	public static PresetParticlePacket dec(FriendlyByteBuf buffer) {
		return new PresetParticlePacket(buffer.readEnum(FXPreset.class),
				new Vec3(buffer.readDouble(),buffer.readDouble(),buffer.readDouble()));
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			Minecraft mc = ClientHelper.mc();
			ClientLevel level = mc.level;
			Random rand = level.getRandom();
			double
				x = pos.x,
				y = pos.y,
				z = pos.z;
			level.addParticle(ParticleTypes.FLASH, true, x, y, z, 0, 0, 0);
			for (int i = 0; i < 3; i++) {
				Particle p = ClientHelper.hackyParticle(ParticleTypes.FLASH, true, false, rand.nextGaussian() + x, rand.nextGaussian() + y, rand.nextGaussian() + z, 0, Math.abs(rand.nextGaussian()/10), 0);
				p.setColor(1, 0.25f, 0);
			}
			
			for (int i = 0; i < 256; i++) {
				level.addParticle(ParticleTypes.FLAME, true, rand.nextGaussian() * 2 + x, rand.nextGaussian() * 2 + y, rand.nextGaussian() * 2 + z, 0, Math.abs(rand.nextGaussian()/10), 0);
				level.addParticle(ParticleTypes.SMALL_FLAME, true, rand.nextGaussian() * 2 + x, rand.nextGaussian() * 2 + y, rand.nextGaussian() * 2 + z, 0, Math.abs(rand.nextGaussian()/10), 0);
			}
			
			for (int i = 0; i < 128; i++) {
				level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, rand.nextGaussian() + x, rand.nextGaussian() + y, rand.nextGaussian() + z, 0, Math.abs(rand.nextGaussian()/10), 0);
			}
			// addParticle(pParticleData, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
		});
		return true;
	}

}
