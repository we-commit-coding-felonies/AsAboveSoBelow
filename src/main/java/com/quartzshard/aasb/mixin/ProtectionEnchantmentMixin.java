package com.quartzshard.aasb.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.data.tags.DmgTP;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

/**
 * 
 */
@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionEnchantmentMixin {
	
	@Inject(method = "getDamageProtection", at = @At("RETURN"), cancellable = true)
	protected void onGetDamageProtection(int enchLevel, DamageSource dmgSrc, CallbackInfoReturnable<Integer> cir) {
		if (this.type == ProtectionEnchantment.Type.FIRE && dmgSrc.is(DmgTP.IS_STRONG_FIRE)) {
			cir.setReturnValue(enchLevel * 2);
		}
	}
	
	
	// :reallyhighrestransparentshadow:
	@Shadow @Final
	public ProtectionEnchantment.@Nullable Type type;
}
