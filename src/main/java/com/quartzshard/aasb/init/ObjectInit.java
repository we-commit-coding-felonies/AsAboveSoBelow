package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.lab.LabProcess;
import com.quartzshard.aasb.common.block.*;
import com.quartzshard.aasb.common.block.lab.DebugLabMultiblock;
import com.quartzshard.aasb.common.block.lab.LabBlock;
import com.quartzshard.aasb.common.block.lab.LabMultiblock;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.common.block.lab.te.debug.LabDebugEndTE;
import com.quartzshard.aasb.common.block.lab.te.debug.LabDebugStartTE;
import com.quartzshard.aasb.common.block.lab.te.debug.capability.LabDebugCapabilityRecieveTE;
import com.quartzshard.aasb.common.block.lab.te.debug.capability.LabDebugCapabilitySendTE;
import com.quartzshard.aasb.common.block.lab.te.starters.*;
import com.quartzshard.aasb.common.block.lab.te.modifiers.*;
import com.quartzshard.aasb.common.block.lab.te.finishers.*;
import com.quartzshard.aasb.common.effect.*;
import com.quartzshard.aasb.common.entity.living.*;
import com.quartzshard.aasb.common.entity.projectile.*;
import com.quartzshard.aasb.common.item.*;
import com.quartzshard.aasb.common.item.equipment.armor.*;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.*;
import com.quartzshard.aasb.common.item.equipment.tool.*;
import com.quartzshard.aasb.common.item.equipment.tool.herm.*;
import com.quartzshard.aasb.common.item.equipment.trinket.*;
import com.quartzshard.aasb.common.item.flask.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * anything tangible, such as blocks items or entities
 * @author solunareclipse1
 */
public class ObjectInit {


	public static void init(IEventBus bus) {
		Blocks.REG.register(bus);
		Items.initializeLabBlockItemMap();
		Items.REG.register(bus);
		Entities.REG.register(bus);
		TileEntities.REG.register(bus);
		MobEffects.REG.register(bus);
	}
	
	public class Items {
		private static final DeferredRegister<Item> REG = DeferredRegister.create(ForgeRegistries.ITEMS, AsAboveSoBelow.MODID);
		//Common item properties
		public static final Item.Properties PROPS_GENERIC = new Item.Properties().tab(ModInit.ITEM_GROUP);
		public static final Item.Properties PROPS_UNSTACKABLE = new Item.Properties().tab(ModInit.ITEM_GROUP).stacksTo(1);
		public static final Item.Properties PROPS_GENERIC_T1 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER1.get());
		public static final Item.Properties PROPS_GENERIC_T2 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER2.get());
		public static final Item.Properties PROPS_GENERIC_T3 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER3.get());
		public static final Item.Properties PROPS_GENERIC_T4 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER4.get());
		public static final Item.Properties PROPS_GENERIC_T5 = new Item.Properties().tab(ModInit.ITEM_GROUP).rarity(AASBRarity.TIER5.get());
		public static final Item.Properties PROPS_MINIUM = makeProps(AASBRarity.SPECIAL.get(), 1, false);
		public static final Item.Properties PROPS_HERM_GEAR = makeProps(AASBRarity.TIER4.get(), 1, true);
		public static final Item.Properties PROPS_JEWELRY = makeProps(AASBRarity.TIER5.get(), 1, true);

		// Items

		public static final RegistryObject<Item>
				// Stackable items / materials
				ASH = basic("ash"),
				SOOT = basic("soot"),
				SALT = basic("salt"),
				SPUT = basic("sput"),
				AETHER = REG.register("aether", () -> new Item(makeProps(AASBRarity.SPECIAL.get(), 64, false))),
				MATERIA_1 = REG.register("materia_infirma", () -> new Item(PROPS_GENERIC_T1)),
				MATERIA_2 = REG.register("materia_minor", () -> new Item(PROPS_GENERIC_T2)),
				MATERIA_3 = REG.register("materia_modica", () -> new Item(PROPS_GENERIC_T3)),
				MATERIA_4 = REG.register("materia_major", () -> new Item(PROPS_GENERIC_T4)),
				MATERIA_5 = REG.register("materia_prima", () -> new Item(PROPS_GENERIC_T5)),
				QUINTESSENCE = REG.register("quintessential_condensate", () -> new Item(PROPS_GENERIC_T4)),
				LEAD_INGOT = basic("lead_ingot"),
				TIN_INGOT = basic("tin_ingot"),
				SILVER_INGOT = basic("silver_ingot"),
				BRONZE_INGOT = basic("bronze_ingot"),
				BRASS_INGOT = basic("brass_ingot"),
				MERCURY = basic("hydrargyrum"),
				
			
				// Unstackable items
				PHILOSOPHERS_STONE = REG.register("philosophers_stone", () -> new Item(makeProps(AASBRarity.SPECIAL.get(), 1, true))),
				MINIUM_STONE = REG.register("minium_stone", () -> new Item(PROPS_MINIUM)),
				ELIXIR_OF_LIFE = REG.register("elixir_of_life", () -> new Item(PROPS_MINIUM)),
				LOOT_BALL = REG.register("complex_mass", () -> new LootBallItem(PROPS_UNSTACKABLE)),
				
				// Flasks
				FLASK_LEAD = REG.register("lead_flask", () -> new FlaskItem(6000, PROPS_UNSTACKABLE)),
				FLASK_GOLD = REG.register("golden_flask", () -> new FlaskItem(600, PROPS_UNSTACKABLE)),
				FLASK_AETHER = REG.register("aether_flask", () -> new StorageFlaskItem(PROPS_UNSTACKABLE)),
				
				// Trinkets
				GLOVE = REG.register("glove", () -> new GloveItem(PROPS_UNSTACKABLE)),
				RING = REG.register("ring", () -> new RingItem(PROPS_UNSTACKABLE)),
				CHARM = REG.register("charm", () -> new CharmItem(PROPS_UNSTACKABLE)),
			
				// Hermetic Stuff
				HERMETIC_HELMET = REG.register("hermetic_armet", () -> new HermeticArmorItem(EquipmentSlot.HEAD, PROPS_HERM_GEAR, 0.2f)),
				HERMETIC_CHESTPLATE = REG.register("hermetic_cuirass", () -> new HermeticArmorItem(EquipmentSlot.CHEST, PROPS_HERM_GEAR, 0.4f)),
				HERMETIC_LEGGINGS = REG.register("hermetic_greaves", () -> new HermeticArmorItem(EquipmentSlot.LEGS, PROPS_HERM_GEAR, 0.3f)),
				HERMETIC_BOOTS = REG.register("hermetic_sabatons", () -> new HermeticArmorItem(EquipmentSlot.FEET, PROPS_HERM_GEAR, 0.1f)),
				HERMETIC_SWORD = REG.register("hermetic_blade", () -> new HermeticSwordItem(3, -2.2f, PROPS_HERM_GEAR)),
				HERMETIC_PICKAXE = REG.register("hermetic_hammer", () -> new HermeticPickaxeItem(1, -2.6f, PROPS_HERM_GEAR)),
				HERMETIC_SHOVEL = REG.register("hermetic_spade", () -> new HermeticShovelItem(2, -2.8f, PROPS_HERM_GEAR)),
				HERMETIC_AXE = REG.register("hermetic_hatchet", () -> new HermeticAxeItem(5, -2.8f, PROPS_HERM_GEAR)),
				HERMETIC_HOE = REG.register("hermetic_scythe", () -> new HermeticHoeItem(-4, 0.2f, PROPS_HERM_GEAR)),
			
				// Misc stuff
				OMNITOOL = REG.register("internal_omnitool", () -> new InternalOmnitool(9999, 9999, AASBToolTier.HERMETIC, BlockTags.MINEABLE_WITH_PICKAXE, PROPS_UNSTACKABLE)),
			
				CIRCLET = REG.register("seer_circlet", () -> new CircletItem(PROPS_JEWELRY)),
				AMULET = REG.register("philosopher_amulet", () -> new AmuletItem(PROPS_JEWELRY)),
				POCKETWATCH = REG.register("astrologer_pocketwatch", () -> new PocketwatchItem(PROPS_JEWELRY)),
				ANKLET = REG.register("prophet_anklet", () -> new AnkletItem(PROPS_JEWELRY)),

				// BlockItems
				ASH_STONE_BLOCKITEM = fromBlock(Blocks.ASH_STONE),
				WAYSTONE_BLOCKITEM = fromBlock(Blocks.WAYSTONE);/*,
				DEBUG_LAB_SENDER_BLOCKITEM = fromBlock(Blocks.DEBUG_LAB_SENDER),
				DEBUG_LAB_RECIEVER_BLOCKITEM = fromBlock(Blocks.DEBUG_LAB_RECIEVER),
				DEBUG_LAB_START_BLOCKITEM = fromBlock(Blocks.DEBUG_LAB_START),
				DEBUG_LAB_END_BLOCKITEM = fromBlock(Blocks.DEBUG_LAB_END);*/
		
		// Lab BlockItems Map
		/** <b><i><u>DO NOT MODIFY THIS</b></i></u> */
		public static final Map<LabProcess,RegistryObject<BlockItem>> LAB_BLOCKITEM_MAP = new HashMap<>(Blocks./*Labs.*/NUM_LABS);
		static void initializeLabBlockItemMap() {
			for (Entry<LabProcess,RegistryObject<LabBlock>> lab : Blocks./*Labs.*/LAB_MAP.entrySet()) {
				RegistryObject<BlockItem> ro = fromBlockAlt(lab.getValue());
				LAB_BLOCKITEM_MAP.put(lab.getKey(), ro);
			}
		}
		
		
		// constructs a new Item Properties using an existing base
		private static Item.Properties makeProps(Rarity rarity, int maxStack, boolean fireRes) {
			Item.Properties props = new Item.Properties()
					.tab(ModInit.ITEM_GROUP)
					.rarity(rarity)
					.stacksTo(maxStack);
			if (fireRes) {
				props.fireResistant();
			}
			return props;
		}
		
		public static RegistryObject<Item> basic(String name) {
			return REG.register(name, () -> new Item(PROPS_GENERIC));
		}
		
		// Conveniance function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
		public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
			return REG.register(block.getId().getPath(), () -> new BlockItem(block.get(), PROPS_GENERIC));
		}
		
		// Conveniance function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
		// BlockItem type variant
		public static <B extends Block> RegistryObject<BlockItem> fromBlockAlt(RegistryObject<B> block) {
			return REG.register(block.getId().getPath(), () -> new BlockItem(block.get(), PROPS_GENERIC));
		}
	}
	
	public class Blocks {
		private static final DeferredRegister<Block> REG = DeferredRegister.create(ForgeRegistries.BLOCKS, AsAboveSoBelow.MODID);
		public static final BlockBehaviour.Properties BLOCK_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE).strength(2f).requiresCorrectToolForDrops();
		public static final BlockBehaviour.Properties PROPS_TEMPBLOCK = BlockBehaviour.Properties.of(Material.ICE).noDrops().instabreak();
		
		// Simple
		public static final RegistryObject<Block> ASH_STONE = REG.register("ashen_stone", () -> new Block(BLOCK_PROPERTIES));
		public static final RegistryObject<Block> WAYSTONE = REG.register("waystone", () -> new Block(BLOCK_PROPERTIES));
		

		public static final RegistryObject<@NotNull AirIceBlock> AIR_ICE = REG.register("air_ice", () -> new AirIceBlock(PROPS_TEMPBLOCK.friction(0.9f).randomTicks().sound(SoundType.GLASS).noOcclusion()));
		/*public static final RegistryObject<@NotNull DebugLabMultiblock> DEBUG_LAB_SENDER = REG.register("debug_lab_sender", () -> new DebugLabMultiblock(BLOCK_PROPERTIES, true));
		public static final RegistryObject<@NotNull DebugLabMultiblock> DEBUG_LAB_RECIEVER = REG.register("debug_lab_reciever", () -> new DebugLabMultiblock(BLOCK_PROPERTIES, false));
		public static final RegistryObject<@NotNull LabBlock> DEBUG_LAB_START = REG.register("debug_lab_start", () -> new LabBlock(LabProcess.DISTILLATION, BLOCK_PROPERTIES));
		public static final RegistryObject<@NotNull LabBlock> DEBUG_LAB_END = REG.register("debug_lab_end", () -> new LabBlock(LabProcess.SOLUTION, BLOCK_PROPERTIES));*/
		
		//public class Labs {
			public static final int NUM_LABS = (int) (23 / 0.75) + 1;
			/** <b><i><u>DO NOT MODIFY THIS</b></i></u> */
			public static final Map<LabProcess,RegistryObject<LabBlock>> LAB_MAP = new HashMap<>(NUM_LABS);

			public static final RegistryObject<LabBlock> EVAPORATION = reg("evaporation_alembic", LabProcess.EVAPORATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> DESICCATION = reg("desiccation_desiccator", LabProcess.DESICCATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> SUBLIMATION = reg("sublimation_watchglass", LabProcess.SUBLIMATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> DISTILLATION = reg("distillation_retort", LabProcess.DISTILLATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> COHOBATION = reg("cohobation_klein_retort", LabProcess.COHOBATION, BLOCK_PROPERTIES);
			

			public static final RegistryObject<LabBlock> OXIDATION = reg("oxidation_phlogisticator", LabProcess.OXIDATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> CONGELATION = reg("congelation_depositioner", LabProcess.CONGELATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> CERATION = reg("ceration_heating_mantle", LabProcess.CERATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> DEHYDRATION = reg("dehydration_vacuum_chamber", LabProcess.DEHYDRATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> EXALTATION = reg("exaltation_altar", LabProcess.EXALTATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> CONDEMNATION = reg("condemnation_corruptor", LabProcess.CONDEMNATION, BLOCK_PROPERTIES);

			public static final RegistryObject<LabBlock> FIXATION = reg("fixation_aperture", LabProcess.FIXATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> AMALGAMATION = reg("amalgamation_crucible", LabProcess.AMALGAMATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> HOMOGENIZATION = reg("homogenization_automortar", LabProcess.HOMOGENIZATION, BLOCK_PROPERTIES);

			public static final RegistryObject<LabBlock> CONJUNCTION = reg("conjunction_pressure_chamber", LabProcess.CONJUNCTION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> STAGNATION = reg("stagnation_aspirator", LabProcess.STAGNATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> SEPARATION = reg("separation_funnel", LabProcess.SEPARATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> FILTRATION = reg("filtration_centrifuge", LabProcess.FILTRATION, BLOCK_PROPERTIES);

			public static final RegistryObject<LabBlock> PROJECTION = reg("projection_magic_mirror", LabProcess.PROJECTION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> CONDENSATION = reg("condensation_aludel", LabProcess.CONDENSATION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> SOLUTION = reg("solution_dissolver", LabProcess.SOLUTION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> DIGESTION = reg("digestion_athanor", LabProcess.DIGESTION, BLOCK_PROPERTIES);
			public static final RegistryObject<LabBlock> MULTIPLICATION = reg("multiplication_cloner", LabProcess.MULTIPLICATION, BLOCK_PROPERTIES);
			
			private static RegistryObject<LabBlock> reg(String name, LabProcess process, BlockBehaviour.Properties props) {
				RegistryObject<@NotNull LabBlock> ro = REG.register(name, () -> new LabBlock(process, props));
				LAB_MAP.put(process, ro);
				return ro;
			}
		//}
		
	}
	
	public class Entities {
		private static final DeferredRegister<EntityType<?>> REG = DeferredRegister.create(ForgeRegistries.ENTITIES, AsAboveSoBelow.MODID);

		public static final RegistryObject<EntityType<SmartArrow>> SMART_ARROW = REG.register("smart_arrow", () -> EntityType.Builder.<SmartArrow>of(SmartArrow::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(4)
				.updateInterval(20)
				.fireImmune()
				.noSummon().noSave()
				.build("smart_arrow"));
		public static final RegistryObject<EntityType<SentientArrow>> SENTIENT_ARROW = REG.register("sentient_arrow", () -> EntityType.Builder.<SentientArrow>of(SentientArrow::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(4)
				.updateInterval(20)
				.fireImmune()
				.noSummon().noSave()
				.build("sentient_arrow"));
		public static final RegistryObject<EntityType<HorrorEntity>> HORROR = REG.register("horror", () -> EntityType.Builder.<HorrorEntity>of(HorrorEntity::new, MobCategory.MONSTER)
				.sized(0.6f, 1.95f)
				.clientTrackingRange(8)
				.build("horror"));
		
		// modeled after botania mana burst
		public static final RegistryObject<EntityType<MustangProjectile>> MUSTANG = REG.register("mustang", () -> EntityType.Builder.<MustangProjectile>of(MustangProjectile::new, MobCategory.MISC)
				.sized(0.5f, 0.5f)
				.clientTrackingRange(6)
				.updateInterval(10)
				.fireImmune()
				.noSummon().noSave()
				.build("mustang"));
	}
	
	// yes, i am using the old lingo, because old habits die hard and BlockEntity sounds stupid
	public class TileEntities {
		private static final DeferredRegister<BlockEntityType<?>> REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AsAboveSoBelow.MODID);
		
		//public static final RegistryObject<BlockEntityType<LabTE>> LAB_TE = REG.register("lab", () -> BlockEntityType.Builder.of(LabTE::new, Blocks.LAB.get()).build(null)),
		/*public static final RegistryObject<BlockEntityType<LabDebugCapabilitySendTE>> DEBUG_LAB_CAPABILITY_SENDER_TE =
				REG.register("debug_lab_sender_te", () -> BlockEntityType.Builder.of(LabDebugCapabilitySendTE::new, Blocks.DEBUG_LAB_SENDER.get()).build(null));
		public static final RegistryObject<BlockEntityType<LabDebugCapabilityRecieveTE>> DEBUG_LAB_CAPABILITY_RECIEVER_TE =
				REG.register("debug_lab_reciever_te", () -> BlockEntityType.Builder.of(LabDebugCapabilityRecieveTE::new, Blocks.DEBUG_LAB_RECIEVER.get()).build(null));
		public static final RegistryObject<BlockEntityType<LabDebugStartTE>> DEBUG_LAB_START_TE =
				REG.register("debug_lab_start_te", () -> BlockEntityType.Builder.of(LabDebugStartTE::new, Blocks.DEBUG_LAB_START.get()).build(null));
		public static final RegistryObject<BlockEntityType<LabDebugEndTE>> DEBUG_LAB_END_TE =
				REG.register("debug_lab_end_te", () -> BlockEntityType.Builder.of(LabDebugEndTE::new, Blocks.DEBUG_LAB_END.get()).build(null));*/
		
		
		//public class Labs {
			/** <b><i><u>DO NOT MODIFY THIS</b></i></u> */
			public static final Map<LabProcess,RegistryObject<BlockEntityType<? extends LabTE>>> LAB_TE_MAP = new HashMap<>(Blocks./*Labs.*/NUM_LABS);
			
			public static final RegistryObject<BlockEntityType<? extends LabTE>>
					EVAPORATION_TE = make("evaporation_te", LabProcess.EVAPORATION, EvaporationTE::new, Blocks./*Labs.*/EVAPORATION),
					DESICCATION_TE = make("desiccation_te", LabProcess.DESICCATION, DesiccationTE::new, Blocks./*Labs.*/DESICCATION),
					SUBLIMATION_TE = make("sublimation_te", LabProcess.SUBLIMATION, SublimationTE::new, Blocks./*Labs.*/SUBLIMATION),
					DISTILLATION_TE = make("distillation_te", LabProcess.DISTILLATION, DistillationTE::new, Blocks./*Labs.*/DISTILLATION),
					//COHOBATION_TE = make("cohobation_te", LabProcess.COHOBATION, CohobationTE::new, Blocks./*Labs.*/COHOBATION),
					
					OXIDATION_TE = make("oxidation_te", LabProcess.OXIDATION, OxidationTE::new, Blocks./*Labs.*/OXIDATION),
					CONGELATION_TE = make("congelation_te", LabProcess.CONGELATION, CongelationTE::new, Blocks./*Labs.*/CONGELATION),
					CERATION_TE = make("ceration_te", LabProcess.CERATION, CerationTE::new, Blocks./*Labs.*/CERATION),
					DEHYDRATION_TE = make("dehydration_te", LabProcess.DEHYDRATION, DehydrationTE::new, Blocks./*Labs.*/DEHYDRATION),
					EXALTATION_TE = make("exaltation_te", LabProcess.EXALTATION, ExaltationTE::new, Blocks./*Labs.*/EXALTATION),
					CONDEMNATION_TE = make("condemnation_te", LabProcess.CONDEMNATION, CondemnationTE::new, Blocks./*Labs.*/CONDEMNATION),
					//FIXATION_TE = make("fixation_te", LabProcess.FIXATION, FixationTE::new, Blocks./*Labs.*/FIXATION),
					//AMALGAMATION_TE = make("amalgamation_te", LabProcess.AMALGAMATION, AmalgamationTE::new, Blocks./*Labs.*/AMALGAMATION),
					//HOMOGENIZATION_TE = make("homogenization_te", LabProcess.HOMOGENIZATION, HomogenizationTE::new, Blocks./*Labs.*/HOMOGENIZATION),
					//CONJUNCTION_TE = make("conjunction_te", LabProcess.CONJUNCTION, ConjunctionTE::new, Blocks./*Labs.*/CONJUNCTION),
					//STAGNATION_TE = make("stagnation_te", LabProcess.STAGNATION, StagnationTE::new, Blocks./*Labs.*/STAGNATION),
					//SEPARATION_TE = make("separation_te", LabProcess.SEPARATION, SeparationTE::new, Blocks./*Labs.*/SEPARATION),
					//FILTRATION_TE = make("filtration_te", LabProcess.FILTRATION, FiltrationTE::new, Blocks./*Labs.*/FILTRATION),
					
					//PROJECTION_TE = make("projection_te", LabProcess.PROJECTION, ProjectionTE::new, Blocks./*Labs.*/PROJECTION),
					//CONDENSATION_TE = make("condensation_te", LabProcess.CONDENSATION, CondensationTE::new, Blocks./*Labs.*/CONDENSATION),
					SOLUTION_TE = make("solution_te", LabProcess.SOLUTION, SolutionTE::new, Blocks./*Labs.*/SOLUTION);//,
					//DIGESTION_TE = make("digestion_te", LabProcess.DIGESTION, DigestionTE::new, Blocks./*Labs.*/DIGESTION),
					//MULTIPLICATION_TE = make("multiplication_te", LabProcess.MULTIPLICATION, MultiplicationTE::new, Blocks./*Labs.*/MULTIPLICATION);
			
			private static RegistryObject<BlockEntityType<? extends LabTE>> make(String name, LabProcess process, BlockEntitySupplier<? extends LabTE> factory, RegistryObject<LabBlock> block) {
				RegistryObject<BlockEntityType<? extends LabTE>> ro = REG.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
				LAB_TE_MAP.put(process, ro);
				return ro;
			}
			
			public static RegistryObject<BlockEntityType<? extends LabTE>> get(LabProcess process) {
				return LAB_TE_MAP.get(process);
			}
		//}
	}
	
	public class MobEffects {
		private static final DeferredRegister<MobEffect> REG = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AsAboveSoBelow.MODID);
		
		public static final RegistryObject<MobEffect> TRANSMUTING = REG.register("transmuting", () -> new TransmutingEffect());
	}

}
