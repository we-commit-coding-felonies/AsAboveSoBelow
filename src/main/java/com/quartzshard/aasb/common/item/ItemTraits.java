package com.quartzshard.aasb.common.item;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.util.Colors;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;

/**
 * Tool Tier, Rarity, etc
 */
public class ItemTraits {
	public enum Rarity {
		MATERIA_NEG2("MATERIA_NEG2", style -> style.withColor(Colors.MATERIA_NEG2.I)),
		MATERIA_NEG1("MATERIA_NEG1", style -> style.withColor(Colors.MATERIA_NEG1.I)),
		MATERIA_0("MATERIA_0", style -> style.withColor(Colors.MATERIA_0.I)),
		MATERIA_1("INFIRMA", style -> style.withColor(Colors.MATERIA_INFIRMA.I)),
		MATERIA_2("MINOR", style -> style.withColor(Colors.MATERIA_MINOR.I)),
		MATERIA_3("MODICA", style -> style.withColor(Colors.MATERIA_MODICA.I)),
		MATERIA_4("MAJOR", style -> style.withColor(Colors.MATERIA_MAJOR.I)),
		MATERIA_5("PRIMA", style -> style.withColor(Colors.MATERIA_PRIMA.I)),
		MATERIA_6("MATERIA_6", style -> style.withColor(Colors.MATERIA_6.I)),
		
		NULLIFIED("NULLIFIED", style -> style.withColor(Colors.AETHER.I)),
		QUINTESSENTIAL("QUINTESSENTIAL", style -> style.withColor(Colors.MID_PURPLE.I)),
		IMPOSSIBLE("IMPOSSIBLE", style -> style.withColor(Colors.PHILOSOPHERS.I))
		;
		
		private final net.minecraft.world.item.Rarity rarity;

		private Rarity(String name, ChatFormatting color) {
			rarity = net.minecraft.world.item.Rarity.create(name, color);
		}
		private Rarity(String name, UnaryOperator<Style> styleMod) {
			rarity = net.minecraft.world.item.Rarity.create(name, styleMod);
		}
		
		public net.minecraft.world.item.Rarity get() {
			return this.rarity;
		}
	}
	
	public enum Tier implements net.minecraft.world.item.Tier {
		HERMETIC("hermetic", 0, 15, 9, 5, 30, () -> Ingredient.EMPTY, Tiers.NETHERITE, null);

		private final String name;
		private final int durability, harvest, ench;
		private final float speed, damage;
		private final Supplier<Ingredient> repair;
		
		Tier(String name, int durability, float speed, float damage, int harvest, int ench, Supplier<Ingredient> repair, net.minecraft.world.item.@NotNull Tier prev, @Nullable ResourceLocation next) {
			this.name = name;
			this.durability = durability;
			this.speed = speed;
			this.damage = damage;
			this.harvest = harvest;
			this.ench = ench;
			this.repair = repair;
			TierSortingRegistry.registerTier(this, AASB.rl(name), List.of(prev), next == null ? Collections.emptyList() : List.of(next));
		}

		@Override
		public String toString() {
			return name;
		}
		
		

		@Override
		public int getUses() {
			return durability;
		}

		@Override
		public float getSpeed() {
			return speed;
		}

		@Override
		public float getAttackDamageBonus() {
			return damage;
		}

		@Override
		public int getLevel() {
			return harvest;
		}

		@Override
		public int getEnchantmentValue() {
			return ench;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return repair.get();
		}
	}
}
