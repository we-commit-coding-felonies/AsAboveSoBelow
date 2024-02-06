package com.quartzshard.aasb.api.alchemy.rune.shape;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.common.entity.projectile.MustangEntity;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.EntUtil.Projectiles;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

public class FireRune extends ShapeRune {

	public FireRune() {
		super(ShapeAspect.FIRE);
	}

	/**
	 * Normal: Fireball shotgun <br>
	 * Strong: Mustang
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		if (state == BindState.PRESSED) {
			if (player.level().isRainingAt(player.blockPosition())) {
				player.level().playSound(null, player, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 2f);
				return true;
			}
			if (strong) {
				//EntityManaBurst burst = new EntityManaBurst(player);
				MustangEntity burst = new MustangEntity(level, player);

				float motionModifier = 14f;
				burst.setDeltaMovement(burst.getDeltaMovement().scale(motionModifier));
				player.level().addFreshEntity(burst);
				PlayerUtil.coolDown(player, stack.getItem(), 30);
				return true;
			}
			for (int i = 0; i < 5; i++) {
				Projectiles.fireball(level, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(2)), player);
			}
			level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1);
			PlayerUtil.coolDown(player, stack.getItem(), 3);
			return true;
		}
		return false;
	}

	/**
	 * Normal: Bottomless lava bucket <br>
	 * Strong: Floodfill lava bucket
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Fire immunity <br>
	 * Strong: Lava immunity + Mustang resistance
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}
	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isEnchantable() {
		return true;
	}

	@Override
	public boolean hasToolAbility() {
		return false;
	}

	@Override
	public boolean isMajorToolRune() {
		return true;
	}

	@Override
	public boolean toolAbility(ItemStack stack, ToolStyle style, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

}
