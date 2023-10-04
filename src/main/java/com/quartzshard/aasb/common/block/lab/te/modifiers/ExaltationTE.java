package com.quartzshard.aasb.common.block.lab.te.modifiers;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.MultiShapeChamber;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.util.NBTHelper.TagKeys;
import com.quartzshard.aasb.util.WorldHelper.Side;

import static com.quartzshard.aasb.init.ObjectInit.TileEntities./*Labs.*/*;
import static com.quartzshard.aasb.api.alchemy.lab.LabProcess.*;

public class ExaltationTE extends LabTE {
	public static final ImmutableMap<Side,Integer> SIDE_SLOT_MAP = ImmutableMap.<Side,Integer>builder()
																		.put(Side.RIGHT, 1)
																		.put(Side.FRONT, 2)
																		.put(Side.LEFT, 3)
																	.build();

	public ExaltationTE(BlockPos pos, BlockState state) {
		super(EXALTATION_TE.get(), pos, state, 300, EXALTATION);
	}
	// Shape OUTPUT
	private final ShapeChamber shapeOut = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	private final LazyOptional<IHandleShape> xShapeOut = LazyOptional.of(() -> shapeOut);
	
	// MultiShape INPUT
	private final MultiShapeChamber shapesIn = new MultiShapeChamber(10, 4, (q) -> q != AspectShape.UNIVERSAL) {
		@Override
		public void onChanged() {
			System.out.println("die");
			setChangedInput();
		}
		
		@Override
		public int receiveFrom(ShapeStack stack, Direction dir) {
			int inAmt = stack.getAmount();
			if (dir.getAxis() != Direction.Axis.Y) {
				Side side = Side.rel(dir, getFacing());
				if (side != Side.BACK) {
					Integer mappedSlot = SIDE_SLOT_MAP.get(side);
					if (mappedSlot != null) { // sanity check. probably not needed, but doesnt hurt
						int slot = mappedSlot.intValue();
						int mainLeft = this.insert(stack, AspectAction.SIMULATE, slot);
						if (mainLeft == 0) {
							this.insert(stack, AspectAction.EXECUTE, slot);
							return 0;
						}
						int extraLeft = this.insert(stack, AspectAction.SIMULATE, 0);
						if (extraLeft == 0) {
							this.insert(stack, AspectAction.EXECUTE, 0);
							return 0;
						}
						return stack.getAmount();/*
						System.out.println(slot);
						if (canMaybeFitInSlot(stack, slot)) {
							int rem = this.insert(stack, AspectAction.EXECUTE, slot);
							if (rem != inAmt)
								return rem;
						}
						
						// Raw aspects only get moved around in cardinal directions (N S E W), but this lab block needs 5 I/O
						// As a result, something special needs to be done to fit 5 I/O into 4 sides in order for this block to work
						// The solution here is to simply designate one of the 4 inputs (in this case slot 0) to be accessible from the other 3 input sides,
						//	leaving the last side as the output side. Simple, flexible, and hopefully easy for players to grasp
						// This makes automating this specific device much trickier, as players will have to engineer a solution to our problem themselves.
						// It provides a challenging puzzle with a suitably high reward (full-auto quintessence) for those who want it,
						//	while not making it impossible for more casual / less experienced players to progress.
						// Almost certainly requires flasks as part of the solution, giving them an interesting use and showing their possibilities
						// TODO: test Exaltation extensively to make sure all of this theoretical design actually works as intended
						System.out.println(0);
						if (canMaybeFitInSlot(stack, 0)) {
							int rem = this.insert(stack, AspectAction.EXECUTE, 0);
							if (rem != inAmt)
								return rem;
						}*/
					}
						
				}
			}
			return stack.getAmount();
		}
	};
	private final LazyOptional<IHandleShape> xShapesIn = LazyOptional.of(() -> shapesIn);

	@Override
	@Nullable
	protected LabRecipeData packInputs() {
		NonNullList<ShapeStack> shapes = LabRecipeData.sl(4);
		for (int i = 0; i < shapesIn.getChamberCount(); i++) {
			ShapeStack stack = shapesIn.extract(1, AspectAction.SIMULATE, i);
			if (stack.isEmpty())
				return null;
			shapes.set(i, stack);
		}
		return new LabRecipeData(null, null, null, shapes, null);
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
			for (int i = 0; i < shapesIn.getChamberCount(); i++) {
				System.out.println("eatin shapes");
				shapesIn.extract(1, AspectAction.EXECUTE, i);
			}
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
		if (type == PushType.SHAPE) {
			return new Side[] {Side.BACK};
		}
		return new Side[] {};
	}

	@Override
	protected LinkedHashMap<String, String> getDebugInfoSpecific(LinkedHashMap<String, String> info) {
		// TODO Auto-generated method stub
		info.put(TK_MSHAPESIN, ""+shapesIn.serialize());
		return info;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction dir) {
		if (dir != null) {
			switch (Side.rel(dir, getFacing())) {
			case FRONT:
			case LEFT:
			case RIGHT:
				if (cap == AASBCapabilities.SHAPE_HANDLER)
					return xShapesIn.cast();
				break;
			default:
				break;
			}
		}
		return super.getCapability(cap, dir);
	}


	public static final String
		TK_TE = "ExaltationTE",
			TK_INV = TagKeys.MULTIINV,
				TK_MSHAPESIN = "MultiShapeInput",
				TK_SHAPEOUT = "ShapeOutput";
	@Override
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		CompoundTag invDat = new CompoundTag();
		
		if (!shapesIn.isEmpty()) {
			CompoundTag toWrite = shapesIn.serialize();
			System.out.println(toWrite);
			if (toWrite != null && !toWrite.isEmpty()) {
				invDat.put(TK_MSHAPESIN, toWrite);
			}
		}
		if (!shapeOut.isEmpty()) {
			CompoundTag toWrite = shapeOut.serialize();
			if (toWrite != null)
				invDat.put(TK_SHAPEOUT, toWrite);
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
					if (invDat.contains(TK_MSHAPESIN)) {
						shapesIn.deserialize(invDat.getCompound(TK_MSHAPESIN));
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
