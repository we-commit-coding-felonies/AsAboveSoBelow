package com.quartzshard.aasb.common.damage;

import java.util.Optional;

import com.quartzshard.aasb.data.AASBTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockAgnosticDamageCalculator extends ExplosionDamageCalculator {
	final float blockResist;
	
	public BlockAgnosticDamageCalculator(float blockResist) {
		this.blockResist = blockResist;
	}
	
	@Override
	public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {
		if (blockResist < 0 || state.is(AASBTags.BlockTP.WAYBLAST_RESIST)) {
			return super.getBlockExplosionResistance(explosion, reader, pos, state, fluid);
		}
		return Optional.of(blockResist);
	}
	
	@Override
	public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
		return !state.is(AASBTags.BlockTP.WAYBLAST_IMMUNE);
	}
}
