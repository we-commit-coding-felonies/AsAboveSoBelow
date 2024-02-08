package com.quartzshard.aasb.common.item.equipment.curio;

import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.init.AlchInit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class GloveItem extends AbilityCurioItem implements IRuneable, ICurioItem {

	public GloveItem(int maxRunes, Properties props) {
		super(maxRunes, props);
	}

	@Override
	public ItemAbility getAbility(ItemStack stack) {
		return ItemAbility.COMBAT;
	}

}
