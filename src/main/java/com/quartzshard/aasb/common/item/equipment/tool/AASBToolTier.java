package com.quartzshard.aasb.common.item.equipment.tool;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AsAboveSoBelow;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.common.TierSortingRegistry;

public enum AASBToolTier implements Tier {
	HERMETIC("hermetic", 0, 13, 9, 5, 30, () -> Ingredient.EMPTY, Tiers.NETHERITE, null);

	private final String name;
	private final int durability;
	private final float speed;
	private final float damage;
	private final int harvest;
	private final int ench;
	private final Supplier<Ingredient> repair;

	AASBToolTier(String name, int durability, float speed, float damage, int harvest, int ench, Supplier<Ingredient> repair, Tier prev, @Nullable ResourceLocation next) {
		this.name = name;
		this.durability = durability;
		this.speed = speed;
		this.damage = damage;
		this.harvest = harvest;
		this.ench = ench;
		this.repair = repair;
		TierSortingRegistry.registerTier(this, AsAboveSoBelow.rl(name), List.of(prev), next == null ? Collections.emptyList() : List.of(next));
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