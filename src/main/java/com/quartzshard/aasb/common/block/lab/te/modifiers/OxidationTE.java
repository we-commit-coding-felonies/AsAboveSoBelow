package com.quartzshard.aasb.common.block.lab.te.modifiers;

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
import com.quartzshard.aasb.common.block.lab.te.templates.ShapeShifterTE;
import com.quartzshard.aasb.init.ObjectInit.Items;

import static com.quartzshard.aasb.init.ObjectInit.TileEntities./*Labs.*/*;

import org.jetbrains.annotations.NotNull;

import static com.quartzshard.aasb.api.alchemy.lab.LabProcess.*;

public class OxidationTE extends ShapeShifterTE {
	@SuppressWarnings("unchecked")
	public OxidationTE(BlockPos pos, BlockState state) {
		super((BlockEntityType<OxidationTE>) OXIDATION_TE.get(), pos, state, OXIDATION);
	}
}
