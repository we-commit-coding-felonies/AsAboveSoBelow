package com.quartzshard.aasb.init;

import java.util.function.Supplier;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.AspectForm;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class AlchemyInit {
	
	
	public static void init(IEventBus bus) {
		FormTree.REG.register(bus);
	}
		
		
	public class FormTree { 
		private static final DeferredRegister<AspectForm> REG = DeferredRegister.create(AsAboveSoBelow.rl("form_tree"), AsAboveSoBelow.MODID);
		public static final Supplier<IForgeRegistry<AspectForm>> REGISTRY_SUPPLIER = REG.makeRegistry(AspectForm.class, () -> {
	       		RegistryBuilder<AspectForm> builder = new RegistryBuilder<>();
	       		return builder;
	       	}
		);
	
		public static final RegistryObject<AspectForm> 
			MATERIA = REG.register("materia", () -> new AspectForm(AsAboveSoBelow.rl("materia"), null)),
			MINERAL = REG.register("mineral", () -> new AspectForm(AsAboveSoBelow.rl("mineral"), MATERIA.get())),
			METAL = REG.register("metal", () -> new AspectForm(AsAboveSoBelow.rl("metal"), MINERAL.get()));
	}
}
