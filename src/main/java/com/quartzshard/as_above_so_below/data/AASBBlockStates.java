package com.quartzshard.as_above_so_below.data;

import com.quartzshard.as_above_so_below.AsAboveSoBelow;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class AASBBlockStates extends BlockStateProvider {
    
    public AASBBlockStates(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, AsAboveSoBelow.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        //Register block states
    }

}
