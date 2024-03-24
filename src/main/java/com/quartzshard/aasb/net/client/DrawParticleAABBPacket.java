package com.quartzshard.aasb.net.client;

import java.util.function.Supplier;

import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.RenderUtil;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

public record DrawParticleAABBPacket(Vec3 cMin, Vec3 cMax, AABBParticlePreset preset) {
	
	public enum AABBParticlePreset {
		INVALID,
		
		DEBUG,
		DEBUG_FILL,
		
		DEBUG_TICK,
		DEBUG_TICK_FILL,
		
		SENTIENT_ARROW_TARGET_LOST
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(cMin.x); //
		buffer.writeDouble(cMin.y); // min corner
		buffer.writeDouble(cMin.z); //
		
		buffer.writeDouble(cMax.x); //
		buffer.writeDouble(cMax.y); // max corner
		buffer.writeDouble(cMax.z); //
		
		buffer.writeEnum(preset); // particle preset
	}

	public static DrawParticleAABBPacket dec(FriendlyByteBuf buffer) {
		return new DrawParticleAABBPacket(
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // min corner
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // max corner
				buffer.readEnum(AABBParticlePreset.class) // particle preset
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			@SuppressWarnings("resource") @Nullable
			ClientLevel level = ClientUtil.mc().level;
			AABB box = new AABB(cMin, cMax);
			Vec3 cent = box.getCenter();
			boolean infRange = false;
			ParticleOptions particle;
			double stepSize = 0.1;
			switch (preset) {
			
			case DEBUG_FILL: // debug fill
				RenderUtil.drawAABBWithParticles(box, ParticleTypes.DRIPPING_WATER, stepSize, level, true, true);
			case DEBUG: // debug outline
				particle = ParticleTypes.DRIPPING_HONEY;
				infRange = true;
				break;
				
			case DEBUG_TICK_FILL: // debug fill (ticking)
				RenderUtil.drawAABBWithParticles(box, ParticleTypes.BUBBLE_POP, stepSize, level, true, true);
			case DEBUG_TICK: // debug outline (ticking)
				particle = ParticleTypes.ELECTRIC_SPARK;
				infRange = true;
				break;
				
			case SENTIENT_ARROW_TARGET_LOST: // smart arrow lost target
				stepSize = 1;
				particle = ParticleTypes.ENCHANT;
				break;
			
			default: // invalid
				Logger.warn("DrawParticleAABBPacket", "InvalidPreset", "AABB particles preset " + preset + " is undefined!");
				level.playSound(null, cent.x, cent.y, cent.z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 100, 2);
				level.addAlwaysVisibleParticle(ParticleTypes.ELDER_GUARDIAN, cent.x, cent.y, cent.z, 0, 0, 0);
				level.addAlwaysVisibleParticle(ParticleTypes.EXPLOSION, cent.x, cent.y, cent.z, 0, 0, 0);
				RenderUtil.drawAABBWithParticles(box, ParticleTypes.DRAGON_BREATH, stepSize, level, true, true);
				particle = ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
				infRange = true;
				break;
			}
			RenderUtil.drawAABBWithParticles(box, particle, stepSize, level, false, infRange);
		});
		return true;
	}
}
