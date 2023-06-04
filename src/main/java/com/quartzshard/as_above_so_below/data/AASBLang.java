package com.quartzshard.as_above_so_below.data;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;
import com.quartzshard.as_above_so_below.init.ObjectInit;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class AASBLang extends LanguageProvider {

    public AASBLang(DataGenerator gen, String locale) {
        super(gen, AsAboveSoBelow.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + AsAboveSoBelow.MODID, "As Above, So Below");
        
        add(ObjectInit.Items.PHILOSOPHERS_STONE.get(), "The Philosopher's Stone");
        add(ObjectInit.Items.MINIUM_STONE.get(),"Minium Stone");

    }
}
