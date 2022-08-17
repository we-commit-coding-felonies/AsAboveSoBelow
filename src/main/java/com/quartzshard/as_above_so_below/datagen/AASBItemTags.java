package com.quartzshard.as_above_so_below.datagen;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AASBItemTags extends ItemTagsProvider {

    public AASBItemTags(DataGenerator gen, BlockTagsProvider blockTags, ExistingFileHelper helper) {
        super(gen, blockTags, AsAboveSoBelow.MODID, helper);
    }

    @Override
    protected void addTags() {

    }

    @Override
    public String getName() {
        return "As Above, So Below tags";
    }
}
