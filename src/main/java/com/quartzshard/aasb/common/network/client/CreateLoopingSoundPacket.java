package com.quartzshard.aasb.common.network.client;

import java.util.function.Supplier;

import com.quartzshard.aasb.client.sound.SentientAmbient;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public record CreateLoopingSoundPacket(byte type, int entId) {
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeByte(type);
		buffer.writeInt(entId);
	}

	public static CreateLoopingSoundPacket dec(FriendlyByteBuf buffer) {
		return new CreateLoopingSoundPacket(
				buffer.readByte(),
				buffer.readInt()
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			@SuppressWarnings("resource")
			ClientLevel level = Minecraft.getInstance().level;
			Entity entity = level.getEntity(entId);
			if (entity != null) {
				switch (type) {
				
				case 1: // sentient whispers
					getSoundManager().play(new SentientAmbient(entity, 392));
					break;
				
				default: // unknown
					LogHelper.warn("CreateLoopingSoundPacket", "UnknownType", "Looping sound of type " + type + " is undefined!");
					getSoundManager().play(new SimpleSoundInstance(SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, Float.MAX_VALUE, 2, entity.getX(), entity.getY(), entity.getZ()));
					level.addAlwaysVisibleParticle(ParticleTypes.ELDER_GUARDIAN, entity.getX(), entity.getY(), entity.getZ(), 0, 0, 0);
					break;
				}
			}
		});
		return true;
	}
	
	/**
	 * DO NOT CALL THIS SERVERSIDE!!!!!
	 * @return clients sound manager
	 */
	private SoundManager getSoundManager() {
		return Minecraft.getInstance().getSoundManager();
	}
}