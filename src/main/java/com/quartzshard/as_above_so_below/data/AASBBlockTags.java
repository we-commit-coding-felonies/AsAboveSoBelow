package com.quartzshard.as_above_so_below.data;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AASBBlockTags extends BlockTagsProvider {

    public AASBBlockTags(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, AsAboveSoBelow.MODID, helper);
    }

    @Override
    protected void addTags() {
        
    }

    @Override
    public String getName() {
        return "As Above, So Below tags";
    }
}
