package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
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
    	// BlockItems
		block(ObjectInit.Items.WAYSTONE_BLOCKITEM, "block/waystone");

		basic(ObjectInit.Items.MINIUM_STONE, "item/minium_stone");
		basic(ObjectInit.Items.PHILOSOPHERS_STONE, "item/philosophers_stone");

		placeholder(ObjectInit.Items.DARK_MATTER_HELMET);
		placeholder(ObjectInit.Items.DARK_MATTER_CHESTPLATE);
		placeholder(ObjectInit.Items.DARK_MATTER_LEGGINGS);
		placeholder(ObjectInit.Items.DARK_MATTER_BOOTS);
    }
    
    /**
     * @deprecated
     * Gives an item a placeholder texture
     * This should not be used outside of a development environment
     * @param ro
     */
    @Deprecated
    private void placeholder(RegistryObject<? extends Item> ro) {
        singleTexture(ro.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("placeholder"));
    }

	private void basic(RegistryObject<? extends Item> ro, String tex) {
		basic(ro, modLoc(tex));
	}
	private void basic(RegistryObject<? extends Item> ro, ResourceLocation tex) {
        singleTexture(ro.getId().getPath(), mcLoc("item/generated"), "layer0", tex);
	}

	private void tool(RegistryObject<? extends Item> ro, String tex) {
		tool(ro, modLoc(tex));
	}
	private void tool(RegistryObject<? extends Item> ro, ResourceLocation tex) {
        singleTexture(ro.getId().getPath(), mcLoc("item/handheld"), "layer0", tex);
	}

	private void block(RegistryObject<? extends Item> ro, String tex) {
		block(ro, modLoc(tex));
	}
	private void block(RegistryObject<? extends Item> ro, ResourceLocation tex) {
		withExistingParent(ro.getId().getPath(), tex);
	}


	@Override
	public String getName() {
		return AsAboveSoBelow.DISPLAYNAME + " | Item Models";
	}
}
