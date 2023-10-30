package com.quartzshard.aasb.common.block.lab.te.templates;

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

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import com.quartzshard.aasb.api.alchemy.lab.LabFunction;
import com.quartzshard.aasb.api.alchemy.lab.LabProcess;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.util.NBTHelper.TagKeys;
import com.quartzshard.aasb.util.WorldHelper.Side;

/**
 * Lab block that takes an item and extracts one of its aspects, leaving behind waste
 */
public abstract class AspectExtractorTE extends LabTE {

	public static final String
		TK_TE = "AspectExtractorTE",
			TK_INV = TagKeys.MULTIINV,
				TK_ITEMIN = "ItemInput",
				TK_ITEMOUT = "ItemOutput";
	public AspectExtractorTE(BlockEntityType<? extends AspectExtractorTE> type, BlockPos pos, BlockState state,
			LabProcess process,
			Item wasteItem) {
		this(type, pos, state, process.getFunc(), wasteItem);
	}
	public AspectExtractorTE(BlockEntityType<? extends AspectExtractorTE> type, BlockPos pos, BlockState state,
			LabFunction func,
			Item wasteItem) {
		super(type, pos, state, 200, func);
		this.wasteItem = wasteItem;
	}
	public final Item wasteItem;

	// Item INPUT
	private final ItemStackHandler itemIn = new ItemStackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			setChangedInput();
		}
		
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return true; // TODO implement with mapper
		}
	};
	private final LazyOptional<IItemHandler> xItemIn = LazyOptional.of(() -> itemIn);
	
	// Item OUTPUT
	private final ItemStackHandler itemOut = new ItemStackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			setChanged();
		}
		
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return stack.getItem() == wasteItem;
		}
	};
	private final LazyOptional<IItemHandler> xItemOut = LazyOptional.of(() -> itemOut);

	@Override
	@Nullable
	protected LabRecipeData packInputs() {
		ItemStack input = itemIn.extractItem(0, 1, true);
		if (!input.isEmpty()) {
			NonNullList<ItemStack> il = LabRecipeData.il(1);
			il.set(0, input);
			return new LabRecipeData(il, null, null, null, null);
		}
		return null;
	}

	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		if (LabRecipeData.hasItemStacks(dat.items)) {
			ItemStack out = dat.items.get(0);
			if (!out.isEmpty() && itemOut.insertItem(0, out, true).isEmpty()) {
				itemOut.insertItem(0, out, false);
				return true;//super.unpackOutputs(dat);
			}
		}
		return false;
	}

	@Override
	protected void consumeInputs(LabRecipeData toConsume) {
		if (LabRecipeData.hasItemStacks(toConsume.items)) {
			itemIn.extractItem(0, 1, false);
		}
	}

	@Override
	protected boolean tryPushItem(IItemHandler target, Direction dir) {
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
		return false;
	}

	@Override
	protected boolean tryPushFluid(IFluidHandler target, Direction dir) {
		return false;
	}

	@Override
	protected Side[] getPushingSides(PushType type) {
		switch (type) {
		case ITEM:
			return new Side[] {Side.BOTTOM};
		case WAY:
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
		if (dir != null) {
			switch (Side.rel(dir, getFacing())) {
			case FRONT:
			case LEFT:
			case RIGHT:
				if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					return xItemIn.cast();
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
	
	@Override
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		CompoundTag invDat = new CompoundTag();
		
		if (!itemIn.getStackInSlot(0).isEmpty()) {
			invDat.put(TK_ITEMIN, itemIn.serializeNBT());
		}
		if (!itemOut.getStackInSlot(0).isEmpty()) {
			invDat.put(TK_ITEMOUT, itemIn.serializeNBT());
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
					if (invDat.contains(TK_ITEMOUT)) {
						itemOut.deserializeNBT(invDat.getCompound(TK_ITEMOUT));
					}
				}
				
			}
		}
		super.load(teData);
	}

}
