package com.quartzshard.aasb.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CrumblingStoneBlock extends Block {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	
	public CrumblingStoneBlock(Properties props) {
		super(props);
		this.registerDefaultState(stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		int i = state.getValue(AGE);
		if (i < 2) {
			level.setBlock(pos, state.setValue(AGE, Integer.valueOf(i + 1)), 2);
		} else {
			level.destroyBlock(pos, false);
		}
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
