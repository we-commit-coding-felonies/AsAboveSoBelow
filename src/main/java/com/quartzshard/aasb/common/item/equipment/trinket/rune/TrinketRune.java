package com.quartzshard.aasb.common.item.equipment.trinket.rune;

import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class TrinketRune extends ForgeRegistryEntry<TrinketRune> {
	public TrinketRune() {
	}
	
	
	/**
	 * Called by Gloves
	 * @return if the ability was used successfully
	 */
	public abstract boolean combatAbility();
	
	/**
	 * Called by Rings
	 * @return if the ability was used successfully
	 */
	public abstract boolean utilityAbility();
	
	/**
	 * Called by Charms every tick
	 * @return if the ability was used successfully
	 */
	public abstract boolean passiveAbility();
}
