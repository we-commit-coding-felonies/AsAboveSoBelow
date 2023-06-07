package com.quartzshard.aasb.util;

import java.util.LinkedHashMap;

import net.minecraft.util.Mth;

/**
 * Contains functions for calculating & working with RGB / HSV values <br>
 * Also has some color constants avaliable to use
 * @author solunareclipse1
 */
public class ColorsHelper {	
	
	/**
	 * Color constants <br>
	 * Values are stored as ints <br>
	 * Ranges for each value are the same as GIMPs color picker <br>
	 * For reference, heres a list:
	 * <li> <b>R, G, B</b> = 0-255
	 * <li> <b>H</b> = 0-360
	 * <li> <b>S, V</b> = 0-100
	 * <li> <b>I</b> = Integer color value (0xRRGGBB)
	 */
	public enum Color {
		MID_RED(128,0,0, 0,100,50, 0x800000),
		MID_YELLOW(128,128,0, 60,100,50, 0x808000),
		MID_GREEN(0,128,0, 120,100,50, 0x008000),
		MID_TEAL(0,128,128, 180,100,50, 0x008080),
		MID_BLUE(0,0,128, 240,100,50, 0x000080),
		MID_PURPLE(128,0,128, 300,100,50, 0x800080),
		MID_GRAY(128,128,128, 0,0,50, 0x808080),
		
		COVALENCE_GREEN(48,191,100, 142,75,75, 0x30bf64),
		COVALENCE_TEAL(48,182,191, 184,75,75, 0x30b6bf),
		COVALENCE_BLUE(48,81,191, 226,75,75, 0x3051bf),
		COVALENCE_PURPLE(115,48,191, 268,75,75, 0x7330bf),
		COVALENCE_MAGENTA(191,48,167, 310,75,75, 0xbf30a7),
		PHILOSOPHERS(191,48,117, 331,75,75, 0xbf3075);
		public final int R,G,B,H,S,V,I;
		
		private Color(int r, int g, int b, int h, int s, int v, int i) {
			R = r;
			G = g;
			B = b;
			H = h;
			S = s;
			V = v;
			I = i;
		}
	}
	
	/**
	 * Originally from ProjectTwEaked <br>
	 * Designed specifically for use with durability bars
	 * 
	 * @param fillPercent
	 * @return color integer
	 */
	public static int covColorInt(float fillPercent) {
		float f = Math.max(0.3911f, fillPercent / 1.65125495376f);
		return Mth.hsvToRgb(f, 1.0f, 0.824f);
	}
	
	/**
	 * Turns an integer into corresponding Red, Green, and Blue values <br>
	 * Created for working with the output of Mth.hsvToRgb() <br>
	 * 
	 * @param color the color, as an integer
	 * @return array of integers corresponding to RGB
	 */
	public static int[] rgbFromInt(int color) {
		int[] rgb = {(color >> 16 & 255), (color >> 8 & 255), (color & 255)};
		return rgb;
	}
	
	/**
	 * puts r, g, and b in an int[]
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return the array
	 */
	public static int[] rgbMerge(int r, int g, int b) {
		int[] rgb = {r, g, b};
		return rgb;
	}
	
	/**
	 * @deprecated non functional / unfinished.
	 * TODO: make this work, would be useful.
	 * A function that gets a color from a gradient based on a 3rd value
	 * Designed for use with {@link net.minecraft.world.item.Item#getBarColor}
	 * Behaves similarly to the vanilla durability bar colors
	 * 
	 * @param percent Determines what color in the gradient to use, 0.5 is halfway
	 * @param h1 Hue for Color 1
	 * @param s1 Saturation for Color 1
	 * @param v1 Value for Color 1
	 * @param h2 Hue for Color 2
	 * @param s2 Saturation for Color 2
	 * @param v2 Value for Color 2
	 * @return RGB value as an integer
	 */
	public static int gradientBarColor(float percent, float h1, float s1, float v1, float h2, float s2, float v2) {
		boolean hInv, sInv, vInv;
		if (Math.max(h1, h2) == h1) hInv = true; else hInv = false;
		if (Math.max(s1, s2) == s1) sInv = true; else sInv = false;
		if (Math.max(v1, v2) == v1) vInv = true; else vInv = false;
		return Mth.hsvToRgb(Math.max(0.3911F, (float) (1.0F - percent) / 1.65125495376F), 1.0f, 0.824f);
	}
	
	/**
	 * A very fancy function that will fade between 2 different HSV colors <br>
	 * Designed for use with {@link net.minecraft.world.item.Item#getBarColor} <br>
	 * Fades from color 1 to color 2, then back to color 1
	 * <p>
	 * @deprecated Use {@link ColorsHelper#fadingValue(long, int, int, float, float)} instead, its less jank
	 * 
	 * @param timer An incrementing value that the cycle uses to fade, such as the world time
	 * @param cycle How many timer ticks a full cycle takes, lower = faster fade
	 * @param offset The amount of timer ticks to offset this fading by.
	 * @param h1 Hue for Color 1
	 * @param s1 Saturation for Color 1
	 * @param v1 Value for Color 1
	 * @param h2 Hue for Color 2
	 * @param s2 Saturation for Color 2
	 * @param v2 Value for Color 2
	 * 
	 * @return RGB value as an integer
	 */
	public static int fadingColorInt(long timer, int cycle, int offset, float h1, float s1, float v1, float h2, float s2, float v2) {
		
		boolean hInv, sInv, vInv;
		if (Math.max(h1, h2) == h1) hInv = true; else hInv = false;
		if (Math.max(s1, s2) == s1) sInv = true; else sInv = false;
		if (Math.max(v1, v2) == v1) vInv = true; else vInv = false;
		
		int swapPoint = cycle / 2;
		float fade = ((timer % cycle) + offset) % cycle;
		
		float hDiff, sDiff, vDiff, hVal, sVal, vVal;
		hDiff = Math.max(h1, h2) - Math.min(h1, h2);
		sDiff = Math.max(s1, s2) - Math.min(s1, s2);
		vDiff = Math.max(v1, v2) - Math.min(v1, v2);
		
		if (fade < swapPoint) {
			if (hInv) hVal = h1 - ((hDiff * (fade - swapPoint)) / swapPoint); else hVal = h1 + ((hDiff * fade) / swapPoint);
			if (sInv) sVal = s1 - ((sDiff * (fade - swapPoint)) / swapPoint); else sVal = s1 + ((sDiff * fade) / swapPoint);
			if (vInv) vVal = v1 - ((vDiff * (fade - swapPoint)) / swapPoint); else vVal = v1 + ((vDiff * fade) / swapPoint);
		} else {
			if (hInv) hVal = h2 + ((hDiff * fade) / swapPoint); else hVal = h2 - ((hDiff * (fade - swapPoint)) / swapPoint);
			if (sInv) sVal = s2 + ((sDiff * fade) / swapPoint); else sVal = s2 - ((sDiff * (fade - swapPoint)) / swapPoint);
			if (vInv) vVal = v2 + ((vDiff * fade) / swapPoint); else vVal = v2 - ((vDiff * (fade - swapPoint)) / swapPoint);
		}
		
		return Mth.hsvToRgb(hVal, sVal, vVal);
	}
	
	

	/**
	 * Fades from 1 value to the next
	 * 
	 * @param timer An incrementing value
	 * @param length How many timer ticks for a full cycle
	 * @param offset The amount of timer ticks to offset
	 * @param invert if true, will go max -> min instead of min -> max
	 * 
	 * @return Value corresponding to position in the cycle
	 */
	public static float fade(long timer, int length, int offset, boolean invert, float a, float b) {
		float val,
			lower = Math.min(a, b),	// we figure out which of the vals is bigger
			upper = Math.max(a, b),	// means you can put the vals in backwards without issue
			diff = upper - lower,	// difference of the 2 values
			fade = (timer+offset) % length,	// our current spot on the timer
			change = diff * (fade/length);	// how much we should change by
		if (invert)
			val = upper - change;
		else
			val = lower + change;
		if (val < 0) {
			// if val is negative, something has gone wrong
        	LinkedHashMap<String,String> info = new LinkedHashMap<String,String>();
        	info.put("Val", val+"");
        	info.put("Diff", diff+"");
        	info.put("Change", change+"");
        	info.put("Fade", fade+"");
        	info.put("Timer", timer+"");
        	info.put("Length", length+"");
        	info.put("Offset", offset+"");
        	info.put("Min", lower+"");
        	info.put("Max", upper+"");
        	LogHelper.error("ColorsHelper.fade()", "NegativeFade", "A fade was calculated to be negative!", info);
		}
		return val;
	}
	
	/**
	 * Fades back and forth between 2 values <br>
	 * 
	 * @param timer An incrementing value
	 * @param length How many timer ticks for a full cycle
	 * @param offset The amount of timer ticks to offset
	 * 
	 * @return Value corresponding to position in the cycle
	 */
	public static float loopFade(long timer, int length, int offset, float a, float b) {
		float val,
			lower = Math.min(a, b),	// we figure out which of the vals is bigger
			upper = Math.max(a, b),	// means you can put the vals in backwards without issue
			diff = upper - lower,	// difference of the 2 values
			fade = (timer+offset) % length;	// our current spot on the timer
		int swap = length / 2;	// the halfway point, where we change directions
		if (fade > swap)
			/* 
			 * Heres a more readable pseudocode verson of this:
			 * 
			 * halfFade = fade - swap					// shift our current spot on the timer down by the swap point, to get a "half timer"
			 * decAmount = (diff * halfFade) / swap		// the amount we should decrement
			 * output = upper - decAmount				// do the decrement operation
			 */
			val = upper - ((diff * (fade - swap)) / swap);
		else
			// see above, this is the same except were going up instead of down
			// dont need to subtract swap because were in the lower half of the timer
			val = lower + ((diff * fade) / swap);
		if (val < 0) {
			// if val is negative, something has gone wrong
        	LinkedHashMap<String,String> info = new LinkedHashMap<String,String>();
        	info.put("Val", val+"");
        	info.put("Diff", diff+"");
        	info.put("Swap", swap+"");
        	info.put("Fade", fade+"");
        	info.put("Timer", timer+"");
        	info.put("Length", length+"");
        	info.put("Offset", offset+"");
        	info.put("Min", lower+"");
        	info.put("Max", upper+"");
        	LogHelper.error("ColorsHelper.loopFade()", "NegativeFade", "A fade was calculated to be negative!", info);
		}
		return val;
	}
}
