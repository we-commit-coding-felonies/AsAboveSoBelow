package com.quartzshard.aasb.common;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.shape.FireRune;
import com.quartzshard.aasb.api.item.IDigStabilizer;
import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.api.item.IRuneable.ItemAbility;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.AmuletItem;
import com.quartzshard.aasb.data.tags.DmgTP;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;

/**
 * some misc events that dont really have a better place to be
 */
@Mod.EventBusSubscriber(modid = AASB.MODID)
public class Events {
	
	@SubscribeEvent
	public static void staticSpeedBreakerHandler(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();
		if (stack.getItem() instanceof IDigStabilizer tool) {
			Level level = player.level();
			BlockState state = event.getState();
			int breakTicks = tool.blockBreakSpeedInTicks(stack, state);
			if (event.getPosition().isPresent()) {
				float blockStrength = event.getState().getDestroySpeed(level, event.getPosition().get());
				if (breakTicks > 0 && blockStrength >= 0) {
					if (breakTicks > 1 && blockStrength != 0) {
						event.setNewSpeed(32f / (breakTicks/blockStrength));
					} else {
						event.setNewSpeed(Float.POSITIVE_INFINITY);
					}
				}
			}
		}
	}
	
	
	/** what
	 * <p>
	 * i agree, previous me. what?
	 * <p>
	 * i cleaned it up dont worry :3
	 * */
	@SubscribeEvent
	public static void entityDamagedHandler(LivingAttackEvent event) {
		Entity ent = event.getEntity();
		DamageSource dmgSrc = event.getSource();
		
		
		if (ent instanceof Player player) {
			if (dmgSrc.is(DamageTypeTags.IS_FIRE) && !dmgSrc.is(DmgTP.IS_STRONG_FIRE)) {
				IItemHandler curioInv = PlayerUtil.getCuriosInv(player);
				if (curioInv != null) {
					for (int i = 0; i < curioInv.getSlots(); i++) {
						ItemStack stack = curioInv.getStackInSlot(i);
						if (stack.getItem() instanceof IRuneable item && item.getAbility(stack) == ItemAbility.PASSIVE) {
							for (Rune rune : item.getInscribedRunes(stack)) {
								if (rune instanceof FireRune && rune.passiveEnabled(stack)) {
									if (!dmgSrc.is(DamageTypes.LAVA) || item.runesAreStrong(stack)) {
										event.setCanceled(true);
										return;
									}
								}
							}
						}
					}
				}
			}
			ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
			if (stack.getItem() instanceof AmuletItem amulet) {
				amulet.tryShield(event, stack);
			}
		}
	}
}
