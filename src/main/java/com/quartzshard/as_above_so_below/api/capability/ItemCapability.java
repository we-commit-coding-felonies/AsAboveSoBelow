package com.quartzshard.as_above_so_below.api.capability;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class ItemCapability<T> {

	private ItemCapabilityProvider wrapper;

	/**
	 * @apiNote Should only be used by {@link ItemCapabilityWrapper}
	 */
	public void setWrapper(ItemCapabilityProvider wrapper) {
		if (this.wrapper == null) {
			this.wrapper = wrapper;
		}
	}

	public abstract Capability<T> getCapability();

	public abstract LazyOptional<T> getLazyCapability();

	protected ItemStack getStack() {
		return wrapper.getItemStack();
	}

	protected T getItem() {
		return (T) getStack().getItem();
	}
}