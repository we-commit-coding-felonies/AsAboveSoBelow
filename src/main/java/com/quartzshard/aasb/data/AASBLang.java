package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class AASBLang {
	public static final AASBLang INSTANCE = new AASBLang();
	
	public static final String
		CREATIVE_TAB = "itemGroup." + AsAboveSoBelow.MODID,
		TOOLTIP_DM_ARMOR_FLAVOR = "tip.aasb.dm.armor.flavor",
		TOOLTIP_DM_ARMOR_DESC_1 = "tip.aasb.dm.armor.desc.1",
		TOOLTIP_DM_ARMOR_DESC_2 = "tip.aasb.dm.armor.desc.2",
		TOOLTIP_DM_ARMOR_DESC_3 = "tip.aasb.dm.armor.desc.3",
		TOOLTIP_DM_ARMOR_DR = "tip.aasb.dm.armor.dr",
		TOOLTIP_DM_ARMOR_BURNOUT = "tip.aasb.dm.armor.burnout";
	
	public class Provider extends LanguageProvider {
		public Provider(DataGenerator gen, String locale) {
			super(gen, AsAboveSoBelow.MODID, locale);
		}

		@Override
		protected void addTranslations() {
			// Mod stuff
			add(CREATIVE_TAB, "As Above, So Below");
			
			// Items
			add(ObjectInit.Items.PHILOSOPHERS_STONE.get(), "The Philosopher's Stone");
			add(ObjectInit.Items.MINIUM_STONE.get(), "Minium Stone");
			add(ObjectInit.Items.DARK_MATTER_HELMET.get(), "Dark-Matter Helmet");
			add(ObjectInit.Items.DARK_MATTER_CHESTPLATE.get(), "Dark-Matter Chestplate");
			add(ObjectInit.Items.DARK_MATTER_LEGGINGS.get(), "Dark-Matter Leggings");
			add(ObjectInit.Items.DARK_MATTER_BOOTS.get(), "Dark-Matter Boots");
			
			// Blocks
			add(ObjectInit.Blocks.WAYSTONE.get(), "Waystone");
			add(ObjectInit.Blocks.AIR_ICE.get(), "Frozen Air");
			
			// Tooltips
			add(TOOLTIP_DM_ARMOR_FLAVOR, "Become a living fortress with the infalliable Ultrashield");
			add(TOOLTIP_DM_ARMOR_DESC_1, "Provides enormous amounts of protection by absorbing damage");
			add(TOOLTIP_DM_ARMOR_DESC_2, "Protection decreases with more damage absorbed, regenerates over time");
			add(TOOLTIP_DM_ARMOR_DESC_3, "Will violently release all stored energy at once if it absorbs to much");
			add(TOOLTIP_DM_ARMOR_DR, "Currently providing a %s damage reduction");
			add(TOOLTIP_DM_ARMOR_BURNOUT, "Burnout: %s/%s");
			
			// Subtitles
		}
		
		


		@Override
		public String getName() {
			return AsAboveSoBelow.DISPLAYNAME + " | Localization";
		}
	}
}
