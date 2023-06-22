package com.quartzshard.aasb.api.alchemy;

import java.util.HashMap;

/**
 * Handles much of the backend for transmutation <br>
 * not to be confused with PhilosophersStoneItem
 * @author quartzshard
 */
public class PhilosophersStone {
	public static final PhilosophersStone INSTANCE = new PhilosophersStone();
	
	private HashMap<ItemData,AlchemicProperties> Map = new HashMap<ItemData,AlchemicProperties>();
}
