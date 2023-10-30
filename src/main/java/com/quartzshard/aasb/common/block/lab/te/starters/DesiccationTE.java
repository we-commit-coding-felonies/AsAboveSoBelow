package com.quartzshard.aasb.common.block.lab.te.starters;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.util.LazyOptional;

import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.FormChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.common.block.lab.te.templates.AspectExtractorTE;
import com.quartzshard.aasb.init.ObjectInit.Items;

import static com.quartzshard.aasb.init.ObjectInit.TileEntities./*Labs.*/*;

import org.jetbrains.annotations.NotNull;

import static com.quartzshard.aasb.api.alchemy.lab.LabProcess.*;

public class DesiccationTE extends AspectExtractorTE {

	public static final String
		TK_TE = "DesiccationTE",
			//TK_INV = "Inventories",
				TK_SHAPEOUT = "ShapeOutput";
	@SuppressWarnings("unchecked")
	public DesiccationTE(BlockPos pos, BlockState state) {
		super((BlockEntityType<DesiccationTE>) DESICCATION_TE.get(), pos, state, DESICCATION, Items.SALT.get());
	}

	// Form OUTPUT
	private final ShapeChamber shapeOut = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	private final LazyOptional<IHandleShape> xShapeOut = LazyOptional.of(() -> shapeOut);

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
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		CompoundTag invDat = new CompoundTag();
		
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
					if (invDat.contains(TK_SHAPEOUT)) {
						shapeOut.deserialize(invDat.getCompound(TK_SHAPEOUT));
					}
				}
				
			}
		}
		super.load(teData);
	}

	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		if (LabRecipeData.hasAspectStacks(dat.shapes)) {
			ShapeStack out = dat.shapes.get(0);
			if (!out.isEmpty() && shapeOut.insert(out, AspectAction.SIMULATE) == 0) {
				shapeOut.insert(out, AspectAction.EXECUTE);
				return super.unpackOutputs(dat);
			}
		}
		return false;
	}
}
