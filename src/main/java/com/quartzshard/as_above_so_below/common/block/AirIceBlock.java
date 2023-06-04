package com.quartzshard.as_above_so_below.common.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * similar to frost walker ice, but doesnt leave water behind
 * @author solunareclipse1
 */
public class AirIceBlock extends HalfTransparentBlock {
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	
	public AirIceBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random rand) {
		int i = state.getValue(AGE);
		if (i < 3) {
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
