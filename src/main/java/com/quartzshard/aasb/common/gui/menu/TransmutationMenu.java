package com.quartzshard.aasb.common.gui.menu;

import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.Phil;
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
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class TransmutationMenu extends AbstractContainerMenu {
	public static final int
		IDX_INV_START = 0,						// 0
		IDX_INV_END = IDX_INV_START + 27,		// 27

		IDX_HOTBAR_START = IDX_INV_END,			// 27
		IDX_HOTBAR_END = IDX_HOTBAR_START + 9,	// 36

		IDX_INPUT_START = IDX_HOTBAR_END,		// 36
		IDX_INPUT_END = IDX_INPUT_START + 12,	// 48

		IDX_OUTPUT_START = IDX_INPUT_END,		// 48
		IDX_OUTPUT_END = IDX_OUTPUT_START + 12; // 60
	public static final int RESULT_SLOT = 0;
	public static final int CRAFT_SLOT_START = 1;
	public static final int CRAFT_SLOT_END = 10;
	public static final int INV_SLOT_END = 37;
	public static final int USE_ROW_SLOT_END = 46;
	//private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 5, 4);
	private final TransmutationContainer craftSlots = new TransmutationContainer(this, 12);
	private final TransmutationResultContainer resultSlots = new TransmutationResultContainer(this, 12);
	//private float[] flowData = new float[12];
	private final ContainerLevelAccess access;
	private final Player player;

	public TransmutationMenu(int id, Inventory playerInv) {
		this(id, playerInv, ContainerLevelAccess.NULL);
	}

	public TransmutationMenu(int id, Inventory playerInv, ContainerLevelAccess cla) {
		super(ModInit.MENU_TRANSTAB.get(), id);
		this.access = cla;
		this.player = playerInv.player;

		for (int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 35 + j * 18, 111 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInv, i, 35 + i * 18, 169));
		}

		for (int i = 0; i < 12; i++) {
			// input, 44 both
			Vector2i pos = xyForIdx(i);
			this.addSlot(new Slot(craftSlots, i, 44 + 18*pos.x, 44 + 18*pos.y));
		}

		for (int i = 0; i < 12; i++) {
			// output, 170x 44y, inverted
			Vector2i pos = xyForIdx(i);
			this.addSlot(new TransmutationResultSlot(resultSlots, craftSlots, i, 170 - 18*pos.x, 44 - 18*pos.y));
		}
	}

	protected static void recalculate(TransmutationMenu menu, Level level, Player player, TransmutationContainer input, TransmutationResultContainer result) {
		if (!level.isClientSide) {
			ServerPlayer serverplayer = (ServerPlayer)player;
			List<ItemStack> stacks = new ArrayList<>();
			//ItemStack itemstack = ItemStack.EMPTY;
			if (!input.isEmpty()) {
				AlchData aspects = menu.resolveInputs();//Phil.resolveToAspects(input.getStacks());
				Logger.debug("TransmutationMenu.recalculate()", "AspectsResolved", aspects.toString());
				if (!aspects.complexity().allowsNull()) {
					Phil.TransmutationData targets = Phil.getTransmutationTargets(aspects, 64);
					if (!targets.targets().isEmpty()) {
						int i = 0;
						for (Phil.FlowData dat : targets.targets()) {
							if (i >= 12) break;
							stacks.add(dat.stack());
							i++;
						}
					}
				}
			}
			if (!result.isEmpty())
				result.clearContent();
			int i = 0;
			for (ItemStack stack : stacks) {
				result.setItem(i, stack);
				menu.setRemoteSlot(i+12, stack);
				serverplayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), i+12, stack));
				i++;
			}
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void slotsChanged(Container container) {
		this.access.execute((lvl, bPos) -> {
			recalculate(this, lvl, this.player, this.craftSlots, this.resultSlots);
		});
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.access.execute((lvl, bPos) -> {
			this.clearContainer(player, this.craftSlots);
		});
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	@Override
	@NotNull
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack retStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			retStack = slotStack.copy();
			//if (index == RESULT_SLOT) {
			if (IDX_OUTPUT_START <= index  && index < IDX_OUTPUT_END) {
				this.access.execute((lvl, bPos) -> {
					slotStack.getItem().onCraftedBy(slotStack, lvl, player);
				});
				//if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_END, USE_ROW_SLOT_END, true)) {
				if (!this.moveItemStackTo(slotStack, IDX_INV_START, IDX_HOTBAR_END, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(slotStack, retStack);
			//} else if (index >= CRAFT_SLOT_END && index < USE_ROW_SLOT_END) {
			} else if (IDX_INV_START <= index && index < IDX_HOTBAR_END) {
				//if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
				if (!this.moveItemStackTo(slotStack, IDX_INPUT_START, IDX_INPUT_END, false)) {
					//if (index < INV_SLOT_END) {
					if (index < IDX_INV_END) {
						//if (!this.moveItemStackTo(slotStack, INV_SLOT_END, USE_ROW_SLOT_END, false)) {
						if (!this.moveItemStackTo(slotStack, IDX_HOTBAR_START, IDX_HOTBAR_END, false)) {
							return ItemStack.EMPTY;
						}
					//} else if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_END, INV_SLOT_END, false)) {
					} else if (!this.moveItemStackTo(slotStack, IDX_INV_START, IDX_INV_END, false)) {
						return ItemStack.EMPTY;
					}
				}
			//} else if (!this.moveItemStackTo(slotStack, CRAFT_SLOT_END, USE_ROW_SLOT_END, false)) {
			} else if (!this.moveItemStackTo(slotStack, IDX_INV_START, IDX_HOTBAR_END, false)) {
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
			//if (index == RESULT_SLOT) {
			if (IDX_OUTPUT_START <= index  && index < IDX_OUTPUT_END) {
				player.drop(slotStack, false);
			}
		}

		return retStack;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
	 * null for the initial slot that was double-clicked.
	 */
	@Override
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

	private static Vector2i xyForIdx(int idx) {
		int x = 0, y = 0;
		switch (idx) {
			// 1x
			case 0:
				y = -1;
				break;
			case 1:
				x = -1;
				break;
			case 2:
				y = 1;
				break;
			case 3:
				x = 1;
				break;

			// 3x
			case 4:
				x = 1;
				y = -1;
				break;
			case 6:
				x = -1;
				y = -1;
				break;
			case 8:
				x = -1;
				y = 1;
				break;
			case 10:
				x = 1;
				y = 1;
				break;

			// 5x
			case 5:
				y = -2;
				break;
			case 7:
				x = -2;
				break;
			case 9:
				y = 2;
				break;
			case 11:
				x = 2;
				break;

			// failsafe
			default:
				Logger.warn("TransmutationMenu.xyForIdx()", "UnmappedIndex", "Will return 0,0");
				break;
		}
		return new Vector2i(x,y);
	}

	public AlchData resolveInputs() {
		return Phil.resolveToAspects(this.craftSlots.getStacks());
	}
}