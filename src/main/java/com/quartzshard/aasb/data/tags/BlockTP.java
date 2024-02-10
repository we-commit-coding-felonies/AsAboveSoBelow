package com.quartzshard.aasb.data.tags;

import java.util.concurrent.CompletableFuture;

import com.quartzshard.aasb.AASB;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTP extends BlockTagsProvider {
	
	public BlockTP(PackOutput out, CompletableFuture<HolderLookup.Provider> lp, ExistingFileHelper help) {
		super(out, lp, AASB.MODID, help);
	}
	
	public static final TagKey<Block>
		AREABLAST_EFFECTIVE = c("areablast_effective"),
	
		ARROW_NOCLIP = c("sentient_arrow_noclip"),
		ARROW_DESTROY = c("sentient_arrow_destroy"),
		
		HYPERSICKLE_CULLS = c("hypersickle_culls"),
		HYPERSICKLE_PATHUNDER = c("hypersickle_pathunder"),
		SUPERCUT_HARVESTS = c("supercut_harvests"),
		
		TICKACCEL_LIST = c("tickaccel_list"),
		
		WAYBLAST_RESIST = c("wayblast_resist"),
		WAYBLAST_IMMUNE = c("wayblast_immune"),
		
		MUSTANG_VAPORIZES = c("mustang_vaporizes"),
		MUSTANG_INCINERATES = c("mustang_incinerates"),
		MUSTANG_MELTS = c("mustang_melts"),
		MUSTANG_LIQUEFIES = c("mustang_liquefies");
	
	@Override
	protected void addTags(Provider prov) {
		//tag(BlockTags.MINEABLE_WITH_PICKAXE)
		//	.add(ObjectInit.Blocks.WAYSTONE.get());
		//
		//tag(BlockTags.ICE)
		//	.add(ObjectInit.Blocks.AIR_ICE.get());
		
		tag(ARROW_DESTROY)
			.add(Blocks.ICE)
			.add(Blocks.FROSTED_ICE)
			//.add(ObjectInit.Blocks.AIR_ICE.get())
			.add(Blocks.SCAFFOLDING)
			.addTag(net.minecraftforge.common.Tags.Blocks.GLASS)
			.addTag(net.minecraftforge.common.Tags.Blocks.GLASS_PANES);
		
		tag(ARROW_NOCLIP)
			.add(Blocks.ICE)
			.add(Blocks.FROSTED_ICE)
			//.add(ObjectInit.Blocks.AIR_ICE.get())
			.add(Blocks.SCAFFOLDING)
			.addTag(net.minecraftforge.common.Tags.Blocks.GLASS)
			.addTag(net.minecraftforge.common.Tags.Blocks.GLASS_PANES)
			.add(Blocks.AIR)
			.add(Blocks.LAVA)
			.add(Blocks.IRON_BARS)
			.addTag(BlockTags.SIGNS)
			.addTag(BlockTags.LEAVES)
			.addTag(BlockTags.BANNERS)
			.addTag(BlockTags.BUTTONS)
			.addTag(BlockTags.CROPS)
			.addTag(BlockTags.REPLACEABLE) // REPLACABLE_PLANTS
			.addTag(BlockTags.FLOWERS)
			.addTag(BlockTags.CLIMBABLE);
		
		tag(HYPERSICKLE_CULLS)
			.addTag(BlockTags.CAVE_VINES)
			.addTag(BlockTags.FLOWERS)
			.addTag(BlockTags.REPLACEABLE) // REPLACABLE_PLANTS
			.add(Blocks.VINE)
			.add(Blocks.BIG_DRIPLEAF)
			.add(Blocks.BIG_DRIPLEAF_STEM)
			.add(Blocks.SMALL_DRIPLEAF)
			.add(Blocks.CACTUS);
		tag(SUPERCUT_HARVESTS)
			.addTag(BlockTags.LOGS)
			.addTag(BlockTags.LEAVES);
			
		tag(WAYBLAST_RESIST)
			.addTag(BlockTags.WITHER_IMMUNE);
		
		tag(MUSTANG_VAPORIZES)
			.add(Blocks.KELP)
			.add(Blocks.KELP_PLANT)
			.add(Blocks.SEAGRASS)
			.add(Blocks.TALL_SEAGRASS)
			.add(Blocks.WATER)
			.addTag(BlockTags.SNOW);
		
		// TODO fill this out for everything in vanilla
		// maybe find a way to just do all flammable blocks
		tag(MUSTANG_INCINERATES)
			.addTag(BlockTags.LOGS_THAT_BURN)
			.addTag(BlockTags.LEAVES)
			.addTag(net.minecraftforge.common.Tags.Blocks.BARRELS_WOODEN)
			.addTag(net.minecraftforge.common.Tags.Blocks.CHESTS_WOODEN)
			.addTag(net.minecraftforge.common.Tags.Blocks.FENCES_WOODEN)
			.addTag(net.minecraftforge.common.Tags.Blocks.FENCE_GATES_WOODEN)
			.addTag(BlockTags.BAMBOO_BLOCKS)
			.addTag(BlockTags.WOOL)
			.addTag(BlockTags.WOOL_CARPETS)
			.add(Blocks.COAL_BLOCK)
			.add(Blocks.FERN)
			.add(Blocks.LARGE_FERN)
			.add(Blocks.GRASS)
			.add(Blocks.TALL_GRASS)
			.addTag(BlockTags.FLOWERS)
			.add(Blocks.PUMPKIN)
			.add(Blocks.MELON)
			

			.remove(Blocks.WARPED_FENCE)
			.remove(Blocks.WARPED_FENCE_GATE)
			.remove(Blocks.WARPED_HANGING_SIGN)
			.remove(Blocks.WARPED_SIGN)
			.remove(Blocks.CRIMSON_FENCE)
			.remove(Blocks.CRIMSON_FENCE_GATE)
			.remove(Blocks.CRIMSON_HANGING_SIGN)
			.remove(Blocks.CRIMSON_SIGN);
		
		tag(MUSTANG_MELTS)
			.addTag(BlockTags.SNOW)
			.addTag(BlockTags.ICE);
		
		tag(MUSTANG_LIQUEFIES)
			.addTag(net.minecraftforge.common.Tags.Blocks.COBBLESTONE)
			.addTag(net.minecraftforge.common.Tags.Blocks.NETHERRACK)
			.addTag(net.minecraftforge.common.Tags.Blocks.OBSIDIAN)
			.addTag(net.minecraftforge.common.Tags.Blocks.STONE);
		
		//tag(WAYBLAST_IMMUNE)
		//	.addTag(BlockTags.WITHER_IMMUNE);
	}
	
	private static TagKey<Block> c(String name) {
		return TagKey.create(Registries.BLOCK, AASB.rl(name));
	}
	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | Block Tags";
	}
}
