package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.common.item.equipment.armor.jewellery.AmuletItem;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.level.Level;

@Mixin(ArmorDyeRecipe.class)
public class ArmorDyeRecipeMixin {
	
	@Inject(method = "matches", at = @At("HEAD"), cancellable = true)
	protected void onCheckMatch(CraftingContainer inv, Level level, CallbackInfoReturnable<Boolean> cir) {
		for (ItemStack stack : inv.getItems()) {
			if (stack.getItem() instanceof AmuletItem) {
				cir.setReturnValue(false);
			}
		}
	}
}
