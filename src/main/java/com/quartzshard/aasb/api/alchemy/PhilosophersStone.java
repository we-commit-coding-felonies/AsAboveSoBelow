package com.quartzshard.aasb.api.alchemy;

import java.util.HashMap;

public class PhilosophersStone {
	public static final PhilosophersStone INSTANCE = new PhilosophersStone();
	
	private HashMap<ItemData,AlchemicProperties> Map = new HashMap<ItemData,AlchemicProperties>();
}
