package com.quartzshard.aasb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * AABB and related stuff
 * @author solunareclipse1
 *
 */
public class BoxHelper {
	
	public static AABB getCubeForAoeInFront(BlockPos pos, Direction direction, float o) {
		float x = pos.getX()+0.5f;
		float y = pos.getY()+0.5f;
		float z = pos.getZ()+0.5f;
		return switch (direction) {
			case EAST -> new AABB(x-o, y-o, z-o, x, y+o, z+o);
			case WEST -> new AABB(x, y-o, z-o, x+o, y+o, z+o);
			case UP -> new AABB(x-o, y-o, z-o, x+o, y, z+o);
			case DOWN -> new AABB(x-o, y, z-o, x+o, y+o, z+o);
			case SOUTH -> new AABB(x-o, y-o, z-o, x+o, y+o, z);
			case NORTH -> new AABB(x-o, y-o, z, x+o, y+o, z+o);
		};
	}
	
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

	public static AABB moveBoxTo(AABB box, BlockPos pos) {
		return moveBoxTo(box, Vec3.atCenterOf(pos));
	}
	public static AABB moveBoxTo(AABB box, Vec3 pos) {
		double x = box.getXsize();
		double y = box.getYsize();
		double z = box.getZsize();
		return AABB.ofSize(pos, x,y,z);
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
	
	// https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/utils/WorldHelper.java
	public static Iterable<BlockPos> allBlocksInBox(AABB box) {
		return allBlocksInBounds(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ));
	}
	public static Iterable<BlockPos> allBlocksInBounds(BlockPos corner1, BlockPos corner2) {
		return () -> BlockPos.betweenClosedStream(corner1, corner2).iterator();
	}

	public static Vec3 getMin(AABB box) {
		return new Vec3(box.minX, box.minY, box.minZ);
	}
	public static Vec3 getMax(AABB box) {
		return new Vec3(box.maxX, box.maxY, box.maxZ);
	}
	
	public enum Corner {
		DOWN_NORTH_WEST(0),
		DOWN_SOUTH_WEST(1),
		UP_NORTH_WEST(2),
		UP_SOUTH_WEST(3),
		DOWN_NORTH_EAST(4),
		DOWN_SOUTH_EAST(5),
		UP_NORTH_EAST(6),
		UP_SOUTH_EAST(7);
		
		public final int index;
		private Corner(int index) {
			this.index = index;
		}
	}
	

	public static Vec3 getCorner(AABB box, Corner corner) {
		return getCorner(box, corner.index);
	}
	/**
	 * gets the specified corner
	 * @param box
	 * @param index a 3 bit int flag, defining if X, Y, and Z are their min (0) or max (1), respectively
	 * @return
	 */
	public static Vec3 getCorner(AABB box, int id) {
		return new Vec3(
				(id&4) == 0 ? box.minX : box.maxX,
				(id&2) == 0 ? box.minY : box.maxY,
				(id&1) == 0 ? box.minZ : box.maxZ
			);
	}
	
	/**
	 * getting all 8 corner points of a box <br>
	 * The index of an item in the returned array corresponds with what corner it is:
	 * its a 3 bit int flag, defining if X, Y, and Z are their min (0) or max (1), respectively
	 * @param box
	 * @return array of vec3, as described above
	 */
	public static Vec3[] getAllCorners(AABB box) {
		Vec3[] corners = new Vec3[8];
		for (int i = 0; i < 8; i++) {
			corners[i] = getCorner(box, i);
		}
		return corners;
	}
	
	/**
	 * returns a certain side of the box, represented as a min & max corner
	 * @param box
	 * @param side
	 * @return
	 */
	public static Tuple<Vec3,Vec3> getSide(AABB box, Direction side) {
		int corner; // a 3 bit index value for a corner
		boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
		switch (side.getAxis()) {
		// we set the corner here based on axis, or its bitwise inversion if direction is negative
		case X:
			corner = positive ? 4 : 3;
			break; // 100, 011
		case Y:
			corner = positive ? 2 : 5;
			break; // 010, 101
		default:
		case Z:
			corner = positive ? 1 : 6;
			break; // 001, 110
		}
		// we know for a fact that positives always have a max of 111
		// similarly, we know that negatives are always 000
		// the corner variable is used here as the one that decides the other value
		int min = positive ? corner : 0;
		int max = positive ? 7 : corner;
		return new Tuple<>(getCorner(box, min), getCorner(box, max));
	}
	
	public static Map<Direction, Tuple<Vec3,Vec3>> getAllSides(AABB box) {
		Map<Direction, Tuple<Vec3,Vec3>> sides = new HashMap<>();
		for (Direction side : Direction.values()) {
			sides.put(side, getSide(box, side));
		}
		return sides;
	}
	
	/**
	 * @param box
	 * @return volume of box in cubic meters
	 */
	public static double volume(AABB box) {
		return box.getXsize() * box.getYsize() * box.getZsize();
	}
	
	/**
	 * @param box
	 * @return surface area of box in square meters
	 */
	public static double surfaceArea(AABB box) {
		double sa = 0;
		for (Entry<Direction, Tuple<Vec3,Vec3>> side : getAllSides(box).entrySet()) {
			Direction.Axis axis = side.getKey().getAxis();
			Vec3 min = side.getValue().getA();
			Vec3 max = side.getValue().getB();
			switch (axis) {
			case X:
				sa += (max.y - min.y) * (max.z - min.z);
				continue;
			case Y:
				sa += (max.x - min.x) * (max.z - min.z);
				continue;
			case Z:
				sa += (max.y - min.y) * (max.x - min.x);
				continue;
			}
		}
		return sa;
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
