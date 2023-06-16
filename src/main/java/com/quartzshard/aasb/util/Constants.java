package com.quartzshard.aasb.util;

/**
 * Contains a bunch of misc unchanging values
 * @author solunareclipse1
 */
public class Constants {
	/**
	 * Pre-calculated value of sqrt(pi+e)
	 */
	public static final double SQRT_PI_E = 2.42071776175d;
	
	public class Xp {
		// beyond this, xp required for next level > Long.MAX_VALUE
		public static final long MAX_LVL = 1024819115206086218l;
		
		// the xp required to go from MAX_LEVEL-1 to MAX_LEVEL
		public static final long MAX_REMAINDER = 9223372031843982336l;
		
		// levels higher than this will overflow when converting to points
		public static final int TRANSFER_MAX_LVL = 1431655783;
		
		// the raw value, in points, of TRANSFER_MAX_LVL levels
		public static final long TRANSFER_MAX_POINTS = 9223372031843981383l;
		
		// beyond this, xp required for next level > Integer.MAX_VALUE
		public static final int VANILLA_MAX_LVL = 238609312;
		
		// the xp required to go from VANILLA_MAX_LVL-1 to VANILLA_MAX_LVL
		public static final int VANILLA_MAX_REMAINDER = 2147483641;
		
		// the raw value, in points, of VANILLA_MAX_LVL levels
		public static final long VANILLA_MAX_POINTS = 256204778204999068l;
		
		// pre-calculated values of some powers of ten levels, up to 10^9
		// level = 10^index, so first item (10^0) is the amount of xp for level 1
		public static final long[] LVL_POWS_OF_TEN = {
				7, // 1
				160, // 10
				30970, // 100
				4339720, // 1k
				448377220, // 10k
				44983752220l, // 100k
				4499837502220l, // 1m
				449998375002220l, // 10m
				44999983750002220l, // 100m
				4499999837500002220l // 1bil
		};
	}
	
}
