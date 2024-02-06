package com.quartzshard.aasb.api.alchemy;


import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;


// Basically, ItemInfo from projectE. Thanks, sin!
// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/api/java/moze_intel/projecte/api/ItemInfo.java
/**
 * Class used for keeping track of a combined {@link Item} and {@link CompoundTag}. Unlike {@link ItemStack} this class does not keep track of count, and overrides {@link
 * #equals(Object)} and {@link #hashCode()} so that it can be used properly in a {@link java.util.Set}.
 *
 * @implNote If the {@link CompoundTag} this {@link ItemData} is given is empty, then it converts it to being null.
 * @apiNote {@link ItemData} and the data it stores is Immutable
 */
public class ItemData {

	private final Item item;
	@Nullable
	private final CompoundTag nbt;

	private ItemData(ItemLike item, @Nullable CompoundTag nbt) {
		this.item = item.asItem();
		this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
	}
	
	/**
	 * Creates an {@link ItemData} object from a given {@link Item} with an optional {@link CompoundTag} attached.
	 */
	public static ItemData fromItem(ItemLike item, @Nullable CompoundTag nbt) {
		return new ItemData(item, nbt);
	}
	
	public static ItemData fromItem(ItemLike item) {
		return new ItemData(item, null);
	}

	public static ItemData fromStack(ItemStack stack) {
		return fromItem(stack.getItem(), stack.getTag());
	}
	/**
	 * @return The {@link Item} stored in this {@link ItemData}.
	 */
	@NotNull
	public Item getItem() {
		return item;
	}
	
	/**
	 * @return The {@link CompoundTag} stored in this {@link ItemData}, or null if there is no nbt data stored.
	 *
	 * @apiNote The returned {@link CompoundTag} is a copy so as to ensure that this {@link ItemData} is not accidentally modified via modifying the returned {@link
	 * CompoundTag}. This means it is safe to modify the returned {@link CompoundTag}
	 */
	@SuppressWarnings("null") // Literally a null check right there, Eclipse is blind
	@Nullable
	public CompoundTag getNBT() {
		return nbt == null ? null : nbt.copy();
	}
	
	public boolean hasNBT() {
		return nbt != null;
	}
	
	@SuppressWarnings("null") // Null only ever shows up when the registry doesnt support tags. The Item registry supports tags, so its fine
	public boolean is(TagKey<Item> tag) {
		return ForgeRegistries.ITEMS.tags().getTag(tag).contains(getItem());
	}
	
	/**
	 * @return A new {@link ItemStack} created from the stored {@link Item} and {@link CompoundTag}
	 */
	public ItemStack createStack() {
		ItemStack stack = new ItemStack(item);
		CompoundTag nbt = getNBT();
		if (nbt != null) {
			//Only set the NBT if we have some, other then allow the item to use its default NBT
			stack.setTag(nbt);
		}
		return stack;
	}

	/**
	 * Writes the item and nbt fields to a NBT object.
	 */
	@SuppressWarnings("null") // If an item has a null ResourceLocation, that seems very bad and its probably OK to crash
	public CompoundTag write(CompoundTag nbt) {
		nbt.putString("item", ForgeRegistries.ITEMS.getKey(item).toString()); //item.getRegistryName().toString());
		if (this.nbt != null) {
			nbt.put("nbt", this.nbt);
		}
		return nbt;
	}

	@SuppressWarnings("null") // Literally a null check right there, Eclipse is blind
	@Override
	public int hashCode() {
		int code = item.hashCode();
		if (nbt != null) {
			code = 31 * code + nbt.hashCode();
		}
		return code;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof ItemData other) {
			return item == other.item && Objects.equals(nbt, other.nbt);
		}
		return false;
	}

	@SuppressWarnings("null") // If an item has a null ResourceLocation, that seems very bad and its probably OK to crash
	@Override
	public String toString() {
		String str = "";
		str += ForgeRegistries.ITEMS.getKey(item).toString();
		if (nbt != null) {
			str += nbt.toString();
		}
		return str;
	}
}
