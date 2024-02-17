package com.quartzshard.aasb.client.sound;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.init.FxInit;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

/**
 * spooooooky
 * @author solunareclipse1
 */
public class SentientWhispersAmbient extends AbstractTickableSoundInstance {
	// sound lasts for 392.4 ticks before looping (at normal speed/pitch)
	private final Entity entity;
	private float step = 0;
	private float nextPitch = 1;
	private int maxPitchChangeTime = 0;
	private int ceaseTimer = 0;

	public SentientWhispersAmbient(Entity entity) {
		super(FxInit.SND_SENTIENT_WHISPERS.get(), SoundSource.NEUTRAL, entity.level().random);
		this.entity = entity;
		this.looping = true;
		this.delay = 0;
	}

	public SentientWhispersAmbient(Entity entity, int maxPitchChangeTime) {
		super(FxInit.SND_SENTIENT_WHISPERS.get(), SoundSource.NEUTRAL, entity.level().random);
		this.entity = entity;
		this.looping = true;
		this.delay = 0;
		this.maxPitchChangeTime = maxPitchChangeTime;
	}

	@Override
	public void tick() {
		if (mustCease()) cease();
		else {
			// borked
			//if (entity instanceof SentientArrow arrow) {
			//	if (arrow.hasTarget()) {
			//		this.x = arrow.getTarget().getX();
			//		this.y = arrow.getTarget().getY();
			//		this.z = arrow.getTarget().getZ();
			//	} else {
			//		this.x = entity.getX();
			//		this.y = entity.getY();
			//		this.z = entity.getZ();
			//	}
			//}
			this.x = entity.getX();
			this.y = entity.getY();
			this.z = entity.getZ();
			// cool pitch-changing over time
			if (maxPitchChangeTime > 0) {
				if (step == 0) {
					step = (nextPitch - pitch)/entity.level().random.nextInt(1, maxPitchChangeTime+1);
				}
				pitch += step;
				// checks for overshoots, just in case
				if ( pitch == nextPitch || (step > 0 && pitch > nextPitch) || (step < 0 && pitch < nextPitch) ) {
					nextPitch = AASB.RNG.nextFloat(0.1f, 2f);
					step = 0;
				}
			}
		}
	}
	
	protected void cease() {
		this.stop();
	}
	
	protected boolean mustCease() {
		if (entity instanceof SentientArrowEntity arrow && arrow.isInert()) {
			if (arrow.isInert()) ceaseTimer++;
			else ceaseTimer = 0;
		}
		return entity.isRemoved() || ceaseTimer > 6;
	}
}