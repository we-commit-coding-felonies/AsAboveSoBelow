package com.quartzshard.aasb.common.block.lab.te.templates;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.alchemy.lab.LabFunction;
import com.quartzshard.aasb.api.alchemy.lab.LabProcess;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.util.NBTHelper.TagKeys;
import com.quartzshard.aasb.util.WorldHelper.Side;

public class ShapeShifterTE extends LabTE {

	public ShapeShifterTE(BlockEntityType<? extends ShapeShifterTE> type, BlockPos pos, BlockState state,
			LabProcess process) {
		this(type, pos, state, process.getFunc());
	}
	public ShapeShifterTE(BlockEntityType<? extends ShapeShifterTE> type, BlockPos pos, BlockState state,
			LabFunction func) {
		super(type, pos, state, 100, func);
	}
	// Shape INPUT
	private final ShapeChamber shapeIn = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChangedInput();
		}
	};
	private final LazyOptional<IHandleShape> xShapeIn = LazyOptional.of(() -> shapeIn);
	// Shape OUTPUT
	private final ShapeChamber shapeOut = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	private final LazyOptional<IHandleShape> xShapeOut = LazyOptional.of(() -> shapeOut);
	
	@Override
	@Nullable
	protected LabRecipeData packInputs() {
		if (!shapeIn.getContents().isEmpty()) {
			NonNullList<ShapeStack> shapes = LabRecipeData.sl(1);
			shapes.set(0, shapeIn.extract(1, AspectAction.SIMULATE));
			return new LabRecipeData(null, null, null, shapes, null);
		}
		return null;
	}
	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		if (LabRecipeData.hasAspectStacks(dat.shapes)) {
			ShapeStack out = dat.shapes.get(0);
			if (!out.isEmpty() && shapeOut.insert(out, AspectAction.SIMULATE) == 0) {
				shapeOut.insert(out, AspectAction.EXECUTE);
				return true;
			}
		}
		return false;
	}
	@Override
	protected void consumeInputs(LabRecipeData toConsume) {
		if (LabRecipeData.hasAspectStacks(toConsume.shapes)) {
			shapeIn.extract(1, AspectAction.EXECUTE);
		}
		
	}
	@Override
	protected boolean tryPushItem(IItemHandler target, Direction dir) {
		return false;
	}
	@Override
	protected boolean tryPushFluid(IFluidHandler target, Direction dir) {
		return false;
	}
	@Override
	protected boolean tryPushWay(IHandleWay target, Direction dir) {
		return false;
	}
	@Override
	protected boolean tryPushShape(IHandleShape target, Direction dir) {
		ShapeStack toPush = shapeOut.extract(1, AspectAction.SIMULATE);
		if (!toPush.isEmpty()) {
			int left = target.receiveFrom(toPush, dir.getOpposite());
			if (left == 0) {
				shapeOut.extract(1, AspectAction.EXECUTE);
				return true;
			}
		}
		return false;
	}
	@Override
	protected boolean tryPushForm(IHandleForm target, Direction dir) {
		return false;
	}
	@Override
	protected Side[] getPushingSides(PushType type) {
		switch (type) {
		case SHAPE:
			return new Side[] {Side.LEFT, Side.RIGHT};
		default:
			break;
		}
		return new Side[] {};
	}
	@Override
	protected LinkedHashMap<String, String> getDebugInfoSpecific(LinkedHashMap<String, String> info) {
		return info;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction dir) {
		if (dir != null) {
			switch (Side.rel(dir, getFacing())) {
			case FRONT:
			case BACK:
				if (cap == AASBCapabilities.SHAPE_HANDLER)
					return xShapeIn.cast();
				break;
			default:
				break;
			}
		}
		return super.getCapability(cap, dir);
	}


	public static final String
		TK_TE = "ShapeShifterTE",
			TK_INV = TagKeys.MULTIINV,
				TK_SHAPEIN = "ShapeInput",
				TK_SHAPEOUT = "ShapeOutput";
	@Override
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		CompoundTag invDat = new CompoundTag();
		
		if (!shapeIn.isEmpty()) {
			invDat.put(TK_SHAPEIN, shapeIn.serialize());
		}
		if (!shapeOut.isEmpty()) {
			invDat.put(TK_SHAPEOUT, shapeOut.serialize());
		}
		if (!invDat.isEmpty()) {
			dat.put(TK_INV, invDat);
		}
		
		if (!dat.isEmpty())
			labData.put(TK_TE, dat);
		return super.saveLabData(labData);
	}

	@Override
	public void load(CompoundTag teData) {
		if (teData.contains(TK_DATA)) {
			CompoundTag labData = teData.getCompound(TK_DATA);
			if (labData.contains(TK_TE)) {
				CompoundTag dat = labData.getCompound(TK_TE);
				if (dat.contains(TK_INV)) {
					CompoundTag invDat = labData.getCompound(TK_INV);
					if (invDat.contains(TK_SHAPEIN)) {
						shapeIn.deserialize(invDat.getCompound(TK_SHAPEIN));
					}
					if (invDat.contains(TK_SHAPEOUT)) {
						shapeOut.deserialize(invDat.getCompound(TK_SHAPEOUT));
					}
				}
				
			}
		}
		super.load(teData);
	}
}
