package com.quartzshard.aasb.util;

import java.util.Random;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * AABB and related stuff
 * @author solunareclipse1
 *
 */
public class BoxHelper {
	
	/**
	 * grows the box so that it is a cube, with side lengths = to longest side length of input
	 * @param box
	 * @return
	 */
	public static AABB growToCube(AABB box) {
		double x = box.getXsize();
		double y = box.getYsize();
		double z = box.getZsize();
		double s = Math.max(x, Math.max(y, z));
		return AABB.ofSize(box.getCenter(), s,s,s);
	}
	
	/**
	 * shrinks the box so that it is a cube, with side lengths = to shortest side length of input
	 * @param box
	 * @return
	 */
	public static AABB shrinkToCube(AABB box) {
		double x = box.getXsize();
		double y = box.getYsize();
		double z = box.getZsize();
		double s = Math.min(x, Math.min(y, z));
		return AABB.ofSize(box.getCenter(), s,s,s);
	}
	
	/**
	 * changes the box so that it is a cube, with side lengths = to average side length of input
	 * @param box
	 * @return
	 */
	public static AABB toCube(AABB box) {
		double s = box.getSize();
		return AABB.ofSize(box.getCenter(), s,s,s);
	}

	/**
	 * gets a random point within the box
	 * @param box
	 * @param rand
	 * @return
	 */
	public static Vec3 randomPointInBox(AABB box, Random rand) {
		return new Vec3(
			rand.nextDouble(box.minX, box.maxX),
			rand.nextDouble(box.minY, box.maxY),
			rand.nextDouble(box.minZ, box.maxZ)
		);
	}

	/**
	 * Draws an AABB with particles
	 * 
	 * @param box the AABB to draw
	 * @param particle the particle to use
	 * @param stepSize lower = more particles
	 * @param level the level to put particles in
	 * @param fill if true, draws a solid box (filled with particles), instead of just an outline
	 */
	public static void drawAABBWithParticles(AABB box, ParticleOptions particle, double stepSize, ClientLevel level, boolean fill, boolean infRange) {
		if (fill) {
			for (double i = box.minX; i < box.maxX; i += stepSize) {
	    		for (double j = box.minY; j < box.maxY; j += stepSize) {
	        		for (double k = box.minZ; k < box.maxZ; k += stepSize) {
	            		level.addParticle(particle, infRange, i, j, k, 0, 0, 0);
	        		}
	    		}
			}
		}
		
		for (double i = box.minX; i < box.maxX; i += stepSize) {
			level.addParticle(particle, infRange, i, box.minY, box.minZ, 0, 0, 0);
			level.addParticle(particle, infRange, i, box.minY, box.maxZ, 0, 0, 0);
		}
		for (double i = box.minY; i < box.maxY; i += stepSize) {
			level.addParticle(particle, infRange, box.minX, i, box.minZ, 0, 0, 0);
			level.addParticle(particle, infRange, box.minX, i, box.maxZ, 0, 0, 0);
		}
		for (double i = box.minZ; i < box.maxZ; i += stepSize) {
			level.addParticle(particle, infRange, box.minX, box.minY, i, 0, 0, 0);
			level.addParticle(particle, infRange, box.minX, box.maxY, i, 0, 0, 0);
		}
		for (double i = box.maxX; i > box.minX; i -= stepSize) {
			level.addParticle(particle, infRange, i, box.maxY, box.maxZ, 0, 0, 0);
			level.addParticle(particle, infRange, i, box.maxY, box.minZ, 0, 0, 0);
		}
		for (double i = box.maxY; i > box.minY; i -= stepSize) {
			level.addParticle(particle, infRange, box.maxX, i, box.maxZ, 0, 0, 0);
			level.addParticle(particle, infRange, box.maxX, i, box.minZ, 0, 0, 0);
		}
		for (double i = box.maxZ; i > box.minZ; i -= stepSize) {
			level.addParticle(particle, infRange, box.maxX, box.maxY, i, 0, 0, 0);
			level.addParticle(particle, infRange, box.maxX, box.minY, i, 0, 0, 0);
		}
	}

}
