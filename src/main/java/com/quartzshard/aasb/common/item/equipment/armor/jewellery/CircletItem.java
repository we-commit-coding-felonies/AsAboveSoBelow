package com.quartzshard.aasb.common.item.equipment.armor.jewellery;

import java.util.List;
import com.quartzshard.aasb.api.item.bind.ICanHeadMode;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.MathUtil;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.RenderUtil;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CircletItem extends JewelleryArmorItem implements ICanHeadMode {
	public CircletItem(Properties props) {
		super(Type.HELMET, props);
	}
	
	public static final String TAG_SIGHT = "ClairvoyanceEnabled";
	
	@Override
	public void onArmorTick(ItemStack stack, @NotNull Level level, Player player) {
		// TODO: COST
		if (sightEnabled(stack)) {
			
			// xray
			double range;
			boolean client = level.isClientSide;
			if (client) {
				// getEffectiveRenderDistance() is radius in chunks, so we convert to diameter in blocks
				range = ClientUtil.mc().level.getServerSimulationDistance() * 32;
				//System.out.println(range);
			} else {
				range = ((ServerLevel)level).getServer().getPlayerList().getViewDistance() * 32;
			}
			AABB box = AABB.ofSize(player.getEyePosition(), range, range, range);
			//box = AABB.ofSize(new Vec3(2326,64,455), range, range, range);
			//AABB box = AABB.ofSize(player.getEyePosition(), range/4, range/4, range/4).expandTowards(player.getLookAngle().scale(range));
			//box = box.move(player.getLookAngle().scale(range/2)); // move the box forward so it doesnt glow things behind
			if ((false /*|| DebugCfg.HITBOX_CLIENT.get()) && player.level.isClientSide*/)) {
				RenderUtil.drawAABBWithParticles(box, ParticleTypes.ELECTRIC_SPARK, 0.5, (ClientLevel)level, false, true);
			}
			if (level instanceof ServerLevel lvl) {
				List<LivingEntity> haters = player.level().getEntitiesOfClass(LivingEntity.class, box, (ent) -> hatesSeer(ent, player));
				for (LivingEntity ent : haters) {
					// this shouldnt be necessary anymore
					//if (ent.is(player)) continue;

					if (ent instanceof EnderMan man) {
						if (lvl.dimension() == Level.END || man.hasLineOfSight(player)) {
							// get mad!
							// we dont tp in end because lag
							man.setBeingStaredAt();
							man.setTarget(player);
						} else {
							RandomSource r = ent.getRandom();
							if (r.nextInt(3) == 0) {
								float pitch = Math.min(1.3f, ((r.nextFloat() - r.nextFloat()) * 0.4f + 1)*2);
								lvl.playSound(null, man.blockPosition(), SoundEvents.ENDERMAN_SCREAM, man.getSoundSource(), 1, pitch);
								man.playSound(SoundEvents.ENDERMAN_SCREAM, 1, pitch);
								EntUtil.attemptRandomTeleport(ent);
							}
						}
					}
					//if (applied >= max) break;
					//MobEffectInstance potion = ent.getEffect(MobEffects.GLOWING);
					//if (potion == null || potion.getDuration() <= 2) {
					//	ent.addEffect(new MobEffectInstance(MobEffects.GLOWING, time, 0, true, false));
					//	applied++;
					//}
				}
			}
			
			// night vision
			if (player.hasEffect(MobEffects.NIGHT_VISION)) {
				MobEffectInstance nv = player.getEffect(MobEffects.NIGHT_VISION);
				if (nv.getDuration() > 210) {
					return;
				}
			}
			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
		}
	}
	
	private static boolean hatesSeer(LivingEntity ent, Player player) {
		if (ent.isAlive() && ent instanceof EnderMan man) {
			LivingEntity target = man.getTarget();
			if (!(player.isCreative() || player.isSpectator())
					&& (target == null || !target.is(player))) {
				@NotNull Vec3 lookVec = player.getLookAngle();
				Vec3 betweenVec = man.position().subtract(player.position()).normalize();
				return MathUtil.angleBetweenDeg(lookVec, betweenVec) < (player.isScoping() ? 11 : 110);
			}
		}
		return false;
	}
	
	public static boolean canBeXrayd(LivingEntity ent) {
		if (!ent.isAlive())
			return false;
		ItemStack helmet = ent.getItemBySlot(EquipmentSlot.HEAD);
		if (helmet.getItem() instanceof CircletItem circlet && circlet.sightEnabled(helmet))
			return false;
		if (ent instanceof Player plr && (plr.isCreative() || plr.isSpectator()))
			return false;
		return true;
	}
	
	@Override
	public boolean onPressedHeadMode(ItemStack stack, @NotNull ServerPlayer player, ServerLevel level) {
		boolean see = sightEnabled(stack);
		setSight(stack, !see);
		if (see) {
			MobEffectInstance nv = player.getEffect(MobEffects.NIGHT_VISION);
			if (nv != null
				&& (210 >= nv.getDuration() || nv.getDuration() > 220) ) {
				player.removeEffect(MobEffects.NIGHT_VISION);
			}
		}
		return true;
	}
	
	public boolean sightEnabled(ItemStack stack) {
		return NBTUtil.getBoolean(stack, TAG_SIGHT, true);
	}
	
	public void toggleSight(ItemStack stack) {
		setSight(stack, !sightEnabled(stack));
	}
	
	public void setSight(ItemStack stack, boolean state) {
		NBTUtil.setBoolean(stack, TAG_SIGHT, state);
	}

}
