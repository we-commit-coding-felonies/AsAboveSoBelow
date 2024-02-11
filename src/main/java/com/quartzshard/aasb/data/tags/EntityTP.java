package com.quartzshard.aasb.data.tags;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EntityTP extends EntityTypeTagsProvider {

	public EntityTP(PackOutput packOut, CompletableFuture<Provider> prov, @Nullable ExistingFileHelper help) {
		super(packOut, prov, AASB.MODID, help);
	}
	
	public static final TagKey<EntityType<?>>
		ITEMIZER_LIST = c("itemizer_list"),
		HOMING_LIST = c("homing_list"),
		CLAIRVOYANCE_LIST = c("clairvoyance_list");
	
	@Override
	protected void addTags(Provider prov) {
		tag(ITEMIZER_LIST)
			.add(EntityType.ENDER_DRAGON);
		
		tag(HOMING_LIST)
			.add(EntityType.ARMOR_STAND)
			.add(EntityType.ENDERMAN);
		
		tag(CLAIRVOYANCE_LIST)
			.add(EntityType.ARMOR_STAND);
	}
	
	private static TagKey<EntityType<?>> c(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, AASB.rl(name));
	}
	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | Entity Tags";
	}

}
