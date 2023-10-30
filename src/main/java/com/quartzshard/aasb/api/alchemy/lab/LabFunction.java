package com.quartzshard.aasb.api.alchemy.lab;

import com.quartzshard.aasb.api.misc.SemiNullableFunctions.MixedNullableReturnFunction;

@FunctionalInterface
public interface LabFunction extends MixedNullableReturnFunction<LabRecipeData,LabRecipeData> {}
