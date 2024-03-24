package com.quartzshard.aasb.api.item;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Items that can contain Way, and possibly inserted to / extracted from
 */
public interface IWayHolder {
	String TK_STOREDWAY = "StoredWay";

	
	/**
	 * Gets the amount of way stored in the stack
	 * @param stack The ItemStack to check storage of
	 * @return Amount of Way stored
	 */
	default long getStoredWay(ItemStack stack) {
		return NBTUtil.getLong(stack, TK_STOREDWAY, 0);
	}
	
	/**
	 * Gets the maximum amount of way a stack can store
	 * @param stack
	 * @return the maximum amount of way the stack can store
	 */
	long getMaxWay(ItemStack stack);

	/**
	 * Checks if any amount of Way can be inserted
	 * @param stack
	 * @return if at least 1 Way can be inserted right now
	 */
	default boolean canInsertWay(ItemStack stack) {
		return getStoredWay(stack) == 0;
	}
	
	/**
	 * Checks if the given amount of Way is able to be inserted
	 * @param stack
	 * @param query
	 * @return If insertWay() called with the given query would totally succeed (partial insertions would mean false)
	 */
	default boolean canInsertWay(ItemStack stack, long query) {
		return canInsertWay(stack)
			&& getStoredWay(stack) + query <= getMaxWay(stack);
	}
	
	/**
	 * Attempts to insert Way into the stack
	 * @param stack
	 * @param amount
	 * @return the amount that was actually inserted
	 * @apiNote the default implementation here will never do partial insertions, and thus will always return either 0 or `amount`
	 */
	default long insertWay(ItemStack stack, long amount) {
		if (canInsertWay(stack, amount)) {
			long newWay = getStoredWay(stack) + amount;
			setStoredWay(stack, newWay);
			return amount;
		}
		return 0;
	}

	/**
	 * Checks if any amount of Way can be extracted
	 * @param stack
	 * @return if at least 1 Way can be extracted right now
	 */
	default boolean canExtractWay(ItemStack stack) {
		return getStoredWay(stack) > 0;
	}
	
	/**
	 * Checks if the given amount of Way is able to be extracted
	 * @param stack
	 * @param query
	 * @return If extractWay() called with the given query would totally succeed (partial extractions would mean false)
	 */
	default boolean canExtractWay(ItemStack stack, long query) {
		return canExtractWay(stack) && getStoredWay(stack) >= query;
	}

	
	/**
	 * Attempts to extract Way from the stack
	 * @param stack
	 * @param amount
	 * @return the amount that was actually extracted
	 * @apiNote the default implementation here will never do partial extractions, and thus will always return either 0 or `amount`
	 */
	default long extractWay(ItemStack stack, long amount) {
		if (canExtractWay(stack, amount)) {
			long newWay = getStoredWay(stack) - amount;
			setStoredWay(stack, newWay);
			return amount;
		}
		return 0;
	}
	
	/**
	 * Directly sets the stored Way of an ItemStack <br>
	 * Try to use insertWay() and extractWay() instead where possible
	 * @param stack Stack to set storage of
	 * @param newWay New Way value of the storage
	 */
	default void setStoredWay(ItemStack stack, long newWay) {
		NBTUtil.setLong(stack, TK_STOREDWAY, newWay);
	}
	
	/**
	 * @param oldStack
	 * @param newStack
	 * @return False if the items are the same except for Way charge
	 */
	default boolean stacksDifferentIgnoreWay(@NotNull ItemStack oldStack, ItemStack newStack) {
		if (!newStack.is(oldStack.getItem()))
			return true;

		CompoundTag newTag = newStack.getTag();
		@Nullable CompoundTag oldTag = oldStack.getTag();

		if (newTag == null || oldTag == null)
			return !(newTag == null && oldTag == null);
		@NotNull Set<String> newKeys = new HashSet<>(newTag.getAllKeys());
		Set<String> oldKeys = new HashSet<>(oldTag.getAllKeys());

		newKeys.remove(TK_STOREDWAY);
		oldKeys.remove(TK_STOREDWAY);

		if (!newKeys.equals(oldKeys))
			return true;

		return !newKeys.stream().allMatch(key -> Objects.equals(newTag.get(key), oldTag.get(key)));
	}
}
