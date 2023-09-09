package com.quartzshard.aasb.util;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class WorldHelper {
	public static boolean isBlockLoaded(@Nullable BlockGetter world, @NotNull BlockPos pos) {
		if (world == null) {
			return false;
		} else if (world instanceof LevelReader reader) {
			if (reader instanceof Level level && !level.isInWorldBounds(pos)) {
				return false;
			}
			return reader.hasChunkAt(pos);
		}
		return true;
	}
	
	@Nullable
	public static BlockEntity getBlockEntity(@Nullable BlockGetter level, @NotNull BlockPos pos) {
		if (!isBlockLoaded(level, pos)) {
			//If the world is null or its a world reader and the block is not loaded, return null
			return null;
		}
		return level.getBlockEntity(pos);
	}


	public static List<BlockEntity> allTEInBox(Level level, AABB box) {
		List<BlockEntity> list = new ArrayList<>();
		for (BlockPos pos : BoxHelper.allBlocksInBox(box)) {
			BlockEntity te = getBlockEntity(level, pos);
			if (te != null) {
				list.add(te);
			}
		}
		return list;
	}
	
	/**
	 * Relative side. Helps with defining sidedness of blocks that can be rotated <br>
	 * Provides translation functions to/from its absolute cousin, Direction
	 * <p>
	 * NOTE: it does NOT provide translations for any rotations around the X or Z axis <br>
	 * This is only useful for blocks which only rotate around the Y axis (changing left, right, front, and back) <br>
	 * The TOP and BOTTOM values are only here for ease of use
	 */
	public enum Side {
		TOP(UP,UP,UP,UP),BOTTOM(DOWN,DOWN,DOWN,DOWN),
		
		BACK(NORTH, SOUTH, EAST, WEST), // Relative north
		FRONT(SOUTH, NORTH, WEST, EAST), // Relative south
		RIGHT(EAST, WEST, SOUTH, NORTH), // Relative east
		LEFT(WEST, EAST, NORTH, SOUTH); // Relative west
		
		private final Direction n,s,e,w;
		private Side(Direction n, Direction s, Direction e, Direction w) {
			this.n=n;
			this.s=s;
			this.e=e;
			this.w=w;
		}
		
		/**
		 * Translates the given relative Side to absolute Direction
		 * @param rel The relative side to be translated to absolute
		 * @param facing The direction that BACK corresponds to (the "local north")
		 * @return The corresponding Direction
		 */
		public Direction abs(Direction facing) {
			switch (facing) {
			case NORTH: return n;
			case SOUTH: return s;
			case EAST: return e;
			case WEST: return w;
			
			default: // we cant handle up & down facings so we just return it back
				return facing;
			}
		}
		
		/**
		 * Translate the given absolute Direction to relative Side
		 * @param abs the absolute direction to be translated to relative
		 * @param facing The direction that BACK corresponds to (the "local north")
		 * @return The corresponding Direction
		 */
		public static Side rel(Direction abs, Direction facing) {
			switch (facing) {
			case NORTH: return n(abs);
			case SOUTH: return s(abs);
			case EAST: return e(abs);
			case WEST: return w(abs);
			
			default: return v(abs);
			}
		}
		
		private static Side n(Direction abs) {
			switch (abs) {
			case NORTH: return BACK;
			case SOUTH: return FRONT;
			case EAST: return RIGHT;
			case WEST: return LEFT;
			default: throw new IllegalArgumentException("Side.n() called with vertical direction " + abs.getName().toUpperCase());
			}
		}
		
		private static Side s(Direction abs) {
			switch (abs) {
			case NORTH: return FRONT;
			case SOUTH: return BACK;
			case EAST: return LEFT;
			case WEST: return RIGHT;
			default: throw new IllegalArgumentException("Side.s() called with vertical direction " + abs.getName().toUpperCase());
			}
		}
		
		private static Side e(Direction abs) {
			switch (abs) {
			case NORTH: return LEFT;
			case SOUTH: return RIGHT;
			case EAST: return FRONT;
			case WEST: return BACK;
			default: throw new IllegalArgumentException("Side.e() called with vertical direction " + abs.getName().toUpperCase());
			}
		}
		
		private static Side w(Direction abs) {
			switch (abs) {
			case NORTH: return RIGHT;
			case SOUTH: return LEFT;
			case EAST: return BACK;
			case WEST: return FRONT;
			default: throw new IllegalArgumentException("Side.w() called with vertical direction " + abs.getName().toUpperCase());
			}
		}
		
		private static Side v(Direction abs) {
			switch (abs) {
			case UP: return TOP;
			case DOWN: return BOTTOM;
			default: throw new IllegalArgumentException("Side.v() called with a non-vertical direction " + abs.getName().toUpperCase());
			}
		}
	}
}
