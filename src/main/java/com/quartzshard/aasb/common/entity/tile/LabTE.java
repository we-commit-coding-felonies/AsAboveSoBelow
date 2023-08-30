package com.quartzshard.aasb.common.entity.tile;

import com.quartzshard.aasb.init.ObjectInit.TileEntities;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LabTE extends BlockEntity {
	public LabTE(BlockPos pos, BlockState state) {
		super(TileEntities.LAB_TE.get(), pos, state);
	}

    public void tickServer() {
    	System.out.println("lab tick");
    }
}
