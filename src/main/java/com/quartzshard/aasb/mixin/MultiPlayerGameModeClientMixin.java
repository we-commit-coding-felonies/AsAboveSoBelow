package com.quartzshard.aasb.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.util.ClientUtil;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
	
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
