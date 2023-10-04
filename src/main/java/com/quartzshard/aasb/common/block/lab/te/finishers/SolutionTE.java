package com.quartzshard.aasb.common.block.lab.te.finishers;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import com.quartzshard.aasb.api.alchemy.aspects.stack.*;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.FormChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.lab.LabFunction;
import com.quartzshard.aasb.api.alchemy.lab.LabProcess;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.api.misc.Executor;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.common.item.flask.StorageFlaskItem;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.NBTHelper.TagKeys;
import com.quartzshard.aasb.util.WorldHelper.Side;

import static com.quartzshard.aasb.init.ObjectInit.TileEntities./*Labs.*/*;
import static com.quartzshard.aasb.api.alchemy.lab.LabProcess.*;

/**
 * Takes a shape and a form, puts it into flask(s)
 */
public class SolutionTE extends LabTE {
	public SolutionTE(BlockPos pos, BlockState state) {
		super(SOLUTION_TE.get(), pos, state, 40, SOLUTION);
	}

	// Item INPUT
	private final ItemStackHandler itemIn = new ItemStackHandler(2) {
		@Override
		public void onContentsChanged(int slot) {
			setChangedInput();
		} 
		
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			if (slot == 1) {
				return stack.getItem() instanceof StorageFlaskItem flask && !flask.hasStored(stack);
			}
			return stack.getItem() instanceof FlaskItem flask && !flask.hasStored(stack);
		}
	};
	private final LazyOptional<IItemHandler> xItemIn = LazyOptional.of(() -> itemIn);
	
	// Alkahest INPUT TODO: add alkahest and update accordingly (make it not use water)
	private final FluidTank alkIn = new FluidTank(16180, (f) -> f.getFluid().isSame(Fluids.WATER)) {
		@Override
		protected void onContentsChanged() {
			setChangedInput();
		}
	};
	private final LazyOptional<IFluidHandler> xAlkIn = LazyOptional.of(() -> alkIn);
	
	// Shape INPUT
	private final ShapeChamber shapeIn = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChangedInput();
		}
	};
	private final LazyOptional<IHandleShape> xShapeIn = LazyOptional.of(() -> shapeIn);
	
	// Form INPUT
	private final FormChamber formIn = new FormChamber(10) {
		@Override
		public void onChanged() {
			setChangedInput();
		}
	};
	private final LazyOptional<IHandleForm> xFormIn = LazyOptional.of(() -> formIn);
	
	// Item OUTPUT
	private final ItemStackHandler itemOut = new ItemStackHandler(2) {
		@Override
		public void onContentsChanged(int slot) {
			setChanged();
		}
	};

	@Override
	@Nullable
	protected LabRecipeData packInputs() {
		// FLASK PACKING
		NonNullList<ItemStack> items;
		ItemStack flask1 = itemIn.extractItem(0, 1, true);
		ItemStack flask2;
		if (flask1.getItem() instanceof StorageFlaskItem) {
			flask2 = itemIn.extractItem(1, 1, true);
			if (flask2.getItem() instanceof StorageFlaskItem) {
				items = LabRecipeData.il(2);
				items.set(1, flask2);
			} else return null; // wasnt an aether flask, so invalid
		} else {
			items = LabRecipeData.il(1);
		}
		items.set(0, flask1);
		
		// ALKAHEST PACKING
		NonNullList<FluidStack> fluids = LabRecipeData.ll(1);
		FluidStack alk = alkIn.drain(1618, FluidAction.SIMULATE);
		if (alk.getAmount() == 1618) {
			fluids.set(0, alk);
		} else return null;
		
		// SHAPE PACKING
		NonNullList<ShapeStack> shapes = LabRecipeData.sl(1);
		ShapeStack shapeToBottle = shapeIn.extract(1, AspectAction.SIMULATE);
		if (!shapeToBottle.isEmpty()) {
			shapes.set(0, shapeToBottle);
		} else return null;
		
		// FORM PACKING
		NonNullList<FormStack> forms = LabRecipeData.fl(1);
		FormStack formToBottle = formIn.extract(1, AspectAction.SIMULATE);
		if (!formToBottle.isEmpty()) {
			forms.set(0, formToBottle);
		} else return null;
		
		// janky way to send current game time to recipe func
		NonNullList<WayStack> timeData = LabRecipeData.wl(1);
		timeData.set(0, new WayStack(level.getGameTime(), 1));

		return new LabRecipeData(items, fluids, timeData, shapes, forms);
	}

	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		Executor xc = () -> LogHelper.debug("SolutionTE.unpackOutputs().xc()", "ExecutorStarted");
		if (LabRecipeData.hasItemStacks(dat.items)) {
			ItemStack out = dat.items.get(0);
			if (!out.isEmpty() && itemOut.insertItem(0, out, true).isEmpty()) {
				xc = xc.also(() -> itemOut.insertItem(0, out, false));
				// second output checking
				if (dat.items.size() == 2) {
					ItemStack out2 = dat.items.get(1);
					if (!out2.isEmpty()) {
						// if there is a second output, we need to make sure it can fit as well
						// if it doesnt we dont output at all, because partial outputs are forbidden
						if (itemOut.insertItem(1, out2, true).isEmpty()) {
							xc = xc.also(() -> itemOut.insertItem(1, out2, false));
						} else return false; // return false early because bad
					}
				}
				xc.execute();
				return true;//super.unpackOutputs(dat);
			}
		}
		return false;
	}

	@Override
	protected void consumeInputs(LabRecipeData toConsume) {
		if (LabRecipeData.hasItemStacks(toConsume.items)) {
			itemIn.extractItem(0, 1, false);
			if (toConsume.items.size() == 2) {
				itemIn.extractItem(1, 1, false);
			}
			shapeIn.extract(1, AspectAction.EXECUTE);
			formIn.extract(1, AspectAction.EXECUTE);
			alkIn.drain(1618, FluidAction.EXECUTE);
		}
	}

	@Override
	protected boolean tryPushItem(IItemHandler target, Direction dir) {
		boolean didDo = false;
		ItemStack toPush = itemOut.extractItem(0, 1, true);
		if (!toPush.isEmpty()) {
			for (int i = 0; i < target.getSlots(); i++) {
				ItemStack rem = target.insertItem(i, toPush, true);
				if (rem.isEmpty()) {
					target.insertItem(i, itemOut.extractItem(0, 1, false), false);
					didDo = true;
				}
			}
		}
		toPush = itemOut.extractItem(1, 1, true);
		if (!toPush.isEmpty()) {
			for (int i = 0; i < target.getSlots(); i++) {
				ItemStack rem = target.insertItem(i, toPush, true);
				if (rem.isEmpty()) {
					target.insertItem(i, itemOut.extractItem(1, 1, false), false);
					didDo = true;
				}
			}
		}
		return didDo;
	}

	@Override
	protected boolean tryPushFluid(IFluidHandler target, Direction dir) {
		return false;
	}

	@Override
	protected boolean tryPushWay(IHandleWay target, Direction dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean tryPushShape(IHandleShape target, Direction dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean tryPushForm(IHandleForm target, Direction dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Side[] getPushingSides(PushType type) {
		switch (type) {
		case ITEM:
			return new Side[] {Side.BOTTOM};
		default:
			break;
		}
		return new Side[] {};
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction dir) {
		if (dir != null) {
			switch (Side.rel(dir, getFacing())) {
			case FRONT:
			case BACK:
				if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					return xItemIn.cast();
				break;
			case LEFT:
				if (cap == AASBCapabilities.SHAPE_HANDLER) {
					return xShapeIn.cast();
				}
				break;
			case RIGHT:
				if (cap == AASBCapabilities.FORM_HANDLER) {
					return xFormIn.cast();
				}
				break;
			case TOP:
				if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
					return xAlkIn.cast();
				}
				break;
			default:
				break;
			}
		}
		return super.getCapability(cap, dir);
	}

	@Override
	protected LinkedHashMap<String, String> getDebugInfoSpecific(LinkedHashMap<String, String> info) {
		return info;
	}


	public static final String
		TK_TE = "SolutionTE",
			TK_INV = TagKeys.MULTIINV,
				TK_ITEMIN = "ItemInput",
				TK_LIQUIDIN = "FluidInput",
				TK_SHAPEIN = "ShapeInput",
				TK_FORMIN = "FormInput",
				TK_ITEMOUT = "ItemOutput";
	@Override
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		CompoundTag invDat = new CompoundTag();
		
		for (int i = 0; i < itemIn.getSlots(); i++) {
			if (!itemIn.getStackInSlot(i).isEmpty()) {
				invDat.put(TK_ITEMIN, itemIn.serializeNBT());
				break;
			}
		}
		if (!alkIn.isEmpty()) {
			invDat.put(TK_LIQUIDIN, alkIn.writeToNBT(new CompoundTag()));
		}
		CompoundTag tag = shapeIn.serialize();
		if (tag != null) {
			invDat.put(TK_SHAPEIN, tag);
		}
		tag = formIn.serialize();
		if (tag != null) {
			invDat.put(TK_FORMIN, tag);
		}
		for (int i = 0; i < itemOut.getSlots(); i++) {
			if (!itemOut.getStackInSlot(i).isEmpty()) {
				invDat.put(TK_ITEMOUT, itemOut.serializeNBT());
				break;
			}
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
					if (invDat.contains(TK_ITEMIN)) {
						itemIn.deserializeNBT(invDat.getCompound(TK_ITEMIN));
					}
					if (invDat.contains(TK_LIQUIDIN)) {
						alkIn.readFromNBT(invDat.getCompound(TK_LIQUIDIN));
					}
					if (invDat.contains(TK_SHAPEIN)) {
						shapeIn.deserialize(invDat.getCompound(TK_SHAPEIN));
					}
					if (invDat.contains(TK_FORMIN)) {
						formIn.deserialize(invDat.getCompound(TK_FORMIN));
					}
					if (invDat.contains(TK_ITEMOUT)) {
						itemOut.deserializeNBT(invDat.getCompound(TK_ITEMOUT));
					}
				}
				
			}
		}
		super.load(teData);
	}

}
