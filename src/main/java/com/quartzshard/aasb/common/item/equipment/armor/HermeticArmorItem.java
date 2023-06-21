package com.quartzshard.aasb.common.item.equipment.armor;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IBurnoutItem;
import com.quartzshard.aasb.api.item.IEnchantmentSynergizer;
import com.quartzshard.aasb.api.item.IShapeRuneItem;
import com.quartzshard.aasb.common.damage.BlockAgnosticDamageCalculator;
import com.quartzshard.aasb.common.damage.source.AASBDmgSrc;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.ColorsHelper;
import com.quartzshard.aasb.util.EntityHelper;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class HermeticArmorItem extends AlchArmor implements IBurnoutItem, IShapeRuneItem, IEnchantmentSynergizer {
	//private static final ItemStack[] DEGRADE_REPLACEMENTS = {
	//		new ItemStack(Items.GOLDEN_BOOTS),
	//		new ItemStack(Items.GOLDEN_LEGGINGS),
	//		new ItemStack(Items.GOLDEN_CHESTPLATE),
	//		new ItemStack(Items.GOLDEN_HELMET)
	//};
	
	/**
	 * weakens with consecutive attacks & regenerates over time
	 * 
	 * @param mat The material of the armor
	 * @param slot The slot the item goes in
	 * @param props The item's properties
	 * @param maxDR The maximum amount of damage reduction this item can provide
	 */
	public HermeticArmorItem(EquipmentSlot slot, Properties props, float maxDr) {
		super(HermeticArmorMaterial.MAT, slot, props, maxDr);
		MinecraftForge.EVENT_BUS.addListener(this::checkDegrade);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		tips.add(new TextComponent(" "));
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_ARMOR_FLAVOR).withStyle(ChatFormatting.UNDERLINE)); // Flavor
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_ARMOR_DESC_1)); //
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_ARMOR_DESC_2)); // info
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_ARMOR_DESC_3)); //
		float dr = getDr(stack, DamageSource.CACTUS)*100;
		Component drText = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(dr)+"%").withStyle(ChatFormatting.GREEN);
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_ARMOR_DR, drText));
		Style style = Style.EMPTY.withColor(getBarColor(stack));
		Component burnoutText = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(getBurnout(stack))).withStyle(style);
		Component maxText = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(getBurnoutMax())).withStyle(ChatFormatting.DARK_RED);
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_ARMOR_BURNOUT, burnoutText, maxText));
	}
	
	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean canBeDepleted() {
		return true;
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return ColorsHelper.covalenceGradient(getBurnoutPercent(stack));
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return  (int)( 13f * getBurnoutPercent(stack) );
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getBurnout(stack) > 0;
	}

	@Override
	public int getBurnoutMax() {
		return (int) (16384f * (getDefense()/30f));
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		int burnout = getBurnout(stack);
		int next = burnout + 8*amount;
		if (next >= getBurnoutMax()) {
			setBurnout(stack, getBurnoutMax());
			NBTHelper.Item.setBoolean(stack, "burnout_overload", true);
		} else {
			setBurnout(stack, burnout + 8*amount);
		}
		return 0;
	}

	@Override
	public double calculateBonus(ItemStack stack) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (level.isClientSide) return;
		int burnout = getBurnout(stack);
		int leakTime = Math.round(12f - (9f*getBurnoutPercent(stack)));
		if (burnout > 0 && level.getGameTime() % leakTime == 0) {
			setBurnout(stack, burnout-1);
			level.playSound(null, entity.blockPosition(), EffectInit.Sounds.WAY_LEAK.get(), entity.getSoundSource(), 1f, 1);
		}
	}
	
	/**
	 * catastrophic armor failure
	 * @param event
	 */
	public void checkDegrade(LivingEquipmentChangeEvent event) {
		ItemStack to = event.getTo();
		if ( to.getItem() instanceof HermeticArmorItem && NBTHelper.Item.getBoolean(to, "burnout_overload", false) ) {
			LivingEntity wearer = event.getEntityLiving();
			Optional<IItemHandler> itemHandlerCap = wearer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).resolve();
			if (itemHandlerCap.isPresent()) {
				IItemHandler inv = itemHandlerCap.get();
				float totalBurnOutPercent = 0f;
				for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack stack = inv.getStackInSlot(i);
					if (stack.getItem() instanceof HermeticArmorItem armor) {
						totalBurnOutPercent += Math.min(armor.getBurnoutMax(), getBurnout(stack))/16384f;
						//EquipmentSlot slot = LivingEntity.getEquipmentSlotForItem(stack);
						//inv.extractItem(slot.getIndex(), stack.getCount(), false);
						//ItemStack toInsert = stack.copy();// = DEGRADE_REPLACEMENTS[slot.getIndex()].copy();
						stack.getEnchantmentTags().clear();
						setBurnout(stack, 0);
						NBTHelper.Item.setBoolean(to, "burnout_overload", false);
						//int maxDamage = toInsert.getMaxDamage();
						//int itemDamageValue = wearer.getRandom().nextInt(maxDamage);
						//toInsert.setDamageValue(itemDamageValue);
						//ItemStack inserted = inv.insertItem(slot.getIndex(), toInsert, false);
						//if (inserted == toInsert) {
						//	LogHelper.warn("DarkMatterArmor.checkDegrade()", "ReplaceFailed", "Replacing ["+stack+"] with ["+toInsert+"] failed!");
						//}
					}
				}
				DamageSource dmgSrc = AASBDmgSrc.waybombAccident(wearer);
				float detPower = 5f*totalBurnOutPercent;
				if (!EntityHelper.isInvincible(wearer)) {
					EntityHelper.hurtNoDamI(wearer, AASBDmgSrc.waybombAccident(wearer).bypassArmor(), (float) Math.exp(detPower));
				}
				BlockAgnosticDamageCalculator nukeCalc = new BlockAgnosticDamageCalculator(1f/detPower);
				Vec3 cent = wearer.getBoundingBox().getCenter();
				wearer.level.explode(wearer, dmgSrc, nukeCalc, cent.x, cent.y, cent.z, 4.5f*detPower, true, Explosion.BlockInteraction.BREAK);
				wearer.level.playSound(null, wearer.blockPosition(), EffectInit.Sounds.WAY_EXPLODE.get(), wearer.getSoundSource(), 1, 1);
			}
		}
	}

	@Override
	public float getDr(ItemStack stack, DamageSource source) {
		float dr = super.getDr(stack, source);// * (1 - 0.75f*getBurnoutPercent(stack));
		return dr;
	}
	
	public static class HermeticArmorMaterial implements ArmorMaterial {
		public static final HermeticArmorMaterial MAT = new HermeticArmorMaterial();
		@Override
		public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
			switch(slot) {
			case HEAD:
				return 5;
			case CHEST:
				return 12;
			case LEGS:
				return 9;
			case FEET:
				return 4;
			default:
				return 0;
			}
		}
		@Override
		public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {return Integer.MAX_VALUE;}
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
		public String getName() {return new ResourceLocation(AsAboveSoBelow.MODID, "herm").toString();}
		@Override
		public float getToughness() {return 5;}
		@Override
		public float getKnockbackResistance() {return 0.25F;}
	}
}
