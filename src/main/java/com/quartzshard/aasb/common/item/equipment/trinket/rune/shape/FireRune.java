package com.quartzshard.aasb.common.item.equipment.trinket.rune.shape;

import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;

public class FireRune extends TrinketRune {

	@Override
	public boolean combatAbility() {
		System.out.println("fire combat");
		return false;
	}

	@Override
	public boolean utilityAbility() {
		System.out.println("fire utility");
		return false;
	}

	@Override
	public boolean passiveAbility() {
		System.out.println("fire passive");
		return false;
	}

}
