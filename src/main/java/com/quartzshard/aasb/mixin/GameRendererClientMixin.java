package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererClientMixin {

	// Disables block outlines when allowInteract is disabled.
	@Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
	private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
		if (AstralProjection.isEnabled()) {
			cir.setReturnValue(false);
		}
	}

	// TODO figure out exactly what this does, re-enable if necessary
	// Makes mouse clicks come from the player rather than the freecam entity when player control is enabled or if interaction mode is set to player.
	//@ModifyVariable(method = "pick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"))
	//private Entity onUpdateTargetedEntity(Entity entity) {
	//	if (AstralProjection.isEnabled())
	//		return ClientUtil.mc().player;
	//	return entity;
	//}
}