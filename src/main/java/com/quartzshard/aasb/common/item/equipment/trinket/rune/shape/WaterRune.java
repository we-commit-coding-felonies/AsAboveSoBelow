package com.quartzshard.aasb.common.item.equipment.trinket.rune.shape;

import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;

public class WaterRune extends TrinketRune {

	@Override
	public boolean combatAbility() {
		System.out.println("WATER combat");
		return false;
	}

	@Override
	public boolean utilityAbility() {
		System.out.println("WATER utility");
		return false;
	}

	@Override
	public boolean passiveAbility() {
		System.out.println("WATER passive");
		return false;
	}

}
