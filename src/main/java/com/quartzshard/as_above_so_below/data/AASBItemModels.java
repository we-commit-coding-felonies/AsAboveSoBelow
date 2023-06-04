package com.quartzshard.as_above_so_below.data;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;
import com.quartzshard.as_above_so_below.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class AASBItemModels extends ItemModelProvider {

    public AASBItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, AsAboveSoBelow.MODID, helper);   
    }

    @Override
    protected void registerModels() {
        singleTexture(ObjectInit.Items.PHILOSOPHERS_STONE.getId().getPath(), mcLoc("item/generated"),"layer0", modLoc("item/philosophers_stone"));
        singleTexture(ObjectInit.Items.MINIUM_STONE.getId().getPath(), mcLoc("item/generated"),"layer0", modLoc("item/minium_stone"));
    }
    
    /**
     * @deprecated
     * Gives an item a placeholder texture
     * This should not be used outside of a development environment
     * @param ro
     */
    @Deprecated
    private void placeholder(RegistryObject<Item> ro) {
        singleTexture(ro.getId().getPath(), mcLoc("item/generated"),"layer0", modLoc("placeholder"));
    }
}
