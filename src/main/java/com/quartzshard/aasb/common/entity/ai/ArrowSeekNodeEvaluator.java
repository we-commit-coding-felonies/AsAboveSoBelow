package com.quartzshard.aasb.common.entity.ai;

import javax.annotation.Nullable;

import com.quartzshard.aasb.data.AASBTags.BlockTP;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Target;

public class ArrowSeekNodeEvaluator extends NodeEvaluator {

	public AbstractArrow arrow;
	public LivingEntity target;

	public ArrowSeekNodeEvaluator() {
	}

	public void prepare(PathNavigationRegion level, AbstractArrow arrow) {
		this.level = level;
		this.arrow = arrow;
		this.nodes.clear();
		this.entityWidth = Mth.floor(arrow.getBbWidth() + 1);
		this.entityHeight = Mth.floor(arrow.getBbHeight() + 1);
		this.entityDepth = Mth.floor(arrow.getBbWidth() + 1);
	}

	@Override
	public Node getStart() {
		BlockPos pos = this.arrow.blockPosition();
		return super.getNode(pos);
	}

	@Override
	public Target getGoal(double x, double y, double z) {
		return new Target(super.getNode(Mth.floor(x), Mth.floor(y), Mth.floor(z)));
	}

	@Override
	@Nullable
	protected Node getNode(int x, int y, int z) {
		Node node = super.getNode(x, y, z);

		BlockPos.MutableBlockPos mBlockPos = new BlockPos.MutableBlockPos();
		BlockPathTypes blockPathTypes = getBlockPathTypeRaw(this.level, mBlockPos.set(x, y, z));
		if (blockPathTypes != BlockPathTypes.OPEN) {
			node.closed = true;
		}
		return node;
	}

	@Override
	public int getNeighbors(Node[] neighbours, Node thisNode) {
		int i = 0;
		Node south = this.getNode(thisNode.x, thisNode.y, thisNode.z + 1);
		if (ArrowSeekNodeEvaluator.isOpen(south)) {
			neighbours[i++] = south;
		}

		Node west = this.getNode(thisNode.x - 1, thisNode.y, thisNode.z);
		if (ArrowSeekNodeEvaluator.isOpen(west)) {
			neighbours[i++] = west;
		}

		Node east = this.getNode(thisNode.x + 1, thisNode.y, thisNode.z);
		if (ArrowSeekNodeEvaluator.isOpen(east)) {
			neighbours[i++] = east;
		}

		Node north = this.getNode(thisNode.x, thisNode.y, thisNode.z - 1);
		if (ArrowSeekNodeEvaluator.isOpen(north)) {
			neighbours[i++] = north;
		}

		Node above = this.getNode(thisNode.x, thisNode.y + 1, thisNode.z);
		if (ArrowSeekNodeEvaluator.isOpen(above)) {
			neighbours[i++] = above;
		}

		Node below = this.getNode(thisNode.x, thisNode.y - 1, thisNode.z);
		if (ArrowSeekNodeEvaluator.isOpen(below)) {
			neighbours[i++] = below;
		}
		// This code below does checking for non-cardinals, but needs work to stop
		// corner clipping
		// i hate you shard
//		Node node6 = this.getNode(thisNode.x, thisNode.y + 1, thisNode.z + 1);
//		if (this.isOpen(node6)) {
//			neighbours[i++] = node6;
//		}
//
//		Node node7 = this.getNode(thisNode.x - 1, thisNode.y + 1, thisNode.z);
//		if (this.isOpen(node7)) {
//			neighbours[i++] = node7;
//		}
//
//		Node node8 = this.getNode(thisNode.x + 1, thisNode.y + 1, thisNode.z);
//		if (this.isOpen(node8)) {
//			neighbours[i++] = node8;
//		}
//
//		Node node9 = this.getNode(thisNode.x, thisNode.y + 1, thisNode.z - 1);
//		if (this.isOpen(node9)) {
//			neighbours[i++] = node9;
//		}
//
//		Node node10 = this.getNode(thisNode.x, thisNode.y - 1, thisNode.z + 1);
//		if (this.isOpen(node10)) {
//			neighbours[i++] = node10;
//		}
//
//		Node node11 = this.getNode(thisNode.x - 1, thisNode.y - 1, thisNode.z);
//		if (this.isOpen(node11)) {
//			neighbours[i++] = node11;
//		}
//
//		Node node12 = this.getNode(thisNode.x + 1, thisNode.y - 1, thisNode.z);
//		if (this.isOpen(node12)) {
//			neighbours[i++] = node12;
//		}
//
//		Node node13 = this.getNode(thisNode.x, thisNode.y - 1, thisNode.z - 1);
//		if (this.isOpen(node13)) {
//			neighbours[i++] = node13;
//		}
//
//		Node node14 = this.getNode(thisNode.x + 1, thisNode.y, thisNode.z - 1);
//		if (this.isOpen(node14)) {
//			neighbours[i++] = node14;
//		}
//
//		Node node15 = this.getNode(thisNode.x + 1, thisNode.y, thisNode.z + 1);
//		if (this.isOpen(node15)) {
//			neighbours[i++] = node15;
//		}
//
//		Node node16 = this.getNode(thisNode.x - 1, thisNode.y, thisNode.z - 1);
//		if (this.isOpen(node16)) {
//			neighbours[i++] = node16;
//		}
//
//		Node node17 = this.getNode(thisNode.x - 1, thisNode.y, thisNode.z + 1);
//		if (this.isOpen(node17)) {
//			neighbours[i++] = node17;
//		}
//
//		Node node18 = this.getNode(thisNode.x + 1, thisNode.y + 1, thisNode.z - 1);
//		if (this.isOpen(node18)) {
//			neighbours[i++] = node18;
//		}
//
//		Node node19 = this.getNode(thisNode.x + 1, thisNode.y + 1, thisNode.z + 1);
//		if (this.isOpen(node19)) {
//			neighbours[i++] = node19;
//		}
//
//		Node node20 = this.getNode(thisNode.x - 1, thisNode.y + 1, thisNode.z - 1);
//		if (this.isOpen(node20)) {
//			neighbours[i++] = node20;
//		}
//
//		Node node21 = this.getNode(thisNode.x - 1, thisNode.y + 1, thisNode.z + 1);
//		if (this.isOpen(node21)) {
//			neighbours[i++] = node21;
//		}
//
//		Node node22 = this.getNode(thisNode.x + 1, thisNode.y - 1, thisNode.z - 1);
//		if (this.isOpen(node22)) {
//			neighbours[i++] = node22;
//		}
//
//		Node node23 = this.getNode(thisNode.x + 1, thisNode.y - 1, thisNode.z + 1);
//		if (this.isOpen(node23)) {
//			neighbours[i++] = node23;
//		}
//
//		Node node24 = this.getNode(thisNode.x - 1, thisNode.y - 1, thisNode.z - 1);
//		if (this.isOpen(node24)) {
//			neighbours[i++] = node24;
//		}
//
//		Node node25 = this.getNode(thisNode.x - 1, thisNode.y - 1, thisNode.z + 1);
//		if (this.isOpen(node25)) {
//			neighbours[i++] = node25;
//		}

		return i;
	}

	private static boolean isOpen(@Nullable Node node) {
		return node != null && !node.closed;
	}

	public static BlockPathTypes getBlockPathTypeRaw(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.isAir() || state.is(BlockTP.ARROW_NOCLIP)) {
			return BlockPathTypes.OPEN;
		}
		return BlockPathTypes.BLOCKED;
	}

	@Override
	public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z, Mob mob,
			int xSize, int ySize, int zSize, boolean canBreakDoor, boolean canOpenDoor) {
		return null;
	}

	@Override
	public BlockPathTypes getBlockPathType(BlockGetter level, int x, int y, int z) {
		return null;
	}
}
