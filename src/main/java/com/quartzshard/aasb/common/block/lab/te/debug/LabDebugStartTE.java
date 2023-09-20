package com.quartzshard.aasb.common.block.lab.te.debug;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
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
import com.quartzshard.aasb.common.block.lab.LabBlock;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.WorldHelper.Side;

public class LabDebugStartTE extends LabTE {
	
	public LabDebugStartTE(BlockPos pos, BlockState state) {
		super(ObjectInit.TileEntities.DEBUG_LAB_START_TE.get(), pos, state, 100, LabProcess.DISTILLATION.getFunc());
	}

	// Item INPUT
	private final ItemStackHandler itemIn = new ItemStackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			setChangedInput();
		}
	};
	private final LazyOptional<IItemHandler> xItemIn = LazyOptional.of(() -> itemIn);
	
	// Item OUTPUT
	private final ItemStackHandler itemOut = new ItemStackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			setChanged();
		}
	};
	private final LazyOptional<IItemHandler> xItemOut = LazyOptional.of(() -> itemOut);
	
	// Shape OUTPUT
	private final ShapeChamber shapeOut = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	private final LazyOptional<IHandleShape> xShapeOut = LazyOptional.of(() -> shapeOut);

	// Form OUTPUT
	private final FormChamber formOut = new FormChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	private final LazyOptional<IHandleForm> xFormOut = LazyOptional.of(() -> formOut);

	@Override
	@Nullable
	protected LabRecipeData packInputs() {
		LogHelper.debug("LabDebugStartTE.packInputs()", "PackInputsSTART");
		ItemStack stack = itemIn.extractItem(0, 1, true);
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof FlaskItem flask
				&& flask.hasStored(stack)
				&& !flask.isExpired(stack, level.getGameTime())) {
				NonNullList<ItemStack> items = LabRecipeData.il(1);
				items.set(0, stack);
				return new LabRecipeData(items, null, null, null, null);
			}
		}
		return null;
	}

	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		System.out.println("clientside?"+  level.isClientSide);
		System.out.println(dat.serialize());
		Executor xc = () -> {};
		boolean doItems = LabRecipeData.hasItemStacks(dat.items),
				doShapes = LabRecipeData.hasAspectStacks(dat.shapes),
				doForms = LabRecipeData.hasAspectStacks(dat.forms);
		if (doItems) {
			System.out.println("i");
			ItemStack outStack = dat.items.get(0);
			ItemStack estLeft = itemOut.insertItem(0, outStack, true);
			if (!estLeft.isEmpty())
				return false;
			xc = xc.also(() -> itemOut.insertItem(0, outStack, false));
		}
		if (doShapes) {
			System.out.println("s");
			ShapeStack outStack = dat.shapes.get(0);
			int estLeft = shapeOut.insert(outStack, AspectAction.SIMULATE);
			if (estLeft != 0)
				return false;
			xc = xc.also(() -> shapeOut.insert(outStack, AspectAction.EXECUTE));
		}
		if (doForms) {
			System.out.println("f");
			FormStack outStack = dat.forms.get(0);
			System.out.println(outStack.serialize());
			int estLeft = formOut.insert(outStack, AspectAction.SIMULATE);
			System.out.println(estLeft);
			if (estLeft != 0)
				return false;
			xc = xc.also(() -> formOut.insert(outStack, AspectAction.EXECUTE));
		}
		xc.execute();
		return doItems || doShapes || doForms;
	}

	@Override
	protected void consumeInputs(LabRecipeData dat) {
		boolean doItems = LabRecipeData.hasItemStacks(dat.items);
		if (doItems) {
			ItemStack toConsume = dat.items.get(0);
			if (!toConsume.isEmpty()) {
				ItemStack consumed = itemIn.extractItem(0, 1, false);
			}
		} else {
			LogHelper.warn("LabDebugStartTE.consumeInputs()", "InvalidConsumption", "Input consumption data was invalid! Clearing input buffers to prevent duplication exploits.");
			for (int i = 0; i < itemIn.getSlots(); i++) {
				itemIn.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}

	@Override
	protected boolean tryPushItem(IItemHandler target, Direction dir) {
		LogHelper.debug("LabDebugStartTE.tryPushItem()", "EmptyFlaskPUSH");
		if (dir == Side.BOTTOM.abs(getFacing())) {
			ItemStack toPush = itemOut.extractItem(0, 1, true);
			if (!toPush.isEmpty()) {
				for (int i = 0; i < target.getSlots(); i++) {
					ItemStack rem = target.insertItem(i, toPush, true);
					if (rem.isEmpty()) {
						target.insertItem(i, itemOut.extractItem(0, 1, false), false);
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected boolean tryPushFluid(IFluidHandler target, Direction dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean tryPushWay(IHandleWay target, Direction dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean tryPushShape(IHandleShape target, Direction dir) {
		LogHelper.debug("LabDebugStartTE.tryPushShape()", "ShapePUSH");
		if (dir == Side.BACK.abs(getFacing())) {
			ShapeStack toPush = shapeOut.extract(1, AspectAction.SIMULATE);
			if (!toPush.isEmpty()) {
				int left = target.receiveFrom(toPush, dir.getOpposite());
				if (left == 0) {
					shapeOut.extract(1, AspectAction.EXECUTE);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean tryPushForm(IHandleForm target, Direction dir) {
		LogHelper.debug("LabDebugStartTE.tryPushForm()", "FormPUSH");
		if (dir == Side.BACK.abs(getFacing())) {
			FormStack toPush = formOut.extract(1, AspectAction.SIMULATE);
			if (!toPush.isEmpty()) {
				int left = target.receiveFrom(toPush, dir.getOpposite());
				if (left == 0) {
					formOut.extract(1, AspectAction.EXECUTE);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected Side[] getPushingSides(PushType type) {
		switch (type) {
		case ITEM:
			return new Side[] {Side.BOTTOM};
		case SHAPE:
		case FORM:
			return new Side[] {Side.BACK};
		default:
			break;
		}
		return new Side[] {};
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction dir) {
		//System.out.println(dir + " vs " + Side.rel(dir, getFacing()));
		switch (Side.rel(dir, getFacing())) { // HopperBlockEntity
		case FRONT:
			if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return xItemIn.cast();
			break;
		default:
			break;
		}
		return super.getCapability(cap, dir);
	}

	@Override
	protected LinkedHashMap<String, String> getDebugInfoSpecific(LinkedHashMap<String, String> info) {
		info.put("Item.I", itemIn.getStackInSlot(0).serializeNBT().getAsString());
		info.put("Item.O", itemOut.getStackInSlot(0).serializeNBT().getAsString());
		info.put("Shape.O", shapeOut.getContents().serialize()+"");
		info.put("Form.O", formOut.getContents().serialize()+"");
		return info;
	}

}
