package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.data.tags.DmgTP;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@Mixin(Entity.class)
public abstract class EntityClientMixin {

	// Makes mouse input rotate the FreeCamera.
	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void onChangeLookDirection(double x, double y, CallbackInfo ci) {
		if (AstralProjection.isEnabled() && this.equals(ClientUtil.mc().player)) {
			AstralProjection.getCamera().turn(x, y);
			ci.cancel();
		}
	}

	// Prevents FreeCamera from pushing/getting pushed by entities.
	@Inject(method = "push", at = @At("HEAD"), cancellable = true)
	private void onPush(Entity entity, CallbackInfo ci) {
		if (AstralProjection.isEnabled() && (entity.equals(AstralProjection.getCamera()) || this.equals(AstralProjection.getCamera()))) {
			ci.cancel();
		}
	}
}
