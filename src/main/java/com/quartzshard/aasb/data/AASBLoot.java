package com.quartzshard.aasb.data;

import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.data.DataGenerator;

public class AASBLoot extends AASBLootTableProvider {

    public AASBLoot(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void addTables() {
    	blockLootTables.put(ObjectInit.Blocks.WAYSTONE.get(), createSimpleBlockTable("waystone_block", ObjectInit.Blocks.WAYSTONE.get()));
    }

}
