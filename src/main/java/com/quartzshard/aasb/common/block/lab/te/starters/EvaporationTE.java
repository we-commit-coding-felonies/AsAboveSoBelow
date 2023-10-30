package com.quartzshard.aasb.common.block.lab.te.starters;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.util.LazyOptional;

import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.WayStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.FormChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.common.block.lab.te.templates.AspectExtractorTE;
import com.quartzshard.aasb.init.ObjectInit.Items;

import static com.quartzshard.aasb.init.ObjectInit.TileEntities./*Labs.*/*;

import org.jetbrains.annotations.NotNull;

import static com.quartzshard.aasb.api.alchemy.lab.LabProcess.*;

public class EvaporationTE extends AspectExtractorTE {

	@SuppressWarnings("unchecked")
	public EvaporationTE(BlockPos pos, BlockState state) {
		super((BlockEntityType<EvaporationTE>) EVAPORATION_TE.get(), pos, state, EVAPORATION, Items.SOOT.get());
	}

	// Form OUTPUT
	private final FormChamber formOut = new FormChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};

	@Override
	protected boolean tryPushWay(IHandleWay target, Direction dir) {
		return false;
	}

	@Override
	protected boolean tryPushShape(IHandleShape target, Direction dir) {
		return false;
	}

	@Override
	protected boolean tryPushForm(IHandleForm target, Direction dir) {
		FormStack toPush = formOut.extract(1, AspectAction.SIMULATE);
		if (!toPush.isEmpty()) {
			int left = target.receiveFrom(toPush, dir.getOpposite());
			if (left == 0) {
				formOut.extract(1, AspectAction.EXECUTE);
				return true;
			}
		}
		return false;
	}
	
	public static final String
		TK_TE = "SublimationTE",
			//TK_INV
				TK_FORMOUT = "FormOutput";
	
	@Override
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		CompoundTag invDat = new CompoundTag();
		
		if (!formOut.isEmpty()) {
			invDat.put(TK_FORMOUT, formOut.serialize());
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
					if (invDat.contains(TK_FORMOUT)) {
						formOut.deserialize(invDat.getCompound(TK_FORMOUT));
					}
				}
				
			}
		}
		super.load(teData);
	}

	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		if (LabRecipeData.hasAspectStacks(dat.forms)) {
			FormStack out = dat.forms.get(0);
			if (!out.isEmpty() && formOut.insert(out, AspectAction.SIMULATE) == 0) {
				formOut.insert(out, AspectAction.EXECUTE);
				return super.unpackOutputs(dat);
			}
		}
		return false;
	}
}
