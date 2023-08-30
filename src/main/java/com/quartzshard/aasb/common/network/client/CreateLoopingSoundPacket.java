package com.quartzshard.aasb.common.network.client;

import java.util.function.Supplier;

import com.quartzshard.aasb.client.sound.SentientAmbient;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.network.NetworkEvent;

public record CreateLoopingSoundPacket(LoopingSound type, int entId) {
	
	public enum LoopingSound {
		SENTIENT_WHISPERS
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeEnum(type);
		buffer.writeInt(entId);
	}

	public static CreateLoopingSoundPacket dec(FriendlyByteBuf buffer) {
		return new CreateLoopingSoundPacket(
				buffer.readEnum(LoopingSound.class),
				buffer.readInt()
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			ClientLevel level = ClientHelper.level();
			Entity entity = level.getEntity(entId);
			if (entity != null) {
				SoundManager sndMgr = ClientHelper.getSoundManager();
				switch (type) {
				
				case SENTIENT_WHISPERS: // sentient whispers
					sndMgr.play(new SentientAmbient(entity, 392));
					break;
				
				default: // unknown
					LogHelper.warn("CreateLoopingSoundPacket", "UnknownType", "Looping sound of type " + type + " is undefined!");
					sndMgr.play(new SimpleSoundInstance(SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, Float.MAX_VALUE, 2, entity.getX(), entity.getY(), entity.getZ()));
					level.addAlwaysVisibleParticle(ParticleTypes.ELDER_GUARDIAN, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
					break;
				}
			}
		});
		return true;
	}
}