package com.quartzshard.aasb.mixin;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

@Mixin(Options.class)
public class OptionsClientMixin {

	// Prevents switching to third person in freecam.
	@Inject(method = "setCameraType", at = @At("HEAD"), cancellable = true)
	private void onSetPerspective(CallbackInfo ci) {
		if (AstralProjection.isEnabled()) {
			ci.cancel();
		}
	}
}