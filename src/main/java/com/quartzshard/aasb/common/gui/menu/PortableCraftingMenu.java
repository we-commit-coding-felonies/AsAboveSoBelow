package com.quartzshard.aasb.common.gui.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import org.jetbrains.annotations.NotNull;

public class PortableCraftingMenu extends CraftingMenu {

	public PortableCraftingMenu(int id, @NotNull Inventory inv, ContainerLevelAccess cla) {
		super(id, inv, cla);
	}
	
	@Override
	public boolean stillValid(Player player) {
		return true;
	}

}
