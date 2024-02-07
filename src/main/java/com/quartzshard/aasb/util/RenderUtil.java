package com.quartzshard.aasb.util;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Abstracted rendering code, drawing things using particles, fun stuff like that <br>
 * Assume things in here only run on the client
 */
public class RenderUtil {
	/**
	 * Draws a line between 2 vectors using particles <br>
	 * 
	 * @param start point A
	 * @param end point B
	 * @param particle the particle to use
	 * @param stepSize lower = more particles
	 * @param level world/level particles are in
	 */
	public static void drawVectorWithParticles(Vec3 start, Vec3 end, ParticleOptions particle, double stepSize, ClientLevel level) {
		Vec3 line = end.subtract(start);
		Vec3 step = line.normalize().scale(stepSize);
		int numSteps = (int) (line.length() / step.length());
		if (step.length() <= 0) {
			// avoids floating point nonsense
			numSteps = 1;
		}
		
		Vec3 curPos = start;
		for (int i = 0; i < numSteps; i++) {
			level.addParticle(particle, true, curPos.x, curPos.y, curPos.z, 0, 0, 0);
			curPos = curPos.add(step);
		}
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
