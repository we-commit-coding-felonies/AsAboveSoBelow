package com.quartzshard.aasb.api.alchemy;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspect.*;
import com.quartzshard.aasb.init.AlchInit;

import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public record AlchData(
		@Nullable WayAspect way,
		@Nullable ShapeAspect shape,
		@Nullable FormAspect form,
		ComplexityAspect complexity) {
	public static final String
		TK_SERWAY = "Way",
		TK_SERSHAPE = "Shape",
		TK_SERFORM = "Form",
		TK_SERCPLX = "Complexity";

	public AlchData(long way, ShapeAspect shape, FormAspect form, ComplexityAspect complexity) {
		this(new WayAspect(way), shape, form, complexity);
	}
	
	public AlchData(WayAspect way, ShapeAspect shape, ResourceLocation form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(form), complexity);
	}
	public AlchData(WayAspect way, ShapeAspect shape, @NotNull String form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(ResourceLocation.tryParse(form)), complexity);
	}
	
	public AlchData(long way, ShapeAspect shape, ResourceLocation form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(form), complexity);
	}
	public AlchData(long way, ShapeAspect shape, String form, ComplexityAspect complexity) {
		this(way, shape, AlchInit.getForm(ResourceLocation.tryParse(form)), complexity);
	}

	public AlchData(CompoundTag tag) {
		this(tag.getLong(TK_SERWAY),
			ShapeAspect.values()[tag.getByte(TK_SERSHAPE)],
			AlchInit.getForm(ResourceLocation.tryParse(tag.getString(TK_SERFORM))),
			ComplexityAspect.values()[tag.getByte(TK_SERCPLX)]);
	}
	
	/**
	 * Generates AlchData from seeds
	 * <p>
	 * If given only 1 or 2, will just use the first seed (seeds[0]) for all 3 aspects <br>
	 * If given 3 or more, the first 3 (seeds[0], seeds[1], and seeds[2])
	 * will be used for Way, Shape, and Form respectively <br>
	 */
	public static AlchData fromSeeds(long... seeds) {
		if (seeds.length > 0) {
			if (seeds.length < 3) {
				return new AlchData(
							WayAspect.fromSeed(seeds[0]),
							ShapeAspect.fromSeed(seeds[0]),
							FormAspect.fromSeed(seeds[0]),
							ComplexityAspect.SEEDGEN
						);
			}
			return new AlchData(
					WayAspect.fromSeed(seeds[0]),
					ShapeAspect.fromSeed(seeds[1]),
					FormAspect.fromSeed(seeds[2]),
					ComplexityAspect.SEEDGEN
				);
		}
		throw new IllegalArgumentException("No seed value was supplied");
	}
	
	/**
	 * Gets whether the flow to another AlchData is perfect (0 violation)
	 * @param other
	 * @return True if flow is perfect
	 */
	@SuppressWarnings("null") // Assuming the AlchData is correct, the complexity check should also filter out nulls
	public boolean flowsTo(AlchData other) {
		return complexity.flowsTo(other.complexity())
				&& way.flowsTo(other.way())
				&& shape.flowsTo(other.shape())
				&& form.flowsTo(other.form());
	}
	
	/**
	 * Gets the flow violation value of a given transmutation
	 * @param other
	 * @return total % flow violation
	 */
	@SuppressWarnings("null") // Assuming the AlchData is correct, the complexity check should also filter out nulls 
	public float violationTo(AlchData other) {
		if (complexity.violationTo(other.complexity()) < 1) {
			return way.violationTo(other.way()) + shape.violationTo(other.shape()) + form.violationTo(other.form());
		}
		return 1;
	}

	@Override
	public String toString() {
		String str = "(";
		str += way == null ? "null" : way.value();
		str += ",";
		str += shape == null ? "null" : shape.name().toLowerCase();
		str += ",";
		str += form == null ? "null" : form.getName().toString();
		str += ",";
		str += complexity.name().toLowerCase();
		return str + ")";
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putLong(TK_SERWAY, way == null ? -1 : way.value());
		tag.putByte(TK_SERSHAPE, shape == null ? (byte)-1 : (byte)shape.ordinal());
		tag.putString(TK_SERFORM, form == null ? "null" : form.getName().toString());
		tag.putByte(TK_SERCPLX, (byte)complexity.ordinal());
		return tag;
	}
}
