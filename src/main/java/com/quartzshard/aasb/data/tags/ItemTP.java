package com.quartzshard.aasb.data.tags;

import java.util.concurrent.CompletableFuture;

import com.quartzshard.aasb.AASB;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTP extends ItemTagsProvider {
	
	public ItemTP(PackOutput out, CompletableFuture<HolderLookup.Provider> lp, ExistingFileHelper help, BlockTP blockTP) {
		super(out, lp, blockTP.contentsGetter(), AASB.MODID, help);
    }
	
	public static final TagKey<Item>
		WAY_FUEL = c("way_fuel");

	@Override
	protected void addTags(Provider prov) {
		tag(WAY_FUEL)
			.add(Items.REDSTONE)
			.add(Items.GLOWSTONE_DUST);
	}
	
	private static TagKey<Item> c(String name) {
		return TagKey.create(Registries.ITEM, AASB.rl(name));
	}
	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | Item Tags";
	}
}
