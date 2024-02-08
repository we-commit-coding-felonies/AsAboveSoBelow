package com.quartzshard.aasb.api.alchemy.rune.form;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.net.client.CreateLoopingSoundPacket;
import com.quartzshard.aasb.net.client.CreateLoopingSoundPacket.LoopingSound;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket.LineParticlePreset;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.EntUtil.Projectiles;
import com.quartzshard.aasb.util.EntUtil.Projectiles.ArrowOptions;
import com.quartzshard.aasb.util.EntUtil.Projectiles.ArrowType;
import com.quartzshard.aasb.util.EntUtil.Projectiles.ShootContext;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EtherealRune extends FormRune {
	public static final String TK_ARROWTRACKER = "SentientArrowTracker";

	public EtherealRune() {
		super(AASB.rl("ethereal"));
	}

	/**
	 * normal: smart arrow swarm <br>
	 * strong: sentient arrow
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		if (strong) {
			if (hasTrackedArrow(stack)) {
				SentientArrowEntity arrow = getTrackedArrow(stack, player.level());
				if (arrow == null) resetTrackedArrow(stack);
				else {
					sentientArrowControl(getTrackedArrow(stack, level), player);
					PlayerUtil.coolDown(player, stack.getItem(), 15);
					return true;
				}
			}
			
			//if (plrEmc >= Archangel.HOMING.get()) {
			SentientArrowEntity arrow = (SentientArrowEntity) Projectiles.shootArrow(1, ArrowType.SENTIENT,
						new ShootContext(player.level(), player),
						new ArrowOptions(1, 1, 0, (byte)0, false, Pickup.DISALLOWED)).get(0);
				changeTrackedArrow(stack, arrow);
				for (ServerPlayer plr : ((ServerLevel)player.level()).players()) {
					NetInit.toClient(new CreateLoopingSoundPacket(LoopingSound.SENTIENT_WHISPERS, arrow.getId()), plr);
				}
				//EmcHelper.consumeAvaliableEmc(player, Archangel.HOMING.get());
				PlayerUtil.coolDown(player, stack.getItem(), 15);
			//}
		} else {
			boolean up = player.onGround();
			long amount = /*Math.min(*/up ? 28 : 56;//, plrEmc/Archangel.SMART.get());
			ShootContext ctx = up ?
					new ShootContext(level, player, new Vec3(-90, 0, 0)) :
					new ShootContext(level, player);
			Projectiles.shootArrow((int)amount, ArrowType.SMART, ctx,
					new ArrowOptions(1, 0.5f, up ? 60 : 300, (byte)0, false, Pickup.DISALLOWED));
			PlayerUtil.coolDown(player, stack.getItem(), 20);
		}
		return true;
	}

	/**
	 * normal: tele through wall <br>
	 * strong: astral projection
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: autorepair with XP, maybe other mods googles? <br>
	 * strong: autorepair with Way
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		// TODO Auto-generated method stub
	}
	
	
	public boolean hasTrackedArrow(ItemStack stack) {
		return NBTUtil.getInt(stack, TK_ARROWTRACKER, -1) != -1;
	}
	
	@Nullable
	public SentientArrowEntity getTrackedArrow(ItemStack stack, Level level) {
		Entity tracked = level.getEntity(NBTUtil.getInt(stack, TK_ARROWTRACKER, -1));
		if (tracked != null && tracked instanceof SentientArrowEntity arrow) {
			return arrow;
		}
		return null;
	}
	
	public void changeTrackedArrow(ItemStack stack, SentientArrowEntity arrow) {
		NBTUtil.setInt(stack, TK_ARROWTRACKER, arrow.getId());
	}
	
	public void resetTrackedArrow(ItemStack stack) {
		NBTUtil.setInt(stack, TK_ARROWTRACKER, -1);
	}
	
	public boolean sentientArrowControl(SentientArrowEntity arrow, ServerPlayer player) {
		// try redirecting the arrow
		boolean foundTarget = arrow.attemptManualRetarget();
		player.level().playSound(null, player, FxInit.SND_WHISTLE.get(), SoundSource.PLAYERS, 1, AASB.RNG.nextFloat(0.1f, 2f));
		if (foundTarget) {
			for (Player nplr : player.level().players()) {
				ServerPlayer plr = (ServerPlayer) nplr;
				Entity target = arrow.getTarget();
				BlockPos pos = plr.blockPosition();
				boolean nearOwner = pos.closerToCenterThan(player.getEyePosition(), 128);
				// owner -> arrow communicate
				if (nearOwner || pos.closerToCenterThan(arrow.getBoundingBox().getCenter(), 128)) {
					NetInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_COMMUNICATE), plr);
				}
				// owner -> target tracer
				if (nearOwner || pos.closerToCenterThan(target.getBoundingBox().getCenter(), 128)) {
					NetInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), target.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
		} else {
			// returning to owner
			for (ServerPlayer plr : ((ServerLevel)player.level()).players()) {
				BlockPos pos = plr.blockPosition();
				boolean nearOwner = pos.closerToCenterThan(player.getEyePosition(), 128);
				// arrow -> owner tracer
				if (nearOwner || pos.closerToCenterThan(arrow.getBoundingBox().getCenter(), 128)) {
					NetInit.toClient(new DrawParticleLinePacket(player.getEyePosition(), arrow.getBoundingBox().getCenter(), LineParticlePreset.SENTIENT_RETARGET), plr);
				}
			}
		}
		return foundTarget;
	}

}
