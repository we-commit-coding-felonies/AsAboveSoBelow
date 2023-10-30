package com.quartzshard.aasb.common.item.equipment.trinket.rune.form;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.common.entity.projectile.SentientArrow;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.common.network.client.CreateLoopingSoundPacket;
import com.quartzshard.aasb.common.network.client.CreateLoopingSoundPacket.LoopingSound;
import com.quartzshard.aasb.common.network.client.DrawParticleLinePacket;
import com.quartzshard.aasb.common.network.client.DrawParticleLinePacket.LineParticlePreset;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.NBTHelper;
import com.quartzshard.aasb.util.PlayerHelper;
import com.quartzshard.aasb.util.ProjectileHelper;
import com.quartzshard.aasb.util.ProjectileHelper.ArrowOptions;
import com.quartzshard.aasb.util.ProjectileHelper.ArrowType;
import com.quartzshard.aasb.util.ProjectileHelper.ShootContext;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EtherealRune extends TrinketRune {
	public static final String TAG_ARROWTRACKER = "SentientArrowTracker";
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		if (strong) {
			if (hasTrackedArrow(stack)) {
				SentientArrow arrow = getTrackedArrow(stack, player.level);
				if (arrow == null) resetTrackedArrow(stack);
				else {
					sentientArrowControl(getTrackedArrow(stack, level), player);
					PlayerHelper.coolDown(player, stack.getItem(), 15);
					return true;
				}
			}
			
			//if (plrEmc >= Archangel.HOMING.get()) {
				SentientArrow arrow = (SentientArrow) ProjectileHelper.shootArrow(1, ArrowType.SENTIENT,
						new ShootContext(player.level, player),
						new ArrowOptions(1, 1, 0, (byte)0, false, Pickup.DISALLOWED)).get(0);
				changeTrackedArrow(stack, arrow);
				for (ServerPlayer plr : ((ServerLevel)player.level).players()) {
					AASBNet.toClient(new CreateLoopingSoundPacket(LoopingSound.SENTIENT_WHISPERS, arrow.getId()), plr);
				}
				//EmcHelper.consumeAvaliableEmc(player, Archangel.HOMING.get());
				PlayerHelper.coolDown(player, stack.getItem(), 15);
			//}
		} else {
			boolean up = player.isOnGround();
			long amount = /*Math.min(*/up ? 28 : 56;//, plrEmc/Archangel.SMART.get());
			ShootContext ctx = up ?
					new ShootContext(level, player, new Vec3(-90, 0, 0)) :
					new ShootContext(level, player);
			ProjectileHelper.shootArrow((int)amount, ArrowType.SMART, ctx,
					new ArrowOptions(1, 0.5f, up ? 60 : 300, (byte)0, false, Pickup.DISALLOWED));
			PlayerHelper.coolDown(player, stack.getItem(), 20);
		}
		return true;
	}

	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// mind stone
		return false;
	}

	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// mending
		return false;
	}
	
	
	
	public boolean hasTrackedArrow(ItemStack stack) {
		return NBTHelper.Item.getInt(stack, TAG_ARROWTRACKER, -1) != -1;
	}
	
	@Nullable
	public SentientArrow getTrackedArrow(ItemStack stack, Level level) {
		Entity tracked = level.getEntity(NBTHelper.Item.getInt(stack, TAG_ARROWTRACKER, -1));
		if (tracked != null && tracked instanceof SentientArrow arrow) {
			return arrow;
		}
		return null;
	}
	
	public void changeTrackedArrow(ItemStack stack, SentientArrow arrow) {
		NBTHelper.Item.setInt(stack, TAG_ARROWTRACKER, arrow.getId());
	}
	
	public void resetTrackedArrow(ItemStack stack) {
		NBTHelper.Item.setInt(stack, TAG_ARROWTRACKER, -1);
	}
	
	public boolean sentientArrowControl(SentientArrow arrow, ServerPlayer player) {
		// try redirecting the arrow
		boolean foundTarget = arrow.attemptManualRetarget();
		player.level.playSound(null, player, EffectInit.Sounds.WHISTLE.get(), SoundSource.PLAYERS, 1, player.getRandom().nextFloat(0.1f, 2f));
		if (foundTarget) {
			for (ServerPlayer plr : player.getLevel().players()) {
				Entity target = arrow.getTarget();
				BlockPos pos = plr.blockPosition();
				boolean nearOwner = pos.closerToCenterThan(player.getEyePosition(), 128);
				// owner -> arrow communicate
				if (nearOwner || pos.closerToCenterThan(arrow.getBoundingBox().getCenter(), 128)) {
					AASBNet.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_COMMUNICATE), plr);
				}
				// owner -> target tracer
				if (nearOwner || pos.closerToCenterThan(target.getBoundingBox().getCenter(), 128)) {
					AASBNet.toClient(new DrawParticleLinePacket(player.getEyePosition(), target.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
		} else {
			// returning to owner
			for (ServerPlayer plr : ((ServerLevel)player.level).players()) {
				BlockPos pos = plr.blockPosition();
				boolean nearOwner = pos.closerToCenterThan(player.getEyePosition(), 128);
				// arrow -> owner tracer
				if (nearOwner || pos.closerToCenterThan(arrow.getBoundingBox().getCenter(), 128)) {
					AASBNet.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
		}
		return foundTarget;
	}
}