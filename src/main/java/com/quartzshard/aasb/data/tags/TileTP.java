package com.quartzshard.aasb.data.tags;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.tag.LazyTagLookup;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class TileTP extends TagsProvider<BlockEntityType<?>> {
	public TileTP(PackOutput out, CompletableFuture<HolderLookup.Provider> prov, @Nullable ExistingFileHelper help) {
		super(out, Registries.BLOCK_ENTITY_TYPE, prov, AASB.MODID, help);
	}
	
	public static final TagKey<BlockEntityType<?>> NO_TICKACCEL = makeTag("no_tickaccel");
	public static final LazyTagLookup<BlockEntityType<?>> L_NO_TICKACCEL = LazyTagLookup.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NO_TICKACCEL);

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {}
    
    private static TagKey<BlockEntityType<?>> makeTag(String name) {
    	return TagKey.create(Registries.BLOCK_ENTITY_TYPE, AASB.rl(name));
    }

	@Override
	public @NotNull String getName() {
		return AASB.MODID.toLowerCase() + " | BlockEntity Tags";
	}
	
}
