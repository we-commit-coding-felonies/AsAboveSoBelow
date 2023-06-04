package com.quartzshard.aasb.data;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class AASBLang extends LanguageProvider {

    public AASBLang(DataGenerator gen, String locale) {
        super(gen, AsAboveSoBelow.MODID, locale);
    }

    @Override
    protected void addTranslations() {
    	// Mod stuff
        add("itemGroup." + AsAboveSoBelow.MODID, "As Above, So Below");
        
        // Items
        add(ObjectInit.Items.PHILOSOPHERS_STONE.get(), "The Philosopher's Stone");
        add(ObjectInit.Items.MINIUM_STONE.get(), "Minium Stone");
        
        // Blocks
        add(ObjectInit.Blocks.WAYSTONE.get(), "Waystone");
        add(ObjectInit.Blocks.AIR_ICE.get(), "Frozen Air");
    }
}
