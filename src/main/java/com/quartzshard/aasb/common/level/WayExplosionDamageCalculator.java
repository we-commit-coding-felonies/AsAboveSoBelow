package com.quartzshard.aasb.common.level;

import java.util.Optional;

import com.quartzshard.aasb.data.tags.BlockTP;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class WayExplosionDamageCalculator extends ExplosionDamageCalculator {
	final float blockResist;
	
	public WayExplosionDamageCalculator(float radius) {
		this.blockResist = 1f/(radius);
	}
	
	@Override
	public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {
		if (blockResist < 0 || state.is(BlockTP.WAYBLAST_RESIST)) {
			return super.getBlockExplosionResistance(explosion, reader, pos, state, fluid);
		}
		return Optional.of(blockResist);
	}
	
	@Override
	public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, @NotNull BlockState state, float power) {
		return !state.is(BlockTP.WAYBLAST_IMMUNE);
	}
}
