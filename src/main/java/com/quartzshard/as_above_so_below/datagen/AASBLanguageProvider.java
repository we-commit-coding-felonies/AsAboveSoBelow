package com.quartzshard.as_above_so_below.datagen;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;
import com.quartzshard.as_above_so_below.setup.ItemInit;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class AASBLanguageProvider extends LanguageProvider {

    public AASBLanguageProvider(DataGenerator gen, String locale) {
        super(gen, AsAboveSoBelow.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + AsAboveSoBelow.DISPLAYNAME, "As Above, So Below");
        add(ItemInit.PHILOSOPHERS_STONE.get(), "Philosopher's Stone");
        add(ItemInit.WHITE_STONE.get(),"White Stone");

    }
}
