package com.quartzshard.aasb.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * generic math functions
 */
public class MathUtil {
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
	
	/**
	 * transposes a list <br>
	 * "borrowed" from https://stackoverflow.com/a/2942044
	 * @param <T>
	 * @param table
	 * @return
	 */
	public static <T> @NotNull List<List<T>> transpose(List<List<T>> table) {
		List<List<T>> ret = new ArrayList<List<T>>();
		final int N = table.get(0).size();
		for (int i = 0; i < N; i++) {
			List<T> col = new ArrayList<T>();
			for (List<T> row : table) {
				col.add(row.get(i));
			}
			ret.add(col);
		}
		return ret;
	}

	public static <T> List<List<T>> getAllCombos(List<List<T>> inSets, List<T> compSet, @NotNull List<List<T>> sets) {
		if (sets.size() == 1) {
			// only 1 input list means we dont do anything
			return sets;
		}
		return actuallyGetAllCombos(inSets, compSet, sets);
	}
	
	private static <T> @NotNull List<List<T>> actuallyGetAllCombos(List<List<T>> inSets, List<T> compSet, List<List<T>> sets) {
		// Mutable list for output
		List<List<T>> outSets = new ArrayList<>();

		// Initial case
		if (inSets.size() == 0) {
			// Pop of the first set, rotate it 90 degrees, compare to second set
			List<List<T>> toTranspose = new ArrayList<>();
			toTranspose.add(sets.remove(0));
			return actuallyGetAllCombos(
				transpose( toTranspose ),
				sets.remove(0),
				sets
			);
		}

		// combinations...
		for (T elem : compSet) {
			for (@NotNull List<T> row : inSets) {
				List<T> newRow = new ArrayList<>(row);
				newRow.add(0, elem);
				outSets.add(newRow);
			}
		}

		// Base case. We're done here.
		if (sets.size() <= 0) {
			return outSets;
		}

		// Recurse. Give the output as the new in, pop the next set out for comparison
		return actuallyGetAllCombos(outSets, sets.remove(0), sets);
	}
	
	/**
	 * Converts the given amount of ticks to Hours, Minutes, Seconds, Milliseconds
	 * @param t ticks
	 * @return 
	 */
	public static long[] ticksToTime(long ticks) {
		long t = ticks;
		long h = t/72000;
		t -= h*72000;
		long m = t/1200;
		t -= m*1200;
		long s = t/20;
		t -= s*20;
		long ms = t*50;
		return new long[] {h,m,s,ms};
	}
	
	
	// following 4 functions taken from projecte EntityHomingArrow
	public static double clampAbs(double param, double maxMagnitude) {
		if (Math.abs(param) > maxMagnitude) {
			if (param < 0) {
				param = -Math.abs(maxMagnitude);
			} else {
				param = Math.abs(maxMagnitude);
			}
		}
		return param;
	}

	public static double angleBetween(@NotNull Vec3 v1, @NotNull Vec3 v2) {
		double vDot = v1.dot(v2) / (v1.length() * v2.length());
		if (vDot < -1.0) {
			vDot = -1.0;
		}
		if (vDot > 1.0) {
			vDot = 1.0;
		}
		return Math.acos(vDot);
	}

	/**
	 * angleBetween, but outputs degrees (0-180) instead of radians (0-pi)
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double angleBetweenDeg(Vec3 v1, Vec3 v2) {
		return Math.toDegrees(angleBetween(v1, v2));
	}

	public static double wrap180Radian(double radian) {
		radian %= 2 * Math.PI;
		while (radian >= Math.PI) {
			radian -= 2 * Math.PI;
		}
		while (radian < -Math.PI) {
			radian += 2 * Math.PI;
		}
		return radian;
	}

	public static @NotNull Vec3 transform(Vec3 axis, double angle, Vec3 normal) {
		//Trimmed down math of javax vecmath calculations, potentially should be rewritten at some point
		double m00 = 1;
		double m01 = 0;
		double m02 = 0;

		double m10 = 0;
		double m11 = 1;
		double m12 = 0;

		double m20 = 0;
		double m21 = 0;
		double m22 = 1;
		double mag = Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
		if (mag >= 1.0E-10) {
			mag = 1.0 / mag;
			double ax = axis.x * mag;
			double ay = axis.y * mag;
			double az = axis.z * mag;

			double sinTheta = Math.sin(angle);
			double cosTheta = Math.cos(angle);
			double t = 1.0 - cosTheta;

			double xz = ax * az;
			double xy = ax * ay;
			double yz = ay * az;

			m00 = t * ax * ax + cosTheta;
			m01 = t * xy - sinTheta * az;
			m02 = t * xz + sinTheta * ay;

			m10 = t * xy + sinTheta * az;
			m11 = t * ay * ay + cosTheta;
			m12 = t * yz - sinTheta * ax;

			m20 = t * xz - sinTheta * ay;
			m21 = t * yz + sinTheta * ax;
			m22 = t * az * az + cosTheta;
		}
		return new Vec3(m00 * normal.x + m01 * normal.y + m02 * normal.z,
				m10 * normal.x + m11 * normal.y + m12 * normal.z,
				m20 * normal.x + m21 * normal.y + m22 * normal.z);
	}
	
	/**
	 * Same as Mth.isPowerOfTwo(), but with longs instead of ints 
	 * @param x Query value
	 * @return If x is a power of 2
	 */
	public static boolean isPowOf2(long x) {
		return x != 0 && (x & x - 1) == 0;
	}
}
