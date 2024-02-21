package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.block.CrumblingStoneBlock;
import com.quartzshard.aasb.init.object.BlockInit;

import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class BlockModelData extends BlockStateProvider {

	public BlockModelData(PackOutput out, ExistingFileHelper help) {
		super(out, AASB.MODID, help);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(BlockInit.ASHEN_STONE.get());

		getVariantBuilder(BlockInit.CRUMBLING_STONE.get())
			.partialState().with(CrumblingStoneBlock.AGE, 0).addModels(modelOf(Blocks.STONE, ""))
			.partialState().with(CrumblingStoneBlock.AGE, 1).addModels(modelOf(Blocks.COBBLESTONE, ""))
			.partialState().with(CrumblingStoneBlock.AGE, 2).addModels(modelOf(Blocks.GRAVEL, ""))
			.partialState().with(CrumblingStoneBlock.AGE, 3).addModels(modelOf(Blocks.SAND, ""))
		;
	}
	
	private ConfiguredModel modelOf(@NotNull Block block, String suffix) {
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
		simpleBlock(block, models().cubeAll(ForgeRegistries.BLOCKS.getKey(block).getPath(), AASB.rl("placeholder")));
	}

}
