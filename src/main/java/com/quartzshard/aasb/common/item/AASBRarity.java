package com.quartzshard.aasb.common.item;

import java.util.function.UnaryOperator;

import com.quartzshard.aasb.util.ColorsHelper.Color;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;

public enum AASBRarity {
	TIER1("MATERIA_INFIRMA", style -> style.withColor(Color.COVALENCE_GREEN.I)),
	TIER2("MATERIA_MINOR", style -> style.withColor(Color.COVALENCE_TEAL.I)),
	TIER3("MATERIA_MODICA", style -> style.withColor(Color.COVALENCE_BLUE.I)),
	TIER4("MATERIA_MAJOR", style -> style.withColor(Color.COVALENCE_PURPLE.I)),
	TIER5("MATERIA_PRIMA", style -> style.withColor(Color.COVALENCE_MAGENTA.I)),
	SPECIAL("PHILOSOPHERS_STONE", style -> style.withColor(Color.PHILOSOPHERS.I))
	;
	
	private final Rarity rarity;

	private AASBRarity(String name, ChatFormatting color) {
		rarity = Rarity.create(name, color);
	}
	private AASBRarity(String name, UnaryOperator<Style> styleMod) {
		rarity = Rarity.create(name, styleMod);
	}
	
	public Rarity get() {
		return this.rarity;
	}
}
