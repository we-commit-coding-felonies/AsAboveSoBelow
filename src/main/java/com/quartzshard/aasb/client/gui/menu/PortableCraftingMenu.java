package com.quartzshard.aasb.client.gui.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class PortableCraftingMenu extends CraftingMenu {

	public PortableCraftingMenu(int id, Inventory inv, ContainerLevelAccess cla) {
		super(id, inv, cla);
	}
	
	@Override
	public boolean stillValid(Player player) {
		return true;
	}

}
