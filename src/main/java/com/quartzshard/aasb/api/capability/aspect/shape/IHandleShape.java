package com.quartzshard.aasb.api.capability.aspect.shape;

import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.WayStack;
import com.quartzshard.aasb.api.capability.aspect.IAspectHandler;

import net.minecraft.core.Direction;

/**
 * Capability Interface <p>
 * See IAspectHandler for more information.
 * This deals specifically with Shape and ShapeStacks
 */
public interface IHandleShape extends IAspectHandler<AspectShape, ShapeStack> {
	@Override
	ShapeStack getChamberContents(int idx);
	
	@Override
	int receiveFrom(ShapeStack stack, Direction side);
}
