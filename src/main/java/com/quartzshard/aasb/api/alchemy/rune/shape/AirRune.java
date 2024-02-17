package com.quartzshard.aasb.api.alchemy.rune.shape;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket.LineParticlePreset;
import com.quartzshard.aasb.net.client.ModifyPlayerVelocityPacket;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WayUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AirRune extends ShapeRune {

	public AirRune() {
		super(ShapeAspect.AIR);
	}

	/**
	 * Normal: Smite <br>
	 * Strong: UNNNNLIMITED POWWWEERRRRR (chain lightning)
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			long wayHeld = WayUtil.getAvaliableWay(player);
			if (wayHeld >= 32) {
				HitResult hitRes = swrgSuperSmite(player, level);
				if (hitRes.getType() != HitResult.Type.MISS) {
					WayUtil.consumeAvaliableWay(player, 32);
					NetInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), hitRes.getLocation(), LineParticlePreset.SMITE), (ServerPlayer)player);
					PlayerUtil.coolDown(player, stack.getItem(), level.isThundering() ? 5 : 10);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Normal: Gust self <br>
	 * Strong: Absurd gust self + nearby
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			long wayHeld = WayUtil.getAvaliableWay(player);
			if (wayHeld >= (strong ? 32 : 16)) {
				WayUtil.consumeAvaliableWay(player, strong ? 32 : 16);
				windGust(player, strong);
				PlayerUtil.coolDown(player, stack.getItem(), strong ? 10 : 7);
				return true;
			}
		}
		return false;
	}

	/**
	 * Normal: Gem glide <br>
	 * Strong: Creative flight
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		return super.passiveAbility(stack, player, level, state, strong, slot);
		//return false;
	}
	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		if (strong) {
			long heldWay = WayUtil.getAvaliableWay(player);
			long cost = 8;
			boolean canAfford = heldWay >= cost;
			Abilities a = player.getAbilities();
			if (canAfford && !unequipped && !a.mayfly) {
				WayUtil.consumeAvaliableWay(player, cost);
				player.getAbilities().mayfly = true;
				player.onUpdateAbilities();
			} else if (unequipped || !canAfford) {
				player.getAbilities().mayfly = false;
				player.getAbilities().flying = false;
				player.onUpdateAbilities();
			}
		} else {
			if (WayUtil.hasWay(player)) {
				player.fallDistance = 0;
				//attemptGustFlight(player);
				if (!player.onGround())// && player.getDeltaMovement().y > 0)
					WayUtil.consumeAvaliableWay(player, 1);
			}
		}
	}
	@Override
	public void tickPassiveClient(ItemStack stack, Player player, Level level, boolean strong, boolean unequipped) {
		if (!strong && this.passiveEnabled(stack) && !player.getAbilities().flying && !player.isFallFlying()) {
			if (WayUtil.hasWay(player)) {
				player.fallDistance = 0;
				attemptGustFlight(player);
			}
		}
	}
	private static void attemptGustFlight(Player player) {
		Vec3 newVec = player.getDeltaMovement();
		if (ClientUtil.isJumpPressed()) {
			newVec = newVec.add(0, 0.1, 0);
		}
		if (!player.onGround()) {
			if (newVec.y() <= 0) {
				//newVec = newVec.multiply(1, 0.9, 1);
			}
			//AttributeInstance moveSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
			//double timeAccelBonus = 0;
			//if (moveSpeed.getModifier(BandOfArcana.TIME_ACCEL_UUID) != null) {
			//	timeAccelBonus = moveSpeed.getModifier(BandOfArcana.TIME_ACCEL_UUID).getAmount()/60;
			//}
			if (player.zza < 0) {
				newVec = newVec.multiply(0.9, 1, 0.9);
			} else if (player.zza > 0 && newVec.lengthSqr() < 3) {// + (6*timeAccelBonus)) {
				newVec = newVec.multiply(1.1, 1, 1.1);
			}
		}
		player.setDeltaMovement(newVec);
	}
	
	private void windGust(ServerPlayer player, boolean strong) {
		int factor = player.onGround() ? 4 : !player.isFallFlying() ? 2 : 1;
		if (strong) factor *= 2;
		Vec3 gust = player.getLookAngle().scale(factor);
		//NetworkInit.toClient(new ModifyPlayerVelocityPacket(gust, (byte)1), (ServerPlayer)player);
		
		//if (DebugCfg.GUST_HITBOX.get()) NetworkInit.toClient(new DrawParticleAABBPacket(new Vec3(area.minX, area.minY, area.minZ), new Vec3(area.maxX, area.maxY, area.maxZ), ParticlePreset.DEBUG), player);
		if (strong) {
			AABB area = AABB.ofSize(player.getBoundingBox().getCenter().subtract(gust), factor/2, factor/2, factor/2).expandTowards(gust.scale(factor)); //player.getBoundingBox().inflate(2).expandTowards(gust);
			for (LivingEntity ent : player.level().getEntitiesOfClass(LivingEntity.class, area, ent -> true)) {
				if (ent instanceof ServerPlayer plr) {
					NetInit.toClient(new ModifyPlayerVelocityPacket(gust, ModifyPlayerVelocityPacket.VecOp.ADD), plr);
				} else {
					ent.setDeltaMovement(ent.getDeltaMovement().add(gust));
				}
			}
		} else NetInit.toClient(new ModifyPlayerVelocityPacket(gust, ModifyPlayerVelocityPacket.VecOp.ADD), player);
		//for (Player plr2 : player.level().players()) {
		//	ServerPlayer plr = (ServerPlayer)plr2;
		//	if (plr.blockPosition().closerToCenterThan(area.getCenter(), 256d)) {
		//		//NetworkInit.toClient(new GustParticlePacket((byte)(factor/2), area.getCenter(), gust), plr);
		//	}
		//}
		if (strong) player.level().playSound(null, player.blockPosition(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, factor, 10);
	}
	
	private HitResult swrgSuperSmite(Player player, Level level) {
		Vec3 pos1 = player.getEyePosition();
		Vec3 ray = player.getLookAngle().scale(120);
		Vec3 pos2 = pos1.add(ray);
		// FIXME i cba right now but entity smite works through walls and that shouldnt be a thing
		HitResult hitRes = ProjectileUtil.getEntityHitResult(player, pos1, pos2, AABB.ofSize(pos1, 0.05, 0.05, 0.05).expandTowards(ray).inflate(2), this::canBeSmitten, 0);
		if (hitRes != null && hitRes.getType() == HitResult.Type.ENTITY) {
			Entity ent = ((EntityHitResult)hitRes).getEntity();
			for (int i = 0; i < (level.isThundering() ? 10 : 1) ; i++) {
				EntUtil.smite(level, ent.position(), (ServerPlayer)player, true);
				ent.setRemainingFireTicks(160);
			}
			level.playSound(null, ent.blockPosition(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1, 1);
			// ding sound effect because cool
			((ServerPlayer)player).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
			ent.invulnerableTime = 0;
			ent.hurt(EntityInit.dmg(DamageTypes.LIGHTNING_BOLT, level), level.isThundering() ? 81 : 9);
		} else {
			hitRes = PlayerUtil.getBlockLookingAtPE(player, 120);
			if (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK) {
				for (int i = 0; i < (level.isThundering() ? 10 : 1) ; i++) {
					EntUtil.smite(level, hitRes.getLocation(), (ServerPlayer)player, false);
				}
				level.playSound(null, BlockPos.containing(hitRes.getLocation()), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1, 1);
			}
		}
		return hitRes;
	}
	
	private boolean canBeSmitten(Entity ent) {
		return ent instanceof LivingEntity
				&& !EntUtil.isInvincible(ent);
	}

	/*
	 * This rune doesnt itself do much with a tool,
	 * but makes whatever major rune it is applied with much more powerful
	 */
	
	@Override
	public boolean isEnchantable() {
		return false;
	}

	@Override
	public boolean hasToolAbility() {
		return false;
	}

	@Override
	public boolean isMajorToolRune() {
		return false;
	}

	@Override
	public boolean toolAbility(ItemStack stack, ToolStyle style, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

}
