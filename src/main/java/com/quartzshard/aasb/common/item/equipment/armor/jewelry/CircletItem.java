package com.quartzshard.aasb.common.item.equipment.armor.jewelry;

import java.util.List;

import com.quartzshard.aasb.api.item.bind.ICanHeadMode;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.config.DebugCfg;
import com.quartzshard.aasb.util.BoxHelper;
import com.quartzshard.aasb.util.EntityHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CircletItem extends JewelryArmor implements ICanHeadMode {
	public CircletItem(Properties props) {
		super(EquipmentSlot.HEAD, props);
	}
	
	public static final String TAG_SIGHT = "Clairvoyance";
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		// TODO: COST
		if (sightEnabled(stack)) {
			
			// xray
			double range = 64;
			AABB box = AABB.ofSize(player.getEyePosition(), range/4, range/4, range/4).expandTowards(player.getLookAngle().scale(range));
			box = box.move(player.getLookAngle().scale(range/8)); // move the box forward so it doesnt glow things behind
			if (DebugCfg.HITBOX_CLIENT.get() && player.level.isClientSide) {
				BoxHelper.drawAABBWithParticles(box, ParticleTypes.ELECTRIC_SPARK, 0.2, (ClientLevel)level, false, true);
			}
			List<LivingEntity> mobs = player.level.getEntitiesOfClass(LivingEntity.class, box, this::canBeXrayd);
			int applied = 0;
			for (LivingEntity ent : mobs) {
				if (ent.is(player)) continue;
				
				int time = 12;
				if ( ent instanceof EnderMan man && !(player.isCreative() || player.isSpectator()) ) {
					LivingEntity target = man.getTarget();
					boolean alreadyTargeting = target != null && target.is(player);
					boolean didAnnoy = false;
					if (!alreadyTargeting && man.hasLineOfSight(player)) {
						man.setTarget(player);
						//didAnnoy = true;
					} else {
						if (!alreadyTargeting && level.dimension() == Level.END) {
							// because endermen spawn so frequently in the end
							// we dont do teleport attempts, its too easy to cause lag
							man.setTarget(player);
							//didAnnoy = true;
						} else if (!alreadyTargeting && ent.getRandom().nextInt(2) != 0) {
							EntityHelper.attemptRandomTeleport(ent);
							// obstructed endermen get shorter duration
							// make them try to teleport more often
							time = 7;
							didAnnoy = true;
						}
					}
					if (didAnnoy) {
						// agony
						man.playSound(SoundEvents.ENDERMAN_SCREAM, 1, (ent.getRandom().nextFloat() - ent.getRandom().nextFloat()) * 0.2f + 1);
					}
				}
				//if (applied >= max) break;
				MobEffectInstance potion = ent.getEffect(MobEffects.GLOWING);
				if (potion == null || potion.getDuration() <= 2) {
					ent.addEffect(new MobEffectInstance(MobEffects.GLOWING, time, 0, true, false));
					applied++;
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
	
	private boolean canBeXrayd(LivingEntity ent) {
		if (!ent.isAlive())
			return false;
		ItemStack helmet = ent.getItemBySlot(EquipmentSlot.HEAD);
		if (helmet.getItem() instanceof CircletItem)
			return false;
		if (ent instanceof Player plr && (plr.isCreative() || plr.isSpectator()))
			return false;
		return !(ent.hasEffect(MobEffects.GLOWING) && ent.getEffect(MobEffects.GLOWING).getDuration() > 2);
	}
	
	@Override
	public boolean onPressedHeadMode(ItemStack stack, ServerPlayer player, ServerLevel level) {
		toggleSight(stack);
		return true;
	}
	
	public boolean sightEnabled(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, TAG_SIGHT, true);
	}
	
	public void toggleSight(ItemStack stack) {
		setSight(stack, !sightEnabled(stack));
	}
	
	public void setSight(ItemStack stack, boolean state) {
		NBTHelper.Item.setBoolean(stack, TAG_SIGHT, state);
	}

}
