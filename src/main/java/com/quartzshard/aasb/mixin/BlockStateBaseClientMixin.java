package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseClientMixin {

	@Shadow
	public abstract Block getBlock();

	@Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("HEAD"), cancellable = true)
	private void onGetCollisionShape(BlockGetter world, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
		if (context instanceof EntityCollisionContext && ((EntityCollisionContext)context).getEntity() instanceof AstralProjection.FreeCamera) {
			if (AstralProjection.isEnabled()) {
				// Ignore all collisions
				cir.setReturnValue(Shapes.empty());
			}
		}
	}
}
