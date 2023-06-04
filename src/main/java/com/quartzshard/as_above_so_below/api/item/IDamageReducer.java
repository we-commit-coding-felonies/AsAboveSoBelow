package com.quartzshard.as_above_so_below.api.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

/**
 * items that give % damage reduction
 * @author solunareclipse1
 */
public interface IDamageReducer {
	
	float getDr(ItemStack stack, DamageSource source);

}
