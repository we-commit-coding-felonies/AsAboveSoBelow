package com.quartzshard.aasb.common.item.equipment.curio;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.init.object.ItemInit;

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

	@Override
	public @Nullable Item getMateriaRuneTarget(ItemStack stack) {
		return (this.getMaxRunes(stack) == 1 ? ItemInit.BRACELET1 : ItemInit.BRACELET2).get();
	}

}
