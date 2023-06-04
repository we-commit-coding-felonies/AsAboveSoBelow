package com.quartzshard.as_above_so_below.data;

import com.quartzshard.as_above_so_below.init.ObjectInit;

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
