package com.quartzshard.aasb.common.item.equipment.curio;

import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class AbilityCurioItem extends Item implements IRuneable, ICurioItem {

	public AbilityCurioItem(int maxRunes, Properties props) {
		super(props);
		this.maxRunes = maxRunes;
	}
	
	private final int maxRunes;
	
	@Override
	public int getMaxRunes(ItemStack stack) {
		return maxRunes;
	}

	@Override
	public boolean runesAreStrong(ItemStack stack) {
		return hasRune(stack, AlchInit.RUNE_QUINTESSENCE.get());
	}
	
	@Override
	public Component getName(ItemStack stack) {
		if (isInscribed(stack)) {
			Component runeText;
			Rune rune1 = this.getRune(stack, 0);
			Rune rune2 = null;
			if (this.getMaxRunes(stack) > 1) {
				rune2 = this.getRune(stack, 1);
			}
			if (rune2 != null) {
				Component runeName2 = rune2.fLoc();
				runeText = Component.translatable(LangData.ITEM_RUNED_2, rune1.fLoc(), rune2.fLoc());
			} else 
				runeText = Component.translatable(LangData.ITEM_RUNED_1, rune1.fLoc());
			return Component.translatable(getAbility(stack).runedLang, runeText);
		}
		return super.getName(stack);
	}
}
