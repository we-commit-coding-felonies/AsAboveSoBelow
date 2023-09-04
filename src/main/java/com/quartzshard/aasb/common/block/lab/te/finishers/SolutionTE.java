package com.quartzshard.aasb.common.block.lab.te.finishers;

import com.quartzshard.aasb.common.block.lab.te.AspectTE;
import com.quartzshard.aasb.init.ObjectInit.TileEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SolutionTE extends AspectTE {

	public SolutionTE(BlockPos pos, BlockState state) {
		super(TileEntities.DISTILLATION.get(), pos, state);
	}

}