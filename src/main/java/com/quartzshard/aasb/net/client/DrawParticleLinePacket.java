package com.quartzshard.aasb.net.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.quartzshard.aasb.util.Colors;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

public record DrawParticleLinePacket(Vec3 start, Vec3 end, LineParticlePreset preset) {
	
	public enum LineParticlePreset {
		DEBUG,
		DEBUG_2,
		DEBUG_3,
		ARROW_TARGET_LOCK,
		SENTIENT_RETARGET,
		SENTIENT_COMMUNICATE,
		SENTIENT_TRACER,
		VINE,
		SMITE
	}
	
	public void enc(@NotNull FriendlyByteBuf buffer) {
		buffer.writeDouble(start.x); //
		buffer.writeDouble(start.y); // start point
		buffer.writeDouble(start.z); //
		
		buffer.writeDouble(end.x); //
		buffer.writeDouble(end.y); // end point
		buffer.writeDouble(end.z); //
		
		buffer.writeEnum(preset); // particle preset
	}

	public static DrawParticleLinePacket dec(@NotNull FriendlyByteBuf buffer) {
		return new DrawParticleLinePacket(
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // start point
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), // end point
				buffer.readEnum(LineParticlePreset.class) // particle preset
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			@SuppressWarnings("resource")
			ClientLevel level = Minecraft.getInstance().level;
			/** particle, stepSize */
			Map<ParticleOptions, Double> particles = new HashMap<>();
			switch (preset) {
			
			case DEBUG: // debug
				particles.put(ParticleTypes.FALLING_SPORE_BLOSSOM, 0.1);
				break;
			
			case DEBUG_2: // debug
				particles.put(ParticleTypes.DRIPPING_HONEY, 0.1);
				break;
			
			case DEBUG_3: // debug
				particles.put(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, 0.1);
				break;
				
			case ARROW_TARGET_LOCK: // smart arrow target-lock
				particles.put(ParticleTypes.ENCHANTED_HIT, 0.2);
				//particles.put(SparkleParticleData.noClip(1, Color.PHILOSOPHERS.R/255f, Color.PHILOSOPHERS.G/255f, Color.PHILOSOPHERS.B/255f, 1), 0.1);
				break;
				
			case SENTIENT_RETARGET: // sentient arrow retarget
				particles.put(ParticleTypes.INSTANT_EFFECT, 0.1);
				//particles.put(SparkleParticleData.corrupt(2, Color.PHILOSOPHERS.R/255f, Color.PHILOSOPHERS.G/255f, Color.PHILOSOPHERS.B/255f, 20), 0.1);
				break;
				
			case SENTIENT_COMMUNICATE: // sentient arrow communicate
				particles.put(ParticleTypes.ENCHANT, 0.1);
				break;
				
			case SENTIENT_TRACER: // sentient arrow tracer
				particles.put(new DustParticleOptions(new Vector3f(Colors.PHILOSOPHERS.R/255f, Colors.PHILOSOPHERS.G/255f, Colors.PHILOSOPHERS.B/255f), 1), 0.1);
				//particles.put(WispParticleData.wisp(0.5f, Color.PHILOSOPHERS.R/255f, Color.PHILOSOPHERS.G/255f, Color.PHILOSOPHERS.B/255f, 1), 0.1);
				break;
				
			case VINE: // vine
				particles.put(ParticleTypes.ITEM_SLIME, 0.1);
				//particles.put(WispParticleData.wisp(0.5f, 0.35f, 0.5f, 0, 1.5f), 0.1);
				break;
				
			case SMITE:
				particles.put(ParticleTypes.ELECTRIC_SPARK, 0.1);
				particles.put(ParticleTypes.ENCHANTED_HIT, 0.1);
				break;
			
			default: // invalid
				Logger.warn("DrawParticleLinePacket", "InvalidPreset", "Line particles preset " + preset + " is undefined!");
				level.playSound(null, start.x, start.y, start.z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 100, 2);
				level.addAlwaysVisibleParticle(ParticleTypes.ELDER_GUARDIAN, start.x, start.y, start.z, 0, 0, 0);
				level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, start.x, start.y, start.z, 0, 0, 0);
				level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, end.x, end.y, end.z, 0, 0, 0);
				particles.put(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, 0.1);
				break;
			}
			for (Map.Entry<ParticleOptions, Double> particle : particles.entrySet()) {
				RenderUtil.drawVectorWithParticles(start, end, particle.getKey(), particle.getValue(), level);
			}
		});
		return true;
	}
}
