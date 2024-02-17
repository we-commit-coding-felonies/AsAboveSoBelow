package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.data.tags.DmgTP;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * @deprecated remove later once confirmed that it isnt needed
 */
//@Mixin(Entity.class)
public abstract class EntityMixin {
	
	//@Inject(method = "isInvulnerableTo", at = @At("RETURN"), cancellable = true)
	//protected void onCheckInvulnerableTo(DamageSource dmgSrc, CallbackInfoReturnable<Boolean> cir) {
	//	if (dmgSrc.is(DmgTP.IS_STRONG_FIRE)
	//			&& dmgSrc.is(DamageTypeTags.IS_FIRE)
	//			&& this.fireImmune()
	//			&& !this.isRemoved()
	//			&& !(this.invulnerable && !dmgSrc.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !dmgSrc.isCreativePlayer())
	//			&& !(dmgSrc.is(DamageTypeTags.IS_FALL) && this.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE))) {
	//		cir.setReturnValue(false);
	//	}
	//}
	//
	//
	//
	//// :reallyhighrestransparentshadow:
	//@Shadow
	//private boolean invulnerable;
	//
	//@Shadow
	//public abstract boolean isRemoved();
	//
	//@Shadow
	//public abstract boolean fireImmune();
	//
	//@Shadow
	//public abstract EntityType<?> getType();
}
