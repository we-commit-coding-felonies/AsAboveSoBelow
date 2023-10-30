package com.quartzshard.aasb.api.capability.aspect.way;

import com.quartzshard.aasb.api.alchemy.aspects.AspectWay;
import com.quartzshard.aasb.api.alchemy.aspects.stack.WayStack;
import com.quartzshard.aasb.api.capability.aspect.IAspectHandler;

import net.minecraft.core.Direction;

/**
 * Capability Interface <p>
 * See IAspectHandler for more information.
 * This deals specifically with Way and WayStacks
 */
public interface IHandleWay extends IAspectHandler<AspectWay, WayStack> {
	@Override
	WayStack getChamberContents(int idx);
	
	@Override
	int receiveFrom(WayStack stack, Direction side);
}
