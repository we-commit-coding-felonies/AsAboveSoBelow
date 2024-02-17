package com.quartzshard.aasb.common.item;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.init.object.ItemInit;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AASB.MODID)
public class ElixirOfLifeItem extends Item {
	
	public ElixirOfLifeItem(Properties props) {
		super(props);
	}

	@Override
	public SoundEvent getDrinkingSound() {
		return FxInit.SND_ELIXIR.get();
	}
	@Override
	public SoundEvent getEatingSound() {
		return FxInit.SND_ELIXIR.get();
	}
	
	@Override
	public int getUseDuration(ItemStack stack) {
		return 40;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}
	

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		Player player = entity instanceof Player ? (Player)entity : null;
		if (player instanceof ServerPlayer plr) {
			CriteriaTriggers.CONSUME_ITEM.trigger(plr, stack);
		}
		if (!level.isClientSide) {
			grantImmortality(entity);
		}
		if (player != null) {
			player.awardStat(Stats.ITEM_USED.get(this));
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
		}
		if (player == null || !player.getAbilities().instabuild) {
			if (stack.isEmpty()) {
				return new ItemStack(ItemInit.FLASK_GOLD_EMPTY.get());
			}
			if (player != null) {
				player.getInventory().add(new ItemStack(ItemInit.FLASK_GOLD_EMPTY.get()));
			}
		}
		entity.gameEvent(GameEvent.DRINK);
		return stack;
	}

	// 
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	// https://github.com/TeamTwilight/twilightforest/blob/52863f0609121438b0524a6d97157972b3f90366/src/main/java/twilightforest/events/CharmEvents.java#L56
	public static void onEntityDied(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();

		//ensure our player is real and in survival before attempting anything
		if (entity.level().isClientSide()
				|| !(entity instanceof Player player)
				|| entity instanceof FakePlayer
				|| player.isCreative()
				|| player.isSpectator())
			return;
		
		ItemStack main = player.getMainHandItem(), off = player.getOffhandItem();
		boolean didDo = false;
		if (main.getItem() instanceof ElixirOfLifeItem) {
			main.shrink(1);
			didDo = true;
		} else if (off.getItem() instanceof ElixirOfLifeItem) {
			off.shrink(1);
			didDo = true;
		}
		
		if (didDo) {
			grantImmortality(entity);
			event.setCanceled(true);
		}
	}
	
	public static void grantImmortality(LivingEntity entity) {
		if (!entity.level().isClientSide) {
			entity.level().playSound(null, entity.blockPosition(), SoundEvents.TOTEM_USE, entity.getSoundSource());
			entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 9)); // really healthy...
			entity.setHealth(entity.getMaxHealth());
			entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 5)); // and invincible!!!
			entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0)); // ...with a catch
			if (entity.hasEffect(EntityInit.BUFF_TRANSMUTING.get()))
				entity.removeEffect(EntityInit.BUFF_TRANSMUTING.get());
			else
				entity.addEffect(new MobEffectInstance(EntityInit.BUFF_TRANSMUTING.get(), 100));
		}
	}
}
