package com.quartzshard.aasb.util;

import java.util.Optional;

import com.quartzshard.aasb.api.item.IWayHolder;
import com.quartzshard.aasb.data.tags.ItemTP;
import com.quartzshard.aasb.init.FxInit;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class WayUtil {
	
	/**
	 * Clamps way to a set maximum value and playes the "waste" noise if any is voided
	 * @param way The Way to be clamped
	 * @param max The maximum value that the Way will be clamped to
	 * @return Math.min(way, max)
	 */
	public static long clampWay(long way, long max, Entity entity) {
		if (way > max) {
			way = max;
			entity.level().playSound(null, entity.blockPosition(), FxInit.SND_WAY_WASTE.get(), entity.getSoundSource());
		}
		return way;
	}

	
	/**
	 * checks if the player has any Way <br>
	 * works similar to getAvaliableWay, but will return immediately upon finding any amount of EMC to speed things up a tad
	 * @param player
	 * @return true if player has any Way
	 */
	public static boolean hasWay(Player player) {
		if (player.isCreative()) {
			return true;
		}
		
		// Curios
		/*
		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (stack.isEmpty()) continue;
				if (getAvaliableEmcOfStack(stack) > 0) return true;
			}
		}*/

		// Inventory
		Optional<IItemHandler> itemHandlerCap = player.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (itemHandlerCap.isPresent()) {
			IItemHandler inv = itemHandlerCap.get();
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) continue;
				if (getStackUsableWay(stack) > 0) return true;
			}
		}
		return false;
	}
	
	public static long getAvaliableWay(Player player) {
		if (player.isCreative())
			return Long.MAX_VALUE;
		long foundWay = 0;

		// Offhand first, because thats probably what the player wants
		foundWay = addToTotal(foundWay, getStackUsableWay(player.getOffhandItem()));
		if (foundWay == Long.MAX_VALUE) return foundWay;

		// Inventory last
		Optional<IItemHandler> oiih = player.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (oiih.isPresent()) {
			IItemHandler inv = oiih.get();			
			for (int i = 0; i < inv.getSlots(); i++) {
				if (i == 40) continue; // offhand, was already checked
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) continue;

				foundWay = addToTotal(foundWay, getStackUsableWay(stack));
				if (foundWay == Long.MAX_VALUE) return foundWay;
			}
		}
		
		return foundWay;
	}
	
	/**
	 * A function to consume Way from all sources in the inventory
	 * 
	 * @param player
	 * @param toConsume
	 * @return The amount of Way consumed. May be more than toConsume if inefficient sources were used!
	 */
	public static long consumeAvaliableWay(Player player, long toConsume) {
		if (player.isCreative())
			return Long.MAX_VALUE;
		boolean didConsume = false;
		long consumed = 0, totalConsumed = 0;

		// Offhand first, because thats probably what the player wants
		consumed = consumeStackUsableWay(toConsume - totalConsumed, player.getOffhandItem());
		if (consumed != 0) {
			didConsume = true;
			totalConsumed = addToTotal(totalConsumed, consumed);
			consumed = 0;
		}
		if (totalConsumed == Long.MAX_VALUE) return totalConsumed;

		// Inventory last
		Optional<IItemHandler> oiih = player.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (oiih.isPresent()) {
			IItemHandler inv = oiih.get();
			for (int i = 0; i < inv.getSlots(); i++) {
				if (i == 40) continue; // offhand, was already checked
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) continue;

				consumed = consumeStackUsableWay(toConsume - totalConsumed, stack);
				if (consumed != 0) {
					didConsume = true;
					totalConsumed = addToTotal(totalConsumed, consumed);
					consumed = 0;
				}
				if (totalConsumed == Long.MAX_VALUE) return totalConsumed;
			}
		}
		if (didConsume) player.containerMenu.broadcastChanges();
		
		return totalConsumed;
	}
	
	/**
	 * A function to consume Way from all sources in the inventory, but will skip the chestplate <br>
	 * used by the Amulet for automatic absorption of inventory Way into its reservoir
	 * 
	 * @param player
	 * @param toConsume
	 * @return The amount of Way consumed. May be more than toConsume if inefficient sources were used!
	 */
	public static long consumeAvaliableWaySkipAmulet(Player player, long toConsume) {
		if (player.isCreative())
			return Long.MAX_VALUE;
		boolean didConsume = false;
		long consumed = 0, totalConsumed = 0;

		// Offhand first, because thats probably what the player wants
		consumed = consumeStackUsableWay(toConsume - totalConsumed, player.getOffhandItem());
		if (consumed != 0) {
			didConsume = true;
			totalConsumed = addToTotal(totalConsumed, consumed);
			consumed = 0;
		}
		if (totalConsumed == Long.MAX_VALUE) return totalConsumed;

		// Inventory last
		Optional<IItemHandler> oiih = player.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
		if (oiih.isPresent()) {
			IItemHandler inv = oiih.get();
			for (int i = 0; i < inv.getSlots(); i++) {
				if (i == 40 || i == 38) continue; // offhand was already checked, and we dont check chestplate
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.isEmpty()) {
					continue;
				}

				consumed = consumeStackUsableWay(toConsume - totalConsumed, stack);
				if (consumed != 0) {
					didConsume = true;
					totalConsumed = addToTotal(totalConsumed, consumed);
					consumed = 0;
				}
				if (totalConsumed == Long.MAX_VALUE) return totalConsumed;
			}
		}
		if (didConsume) player.containerMenu.broadcastChanges();
		
		return totalConsumed;
	}
	
	
	
	public static long getStackUsableWay(ItemStack stack) {
		if (stack.getItem() instanceof IWayHolder waystone && waystone.canExtractWay(stack))
			return waystone.getStoredWay(stack);
		else if (stack.is(ItemTP.WAY_FUEL)) {
			// TODO: fuel items
		}
		
		return 0;
	}

	/**
	 * Tries to consume Way from a fuel item or IWayHolder <br>
	 * If the ItemStack doesnt have enough Way, it will consume all of it
	 * 
	 * @param stack The ItemStack to consume from
	 * @param toConsume The amount of Way to consume
	 * @return The amount of Way that was actually consumed
	 */
	public static long consumeStackUsableWay(long toConsume, ItemStack stack) {
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof IWayHolder waystone && waystone.canExtractWay(stack)) {
				long stored = waystone.getStoredWay(stack);
				return waystone.extractWay(stack, stored < toConsume ? stored : toConsume);
			} else if (stack.is(ItemTP.WAY_FUEL)) {
				// TODO: fuel items
			}
		}
		return 0;
	}
	
	private static long addToTotal(long current, long add) {
		long next = current + add;
		if (next < 0)
			return Long.MAX_VALUE;
		return next;
	}

	public static long getAmuletWay(Player player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
		if (stack.getItem() instanceof IWayHolder item) {
			return item.getStoredWay(stack);
		}
		return 0;
	}
	public static float getAmuletPercent(Player player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
		if (stack.getItem() instanceof IWayHolder item) {
			return (float)item.getStoredWay(stack)/(float)item.getMaxWay(stack);
		}
		return 0;
	}
	public static long consumeAmuletWay(Player player, long toConsume) {
		if (player.isCreative())
			return Long.MAX_VALUE;
		boolean didConsume = false;
		long consumed = 0;

		consumed = consumeStackUsableWay(toConsume, player.getItemBySlot(EquipmentSlot.CHEST));
		didConsume = consumed != 0;
		if (didConsume) player.containerMenu.broadcastChanges();
		
		return consumed;
	}
}
