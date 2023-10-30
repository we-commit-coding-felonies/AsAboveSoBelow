package com.quartzshard.aasb.api.capability.aspect.form;

import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.WayStack;
import com.quartzshard.aasb.api.capability.aspect.IAspectHandler;

import net.minecraft.core.Direction;

/**
 * Capability Interface <p>
 * See IAspectHandler for more information.
 * This deals specifically with Form and FormStacks
 */
public interface IHandleForm extends IAspectHandler<AspectForm, FormStack> {
	@Override
	FormStack getChamberContents(int idx);
	
	@Override
	int receiveFrom(FormStack stack, Direction side);
}
