package com.quartzshard.aasb.api.alchemy.rune.shape;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;

import net.minecraft.network.chat.MutableComponent;

public abstract class ShapeRune extends ToolRune {
	
	public ShapeRune(ShapeAspect shape) {
		this.shape = shape;
	}
	private final ShapeAspect shape;

	@Override
	public MutableComponent loc() {
		return shape.loc();
	}
	@Override
	public MutableComponent fLoc() {
		return shape.fLoc();
	}
	
	public ShapeAspect getShape() {
		return shape;
	}
	
	@Override
	public int color() {
		return shape.color;
	}
}
