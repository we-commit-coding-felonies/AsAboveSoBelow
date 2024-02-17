package com.quartzshard.aasb.init.object;

import java.util.function.Supplier;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.block.CrumblingStoneBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
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
		PROPS_GENERIC = BlockBehaviour.Properties.of(),
		PROPS_GENERIC_SOIL = BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRAVEL),
		PROPS_GENERIC_STONE = BlockBehaviour.Properties.of().strength(1.5F, 6.0F).requiresCorrectToolForDrops();
	
	// Generic blocks
	public static final RegistryObject<Block>
		// Basic / Decor
		ASHEN_STONE = reg("ashen_stone", PROPS_GENERIC_STONE),
		
		
		// Misc
		CRUMBLING_STONE = reg("crumbling_stone", () -> new CrumblingStoneBlock(BlockBehaviour.Properties.of().randomTicks().instabreak().noLootTable().sound(SoundType.NETHERRACK)));

	
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
