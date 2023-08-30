package com.quartzshard.aasb.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.common.entity.projectile.SentientArrow;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.CircletItem;
import com.quartzshard.aasb.data.AASBTags.EntityTP;
import com.quartzshard.aasb.util.ClientHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	
	@Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
	protected void onCheckGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		LocalPlayer player = ClientHelper.mc().player;
		if (entity instanceof SentientArrow arw
			&& arw.getOwner() instanceof Player plr
			&& plr.is(player)) {
			cir.setReturnValue(true);
		} else {
			ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
			int s = ClientHelper.mc().options.getEffectiveRenderDistance() * 32;
			if (AABB.ofSize(player.getEyePosition(), s, s, s).intersects(entity.getBoundingBoxForCulling())) {
				if (!entity.getType().is(EntityTP.CLAIRVOYANCE_BLACKLIST)
					&& entity instanceof LivingEntity ent
					&& CircletItem.canBeXrayd(ent)
					&& stack.getItem() instanceof CircletItem helm
					&& helm.sightEnabled(stack)) {
					cir.setReturnValue(true);
				}
			}
		}
	}
}
