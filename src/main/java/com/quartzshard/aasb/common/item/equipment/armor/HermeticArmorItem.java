package com.quartzshard.aasb.common.item.equipment.armor;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.IRuneableArmor;
import com.quartzshard.aasb.api.item.IWayHolder;
import com.quartzshard.aasb.common.level.WayExplosionDamageCalculator;
import com.quartzshard.aasb.data.tags.DmgTP;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.Colors;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.RenderUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = AASB.MODID)
public class HermeticArmorItem extends ArmorItem implements IRuneableArmor, IWayHolder {
	public static final String
		TK_MUSTDET = "MarkedForDetonation";

	public HermeticArmorItem(Type pType, Properties pProperties) {
		super(HermeticArmorMaterial.MAT, pType, pProperties);
	}

	@Override
	public long getMaxWay(ItemStack stack) {
		return this.hasRune(stack, AlchInit.RUNE_WATER.get()) ? this.runesAreStrong(stack) ? 16384 : 4096 : -1;
	}
	@Override
	public boolean canInsertWay(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getStoredWay(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round((float)getStoredWay(stack) * 13f / (float)getMaxWay(stack));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return Colors.materiaGradient(getEnergySaturation(stack));
	}

	public float getEnergySaturation(ItemStack stack) {
		return (float)getStoredWay(stack)/(float)getMaxWay(stack);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack s, int a, T e, Consumer<T> b) {return 0;}

	@Override
	public void onArmorTick(ItemStack stack, @NotNull Level level, Player player) {
		if (level.isClientSide) return;
		long burnout = getStoredWay(stack);
		int leakTime = runesAreStrong(stack) ? 1 : hasRune(stack, AlchInit.RUNE_EARTH.get()) ? 9 : 3; //Math.round(12f - (9f*getEnergySaturation(stack)));
		if (burnout > 0 && level.getGameTime() % leakTime == 0) {
			setStoredWay(stack, Math.max(0, burnout-1));
			level.playSound(null, player.blockPosition(), FxInit.SND_WAY_LEAK.get(), player.getSoundSource(), 1f, 1);
		}
	}


	/**
	 * damage absorb handling
	 * @param event
	 */
	@SubscribeEvent
	public static void spongeDamage(LivingDamageEvent event) {
		float damage = event.getAmount();
		if (damage > 0) {
			DamageSource source = event.getSource();
			if (!source.is(DmgTP.BYPASSES_DMG_SPONGE) && !source.isCreativePlayer() // things that bypass
				&& !(source.getEntity() == event.getEntity() && source.is(EntityInit.DMG_WAYBOMB))) { // ourself exploding bypasses so we dont loop
				LivingEntity entity = event.getEntity();
				long canStore = 0;
				Map<ItemStack,Long> worn = new HashMap<>();
				for (ItemStack stack : entity.getArmorSlots()) {
					if (!stack.isEmpty()
							&& stack.getItem() instanceof HermeticArmorItem drItem
							&& drItem.getMaxWay(stack) != -1) {
						long l = drItem.getMaxWay(stack) - drItem.getStoredWay(stack);
						worn.put(stack, l);
						canStore += l;
						continue;
					}
					return;
				}
				long wayVal = (long)Math.ceil(16f * (Math.sqrt(damage)));
				if (canStore > wayVal) {
					// we have enough space to sponge the incoming damage
					long perPiece = wayVal;// / 4;
					for (Map.Entry<ItemStack,Long> e : worn.entrySet()) {
						ItemStack stack = e.getKey();
						HermeticArmorItem item = (HermeticArmorItem)stack.getItem();
						System.out.println(item.getStoredWay(stack) + perPiece);
						item.setStoredWay(stack,item.getStoredWay(stack) + perPiece);
					}
					event.setCanceled(true);
				} else {
					// not enough capacity, time to explode
					DamageSource dmgSrc = EntityInit.dmg(EntityInit.DMG_WAYBOMB, entity.level(), entity, entity);
					DamageSource dmgSrcSelf = EntityInit.dmg(EntityInit.DMG_WAYBOMB_ENV, entity.level());
					//ItemStack stack = this.getItem();
					@NotNull WayExplosionDamageCalculator nukeCalc = new WayExplosionDamageCalculator(15);
					Vec3 c = entity.position();
					entity.hurt(dmgSrcSelf, Float.MAX_VALUE);
					entity.level().explode(entity, dmgSrc, nukeCalc, c.x, c.y, c.z, 15, entity.isOnFire(), Level.ExplosionInteraction.BLOCK, true);
					for (Map.Entry<ItemStack,Long> e : worn.entrySet()) {
						ItemStack stack = e.getKey();
						HermeticArmorItem item = (HermeticArmorItem)stack.getItem();
						item.setStoredWay(stack,0);
					}
				}
			}

			/*
			if (totalDr > 0) {
				if (totalDr >= 1) {
					event.setCanceled(true);
				} else {
					event.setAmount(damage * (1f-totalDr));
				}
				entity.level().playSound(null, entity, SoundEvents.SHIELD_BLOCK, entity.getSoundSource(), Math.min(1, totalDr), 0.5f);
				for (Entry<ItemStack, Float> absorber : absorbList.entrySet()) {
					ItemStack stack = absorber.getKey();
					float absorbed = absorber.getValue()*event.getAmount();
					stack.hurtAndBreak(Mth.ceil(absorbed), entity, ent -> {
						armorBreak(stack, ent);
					});
				}
			}*/
		}
	}

	public static class HermeticArmorMaterial implements ArmorMaterial {
		public static final HermeticArmorMaterial MAT = new HermeticArmorMaterial();
		@Override
		public int getDefenseForType(Type slot) {
			if (slot == null) return 0;
			switch(slot) {
				case HELMET:
					return 3;
				case CHESTPLATE:
					return 9;
				case LEGGINGS:
					return 6;
				case BOOTS:
					return 3;
				default:
					return 0;
			}
		}
		@Override
		public int getDurabilityForType(Type type) {return Integer.MAX_VALUE;}
		@Override
		public int getEnchantmentValue() {return 0;}
		@NotNull
		@Override
		public SoundEvent getEquipSound() {return SoundEvents.ARMOR_EQUIP_GENERIC;}
		@NotNull
		@Override
		public Ingredient getRepairIngredient() {return Ingredient.EMPTY;}
		@NotNull
		@Override
		public String getName() {return AASB.rl("hermetic").toString();}
		@Override
		public float getToughness() {return 3;}
		@Override
		public float getKnockbackResistance() {return 0;}
	}
}
