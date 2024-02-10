package com.quartzshard.aasb.common.entity.buff;

import java.util.ArrayList;
import java.util.List;

import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.util.Colors;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TransmutingBuff extends MobEffect {
	
	// potion seller, i am going into battle...
	private static MobEffectInstance[] EFFECTS_OF_DOOM = {
			// Universal
			new MobEffectInstance(MobEffects.ABSORPTION, 60, 4, true, false),				// 0
			new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 4, true, false),				// 1
			new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 3, true, false),		// 2
			new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false),			// 3
			new MobEffectInstance(MobEffects.GLOWING, 60, 0, true, false),					// 4
			new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, true, false),				// 5
			new MobEffectInstance(MobEffects.JUMP, 60, 255, true, false),					// 6
			new MobEffectInstance(MobEffects.LEVITATION, 5, 122, true, false),				// 7
			new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 122, true, false),		// 8
			new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 9, true, false),			// 9
			new MobEffectInstance(MobEffects.REGENERATION, 60, 5, true, false),				// 10
			new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, true, false),				// 11
			new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false),			// 12
			new MobEffectInstance(MobEffects.WEAKNESS, 60, 4, true, false),					// 13
			
			// Player only
			new MobEffectInstance(MobEffects.BAD_OMEN, 60, 233, true, false),				// 14
			new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, true, false),				// 15
			new MobEffectInstance(MobEffects.CONFUSION, 60, 0, true, false),				// 16
			new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 3, true, false),				// 17
			new MobEffectInstance(MobEffects.DIG_SPEED, 60, 3, true, false),				// 18
			new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 60, 0, true, false),			// 19
			new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 60, 233, true, false),	// 20
			new MobEffectInstance(MobEffects.HUNGER, 60, 233, true, false),					// 21
			new MobEffectInstance(MobEffects.LUCK, 60, 233, true, false),					// 22
			new MobEffectInstance(MobEffects.NIGHT_VISION, 60, 0, true, false),				// 23
			new MobEffectInstance(MobEffects.UNLUCK, 60, 233, true, false)					// 24
	};

	public TransmutingBuff() {
		super(MobEffectCategory.NEUTRAL, Colors.PHILOSOPHERS.I);
	}
	
	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<ItemStack>();
		cures.add(new ItemStack(ItemInit.THE_PHILOSOPHERS_STONE.get()));
		return cures;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		entity.invulnerableTime = 0;
		MobEffectInstance effect = entity.getEffect(EntityInit.BUFF_TRANSMUTING.get());
		if (effect != null && entity.isAlive() && isDurationEffectTick(effect.getDuration(), amplifier)) {
			if (amplifier > 0) {
				//entity.addEffect(new MobEffectInstance(EFFECTS_OF_DOOM[3]));
				entity.hurt( EntityInit.dmg(EntityInit.DMG_TRANSMUTE_ENV, entity.level()), Math.max(1, entity.getHealth()/2f) );
				
				// does stuff when effect runs out
				if (effect.getDuration() <= 2) {
					if (entity.getRandom().nextInt(15+amplifier) == 0) {
						// unlucky, effect gets stronger lol
						entity.addEffect(new MobEffectInstance(EntityInit.BUFF_TRANSMUTING.get(), 6+amplifier, amplifier + 1));
					} else {
						entity.curePotionEffects(getCurativeItems().get(0)); // do this so we can apply weaker effect
						if (amplifier > 1) {
							entity.addEffect(new MobEffectInstance(EntityInit.BUFF_TRANSMUTING.get(), amplifier, amplifier - 1));
						} else {
							entity.addEffect(new MobEffectInstance(EntityInit.BUFF_TRANSMUTING.get(), 300*amplifier, 0));
						}
					}
				}
				return;
			}
			int rangeOfEffects = 13+1; // index of last universal, +1 to account for nextInt() being exclusive
			
			if (entity instanceof ServerPlayer player) {
				ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
				/*if (chestplate.getItem() instanceof AmuletItem amulet) {
					//for (int i = 0; i < 13; i++) {
					//	amulet.doLeak(chestplate, player.level, player, 130-amplifier*13);
					//}
				}*/
				rangeOfEffects = EFFECTS_OF_DOOM.length; // if we are a player more effects are avaliable
			}
			
			if (!entity.level().isClientSide()) {
				if (entity.getRandom().nextInt(12) == 0) {
					int amount = entity.getRandom().nextInt(4);
					for (int i = 0; i < amount; i++) {
						MobEffectInstance fx = new MobEffectInstance(EFFECTS_OF_DOOM[entity.getRandom().nextInt(rangeOfEffects)]);
						entity.addEffect(fx);
					}
				}
			}
		} else {
			// removes effects sticking around clientside
			entity.curePotionEffects(getCurativeItems().get(0));
			entity.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration >= 1;
	}
}
