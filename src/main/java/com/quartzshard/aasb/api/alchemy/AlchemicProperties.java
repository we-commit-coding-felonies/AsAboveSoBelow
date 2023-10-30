package com.quartzshard.aasb.api.alchemy;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.AspectWay;
import com.quartzshard.aasb.api.alchemy.aspects.Complexity;

public record AlchemicProperties(@Nullable AspectWay way, @Nullable AspectShape shape, @Nullable AspectForm form, @Nullable Complexity complexity) {

}
