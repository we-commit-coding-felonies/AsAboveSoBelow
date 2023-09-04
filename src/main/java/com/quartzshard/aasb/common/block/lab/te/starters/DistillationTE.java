package com.quartzshard.aasb.common.block.lab.te.starters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.*;
import com.quartzshard.aasb.common.block.lab.te.templates.AspectExtractorTE;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.common.item.flask.StorageFlaskItem;
import com.quartzshard.aasb.init.ObjectInit.TileEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Extracts one of the aspects contained within a flask
 */
public class DistillationTE extends AspectExtractorTE {
	public DistillationTE(BlockPos pos, BlockState state) {
		super(TileEntities.DISTILLATION.get(), pos, state);
	}
	
	public void tempDebugRemoveMeLater() {
		storedShape = null;
		storedForm = null;
	}
	
	private final ItemStackHandler itemInv = createHandler();
	private final LazyOptional<IItemHandler> itemInvExposer = LazyOptional.of(() -> itemInv);
	private ItemStackHandler createHandler() {
		return new ItemStackHandler(1) {

			@Override
			protected void onContentsChanged(int slot) {
				// To make sure the TE persists when the chunk is saved later we need to
				// mark it dirty every time the item handler changes
				setChanged();
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) {
				return stack.getItem() instanceof FlaskItem flask && flask.hasStored(stack);
			}

			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				boolean shouldInsert = getStackInSlot(slot).isEmpty() && isItemValid(slot, stack);
				if (!simulate && shouldInsert) {
					this.setStackInSlot(slot, stack);
				}
				return shouldInsert ? ItemStack.EMPTY : stack;
			}
		};
	}
	
	@Override
	public void tick() {
		//System.out.println(storedShape);
		if (storedShape != null && !storedShape.isValid()) {
			storedShape = null;
			System.out.println("SHAPE nulled");
		}
		if (storedForm != null && !storedForm.isValid()) {
			storedForm = null;
			System.out.println("FORM nulled");
		}
		if (storedShape == null && storedForm == null) {
			ItemStack stack = itemInv.getStackInSlot(0);
			if (stack.getItem() instanceof FlaskItem flask && !flask.isContaminated(stack)) {
				if (flask.isExpired(stack, level.getGameTime())) flask.setContaminated(stack, true);
				else if (flask.hasStored(stack)) {
					boolean didDo = false;
					if (flask.hasStoredShape(stack)) {
						storedShape = new LegacyShapeStack(flask.getStoredShape(stack));
						didDo = true;
					}
					if (flask.hasStoredForm(stack)) {
						storedForm = new LegacyFormStack(flask.getStoredForm(stack));
						didDo = true;
					}
					if (flask instanceof StorageFlaskItem) {
						flask.clearStored(stack);
					} else {
						flask.setContaminated(stack, true);
					}
					System.out.println(didDo ? "refill" : "FAILURE!");
				}
			}
		}
		super.tick();
	}

	@Override
	public ItemStack getLeftoversOf(ItemStack input) {
		if (input.getItem() instanceof FlaskItem flask) {
			flask.setContaminated(input, true);
		}
		return null;
	}

	@Override
	public boolean canPushWayTo(Direction side) {
		return side == Direction.WEST;
	}

	@Override
	public boolean canPushShapeTo(Direction side) {
		return side == Direction.SOUTH;
	}

	@Override
	public boolean canPushFormTo(Direction side) {
		return side == Direction.EAST;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (side == Direction.NORTH && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return itemInvExposer.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		itemInvExposer.invalidate();
	}

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Inventory")) {
            itemInv.deserializeNBT(tag.getCompound("Inventory"));
        }
        if (tag.contains("stored_aspects")) {
            CompoundTag aspectTag = tag.getCompound("stored_aspects");
            storedShape = LegacyShapeStack.fromTag(aspectTag.getCompound("stored_shape"));
            storedForm = LegacyFormStack.fromTag(aspectTag.getCompound("stored_form"));
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("Inventory", itemInv.serializeNBT());
        
        CompoundTag aspectTag = new CompoundTag();
        if (storedShape != null)
        	aspectTag.put("stored_shape", storedShape.toTag());

        if (storedForm != null)
        	aspectTag.put("stored_form", storedForm.toTag());
        tag.put("stored_aspects", aspectTag);
    }

}
