package com.quartzshard.aasb.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.data.tags.DmgTP;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection.FreeCamera;

import net.minecraft.client.Camera;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FogType;

@Mixin(Camera.class)
public abstract class CameraClientMixin {

	@Shadow @Nullable
	private Entity entity;

	@Shadow
	private float eyeHeightOld;

	@Shadow
	private float eyeHeight;

	// When toggling freecam, update the camera's eye height instantly without any transition.
	@Inject(method = "setup", at = @At("HEAD"))
	public void onSetup(BlockGetter area, Entity newFocusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
		if (newFocusedEntity == null || this.entity == null || newFocusedEntity.equals(this.entity)) {
			return;
		}

		if (newFocusedEntity instanceof FreeCamera || this.entity instanceof FreeCamera) {
			this.eyeHeightOld = this.eyeHeight = newFocusedEntity.getEyeHeight();
		}
	}

	// Removes the submersion overlay when underwater, in lava, or powdered snow.
	@Inject(method = "getFluidInCamera", at = @At("HEAD"), cancellable = true)
	public void onGetSubmersionType(CallbackInfoReturnable<FogType> cir) {
		if (AstralProjection.isEnabled()) {
			cir.setReturnValue(FogType.NONE);
		}
	}
}
