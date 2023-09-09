package com.quartzshard.aasb.common.entity.living;

import java.util.Random;

import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;

/*
 * oh boy, manmade horrors beyond my comprehension
 */
public class HorrorEntity extends Husk {
	public HorrorEntity(EntityType<? extends HorrorEntity> type, Level level) {
		super(type, level);
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (level.getDifficulty() != Difficulty.PEACEFUL) {
			boolean didHurt = super.doHurtTarget(entity);
			if (didHurt && this.getMainHandItem().isEmpty() && entity instanceof LivingEntity ent) {
				float timeMult = this.level.getCurrentDifficultyAt(blockPosition()).getEffectiveDifficulty();
				ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140 * (int)timeMult), this);
			}
			return didHurt;
		}
		return false;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	@Override
	protected boolean convertsInWater() {
		return false;
	}

	public static boolean getSpawnAsBabyOdds(Random pRandom) {
		return false;
	}
	
	public static AttributeSupplier.Builder defaultAttributes() {
		return Husk.createAttributes()
				.add(Attributes.ATTACK_DAMAGE, 15)
				.add(Attributes.MAX_HEALTH, 8000)
				.add(Attributes.MOVEMENT_SPEED, 0.17)
				.add(Attributes.KNOCKBACK_RESISTANCE, 10);
	}
}
