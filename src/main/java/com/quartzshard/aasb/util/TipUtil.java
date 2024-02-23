package com.quartzshard.aasb.util;

import com.quartzshard.aasb.api.alchemy.AlchData;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class TipUtil {
	public static record AspectTooltip(AlchData alchData) implements TooltipComponent {}
}
