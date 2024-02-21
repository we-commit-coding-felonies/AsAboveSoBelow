package com.quartzshard.aasb.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeClientMixin {

	// Prevents interacting with blocks when allowInteract is disabled.
	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void onUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
		if (AstralProjection.isEnabled()) {
			cir.setReturnValue(InteractionResult.PASS);
		}
	}

	// Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void onInteract(Player player, Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (entity.equals(ClientUtil.mc().player) || AstralProjection.isEnabled()) {
			cir.setReturnValue(InteractionResult.PASS);
		}
	}

	// Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
	@Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
	private void onInteractAt(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (entity.equals(ClientUtil.mc().player) || AstralProjection.isEnabled()) {
			cir.setReturnValue(InteractionResult.PASS);
		}
	}

	// Prevents attacking self.
	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void onAttack(Player player, Entity target, CallbackInfo ci) {
		if (target.equals(ClientUtil.mc().player)) {
			ci.cancel();
		}
	}
	
	@Inject(method = "sameDestroyTarget", at = @At("RETURN"), cancellable = true)
	protected void onCheckSameDestroyTarget(BlockPos bPos, CallbackInfoReturnable<Boolean> cir) {
		ItemStack stack = ClientUtil.mc().player.getMainHandItem();
		if (stack.getItem() instanceof IHermeticTool herm) {
			cir.setReturnValue(bPos.equals(this.destroyBlockPos) && !destroyingItem.shouldCauseBlockBreakReset(stack));
		}
	}

	// :reallyhighrestransparentshadow:
	@Shadow @Nullable
	private BlockPos destroyBlockPos;
	
	@Shadow @Nullable
	private ItemStack destroyingItem;
}
