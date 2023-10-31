package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.init.ObjectInit.Items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
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
    	// Items
		basic(Items.ASH);
		basic(Items.SOOT);
		basic(Items.SALT);
		basic(Items.SPUT);
		basic(Items.AETHER);
		basic(Items.QUINTESSENCE);
		
		materia(Items.MATERIA_1, 1);
		materia(Items.MATERIA_2, 2);
		materia(Items.MATERIA_3, 3);
		materia(Items.MATERIA_4, 4);
		materia(Items.MATERIA_5, 5);

		basic(Items.MINIUM_STONE);
		basic(Items.ELIXIR_OF_LIFE);
		basic(Items.PHILOSOPHERS_STONE);
		basic(Items.LOOT_BALL);

		flask(Items.FLASK_LEAD);
		flask(Items.FLASK_GOLD);
		flask(Items.FLASK_AETHER);

		placeholder(Items.CHARM);
		placeholder(Items.RING);
		placeholder(Items.GLOVE);

		armor(Items.HERMETIC_HELMET, "item/equipment/armor/herm/");
		armor(Items.HERMETIC_CHESTPLATE, "item/equipment/armor/herm/");
		armor(Items.HERMETIC_LEGGINGS, "item/equipment/armor/herm/");
		armor(Items.HERMETIC_BOOTS, "item/equipment/armor/herm/");
		hermTool(Items.HERMETIC_SWORD, "item/equipment/tool/herm/sword/");
		hermTool(Items.HERMETIC_PICKAXE, "item/equipment/tool/herm/pickaxe/");
		hermTool(Items.HERMETIC_SHOVEL, "item/equipment/tool/herm/shovel/");
		hermTool(Items.HERMETIC_AXE, "item/equipment/tool/herm/axe/");
		hermTool(Items.HERMETIC_HOE, "item/equipment/tool/herm/hoe/");

		armor(Items.CIRCLET, "item/equipment/armor/jewelry/");
		armor(Items.AMULET, "item/equipment/armor/jewelry/");
		armor(Items.POCKETWATCH, "item/equipment/armor/jewelry/");
		armor(Items.ANKLET, "item/equipment/armor/jewelry/");
		
		tool(Items.OMNITOOL, "item/equipment/tool/devtool");
		
		
    	// BlockItems
		block(Items.ASH_STONE_BLOCKITEM, "block/ashen_stone");
		block(Items.WAYSTONE_BLOCKITEM, "block/waystone");
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

	private void basic(RegistryObject<? extends Item> ro) {
		basic(ro, "item/"+ro.getId().getPath());
	}
	private void basic(RegistryObject<? extends Item> ro, String tex) {
		basic(ro, modLoc(tex));
	}
	private void basic(RegistryObject<? extends Item> ro, ResourceLocation tex) {
        singleTexture(ro.getId().getPath(), mcLoc("item/generated"), "layer0", tex);
	}

	private void materia(RegistryObject<? extends Item> ro, int tier) {
		basic(ro, "item/materia/"+tier);
	}
	
	//private void flaskOld(RegistryObject<? extends Item> ro) {
	//	String name = ro.getId().getPath();
	//	withExistingParent(name, mcLoc("item/generated"))
	//		.texture("layer0", modLoc("item/flask/liquid"))
	//		.texture("layer1", modLoc("item/flask/liquid_mix"))
	//		.texture("layer2", modLoc("item/flask/"+name+"_filled"));
	//}
	
	private ItemModelBuilder flask(RegistryObject<? extends Item> ro) {
		String folder = "item/flask/";
		String name = ro.getId().getPath();
		ItemModelBuilder builder = getBuilder(name);
		if (!(ro.get() instanceof FlaskItem item))
			throw new IllegalArgumentException(ro + " is not a flask");
		builder.override()
		.predicate(ClientInit.FLASK_STATUS, 0)
		.model(withExistingParent(folder+name+"/empty", "item/generated")
				.texture("layer0", modLoc(folder+name)))
		.end();

		builder.override()
		.predicate(ClientInit.FLASK_STATUS, 1)
		.model(withExistingParent(folder+name+"/filled", "item/generated")
				.texture("layer0", modLoc("item/flask/liquid"))
				.texture("layer1", modLoc("item/flask/liquid_mix"))
				.texture("layer2", modLoc(folder+name+"_filled")))
		.end();
		return builder;
	}

	private void tool(RegistryObject<? extends Item> ro, String tex) {
		tool(ro, modLoc(tex));
	}
	private void tool(RegistryObject<? extends Item> ro, ResourceLocation tex) {
        singleTexture(ro.getId().getPath(), mcLoc("item/handheld"), "layer0", tex);
	}

	private void armor(RegistryObject<? extends Item> ro, String folder) {
		if (ro.get() instanceof ArmorItem armor) {
			ResourceLocation tex = AsAboveSoBelow.rl(folder+armor.getSlot().getName());
	        basic(ro, tex);
		} else
			throw new IllegalArgumentException(ro + " is not armor");
	}

	private void block(RegistryObject<? extends Item> ro, String tex) {
		block(ro, modLoc(tex));
	}
	private void block(RegistryObject<? extends Item> ro, ResourceLocation tex) {
		withExistingParent(ro.getId().getPath(), tex);
	}
	
	protected ItemModelBuilder hermTool(RegistryObject<? extends Item> reg, String folder) {
		ItemModelBuilder builder = getBuilder(reg.getId().getPath());
		if (!(reg.get() instanceof IHermeticTool item)) throw new IllegalArgumentException(reg + " is not a hermetic tool");
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
	public @NotNull String getName() {
		return AsAboveSoBelow.DISPLAYNAME + " | Item Models";
	}
}
