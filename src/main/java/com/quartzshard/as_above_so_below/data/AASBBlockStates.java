package com.quartzshard.as_above_so_below.data;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;
import com.quartzshard.as_above_so_below.common.block.AirIceBlock;
import com.quartzshard.as_above_so_below.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AASBBlockStates extends BlockStateProvider {
    
    public AASBBlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, AsAboveSoBelow.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
		simpleBlock(ObjectInit.Blocks.WAYSTONE.get());
		
		getVariantBuilder(ObjectInit.Blocks.AIR_ICE.get())
			.partialState().with(AirIceBlock.AGE, 0).addModels(modelOf(Blocks.FROSTED_ICE, "_0"))
			.partialState().with(AirIceBlock.AGE, 1).addModels(modelOf(Blocks.FROSTED_ICE, "_1"))
			.partialState().with(AirIceBlock.AGE, 2).addModels(modelOf(Blocks.FROSTED_ICE, "_2"))
			.partialState().with(AirIceBlock.AGE, 3).addModels(modelOf(Blocks.FROSTED_ICE, "_3"))
		;
    }
    

	
	private ConfiguredModel modelOf(Block block, String suffix) {
		return new ConfiguredModel(models().getExistingFile(ModelLocationUtils.getModelLocation(block, suffix)));
	}

}
