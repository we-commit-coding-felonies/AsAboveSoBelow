package com.quartzshard.aasb.api.alchemy.rune.shape;

import java.util.ArrayDeque;
import java.util.Deque;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.common.entity.projectile.MustangEntity;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.EntUtil.Projectiles;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WayUtil;
import com.quartzshard.aasb.util.WorldUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class FireRune extends ShapeRune {

	public FireRune() {
		super(ShapeAspect.FIRE);
	}

	/**
	 * Normal: Fireball shotgun <br>
	 * Strong: Mustang
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, @NotNull ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO: COST
		if (state == BindState.PRESSED) {
			if (player.level().isRainingAt(player.blockPosition())) {
				player.level().playSound(null, player, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 2f);
				return true;
			}
			if (strong) {
				MustangEntity burst = new MustangEntity(level, player);
				//MobEffects.FIRE_RESISTANCE
				float motionModifier = 14f;
				burst.setDeltaMovement(burst.getDeltaMovement().scale(motionModifier));
				player.level().addFreshEntity(burst);
				PlayerUtil.coolDown(player, stack.getItem(), 30);
				return true;
			}
			for (int i = 0; i < 12; i++) {
				Projectiles.fireball(level, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(2)), player, i == 0 ? 0 : 0.15);
			}
			level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1);
			PlayerUtil.coolDown(player, stack.getItem(), 10);
			return true;
		}
		return false;
	}

	/**
	 * Normal: Bottomless lava bucket <br>
	 * Strong: Floodfill lava bucket
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, @NotNull ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			BlockHitResult hitRes = PlayerUtil.getTargetedBlock(player, strong ? player.getBlockReach()-0.5 : 32);
			if (hitRes.getType() == HitResult.Type.BLOCK) {
				BlockPos origin = hitRes.getBlockPos().relative(hitRes.getDirection());
				if (level.getBlockState(origin).isAir() || level.getFluidState(origin).is(Fluids.FLOWING_LAVA)) {
					if (strong) {
						@NotNull AABB bounds = AABB.ofSize(origin.getCenter(), 20, 20, 20);
						bounds.move(0, -10, 0); // TODO: cost based on Way value of lava
						int cdTime = WorldUtil.floodFillDown(level, origin, bounds, Blocks.LAVA.defaultBlockState()) / 5;
						if (cdTime > 0) {
							PlayerUtil.coolDown(player, stack.getItem(), cdTime);
							return true;
						}
					} else {
						return level.setBlock(origin, Blocks.LAVA.defaultBlockState(), 3);
					}
				}
			}
		}
		return false;
	}

	/**
	 * Normal: Fire immunity <br>
	 * Strong: Lava immunity + Mustang resistance
	 */
	//@Override
	//public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
	//	return false;
	//}
	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		if (this.passiveEnabled(stack) && player.isOnFire()) {
			player.clearFire();
		}
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
