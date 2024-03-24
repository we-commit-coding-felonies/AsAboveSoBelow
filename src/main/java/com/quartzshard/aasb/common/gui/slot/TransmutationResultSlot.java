package com.quartzshard.aasb.common.gui.slot;

import com.quartzshard.aasb.common.gui.container.TransmutationContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class TransmutationResultSlot extends Slot {
	public TransmutationResultSlot(Container container, TransmutationContainer inputContainer, int slot, int x, int y) {
		super(container, slot, x, y);
		this.inputContainer = inputContainer;
	}
	private final TransmutationContainer inputContainer;

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public void onTake(Player pPlayer, ItemStack pStack) {
		inputContainer.clearContent();
		this.container.clearContent();
		//this.checkTakeAchievements(pStack);
		//net.minecraftforge.common.ForgeHooks.setCraftingPlayer(pPlayer);
		//NonNullList<ItemStack> nonnulllist = pPlayer.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, pPlayer.level());
		//net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
		//for(int i = 0; i < nonnulllist.size(); ++i) {
		//	ItemStack itemstack = this.craftSlots.getItem(i);
		//	ItemStack itemstack1 = nonnulllist.get(i);
		//	if (!itemstack.isEmpty()) {
		//		this.craftSlots.removeItem(i, 1);
		//		itemstack = this.craftSlots.getItem(i);
		//	}
		//	if (!itemstack1.isEmpty()) {
		//		if (itemstack.isEmpty()) {
		//			this.craftSlots.setItem(i, itemstack1);
		//		} else if (ItemStack.isSameItemSameTags(itemstack, itemstack1)) {
		//			itemstack1.grow(itemstack.getCount());
		//			this.craftSlots.setItem(i, itemstack1);
		//		} else if (!this.player.getInventory().add(itemstack1)) {
		//			this.player.drop(itemstack1, false);
		//		}
		//	}
		//}
	}
}
