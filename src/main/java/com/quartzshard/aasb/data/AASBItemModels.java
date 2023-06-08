package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IDarkMatterTool;
import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.minecraftforge.client.model.generators.ItemModelBuilder;
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
		dmTool(ObjectInit.Items.DARK_MATTER_SWORD, "item/equipment/tool/dm/sword/");
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
	
	protected ItemModelBuilder dmTool(RegistryObject<? extends Item> reg, String folder) {
		ItemModelBuilder builder = getBuilder(reg.getId().getPath());
		if (!(reg.get() instanceof IDarkMatterTool item)) throw new IllegalArgumentException(reg + " is not a dark matter tool");
		for (int i = 0; i < 13; i++) {
			if (!item.validateRunes(i)) continue;
			String name = folder+"off/"+i;
			builder.override()
			.predicate(ClientInit.SHAPE_RUNE, i)
			.predicate(ClientInit.EMPOWER_CHARGE, 0)
			.model(withExistingParent(name, "item/handheld")
					.texture("layer0", modLoc(name)))
			.end();
			
			name = folder+"on/"+i;
			builder.override()
			.predicate(ClientInit.SHAPE_RUNE, i)
			.predicate(ClientInit.EMPOWER_CHARGE, 1)
			.model(withExistingParent(name, "item/handheld")
					.texture("layer0", modLoc(name)))
			.end();
		}
		return builder;
	}


	@Override
	public String getName() {
		return AsAboveSoBelow.DISPLAYNAME + " | Item Models";
	}
}
