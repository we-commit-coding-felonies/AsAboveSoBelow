package com.quartzshard.aasb.common.gui.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransmutationContainer implements Container {

	private final AbstractContainerMenu menu;
	private final List<ItemStack> stacks;

	public TransmutationContainer(AbstractContainerMenu menu, int size) {
		this.menu = menu;
		this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	@NotNull
	public ItemStack getItem(int slot) {
		return slot >= stacks.size() ? ItemStack.EMPTY : stacks.get(slot);
	}

	@Override
	@NotNull
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = ContainerHelper.removeItem(stacks, slot, count);
		if (!stack.isEmpty()) {
			menu.slotsChanged(this);
		}
		return stack;
	}

	@Override
	@NotNull
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(stacks, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		stacks.set(slot, stack);
		menu.slotsChanged(this);
	}

	@Override
	public void setChanged() {}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.stacks.clear();
	}
}
