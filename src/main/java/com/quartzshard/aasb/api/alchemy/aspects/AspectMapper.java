package com.quartzshard.aasb.api.alchemy.aspects;

import java.util.Arrays;

import com.quartzshard.aasb.api.alchemy.AlchemicProperties;
import com.quartzshard.aasb.api.alchemy.ItemData;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Each crafting process that the mapping considers should implement one of these.
 */
public abstract class AspectMapper extends ForgeRegistryEntry<AspectMapper>{
	private ResourceLocation name;
	public static final float mapTolerance = (float) 0.1;
	
	public AspectMapper(ResourceLocation name) {
		this.name = name;
	}

	public ResourceLocation getName() {
		return name;
	};
	
	/**
	 * This will assign {@link AlchemicProperties} to outputs that don't already have them given the {@link AspectWay}, {@link AspectShape} and {@link AspectForm} of the inputs.
	 * It will account for, but not overwrite existing properties.
	 *
	 * @param inputs
	 * @param outputs
	 */
	protected abstract AlchemicProperties[] map(AlchemicProperties[] inputs, AlchemicProperties[] outputs); 
	/**
	 * Take the ways of the inputs, subtract the known outputs, evenly distribute the remainder between unassigned outputs.
	 * 
	 * @param inputs
	 * @param outputs
	 * @return
	 */
	protected abstract AspectWay[] mapWays(AlchemicProperties[] inputs, AlchemicProperties[] outputs);
	
	/**
	 * Take the known shapes and compute the least-worst flow solution for the outputs
	 * 
	 * @param inputs
	 * @param outputs
	 * @return
	 */
	protected abstract AspectShape[] mapShapes(AlchemicProperties[] inputs, AlchemicProperties[] outputs);
	
	/**
	 * Assign forms to the outputs. This is the loosest of the mapping processes, but generally, you should move away from the root
	 * or stay put.
	 * 
	 * @param inputs
	 * @param outputs
	 * @return
	 */
	protected abstract AspectForm[] mapForms(AlchemicProperties[] inputs, AlchemicProperties[] outputs);
	
	/**
	 * Assign complexity to the outputs
	 * @param inputs
	 * @param outputs
	 * @return
	 */
	protected abstract Complexity[] mapComplexity(AlchemicProperties[] inputs, AlchemicProperties[] outputs);
	
	/*
	{
		
		AlchemicProperties[] needsAssigning = Arrays.stream(outputs).filter(x -> x == null).toArray(AlchemicProperties[]::new);
		AlchemicProperties[] assigned = Arrays.stream(outputs).filter(x -> x != null).toArray(AlchemicProperties[]::new);
		
		if (assigned.length > 0) {
			LogHelper.info("map()", "Complex mapping", "Mapping a recipe with known outputs, here there be loopholes!");
		}
		
		AspectWay[] ways = mapWays(inputs, outputs, needsAssigning);
		AspectShape [] shapes = mapShapes(inputs, outputs);
		AspectForm [] forms = mapForms(inputs, outputs);
		Complexity [] complexities = mapComplexity(inputs, outputs);
				
		
		for (int i = 0; i < outputs.length; i++ ) {
			if (outputs[i] == null) {
				outputs[i] = new AlchemicProperties(ways[i], shapes[i], forms[i], complexities[i]);
			}
		}
		return outputs;		
	}
	*/
}
