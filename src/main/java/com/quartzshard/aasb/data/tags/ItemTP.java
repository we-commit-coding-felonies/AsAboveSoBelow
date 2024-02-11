package com.quartzshard.aasb.data.tags;

import java.util.concurrent.CompletableFuture;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.tag.LazyTagLookup;
import com.quartzshard.aasb.init.object.ItemInit;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemTP extends ItemTagsProvider {
	
	public ItemTP(PackOutput out, CompletableFuture<HolderLookup.Provider> lp, ExistingFileHelper help, BlockTP blockTP) {
		super(out, lp, blockTP.contentsGetter(), AASB.MODID, help);
    }
	
	public static final TagKey<Item>
		WAY_FUEL = c("way_fuel"),
		
		CURIO_HANDS = TagKey.create(Registries.ITEM, new ResourceLocation("curios:hands")),
		CURIO_BRACELET = TagKey.create(Registries.ITEM, new ResourceLocation("curios:bracelet")),
		CURIO_CHARM = TagKey.create(Registries.ITEM, new ResourceLocation("curios:charm")),
		
		// PMD reference
		ITEMIZER_APPLES = c("itemizer_apples"),
		ITEMIZER_GUMMYS = c("itemizer_gummys"),
		ITEMIZER_ORBS = c("itemizer_orbs"),
		ITEMIZER_SEEDS = c("itemizer_seeds"),
		ITEMIZER_BERRIES = c("itemizer_berries"),
		ITEMIZER_DRINKS = c("itemizer_drinks"),
		ITEMIZER_STICKS = c("itemizer_sticks"),
		ITEMIZER_DISCS = c("itemizer_discs"),
		// Non-PMD reference
		ITEMIZER_JUNKS = c("itemizer_junks");
	
	public static final LazyTagLookup<Item>
		L_ITEMIZER_APPLES = lookup(ITEMIZER_APPLES),
		L_ITEMIZER_GUMMYS = lookup(ITEMIZER_GUMMYS),
		L_ITEMIZER_ORBS = lookup(ITEMIZER_ORBS),
		L_ITEMIZER_SEEDS = lookup(ITEMIZER_SEEDS),
		L_ITEMIZER_BERRIES = lookup(ITEMIZER_BERRIES),
		L_ITEMIZER_DRINKS = lookup(ITEMIZER_DRINKS),
		L_ITEMIZER_STICKS = lookup(ITEMIZER_STICKS),
		L_ITEMIZER_DISCS = lookup(ITEMIZER_DISCS),
		L_ITEMIZER_JUNKS = lookup(ITEMIZER_JUNKS)
		;

	@Override
	protected void addTags(Provider prov) {
		tag(WAY_FUEL)
			.add(ItemInit.MATERIA_NEG2.get())
			.add(ItemInit.MATERIA_NEG1.get())
			.add(ItemInit.MATERIA_1.get())
			.add(ItemInit.MATERIA_2.get())
			.add(ItemInit.MATERIA_3.get())
			.add(ItemInit.MATERIA_4.get())
			.add(ItemInit.MATERIA_5.get());

		tag(CURIO_HANDS)
			.add(ItemInit.GLOVE1.get())
			.add(ItemInit.GLOVE2.get())
			.add(ItemInit.COS_GAUNTLET1.get())
			.add(ItemInit.COS_GAUNTLET2.get());
		tag(CURIO_BRACELET)
			.add(ItemInit.BRACELET1.get())
			.add(ItemInit.BRACELET2.get())
			.add(ItemInit.COS_BAND1.get())
			.add(ItemInit.COS_BAND2.get());
		tag(CURIO_CHARM)
			.add(ItemInit.CHARM1.get())
			.add(ItemInit.CHARM2.get())
			.add(ItemInit.COS_TRINKET1.get())
			.add(ItemInit.COS_TRINKET2.get());
		
		tag(ITEMIZER_APPLES)
			.add(Items.APPLE)
			.add(Items.GOLDEN_APPLE)
			.add(Items.ENCHANTED_GOLDEN_APPLE);
		tag(ITEMIZER_GUMMYS)
			.add(Items.BLACK_DYE)
			.add(Items.BLUE_DYE)
			.add(Items.BROWN_DYE)
			.add(Items.CYAN_DYE)
			.add(Items.GRAY_DYE)
			.add(Items.GREEN_DYE)
			.add(Items.LIGHT_BLUE_DYE)
			.add(Items.LIGHT_GRAY_DYE)
			.add(Items.LIME_DYE)
			.add(Items.MAGENTA_DYE)
			.add(Items.ORANGE_DYE)
			.add(Items.PINK_DYE)
			.add(Items.PURPLE_DYE)
			.add(Items.RED_DYE)
			.add(Items.WHITE_DYE)
			.add(Items.YELLOW_DYE);
		tag(ITEMIZER_ORBS)
			.add(Items.ENDER_PEARL)
			.add(Items.ENDER_EYE)
			.add(Items.FIRE_CHARGE)
			.add(Items.SLIME_BALL)
			.add(Items.MAGMA_CREAM)
			.add(Items.HEART_OF_THE_SEA)
			.add(Items.FIREWORK_STAR)
			.add(Items.SNOWBALL);
		tag(ITEMIZER_SEEDS)
			.addTag(net.minecraftforge.common.Tags.Items.SEEDS)
			.add(Items.TORCHFLOWER_SEEDS);
		tag(ITEMIZER_BERRIES)
			.add(Items.SWEET_BERRIES)
			.add(Items.GLOW_BERRIES);
		tag(ITEMIZER_DRINKS)
			.add(Items.EXPERIENCE_BOTTLE)
			.add(Items.HONEY_BOTTLE)
			.add(Items.POTION)
			.add(Items.MILK_BUCKET)
			.add(Items.LAVA_BUCKET)
			.add(Items.WATER_BUCKET)
			.add(ItemInit.ELIXIR_OF_LIFE.get());
		tag(ITEMIZER_STICKS)
			.add(Items.ARROW)
			.add(Items.BAMBOO)
			.add(Items.BLAZE_ROD)
			.add(Items.BONE)
			.add(Items.TRIDENT)
			.add(Items.END_ROD)
			.add(Items.SPECTRAL_ARROW)
			.add(Items.TIPPED_ARROW)
			.add(Items.STICK);
		tag(ITEMIZER_DISCS)
			.add(Items.MUSIC_DISC_11)
			.add(Items.MUSIC_DISC_13)
			.add(Items.MUSIC_DISC_5)
			.add(Items.MUSIC_DISC_BLOCKS)
			.add(Items.MUSIC_DISC_CAT)
			.add(Items.MUSIC_DISC_CHIRP)
			.add(Items.MUSIC_DISC_FAR)
			.add(Items.MUSIC_DISC_MALL)
			.add(Items.MUSIC_DISC_MELLOHI)
			.add(Items.MUSIC_DISC_OTHERSIDE)
			.add(Items.MUSIC_DISC_PIGSTEP)
			.add(Items.MUSIC_DISC_RELIC)
			.add(Items.MUSIC_DISC_STAL)
			.add(Items.MUSIC_DISC_STRAD)
			.add(Items.MUSIC_DISC_WAIT)
			.add(Items.MUSIC_DISC_WARD);
		tag(ITEMIZER_JUNKS)
			.add(ItemInit.ASH.get())
			.add(ItemInit.SOOT.get())
			.add(ItemInit.SALT.get())
			.add(ItemInit.SPUT.get())
			.add(ItemInit.AETHER.get());
	}
	
	private static TagKey<Item> c(String name) {
		return TagKey.create(Registries.ITEM, AASB.rl(name));
	}
	private static LazyTagLookup<Item> lookup(TagKey<Item> tag) {
    	return LazyTagLookup.create(ForgeRegistries.ITEMS, tag);
	}
	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | Item Tags";
	}
}
