package com.quartzshard.aasb.common.block.lab;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.common.entity.tile.LabTE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LabMultiblock extends Block implements EntityBlock {

	public LabMultiblock(Properties props) {
		super(props);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		System.out.println("creation");
		return new LabTE(pos, state);
	}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, bState, te) -> {
            if (te instanceof LabTE lab) {
                lab.tickServer();
            }
        };
    }

}
