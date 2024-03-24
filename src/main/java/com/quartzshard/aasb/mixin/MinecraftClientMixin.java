package com.quartzshard.aasb.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.CircletItem;
import com.quartzshard.aasb.data.tags.EntityTP;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.ClientUtil.AstralProjection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
	protected void onCheckGlowing(Entity entity, @NotNull CallbackInfoReturnable<Boolean> cir) {
		LocalPlayer player = ClientUtil.mc().player;
		if (entity instanceof SentientArrowEntity arw
			&& arw.getOwner() instanceof Player plr
			&& plr.is(player)) {
			cir.setReturnValue(true);
		} else {
			@NotNull ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);
			int s = ClientUtil.mc().options.getEffectiveRenderDistance() * 32;
			if (AABB.ofSize(player.getEyePosition(), s, s, s).intersects(entity.getBoundingBoxForCulling())) {
				if (!entity.getType().is(EntityTP.CLAIRVOYANCE_LIST) // TODO: configurable switch between black/whitelist
					&& !entity.is(player)
					&& entity instanceof LivingEntity ent
					&& CircletItem.canBeXrayd(ent)
					&& stack.getItem() instanceof CircletItem helm
					&& helm.sightEnabled(stack)
					) {
					cir.setReturnValue(true);
				}
			}
		}
	}
	


	// Prevents player from being controlled when freecam is enabled.
	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		if (AstralProjection.isEnabled()) {
			@NotNull Minecraft mc = ClientUtil.mc();
			if (mc.player != null && mc.player.input instanceof KeyboardInput) {
				Input input = new Input();
				input.shiftKeyDown = mc.player.input.shiftKeyDown; // Makes player continue to sneak after freecam is enabled.
				mc.player.input = input;
			}
			mc.gameRenderer.setRenderHand(false);

			//if (Freecam.disableNextTick()) {
			//	Freecam.toggle();
			//	Freecam.setDisableNextTick(false);
			//}
		}
	}

	// Prevents attacks when allowInteract is disabled.
	@Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
	private void onStartAttack(@NotNull CallbackInfoReturnable<Boolean> cir) {
		if (AstralProjection.isEnabled()) {
			cir.cancel();
		}
	}

	// Prevents item pick when allowInteract is disabled.
	@Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
	private void onPickBlock(CallbackInfo ci) {
		if (AstralProjection.isEnabled()) {
			ci.cancel();
		}
	}

	// Prevents block breaking when allowInteract is disabled.
	@Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
	private void onHandleBlockBreaking(CallbackInfo ci) {
		if (AstralProjection.isEnabled()) {
			ci.cancel();
		}
	}
}
