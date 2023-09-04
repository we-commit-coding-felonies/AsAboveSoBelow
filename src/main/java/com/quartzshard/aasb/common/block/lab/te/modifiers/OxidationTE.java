package com.quartzshard.aasb.common.block.lab.te.modifiers;

import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.*;
import com.quartzshard.aasb.common.block.lab.te.templates.AspectExtractorTE;
import com.quartzshard.aasb.init.ObjectInit.TileEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class OxidationTE extends AspectExtractorTE {

	public OxidationTE(BlockPos pos, BlockState state) {
		super(TileEntities.DISTILLATION.get(), pos, state);
	}
	
	public void tempDebugRemoveMeLater() {
		System.out.println(storedShape + " Ox");
		storedShape.setAmount(2);
		storedShape = null;
	}
	
	@Override
	public boolean canAccept(Direction side) {
		return side == Direction.NORTH;
	}
	
	@Override
	public LegacyShapeStack insertShape(LegacyShapeStack toInsert) {
		if (storedShape == null) {
			storedShape = toInsert;
			return null;
		}
		return toInsert;
	}
	
	@Override
	public boolean canAcceptShape(Direction side, LegacyShapeStack query) {
		return canAccept(side) && query.isValid() && storedShape == null;
	}

	@Override
	public ItemStack getLeftoversOf(ItemStack input) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canPushWayTo(Direction side) {
		return false;
	}

	@Override
	public boolean canPushShapeTo(Direction side) {
		return false;
	}

	@Override
	public boolean canPushFormTo(Direction side) {
		return false;
	}

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("stored_shape")) {
            storedShape = LegacyShapeStack.fromTag(tag.getCompound("stored_shape"));
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (storedShape != null) tag.put("stored_shape", storedShape.toTag());
    }

}