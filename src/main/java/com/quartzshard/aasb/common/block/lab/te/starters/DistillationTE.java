package com.quartzshard.aasb.common.block.lab.te.starters;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;

import com.quartzshard.aasb.api.alchemy.lab.LabFunction;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.util.WorldHelper.Side;

import static com.quartzshard.aasb.api.alchemy.lab.LabProcess.*;
import static com.quartzshard.aasb.init.ObjectInit.TileEntities./*Labs.*/*;

public class DistillationTE extends LabTE {

	public static final String
		TK_TE = "DistillationTE";
	public DistillationTE(BlockPos pos, BlockState state) {
		super(DISTILLATION_TE.get(), pos, state, 300, DISTILLATION);
	}

	@Override
	@Nullable
	protected LabRecipeData packInputs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void consumeInputs(LabRecipeData toConsume) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean tryPushItem(IItemHandler target, Direction dir) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected LinkedHashMap<String, String> getDebugInfoSpecific(LinkedHashMap<String, String> info) {
		// TODO Auto-generated method stub
		return null;
	}

}
