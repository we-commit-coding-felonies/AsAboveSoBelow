package com.quartzshard.aasb.common.block.lab;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.common.block.lab.te.AspectTE;
import com.quartzshard.aasb.common.block.lab.te.modifiers.OxidationTE;
import com.quartzshard.aasb.common.block.lab.te.starters.DistillationTE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.items.CapabilityItemHandler;

public class LabMultiblock extends Block implements EntityBlock {
	final boolean ox;
	public LabMultiblock(Properties props, boolean ox) {
		super(props);
		this.ox = ox;
	}

    //@SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DistillationTE dis) {
            	ItemStack held = player.getItemInHand(hand);
            	if (held.isEmpty()) {
            		dis.tempDebugRemoveMeLater();
            	} else {
            		be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).ifPresent(lmao -> {
            			ItemStack swap = lmao.insertItem(0, held, false);
            			if (swap.isEmpty()) {
            				player.setItemInHand(hand, swap);
            			}
            		});
            	}
            } else if (be instanceof OxidationTE ox) {
            	ox.tempDebugRemoveMeLater();
            }
        }
        return InteractionResult.SUCCESS;
    }

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		System.out.println("creation");
		return ox ? new OxidationTE(pos, state) : new DistillationTE(pos, state);
	}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, bState, te) -> {
            if (te instanceof AspectTE lab) {
                lab.tick();
            }
        };
    }

}
