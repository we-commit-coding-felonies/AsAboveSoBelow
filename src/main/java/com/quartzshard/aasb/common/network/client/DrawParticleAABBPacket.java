package com.quartzshard.aasb.common.network.client;

import java.util.function.Supplier;

import com.quartzshard.aasb.util.BoxHelper;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

public record DrawParticleAABBPacket(Vec3 cMin, Vec3 cMax, ParticlePreset preset) {
	
	public enum ParticlePreset {
		INVALID(-1),
		DEBUG(0),
		DEBUG_FILL(1),
		SENTIENT_ARROW_TARGET_LOST(2);
		public final int id;
		
		private ParticlePreset(int id) {
			this.id = id;
		}
		
		public static ParticlePreset fromId(int id) {
			switch (id) {
			
			case 0: return ParticlePreset.DEBUG;
			case 1: return ParticlePreset.DEBUG_FILL;
			case 2: return ParticlePreset.SENTIENT_ARROW_TARGET_LOST;
			
			default: return ParticlePreset.INVALID;
			}
		}
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(cMin.x); //
		buffer.writeDouble(cMin.y); // min corner
		buffer.writeDouble(cMin.z); //
		
		buffer.writeDouble(cMax.x); //
		buffer.writeDouble(cMax.y); // max corner
		buffer.writeDouble(cMax.z); //
		
		buffer.writeInt(preset.id); // particle preset
	}

	public static DrawParticleAABBPacket dec(FriendlyByteBuf buffer) {
		return new DrawParticleAABBPacket(
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // min corner
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // max corner
				ParticlePreset.fromId(buffer.readInt()) // particle preset
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
        	@SuppressWarnings("resource")
			ClientLevel level = Minecraft.getInstance().level;
        	AABB box = new AABB(cMin, cMax);
        	Vec3 cent = box.getCenter();
        	boolean infRange = false;
        	ParticleOptions particle;
        	double stepSize = 0.1;
        	switch (preset) {
        	
        	case DEBUG_FILL: // debug fill
        		BoxHelper.drawAABBWithParticles(box, ParticleTypes.DRIPPING_WATER, stepSize, level, true, true);
        	case DEBUG: // debug outline
        		particle = ParticleTypes.DRIPPING_LAVA;
        		infRange = true;
        		break;
        		
        	case SENTIENT_ARROW_TARGET_LOST: // smart arrow lost target
        		stepSize = 1;
        		particle = ParticleTypes.ENCHANT;
        		break;
        	
        	default: // invalid
				LogHelper.warn("DrawParticleAABBPacket", "InvalidPreset", "AABB particles preset " + preset + " is undefined!");
        		level.playSound(null, cent.x, cent.y, cent.z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 100, 2);
        		level.addAlwaysVisibleParticle(ParticleTypes.ELDER_GUARDIAN, cent.x, cent.y, cent.z, 0, 0, 0);
        		BoxHelper.drawAABBWithParticles(box, ParticleTypes.DRAGON_BREATH, stepSize, level, true, true);
        		particle = ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
        		break;
        	}
        	BoxHelper.drawAABBWithParticles(box, particle, stepSize, level, false, infRange);
        });
        return true;
    }
}
