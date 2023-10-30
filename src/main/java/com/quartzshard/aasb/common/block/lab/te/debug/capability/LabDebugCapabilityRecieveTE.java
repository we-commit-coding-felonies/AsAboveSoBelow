package com.quartzshard.aasb.common.block.lab.te.debug.capability;

import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class LabDebugCapabilityRecieveTE {//extends BlockEntity {
/*
	public LabDebugCapabilityRecieveTE(BlockPos pPos, BlockState pBlockState) {
		super(ObjectInit.TileEntities.DEBUG_LAB_CAPABILITY_RECIEVER_TE.get(), pPos, pBlockState);
	}

	private final ShapeChamber shapeInv = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	private final LazyOptional<IHandleShape> xShapeInv = LazyOptional.of(() -> shapeInv);



	@Override
	public void setRemoved() {
		super.setRemoved();
		xShapeInv.invalidate();
	}

	public void tickServer() {
		//if (!shapeInv.isEmpty()) {
		//	pushShapes();
		//}
	}

	/*private void pushShapes() {
		if (shapeInv.isEmpty()) return;
        AtomicInteger toPush = new AtomicInteger(shapeInv.getAmount());
		if (toPush.get() > 0) {
			for (Direction side : Direction.values()) {
				BlockEntity be = level.getBlockEntity(worldPosition.relative(side));
				if (be != null) {
					Direction oSide = side.getOpposite();
					boolean isDone = be.getCapability(AASBCapabilities.SHAPE_HANDLER, oSide).map(h -> {
						if (h.canAccept(oSide)) {
							int left = h.receiveFrom(shapeInv.getChamberContents(0), oSide);
							int toExtract = toPush.getAndSet(left) - left;
							shapeInv.extract(toExtract, AspectAction.EXECUTE);
							setChanged();
							return toPush.get() > 0;
						}
						return true;
					}).orElse(false);
					if (isDone) {
						return;
					}
				}
			}
		}
	}*//*

	@Override
	public void load(CompoundTag tag) {
		if (tag.contains("StoredShape")) {
			shapeInv.deserialize(tag);
		}
		super.load(tag);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		CompoundTag chamber = shapeInv.serialize();
		if (chamber != null) {
			tag.put("StoredShape", chamber);
		}
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == AASBCapabilities.SHAPE_HANDLER) {
			return xShapeInv.cast();
		}
		return super.getCapability(cap, side);
	}
	*/
}
