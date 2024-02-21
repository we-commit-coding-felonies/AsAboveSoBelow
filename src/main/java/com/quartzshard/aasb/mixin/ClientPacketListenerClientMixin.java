package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.client.multiplayer.ClientPacketListener;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerClientMixin {

	// Disables freecam when the player respawns/switches dimensions.
	@Inject(method = "handleRespawn", at = @At("HEAD"))
	private void onPlayerRespawn(CallbackInfo ci) {
		if (AstralProjection.isEnabled()) {
			AstralProjection.toggle();
		}
	}
}