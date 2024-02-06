package com.quartzshard.aasb.common.item.equipment.tool;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.IDigStabilizer;
import com.quartzshard.aasb.api.item.bind.IHandleKeybind;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.WayUtil;

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
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * tool that can break any block <br>
 * used internally by the shovel areablast, but is also a nice dev / test item
 * @author solunareclipse1
 */
@Mod.EventBusSubscriber(modid = AASB.MODID)
public class OmnitoolItem extends DiggerItem implements IDigStabilizer, IHandleKeybind {
	public OmnitoolItem(float damage, float speed, Tier tier, TagKey<Block> breakableBlocks, Properties props) {
		super(damage, speed, tier, breakableBlocks, props);
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return NBTUtil.getBoolean(stack, "Instamine", false) ? 13 : 0;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return AASB.RNG.nextInt(0, (0xffffff)+1);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (selected) {
			if (entity instanceof Player plr && plr.hasPermissions(4)) {
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
		//if (wasInstamine)
		//	AlchemyInit.TrinketRunes.FIRE.get().combatAbility(stack, player, level, BindState.PRESSED);
		//else
		//	AlchemyInit.TrinketRunes.WATER.get().combatAbility(stack, player, level, BindState.PRESSED);
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
	public boolean handle(PressContext ctx) {
		ServerPlayer plr = ctx.player();
		switch (ctx.bind()) {
		case ITEMMODE:
			if (ctx.state() == BindState.PRESSED) {
				if (!plr.isShiftKeyDown()) {
					plr.displayClientMessage(Component.literal("GODMODE"), false);
					plr.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4));
					plr.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
					plr.addEffect(new MobEffectInstance(MobEffects.HEAL, Integer.MAX_VALUE, 99));
					plr.addEffect(new MobEffectInstance(MobEffects.SATURATION, Integer.MAX_VALUE, 99));
				} else {
					plr.displayClientMessage(Component.literal("mortal mode"), false);
					plr.removeEffect(MobEffects.DAMAGE_RESISTANCE);
					plr.removeEffect(MobEffects.FIRE_RESISTANCE);
					plr.removeEffect(MobEffects.HEAL);
					plr.removeEffect(MobEffects.SATURATION);
				}
			}
			return true;
		case ITEMFUNC_1:
			return ctx.state() == BindState.PRESSED
			&& AlchInit.RUNE_FIRE.get().combatAbility(ctx.stack(), ctx.player(), ctx.level(), BindState.PRESSED, true);
		case ITEMFUNC_2:
			System.out.println(WayUtil.getAvaliableWay(plr));
			return ctx.state() == BindState.PRESSED;
			//&& TrinketRunes.ETHEREAL.get().combatAbility(ctx.stack(), ctx.player(), ctx.level(), BindState.PRESSED, true);
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
}
