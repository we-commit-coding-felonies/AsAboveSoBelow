package com.quartzshard.as_above_so_below.datagen;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;
import com.quartzshard.as_above_so_below.setup.ItemInit;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AASBItemModels extends ItemModelProvider {

    public AASBItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, AsAboveSoBelow.MODID, helper);   
    }

    @Override
    protected void registerModels() {
        singleTexture(ItemInit.PHILOSOPHERS_STONE.getId().getPath(), mcLoc("item/generated"),"layer0", modLoc("item/philosophers_stone"));
    }
}
