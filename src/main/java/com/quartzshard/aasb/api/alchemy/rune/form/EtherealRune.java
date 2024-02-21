package com.quartzshard.aasb.api.alchemy.rune.form;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.net.client.CreateLoopingSoundPacket;
import com.quartzshard.aasb.net.client.CreateLoopingSoundPacket.LoopingSound;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket;
import com.quartzshard.aasb.net.client.DrawParticleLinePacket.LineParticlePreset;
import com.quartzshard.aasb.net.client.FreecamPacket;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.EntUtil.Projectiles;
import com.quartzshard.aasb.util.EntUtil.Projectiles.ArrowOptions;
import com.quartzshard.aasb.util.EntUtil.Projectiles.ArrowType;
import com.quartzshard.aasb.util.EntUtil.Projectiles.ShootContext;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WayUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.items.IItemHandler;

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
	public boolean combatAbility(ItemStack stack, ServerPlayer player, @NotNull ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO: COST
		if (strong) {
			if (hasTrackedArrow(stack)) {
				SentientArrowEntity arrow = getTrackedArrow(stack, level);
				if (arrow == null) resetTrackedArrow(stack); // TODO sometimes the arrow seems to get a bit stuck in unloaded chunks and doesnt despawn? find a fix for this
				else if (!level.isPositionEntityTicking(arrow.blockPosition())) {
					arrow.recallToPlayer(player);
					return true;
				}
				else {
					sentientArrowControl(getTrackedArrow(stack, level), player);
					PlayerUtil.coolDown(player, stack.getItem(), 15);
					return false; // we return false here to prevent arm swing and snap sound
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
			long amount = /*Math.min(*/up ? 14 : 28;//, plrEmc/Archangel.SMART.get());
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
	 * normal: teleport <br>
	 * strong: astral projection
	 */
	@Override
	public boolean utilityAbility(@NotNull ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			if (strong) {
				// TODO cost, either high upfront or constant while active
				NetInit.toClient(new FreecamPacket(true), player);
				return true;
			}
			@NotNull BlockHitResult hitRes = PlayerUtil.getTargetedBlock(player, 64);
			if (hitRes.getType() != BlockHitResult.Type.MISS) {
				BlockPos c = hitRes.getBlockPos().relative(hitRes.getDirection());
				@NotNull EntityTeleportEvent event = new EntityTeleportEvent(player, c.getX(), c.getY(), c.getZ());
				if (!MinecraftForge.EVENT_BUS.post(event)) {
					if (player.isPassenger()) {
						player.stopRiding();
					}
					player.teleportTo(event.getTargetX()+0.5, event.getTargetY()+0.5, event.getTargetZ()+0.5);
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1, 2);
					player.fallDistance = 0;
					PlayerUtil.coolDown(player, stack.getItem(), 10);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * normal: autorepair with XP, maybe other mods googles? <br>
	 * strong: autorepair with Way
	 */
	//@Override
	//public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
	//	return false;
	//}

	@Override
	public void tickPassive(ItemStack stack, @NotNull ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		long playerXp = PlayerUtil.Xp.getXp(player);
		boolean doWay = strong && WayUtil.hasWay(player);
		if (playerXp > 0 || doWay) {
			Optional<IItemHandler> oiih = player.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
			if (oiih.isPresent()) {
				IItemHandler inv = oiih.get();			
				for (int i = 0; i < inv.getSlots(); i++) {
					@NotNull ItemStack repairTarget = inv.getStackInSlot(i);
					if (repairTarget.isDamageableItem() && repairTarget.isDamaged()) {
						repairTarget.setDamageValue(repairTarget.getDamageValue() - 1);
						if (doWay)
							WayUtil.consumeAvaliableWay(player, 8);
						else PlayerUtil.Xp.extractXp(player, 1);
					}
				}
			}
		}
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
	
	public void changeTrackedArrow(@NotNull ItemStack stack, SentientArrowEntity arrow) {
		NBTUtil.setInt(stack, TK_ARROWTRACKER, arrow.getId());
	}
	
	public void resetTrackedArrow(ItemStack stack) {
		NBTUtil.setInt(stack, TK_ARROWTRACKER, -1);
	}
	
	public boolean sentientArrowControl(@NotNull SentientArrowEntity arrow, ServerPlayer player) {
		// try redirecting the arrow
		boolean foundTarget = arrow.attemptManualRetarget();
		player.level().playSound(null, player, FxInit.SND_WHISTLE.get(), SoundSource.PLAYERS, 1, AASB.RNG.nextFloat(0.1f, 2f));
		if (foundTarget) {
			for (Player nplr : player.level().players()) {
				ServerPlayer plr = (ServerPlayer) nplr;
				@Nullable Entity target = arrow.getTarget();
				@NotNull BlockPos pos = plr.blockPosition();
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
				@NotNull BlockPos pos = plr.blockPosition();
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
