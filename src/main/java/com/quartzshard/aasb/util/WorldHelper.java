package com.quartzshard.aasb.util;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.WEST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
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

	public static Direction getHorizontalFacing(Entity entity) {
		Direction[] dirs = Direction.orderedByNearest(entity);
		return dirs[0].getAxis().isVertical() ? dirs[1] : dirs[0];
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
		TOP(abs -> {return Direction.UP;}),
		BOTTOM(abs -> {return Direction.DOWN;}),
		
		BACK(abs -> {return abs;}), // Relative north
		RIGHT(abs -> {return abs.getClockWise();}), // Relative east
		FRONT(abs -> {return abs.getOpposite();}), // Relative south
		LEFT(abs -> {return abs.getCounterClockWise();}); // Relative west
		
		private final UnaryOperator<Direction> rot;
		private Side(UnaryOperator<Direction> rot) {
			this.rot = rot;
		}
		
		/**
		 * Translates the given relative Side to absolute Direction
		 * @param rel The relative side to be translated to absolute
		 * @param facing The direction that BACK corresponds to (the "local north")
		 * @return The corresponding Direction
		 */
		public Direction abs(Direction facing) {
			return rot.apply(facing);
		}
		
		/**
		 * Translate the given absolute Direction to relative Side
		 * @param abs the absolute direction to be translated to relative
		 * @param facing The direction that BACK corresponds to (the "local north")
		 * @return The corresponding Direction
		 */
		public static Side rel(Direction abs, Direction facing) {
			Direction relDir;
			if (abs.getAxis() != Axis.Y) {
				switch (facing) {
				default:
				case NORTH:
					relDir = abs;
					break;
				case EAST:
					relDir = abs.getCounterClockWise();
					break;
				case SOUTH:
					relDir = abs.getOpposite();
					break;
				case WEST:
					relDir = abs.getClockWise();
					break;
				}
			} else relDir = abs;
			return localEquivalent(relDir);
		}
		
		/**
		 * Gets the "local equivalent" of the given Direction. <br>
		 * This doesn't take facing into account, assuming the default facing of north.
		 * <p>
		 * Uses for this are niche, so you probably want to use rel() instead.
		 * @param relDir
		 * @return Local equivalent of the given relative direction
		 */
		public static Side localEquivalent(Direction relDir) {
			switch (relDir) {
			case NORTH: return BACK;
			case EAST: return RIGHT;
			case SOUTH: return FRONT;
			case WEST: return LEFT;
			case UP: return TOP;
			case DOWN: return BOTTOM;
			}
			throw new IllegalArgumentException("Unknown direction: "+ relDir);
		}
	}
}
