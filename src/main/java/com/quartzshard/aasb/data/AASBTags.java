package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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
			return AsAboveSoBelow.DISPLAYNAME + " | Item Tags";
		}
	}
	
	public class BlockTP extends BlockTagsProvider {
		public BlockTP(DataGenerator generator, ExistingFileHelper helper) {
	        super(generator, AsAboveSoBelow.MODID, helper);
	    }

		public static final TagKey<Block> ARROW_NOCLIP = makeTag("sentient_arrow_noclip");
		public static final TagKey<Block> ARROW_ANNIHILATE = makeTag("sentient_arrow_annihilate");
		
		public static final TagKey<Block> HYPERSICKLE_CAN_CULL = makeTag("hypersickle_can_cull");

		public static final TagKey<Block> WAYBLAST_RESIST = makeTag("wayblast_resist");
		public static final TagKey<Block> WAYBLAST_IMMUNE = makeTag("wayblast_immune");
		
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
	        
	        tag(HYPERSICKLE_CAN_CULL)
	        	.addTag(BlockTags.CAVE_VINES)
	        	.addTag(BlockTags.FLOWERS)
	        	.addTag(BlockTags.REPLACEABLE_PLANTS)
	        	.add(Blocks.VINE)
	        	.add(Blocks.BIG_DRIPLEAF)
	        	.add(Blocks.BIG_DRIPLEAF_STEM)
	        	.add(Blocks.SMALL_DRIPLEAF)
	        	.add(Blocks.CACTUS);
	        
	        tag(WAYBLAST_RESIST)
	        	.addTag(BlockTags.WITHER_IMMUNE);
	        
	        //tag(WAYBLAST_IMMUNE)
	        //	.addTag(BlockTags.WITHER_IMMUNE);
	    }
	    
	    private static TagKey<Block> makeTag(String name) {
	    	return TagKey.create(Registry.BLOCK_REGISTRY, AsAboveSoBelow.rl(name));
	    }
		
		


		@Override
		public String getName() {
			return AsAboveSoBelow.DISPLAYNAME + " | Block Tags";
		}
	}
	
	public class EntityTP extends EntityTypeTagsProvider {
		public EntityTP(DataGenerator generator, ExistingFileHelper helper) {
	        super(generator, AsAboveSoBelow.MODID, helper);
	    }

		public static final TagKey<EntityType<?>> ITEMIZER_ENTITY_BLACKLIST = makeTag("itemizer_entity_blacklist");
		public static final TagKey<EntityType<?>> PHILO_HOMING_ARROW_BLACKLIST = makeTag("philo_homing_arrow_blacklist");

	    @Override
	    protected void addTags() {
	        tag(ITEMIZER_ENTITY_BLACKLIST)
	    		.add(EntityType.ENDER_DRAGON)
	        ;
	        
	        tag(PHILO_HOMING_ARROW_BLACKLIST)
	    		.add(EntityType.ARMOR_STAND)
				.add(EntityType.ENDERMAN);
	    }
	    
	    private static TagKey<EntityType<?>> makeTag(String name) {
	    	return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, AsAboveSoBelow.rl(name));
	    }

	    @Override
	    public String getName() {
			return AsAboveSoBelow.DISPLAYNAME + " | Entity Tags";
	    }
	}
}
