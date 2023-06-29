package com.quartzshard.aasb.common.item.equipment.trinket.rune.shape;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.entity.projectile.MustangProjectile;
import com.quartzshard.aasb.common.item.equipment.trinket.CharmItem;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.PlayerHelper;
import com.quartzshard.aasb.util.ProjectileHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID)
public class FireRune extends TrinketRune {

	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		if (player.level.isRainingAt(player.blockPosition())) {
			player.level.playSound(null, player, SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.5f, 2f);
			return true;
		} else {
			if (strong) {
				//EntityManaBurst burst = new EntityManaBurst(player);
				MustangProjectile burst = new MustangProjectile(level, player);

				float motionModifier = 14f;

				//burst.setColor(0xFF4000);
				//burst.setMana(1);
				//burst.setStartingMana(1);
				//burst.setMinManaLoss(1);
				//burst.setManaLossPerTick(0f);
				//burst.setGravity(0F);
				burst.setDeltaMovement(burst.getDeltaMovement().scale(motionModifier));
				//burst.setWarped(true);
				//burst.setSourceLens(stack);
				//if (player.isUnderWater() || burst.isUnderWater()) {
				//	// imperceptibly different color
				//	// used as a "spawnedUnderwater" flag
				//	burst.setColor(0xFF4100);
				//}
				
				player.level.addFreshEntity(burst);
				PlayerHelper.coolDown(player, stack.getItem(), 30);
				return true;
			}
		}
		for (int i = 0; i < 5; i++) {
			ProjectileHelper.fireball(level, player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(2)), player);
		}
		level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 1);
		PlayerHelper.coolDown(player, stack.getItem(), 3);
		return true;
	}

	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		InteractionHand hand = player.getItemInHand(InteractionHand.OFF_HAND) == stack ?
				InteractionHand.OFF_HAND :
				InteractionHand.MAIN_HAND;
		boolean didDo = Items.LAVA_BUCKET.use(level, player, hand).getResult().consumesAction();
		if (didDo) {
			level.playSound(null, player.blockPosition(), SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1, 1);
			PlayerHelper.coolDown(player, stack.getItem(), 20);
			return true;
		}
		return false;
	}

	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// handled elsewhere (fire immunity)
		return false;
	}
	
	@SubscribeEvent
	public static void handleFireImmunity(LivingAttackEvent event) {
		Entity ent = event.getEntity();
		if (ent instanceof Player player) {
			DamageSource source = event.getSource();
			if (source.isFire()) {
				ItemStack[] toCheck = {
						player.getItemBySlot(EquipmentSlot.MAINHAND),
						player.getItemBySlot(EquipmentSlot.OFFHAND)
				};
				for (ItemStack stack : toCheck) {
					if (stack.getItem() instanceof CharmItem charm && charm.hasAnyRune(stack)) {
						if (charm.getRune(stack, true) instanceof FireRune
								|| charm.getRune(stack, false) instanceof FireRune) {
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
	

}
