package com.quartzshard.aasb.common.block.lab.te.templates;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.*;
import com.quartzshard.aasb.common.block.lab.te.AspectTE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AspectExtractorTE extends AspectTE {
	@Nullable protected LegacyWayStack storedWay = null;
	@Nullable protected LegacyShapeStack storedShape = null;
	@Nullable protected LegacyFormStack storedForm = null;

	public AspectExtractorTE(BlockEntityType<? extends AspectExtractorTE> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public abstract ItemStack getLeftoversOf(ItemStack input);

	
	// configs
	@Override
	public boolean canAccept(Direction side) {return false;}
	@Override
	public boolean canAcceptWay(Direction side, LegacyWayStack query) {return false;}
	@Override
	public boolean canAcceptShape(Direction side, LegacyShapeStack query) {return false;}
	@Override
	public boolean canAcceptForm(Direction side, LegacyFormStack query) {return false;}
	
	@Override
	public boolean insertWay(LegacyWayStack toInsert) {return false;}
	@Override
	public LegacyShapeStack insertShape(LegacyShapeStack toInsert) {return toInsert;}
	@Override
	public LegacyFormStack insertForm(LegacyFormStack toInsert) {return toInsert;}

	@Override
	public boolean canPush() {return true;}

	@Override
	protected @Nullable LegacyWayStack getWayToPush(Direction side) {return storedWay;}
	@Override
	protected @Nullable LegacyShapeStack getShapeToPush(Direction side) {return storedShape;}
	@Override
	protected @Nullable LegacyFormStack getFormToPush(Direction side) {return storedForm;}

}
