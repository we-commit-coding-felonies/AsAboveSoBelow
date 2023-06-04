package com.quartzshard.aasb.api.capability;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * based off projecte's capability system, because thats what im used to
 * @author solunareclipse1
 *
 */
public class ItemCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

	private final ItemCapability<?>[] capabilities;
	private final ItemStack itemStack;

	public ItemCapabilityProvider(ItemStack stack, List<Supplier<ItemCapability<?>>> capabilities) {
		itemStack = stack;
		this.capabilities = new ItemCapability<?>[capabilities.size()];
		for (int i = 0; i < capabilities.size(); i++) {
			ItemCapability<?> cap = capabilities.get(i).get();
			this.capabilities[i] = cap;
			cap.setWrapper(this);
		}
	}

	public ItemCapabilityProvider(ItemStack stack, ItemCapability<?>... capabilities) {
		itemStack = stack;
		this.capabilities = capabilities;
		for (ItemCapability<?> cap : this.capabilities) {
			cap.setWrapper(this);
		}
	}

	protected ItemStack getItemStack() {
		return itemStack;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		for (ItemCapability<?> cap : capabilities) {
			if (capability == cap.getCapability()) {
				return cap.getLazyCapability().cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag serializedNBT = new CompoundTag();
		for (ItemCapability<?> cap : capabilities) {
			if (cap instanceof IItemCapabilitySerializable serializableCap) {
				serializedNBT.put(serializableCap.getStorageKey(), serializableCap.serializeNBT());
			}
		}
		return serializedNBT;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		for (ItemCapability<?> cap : capabilities) {
			if (cap instanceof IItemCapabilitySerializable serializableCap && nbt.contains(serializableCap.getStorageKey())) {
				serializableCap.deserializeNBT(nbt.get(serializableCap.getStorageKey()));
			}
		}
	}
}