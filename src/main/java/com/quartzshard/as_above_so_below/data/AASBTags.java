package com.quartzshard.as_above_so_below.data;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;
import com.quartzshard.as_above_so_below.init.ObjectInit;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AASBTags {
	static final AASBTags INSTANCE = new AASBTags();
	
	public class ItemTP extends ItemTagsProvider {

		public ItemTP(DataGenerator gen, BlockTagsProvider blockTags, ExistingFileHelper helper) {
			super(gen, blockTags, AsAboveSoBelow.MODID, helper);
		}

		@Override
		protected void addTags() {

		}

		@Override
		public String getName() {
			return AsAboveSoBelow.DISPLAYNAME + " item tags";
		}
	}
	
	public class BlockTP extends BlockTagsProvider {
		public BlockTP(DataGenerator generator, ExistingFileHelper helper) {
	        super(generator, AsAboveSoBelow.MODID, helper);
	    }

		public static final TagKey<Block> ARROW_NOCLIP = makeTag("sentient_arrow_pathfind_noclip");
		public static final TagKey<Block> ARROW_ANNIHILATE = makeTag("sentient_arrow_pathfind_annihilate");

		public static final TagKey<Block> NUKE_RESIST = makeTag("nuke_resist");
		public static final TagKey<Block> NUKE_IMMUNE = makeTag("nuke_immune");
		
		@Override
	    protected void addTags() {
	        tag(BlockTags.MINEABLE_WITH_PICKAXE)
	        	.add(ObjectInit.Blocks.WAYSTONE.get());
	        
	        tag(BlockTags.ICE)
	        	.add(ObjectInit.Blocks.AIR_ICE.get());
	        
	        tag(ARROW_ANNIHILATE)
	    		.add(Blocks.ICE)
				.add(Blocks.FROSTED_ICE)
				.add(ObjectInit.Blocks.AIR_ICE.get())
	    		.add(Blocks.SCAFFOLDING)
	        	.addTag(net.minecraftforge.common.Tags.Blocks.GLASS)
	        	.addTag(net.minecraftforge.common.Tags.Blocks.GLASS_PANES);
	        
	        tag(ARROW_NOCLIP)
				.add(Blocks.ICE)
				.add(Blocks.FROSTED_ICE)
				.add(ObjectInit.Blocks.AIR_ICE.get())
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
	        	.addTag(BlockTags.REPLACEABLE_PLANTS)
	        	.addTag(BlockTags.FLOWERS)
	        	.addTag(BlockTags.CLIMBABLE);
	        
	        tag(NUKE_RESIST)
	        	.add(Blocks.NETHERITE_BLOCK);
	        
	        tag(NUKE_IMMUNE)
	        	.addTag(BlockTags.WITHER_IMMUNE);
	    }
	    
	    private static TagKey<Block> makeTag(String name) {
	    	return TagKey.create(Registry.BLOCK_REGISTRY, AsAboveSoBelow.rl(name));
	    }

		@Override
		public String getName() {
			return AsAboveSoBelow.DISPLAYNAME + " block tags";
		}
	}
}
