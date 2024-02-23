package com.quartzshard.aasb.common.item.equipment.tool;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.ItemData;
import com.quartzshard.aasb.api.alchemy.Phil;
import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.api.alchemy.aspect.FormAspect;
import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.item.IDigStabilizer;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.api.item.bind.IHandleKeybind;
import com.quartzshard.aasb.client.Keybinds;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.WayUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * tool that can break any block <br>
 * used internally by the shovel areablast, but is also a nice dev / test item <br>
 * has a scary warning on it to hopefully dissuade bug reports if/when people fuck with this
 * @author solunareclipse1
 */
@Mod.EventBusSubscriber(modid = AASB.MODID)
public class OmnitoolItem extends DiggerItem implements IDigStabilizer, IHandleKeybind {
	public OmnitoolItem(float damage, float speed, @NotNull Tier tier, TagKey<Block> breakableBlocks, @NotNull Properties props) {
		super(damage, speed, tier, breakableBlocks, props);
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return NBTUtil.getBoolean(stack, "Instamine", false) ? 13 : 0;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return NBTUtil.getBoolean(stack, "AmIARealBoy-ccccccuthdkhdbhtbindtlhrjinenkcveuhddnvhkvrk", false);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return AASB.RNG.nextInt(0, (0xffffff)+1);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (entity instanceof Player plr) {
			if (!NBTUtil.getBoolean(stack, "AmIARealBoy-ccccccuthdkhdbhtbindtlhrjinenkcveuhddnvhkvrk", false)) {
				NBTUtil.setBoolean(stack, "AmIARealBoy-ccccccuthdkhdbhtbindtlhrjinenkcveuhddnvhkvrk", true);
				Logger.chat("OmnitoolItem.inventoryTick()", "WarningMessage", "PLEASE READ THE FOLLOWING:", plr,
							"You have just given yourself a DEBUG ITEM, which is not normally obtainable",
							"It is intended for use by the developers of AASB for testing various things",
							"The item may behave unpredictably, cause glitches, crash the game, or worse!",
							"By continuing to use this item, you accept the risks involved with doing so",
							"ANY BUG REPORTS RELATED TO THIS ITEM WILL BE IGNORED! YOU HAVE BEEN WARNED!"
						);
			}
			if (selected) {
				if (plr.hasPermissions(4)) {
					if (!isFoil(stack)) {
						NBTUtil.setBoolean(stack, "IsExtremelyOP", true);
					}
				} else {
					if (isFoil(stack)) {
						NBTUtil.setBoolean(stack, "IsExtremelyOP", false);
					}
				}
			}
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		
		if (NBTUtil.getBoolean(stack, "AmIARealBoy-ccccccuthdkhdbhtbindtlhrjinenkcveuhddnvhkvrk", false)) {
			tips.add(LangData.tc(LangData.TIP_PHIL_1));
			tips.add(LangData.tc(LangData.TIP_PHIL_2));
			tips.add(LangData.tc(LangData.TIP_PHIL_3));
			tips.add(LangData.tc(LangData.TIP_PHIL_4));
			tips.add(LangData.tc(LangData.TIP_PHIL_5));
		}
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return NBTUtil.getBoolean(stack, "IsExtremelyOP", false);
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return isFoil(stack) || net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(Tiers.NETHERITE, state);
	}

	public boolean toggleInstamine(ItemStack stack) {
		boolean wasInstamine = NBTUtil.getBoolean(stack, "Instamine", false);
		NBTUtil.setBoolean(stack, "Instamine", !wasInstamine);
		return true;
	}

	@Override
	public int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		return NBTUtil.getBoolean(stack, "Instamine", false) ? 1 : 2;
	}

	/**
	 * serverside code can be thrown in here to quickly test it
	 */
	@Override
	public boolean handle(@NotNull PressContext ctx) {
		ServerPlayer plr = ctx.player();
		switch (ctx.bind()) {
		case ITEMMODE:
			Phil.debugTestChangeMap(QuickAndDirtyRuntimeCodeTests.Mapper.createTestMap());
			return true;
		case ITEMFUNC_1:
			return true;
		case ITEMFUNC_2:
			return true;
			
			
			
		case EMPOWER:
			return ctx.state() == BindState.PRESSED
					&& toggleInstamine(ctx.stack());
		default:
			return false;
		}
	}
	
	@SubscribeEvent
	public static void devToolBreakUnbreakablesHandler(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			ItemStack stack = event.getItemStack();
			if (stack.getItem() instanceof OmnitoolItem tool && tool.isFoil(stack)) {
				BlockPos pos = event.getPos();
				Level level = player.level();
				BlockState block = level.getBlockState(pos);
				if (block.getDestroySpeed(level, pos) < 0 && tool.blockBreakSpeedInTicks(stack, block) == 1) {
					@Nullable ItemEntity drop = player.spawnAtLocation(block.getBlock());
					if (drop != null) {
						drop.setPos(Vec3.atCenterOf(pos));
					}
					level.destroyBlock(pos, true);
					LinkedHashMap<String,String> info = new LinkedHashMap<>();
					info.put("Playername", player.getName().getString());
					info.put("UUID", player.getStringUUID());
					info.put("Destroyed Block", block.toString().substring(5));
					info.put("Block Position", pos.toShortString());
					info.put("Player Position", new BlockPos((int)player.position().x, (int)player.position().y, (int)player.position().z).toShortString());
					Logger.info("OmnitoolItem.devToolBreakUnbreakablesHandler()", "SecurityNotification", "Player destroyed unbreakable block:", info);
				}
			}
		}
	}
	
	
	
	/**
	 * TODO figure out how to actually do tests instead of this garbage
	 */
	public static class QuickAndDirtyRuntimeCodeTests {
		public class Mapper {
			public static @NotNull Map<ItemData,AlchData> createTestMap() {
				@NotNull HashMap<ItemData,AlchData> map = new HashMap<>();
				
				map.put(
						ItemData.fromItem(Items.COBBLESTONE),
						new AlchData(2, ShapeAspect.EARTH, "aasb:rough", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.DIRT),
						new AlchData(1, ShapeAspect.EARTH, "aasb:soil", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.SAND),
						new AlchData(2, ShapeAspect.EARTH, "aasb:terrain", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.GLASS),
						new AlchData(2, ShapeAspect.FIRE, "aasb:soil", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.STONE),
						new AlchData(2, ShapeAspect.EARTH, "aasb:smooth", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.IRON_INGOT),
						new AlchData(256, ShapeAspect.EARTH, "aasb:ferrum", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.GOLD_INGOT),
						new AlchData(2048, ShapeAspect.FIRE, "aasb:aurum", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.SILVER_INGOT.get()),
						new AlchData(1024, ShapeAspect.EARTH, "aasb:argentum", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.COPPER_INGOT),
						new AlchData(128, ShapeAspect.EARTH, "aasb:cuprum", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(Items.IRON_INGOT),
						new AlchData(256, ShapeAspect.EARTH, "aasb:ferrum", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.TIN_INGOT.get()),
						new AlchData(256, ShapeAspect.EARTH, "aasb:stannum", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.LEAD_INGOT.get()),
						new AlchData(128, ShapeAspect.EARTH, "aasb:plumbum", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.MERCURY_BOTTLE.get()),
						new AlchData(512, ShapeAspect.WATER, "aasb:metal", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.SOOT.get()),
						new AlchData(1, ShapeAspect.FIRE, (FormAspect)null, ComplexityAspect.NULLED));
				map.put(
						ItemData.fromItem(ItemInit.SALT.get()),
						new AlchData(1, null, "aasb:mineral", ComplexityAspect.NULLED));
				map.put(
						ItemData.fromItem(ItemInit.SPUT.get()),
						new AlchData(null, ShapeAspect.FIRE, "aasb:organic", ComplexityAspect.NULLED));
				map.put(
						ItemData.fromItem(ItemInit.MATERIA_NEG2.get()),
						new AlchData(1, ShapeAspect.QUINTESSENCE, "aasb:materia", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.MATERIA_NEG1.get()),
						new AlchData(2, ShapeAspect.QUINTESSENCE, "aasb:materia", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.MATERIA_1.get()),
						new AlchData(64, ShapeAspect.QUINTESSENCE, "aasb:materia", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.MATERIA_2.get()),
						new AlchData(128, ShapeAspect.QUINTESSENCE, "aasb:materia", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.MATERIA_3.get()),
						new AlchData(256, ShapeAspect.QUINTESSENCE, "aasb:materia", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.MATERIA_4.get()),
						new AlchData(512, ShapeAspect.QUINTESSENCE, "aasb:materia", ComplexityAspect.SIMPLE));
				map.put(
						ItemData.fromItem(ItemInit.MATERIA_5.get()),
						new AlchData(1024, ShapeAspect.QUINTESSENCE, "aasb:materia", ComplexityAspect.SIMPLE));
				
				return map;
			}
			
		}
	}
}
