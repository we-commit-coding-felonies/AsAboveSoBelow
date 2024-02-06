package com.quartzshard.aasb.api.alchemy;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspect.*;
import com.quartzshard.aasb.init.AlchInit;

import net.minecraft.resources.ResourceLocation;

public record AlchData(
		@Nullable WayAspect way,
		@Nullable ShapeAspect shape,
		@Nullable FormAspect form,
		ComplexityAspect complexity) {

	public AlchData(long way, ShapeAspect shape, FormAspect form, ComplexityAspect complexity) {
		this(new WayAspect(way), shape, form, complexity);
	}
	
	public AlchData(WayAspect way, ShapeAspect shape, ResourceLocation form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(form), complexity);
	}
	public AlchData(WayAspect way, ShapeAspect shape, String form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(ResourceLocation.tryParse(form)), complexity);
	}
	
	public AlchData(long way, ShapeAspect shape, ResourceLocation form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(form), complexity);
	}
	public AlchData(long way, ShapeAspect shape, String form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(ResourceLocation.tryParse(form)), complexity);
	}
}
