package com.quartzshard.aasb.api.alchemy.aspects;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;

import net.minecraft.resources.ResourceLocation;

/**
 * horrific <br>
 * dummy aspect, only used by the empty aspectstack for internal generics shenanigans
 */
public class AspectEmpty implements IAlchemicalFlow<AspectEmpty> {

	@Override
	public boolean flows(AspectEmpty to) {
		return false;
	}

	@Override
	public boolean perpendicular(AspectEmpty to) {
		return false;
	}

	@Override
	public boolean violates(AspectEmpty to) {
		return true;
	}

	@Override
	public String toString() {
		return "EMPTY_ASPECT";
	}
}
