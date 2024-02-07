package com.quartzshard.aasb.data;

import org.jetbrains.annotations.NotNull;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.api.item.IWayHolder;
import com.quartzshard.aasb.common.item.MiniumStoneItem;
import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.init.object.ItemInit;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelData extends ItemModelProvider {

	public ItemModelData(PackOutput out, ExistingFileHelper helper) {
		super(out, AASB.MODID, helper);
	}

	@Override
	protected void registerModels() {
		// Items
		basic(ItemInit.ASH);
		basic(ItemInit.SOOT);
		basic(ItemInit.SALT);
		basic(ItemInit.SPUT);
		basic(ItemInit.AETHER);
		
		materia(ItemInit.MATERIA_1, 1);
		materia(ItemInit.MATERIA_2, 2);
		materia(ItemInit.MATERIA_3, 3);
		materia(ItemInit.MATERIA_4, 4);
		materia(ItemInit.MATERIA_5, 5);
		basic(ItemInit.QUINTESSENCE);
		
		basic(ItemInit.LEAD_INGOT);
		basic(ItemInit.TIN_INGOT);
		basic(ItemInit.SILVER_INGOT);
		basic(ItemInit.MERCURY_BOTTLE);
		basic(ItemInit.BRONZE_INGOT);
		basic(ItemInit.BRASS_INGOT);

		//flask(ItemInit.FLASK_LEAD);
		//flask(ItemInit.FLASK_GOLD);
		//flask(ItemInit.FLASK_AETHER);

		basic(ItemInit.ELIXIR_OF_LIFE);
		miniumStone(ItemInit.MINIUM_STONE);
		basic(ItemInit.PHILOSOPHERS_STONE);

		tool(ItemInit.OMNITOOL, "item/equipment/tool/devtool");
		hermTool(ItemInit.SWORD, "item/equipment/tool/sword/");
		hermTool(ItemInit.PICK, "item/equipment/tool/pickaxe/");
		hermTool(ItemInit.SHOVEL, "item/equipment/tool/shovel/");
		hermTool(ItemInit.AXE, "item/equipment/tool/axe/");
		hermTool(ItemInit.HOE, "item/equipment/tool/hoe/");

		//tool(ItemInit.GLOVE);
		//tool(ItemInit.BRACELET);
		//tool(ItemInit.CHARM);

		//armor(ItemInit.HELMET, "item/equipment/armor/herm/");
		//armor(ItemInit.CHESTPLATE, "item/equipment/armor/herm/");
		//armor(ItemInit.LEGGINGS, "item/equipment/armor/herm/");
		//armor(ItemInit.BOOTS, "item/equipment/armor/herm/");
		armor(ItemInit.CIRCLET, "item/equipment/armor/jewellery/");
		armor(ItemInit.AMULET, "item/equipment/armor/jewellery/");
		armor(ItemInit.POCKETWATCH, "item/equipment/armor/jewellery/");
		armor(ItemInit.ANKLET, "item/equipment/armor/jewellery/");

		placeholder(ItemInit.C_GLOVE);
		placeholder(ItemInit.C_BRACELET);
		placeholder(ItemInit.C_CHARM);
		placeholder(ItemInit.C_CIRCLET);
		placeholder(ItemInit.C_AMULET);
		placeholder(ItemInit.C_POCKETWATCH);
		placeholder(ItemInit.C_ANKLET);
		placeholder(ItemInit.C_AMALGAM);
		
		basic(ItemInit.LOOTBALL);
		simpleWayHolder(ItemInit.WAYSTONE);
		simpleLayered(ItemInit.WAY_GRENADE, "holder", "cracks");
		basic(ItemInit.CHALK);
		basic(ItemInit.AETHERCHALK);
		
		// BlockItems
		//block(ItemInit.ASH_STONE_BLOCKITEM, "block/ashen_stone");
		//block(ItemInit.WAYSTONE_BLOCKITEM, "block/waystone");
	}
	
	/**
	 * @deprecated This should not be used beyond early testing, give things an actual texture!!!
	 * Gives an item a placeholder texture
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
	
	private ItemModelBuilder miniumStone(RegistryObject<? extends Item> ro) {
		ItemModelBuilder builder = getBuilder(ro.getId().getPath());
		if (!(ro.get() instanceof MiniumStoneItem item)) throw new IllegalArgumentException(ro + " is not a minium stone");
		for (int i = 0; i < 8; i++) {
			String name = "item/minium_stone/"+i;
			builder.override()
				.predicate(ClientInit.PRED_MINIUM, i)
				.model(withExistingParent(name, "item/generated")
					.texture("layer0", modLoc(name)))
			.end();
		}
		return builder;
	}
	
	private ItemModelBuilder simpleWayHolder(RegistryObject<? extends Item> ro) {
		String name = ro.getId().getPath();
		ItemModelBuilder builder = getBuilder(name);
		if (!(ro.get() instanceof IWayHolder item)) throw new IllegalArgumentException(ro + " is not a minium stone");
		String path = "item/"+name+"/";
		builder.override()
			.predicate(ClientInit.PRED_WAY_HOLDER, 0)
			.model(withExistingParent(path+"empty", "item/generated")
				.texture("layer0", modLoc(path+"holder")))
		.end();
		builder.override()
			.predicate(ClientInit.PRED_WAY_HOLDER, 1)
			.model(withExistingParent(path+"filled", "item/generated")
				.texture("layer0", modLoc(path+"holder"))
				.texture("layer1", modLoc(path+"way")))
		.end();
		return builder;
	}
	
	private ItemModelBuilder simpleLayered(RegistryObject<? extends Item> ro, String... layers) {
		String name = ro.getId().getPath();
		ItemModelBuilder builder = getBuilder(name);
		String path = "item/"+name;
		ItemModelBuilder b = withExistingParent(path, "item/generated");
		for (int i = 0; i < layers.length; i++) {
			b.texture("layer"+i, modLoc(path+"/"+layers[i]));
		}
		builder.override().model(b).end();
		return builder;
	}
	
	
	

	/*private ItemModelBuilder flask(RegistryObject<? extends Item> ro) {
		basic(ro);
		return null;
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
	}*/

	private void tool(RegistryObject<? extends Item> ro) {
		tool(ro, "item/"+ro.getId().getPath());
	}
	private void tool(RegistryObject<? extends Item> ro, String tex) {
		tool(ro, modLoc(tex));
	}
	private void tool(RegistryObject<? extends Item> ro, ResourceLocation tex) {
		singleTexture(ro.getId().getPath(), mcLoc("item/handheld"), "layer0", tex);
	}

	private void armor(RegistryObject<? extends Item> ro, String folder) {
		if (ro.get() instanceof ArmorItem armor) {
			ResourceLocation tex = AASB.rl(folder+armor.getEquipmentSlot().getName());
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
			if (!IHermeticTool.validateRunesVal(i)) continue;
			String name = folder+"off/"+i;
			builder.override()
			.predicate(ClientInit.PRED_RUNES, i)
			.predicate(ClientInit.PRED_WAY_HOLDER, 0)
			.model(withExistingParent(name, "item/handheld")
					.texture("layer0", modLoc(name)))
			.end();
			
			name = folder+"on/"+i;
			builder.override()
			.predicate(ClientInit.PRED_RUNES, i)
			.predicate(ClientInit.PRED_WAY_HOLDER, 1)
			.model(withExistingParent(name, "item/handheld")
					.texture("layer0", modLoc(name)))
			.end();
		}
		return builder;
	}


	@Override
	public @NotNull String getName() {
		return AASB.MODID.toUpperCase() + " | Item Models";
	}
}
