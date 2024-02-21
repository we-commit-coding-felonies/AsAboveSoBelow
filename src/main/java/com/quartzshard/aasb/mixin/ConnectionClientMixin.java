package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.network.Connection;

@Mixin(Connection.class)
public class ConnectionClientMixin {

	// Disables freecam if the player disconnects.
	@Inject(method = "handleDisconnection", at = @At("HEAD"))
	private void onHandleDisconnection(CallbackInfo ci) {
		if (AstralProjection.isEnabled())
			AstralProjection.toggle();
	}
}