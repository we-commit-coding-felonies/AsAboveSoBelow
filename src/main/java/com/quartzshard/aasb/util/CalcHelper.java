package com.quartzshard.aasb.util;

import net.minecraft.world.phys.Vec3;

/**
 * generic math functions
 */
public class CalcHelper {
	
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
	
	
	// following 4 functions taken from EntityHomingArrow
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

	public static double angleBetween(Vec3 v1, Vec3 v2) {
		double vDot = v1.dot(v2) / (v1.length() * v2.length());
		if (vDot < -1.0) {
			vDot = -1.0;
		}
		if (vDot > 1.0) {
			vDot = 1.0;
		}
		return Math.acos(vDot);
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

	public static Vec3 transform(Vec3 axis, double angle, Vec3 normal) {
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
}
