package com.quartzshard.aasb.api.item;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.alchemy.rune.shape.EarthRune;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

/**
 * A combination of multiple other item-related interfaces, use this if you want to make a new kind of Hermetic Tool with minimal differences
 */
public interface IHermeticTool extends IDigStabilizer, IEmpowerable, IEnchantBoostable, IRuneableTool {

	@Override
	default boolean handle(PressContext ctx) {
		switch (ctx.bind()) {
			case EMPOWER:
				return IEmpowerable.super.handle(ctx);
			default:
				return IRuneableTool.super.handle(ctx);
		}
	}
	
	@Override
	default int wayLeakRate(ItemStack stack) {
		// 1/3t default, 1/t if air, 1/9t if earth
		return runesAreStrong(stack) ? 1 : getMinorRune(stack) instanceof EarthRune ? 9 : 3;
	}
	
	@Override
	default double calculateBonus(ItemStack stack) {
		double chargeMod = getEmpowerPercent(stack);
		if (runesAreStrong(stack)) chargeMod *= Math.PI + Math.E;
		return chargeMod * getBonusStrength(stack);
	}
	
	public static final UUID BASE_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID BASE_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	default Multimap<Attribute,AttributeModifier> enchAttribMods(EquipmentSlot slot, ItemStack stack, Multimap<Attribute,AttributeModifier> attribs) {
		double bonusDamage = calculateBonus(stack);
		if (bonusDamage > 0 && shouldApplyBonus(stack) && slot == EquipmentSlot.MAINHAND) {
			double bonusSpeed = bonusDamage/10d;
			double baseDamage = 0;
			double baseSpeed = 0;
			// we filter out base damage & attack speed, which we modify then apply later
			ImmutableMultimap.Builder<Attribute,AttributeModifier> extra = ImmutableMultimap.builder();
			for (Entry<Attribute,Collection<AttributeModifier>> attr : attribs.asMap().entrySet()) {
				if (attr.getKey() == Attributes.ATTACK_DAMAGE) {
					for (AttributeModifier mod : attr.getValue()) {
						if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
							baseDamage += mod.getAmount();
						} else {
							extra.put(Attributes.ATTACK_DAMAGE, mod);
						}
					}
				} else if (attr.getKey() == Attributes.ATTACK_SPEED) {
					for (AttributeModifier mod : attr.getValue()) {
						if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
							baseSpeed += mod.getAmount();
						} else {
							extra.put(Attributes.ATTACK_SPEED, mod);
						}
					}
				} else {
					extra.putAll(attr.getKey(), attr.getValue());
				}
			}
			ImmutableMultimap.Builder<Attribute,AttributeModifier> modAttr = ImmutableMultimap.builder();
			modAttr.put(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(BASE_DAMAGE_UUID, "enchantment synergy - attack damage", baseDamage + bonusDamage, AttributeModifier.Operation.ADDITION));
			modAttr.putAll(extra.build());
			modAttr.put(Attributes.ATTACK_SPEED,
					new AttributeModifier(BASE_SPEED_UUID, "enchantment synergy - swing speed", baseSpeed + Math.abs(baseSpeed*bonusSpeed), AttributeModifier.Operation.ADDITION));
			attribs = modAttr.build();
		}
		return attribs;
	}
	
	default float calcDestroySpeed(ItemStack stack, float baseSpeed) {
		float speed = baseSpeed;
		if (speed > 1) {
			double bonus = calculateBonus(stack) / 10d;
			if (bonus > 0 && shouldApplyBonus(stack)) {
				speed += speed*bonus;
			}
		}
		return speed;
	}
}
