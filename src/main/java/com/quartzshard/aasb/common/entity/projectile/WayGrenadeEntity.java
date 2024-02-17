package com.quartzshard.aasb.common.entity.projectile;

import com.quartzshard.aasb.common.item.equipment.WayGrenadeItem;
import com.quartzshard.aasb.common.level.WayExplosionDamageCalculator;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.init.object.ItemInit;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * "grenade" is a bit of an understatement
 */
public class WayGrenadeEntity extends ThrowableItemProjectile {
	public WayGrenadeEntity(EntityType<? extends WayGrenadeEntity> entType, Level level) {
		super(entType, level);
	}

	public WayGrenadeEntity(Level level, LivingEntity shooter) {
		super(EntityInit.ENT_WAY_GRENADE.get(), shooter, level);
	}
	
	public WayGrenadeEntity(Level level, LivingEntity shooter, ItemStack stack) {
		super(EntityInit.ENT_WAY_GRENADE.get(), shooter, level);
		this.setItem(stack);
	}

	public WayGrenadeEntity(Level level, double x, double y, double z) {
		super(EntityInit.ENT_WAY_GRENADE.get(), x, y, z, level);
	}

	@Override
	protected Item getDefaultItem() {
		return ItemInit.WAY_GRENADE.get();
	}
	
	@Override
	public void tick() {
		super.tick();
		if (this.isOnFire()) {
			det();
		}
	}
	
	@Override
	public void onHit(HitResult hitRes) {
		det();
	}
	
	/**
	 * KA-BEWMMM!!!
	 * <p>
	 * TODO: make something fancier at higher explosion powers (above ~15)
	 */
	public void det() {
		//level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 4.0F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.5F) * 0.3F);
		level().playSound(null, this.blockPosition(), FxInit.SND_WAY_EXPLODE.get(), this.getSoundSource(), 4.0F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.5F) * 1.25F);
		if (!level().isClientSide) {
			DamageSource dmgSrc = EntityInit.dmg(EntityInit.DMG_WAYBOMB, this.level(), this, this.getOwner());
			ItemStack stack = this.getItem();
			float detPower = ((WayGrenadeItem)getDefaultItem()).getDetPower(stack);
			WayExplosionDamageCalculator nukeCalc = new WayExplosionDamageCalculator(detPower);
			Vec3 c = this.getBoundingBox().getCenter();
			this.level().explode(this, dmgSrc, nukeCalc, c.x, c.y, c.z, detPower, this.isOnFire(), ExplosionInteraction.BLOCK, true);
		}
		this.discard();
	}
	
}
