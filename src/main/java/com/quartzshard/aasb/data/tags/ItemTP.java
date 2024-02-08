package com.quartzshard.aasb.data.tags;

import java.util.concurrent.CompletableFuture;

import com.quartzshard.aasb.AASB;
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

public class ItemTP extends ItemTagsProvider {
	
	public ItemTP(PackOutput out, CompletableFuture<HolderLookup.Provider> lp, ExistingFileHelper help, BlockTP blockTP) {
		super(out, lp, blockTP.contentsGetter(), AASB.MODID, help);
    }
	
	public static final TagKey<Item>
		WAY_FUEL = c("way_fuel"),
		CURIO_HANDS = TagKey.create(Registries.ITEM, new ResourceLocation("curios:hands")),
		CURIO_BRACELET = TagKey.create(Registries.ITEM, new ResourceLocation("curios:bracelet")),
		CURIO_CHARM = TagKey.create(Registries.ITEM, new ResourceLocation("curios:charm"));

	@Override
	protected void addTags(Provider prov) {
		tag(WAY_FUEL)
			.add(Items.REDSTONE)
			.add(Items.GLOWSTONE_DUST);

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
	}
	
	private static TagKey<Item> c(String name) {
		return TagKey.create(Registries.ITEM, AASB.rl(name));
	}
	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | Item Tags";
	}
}
