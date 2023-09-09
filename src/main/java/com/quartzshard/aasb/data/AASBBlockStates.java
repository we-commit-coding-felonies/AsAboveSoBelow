package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.block.AirIceBlock;
import com.quartzshard.aasb.init.ObjectInit;

import org.jetbrains.annotations.NotNull;

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
		simpleBlock(ObjectInit.Blocks.ASH_STONE.get());
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
	

    /**
     * @deprecated
     * Gives an item a placeholder texture
     * This should not be used outside of a development environment
     * @param ro
     */
    @Deprecated
	private void placeholder(Block block) {
		simpleBlock(block, models().cubeAll(block.getRegistryName().getPath(), AsAboveSoBelow.rl("placeholder")));
	}
	
	


	@Override
	public @NotNull String getName() {
		return AsAboveSoBelow.DISPLAYNAME + " | Block States";
	}

}
