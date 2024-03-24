package com.quartzshard.aasb.common.gui.container;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransmutationResultContainer extends TransmutationContainer {
	public TransmutationResultContainer(AbstractContainerMenu menu, int size) {
		super(menu, size);
	}

	@Override
	@NotNull
	public ItemStack removeItem(int slot, int count) {
		return removeItemNoUpdate(slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		stacks.set(slot, stack);
	}

	@Override
	public void setChanged() {}
}
