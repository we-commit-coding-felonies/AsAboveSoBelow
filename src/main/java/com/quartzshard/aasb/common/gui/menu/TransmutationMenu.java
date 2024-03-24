package com.quartzshard.aasb.client.gui.menu;

import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.Phil;
import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.common.gui.container.TransmutationContainer;
import com.quartzshard.aasb.common.gui.container.TransmutationResultContainer;
import com.quartzshard.aasb.common.gui.slot.TransmutationResultSlot;
import com.quartzshard.aasb.init.ModInit;
import com.quartzshard.aasb.util.Logger;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TransmutationMenu extends AbstractContainerMenu {
	public static final int
		IDX_INV_START = 0,						// 0
		IDX_INV_END = IDX_INV_START + 27,		// 27

		IDX_HOTBAR_START = IDX_INV_END,			// 27
		IDX_HOTBAR_END = IDX_HOTBAR_START + 9,	// 36

		IDX_INPUT_START = IDX_HOTBAR_END,		// 36
		IDX_INPUT_END = IDX_INPUT_START + 20,	// 56

		IDX_OUTPUT_START = IDX_INPUT_END,		// 56
		IDX_OUTPUT_END = IDX_OUTPUT_START + 20; // 76
	public static final int RESULT_SLOT = 0;
	public static final int CRAFT_SLOT_START = 1;
	public static final int CRAFT_SLOT_END = 10;
	public static final int INV_SLOT_END = 37;
	public static final int USE_ROW_SLOT_END = 46;
	//private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 5, 4);
	private final TransmutationContainer craftSlots = new TransmutationContainer(this, 20);
	private final TransmutationResultContainer resultSlots = new TransmutationResultContainer(this, 20);
	private final ContainerLevelAccess access;
	private final Player player;

	public TransmutationMenu(int id, Inventory playerInv) {
		this(id, playerInv, ContainerLevelAccess.NULL);
	}

	public TransmutationMenu(int id, Inventory playerInv, ContainerLevelAccess cla) {
		super(ModInit.MENU_TRANSTAB.get(), id);
		this.access = cla;
		this.player = playerInv.player;
		this.addSlot(new TransmutationResultSlot(this.resultSlots, 0, 124, 35));

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(this.craftSlots, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}

		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInv, l, 8 + l * 18, 142));
		}
	}

	protected static void recalculate(AbstractContainerMenu menu, Level level, Player player, TransmutationContainer input, TransmutationResultContainer result) {
		if (!level.isClientSide) {
			ServerPlayer serverplayer = (ServerPlayer)player;
			ItemStack itemstack = ItemStack.EMPTY;
			AlchData aspects = Phil.resolveToAspects(input.getStacks());
			Logger.chat("TransmutationMenu.recalculate()", "AspectsResolved", aspects.toString(), player);
			if (aspects.complexity() != ComplexityAspect.NULLED) {
				Phil.TransmutationData targets = Phil.getTransmutationTargets(aspects, 64);
				if (!targets.targets().isEmpty()) {
					itemstack = targets.targets().get(0).stack();
				}
			}
			/*
			Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, container, level);
			if (optional.isPresent()) {
				CraftingRecipe craftingrecipe = optional.get();
				if (result.setRecipeUsed(level, serverplayer, craftingrecipe)) {
					ItemStack itemstack1 = craftingrecipe.assemble(container, level.registryAccess());
					if (itemstack1.isItemEnabled(level.enabledFeatures())) {
						itemstack = itemstack1;
					}
				}
			}
			*/

			result.setItem(RESULT_SLOT, itemstack);
			menu.setRemoteSlot(RESULT_SLOT, itemstack);
			serverplayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemstack));
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void slotsChanged(Container container) {
		this.access.execute((lvl, bPos) -> {
			recalculate(this, lvl, this.player, this.craftSlots, this.resultSlots);
		});
	}

	//public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
	//	this.craftSlots.fillStackedContents(stackedContents);
	//}

	public void clearCraftingContent() {
		this.craftSlots.clearContent();
		this.resultSlots.clearContent();
	}

	//public boolean recipeMatches(Recipe<? super CraftingContainer> recipe) {
	//	return recipe.matches(this.craftSlots, this.player.level());
	//}

	/**
	 * Called when the container is closed.
	 */
	public void removed(Player player) {
		super.removed(player);
		this.access.execute((lvl, bPos) -> {
			this.clearContainer(player, this.craftSlots);
		});
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean stillValid(Player player) {
		return true;
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	@NotNull
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack retStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			retStack = slotStack.copy();
			if (index == RESULT_SLOT) {
				this.access.execute((lvl, bPos) -> {
					slotStack.getItem().onCraftedBy(slotStack, lvl, player);
				});
				if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_END, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(slotStack, retStack);
			} else if (index >= 10 && index < 46) {
				if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
					if (index < 37) {
						if (!this.moveItemStackTo(slotStack, INV_SLOT_END, USE_ROW_SLOT_END, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_END, INV_SLOT_END, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_END, USE_ROW_SLOT_END, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (slotStack.getCount() == retStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, slotStack);
			if (index == 0) {
				player.drop(slotStack, false);
			}
		}

		return retStack;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
	 * null for the initial slot that was double-clicked.
	 */
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
	}

	//public int getResultSlotIndex() {
	//	return RESULT_SLOT;
	//}
	//public int getGridWidth() {
	//	return this.craftSlots.getWidth();
	//}
	//public int getGridHeight() {
	//	return this.craftSlots.getHeight();
	//}
	//public int getSize() {
	//	return 10;
	//}
	//public RecipeBookType getRecipeBookType() {
	//	return RecipeBookType.CRAFTING;
	//}
	//public boolean shouldMoveToInventory(int pSlotIndex) {
	//	return pSlotIndex != this.getResultSlotIndex();
	//}


	//private static Tuple<Integer,Integer> xyForIdx(int idx) {
	//	switch (idx) {
	//		case
	//	}
	//}
}