package com.quartzshard.aasb.init.object;

import java.util.function.Supplier;

import com.quartzshard.aasb.AASB;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Handles init on blocks & related (such as TEs)
public class BlockInit {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AASB.MODID);
	private static final DeferredRegister<BlockEntityType<?>> TES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AASB.MODID);
	
	public static void init(IEventBus bus) {
		BLOCKS.register(bus);
		TES.register(bus);
	}
	
	// Shared properties
	private static final BlockBehaviour.Properties
		PROPS_GENERIC = BlockBehaviour.Properties.of();
	
	// Generic blocks
	public static final RegistryObject<Block>
		TEST_BLOCK = reg("test_block");

	
	private static final <B extends Block> RegistryObject<B> reg(String name, Supplier<B> sup) {
		return BLOCKS.register(name, sup);
	}
	private static final RegistryObject<Block> reg(String name, BlockBehaviour.Properties props) {
		return reg(name, () -> new Block(props));
	}
	private static final RegistryObject<Block> reg(String name) {
		return reg(name, PROPS_GENERIC);
	}
}
