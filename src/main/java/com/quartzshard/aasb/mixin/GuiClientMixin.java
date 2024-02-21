package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;

@Mixin(Gui.class)
public class GuiClientMixin {

	// Makes HUD correspond to the player rather than the FreeCamera.
	@Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
	private void onGetCameraPlayer(CallbackInfoReturnable<Player> cir) {
		if (AstralProjection.isEnabled()) {
			cir.setReturnValue(ClientUtil.mc().player);
		}
	}
}