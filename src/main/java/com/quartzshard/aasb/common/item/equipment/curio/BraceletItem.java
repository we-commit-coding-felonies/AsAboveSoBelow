package com.quartzshard.aasb.common.item.equipment.curio;

import com.quartzshard.aasb.api.item.IRuneable;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BraceletItem extends AbilityCurioItem implements IRuneable, ICurioItem {

	public BraceletItem(int maxRunes, Properties props) {
		super(maxRunes, props);
	}

	@Override
	public ItemAbility getAbility(ItemStack stack) {
		return ItemAbility.UTILITY;
	}
}
