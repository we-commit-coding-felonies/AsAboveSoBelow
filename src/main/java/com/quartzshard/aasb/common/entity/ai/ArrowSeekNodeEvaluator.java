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

	public void prepare(PathNavigationRegion pLevel, AbstractArrow arrow) {
		this.level = pLevel;
		this.arrow = arrow;
		this.nodes.clear();
		this.entityWidth = Mth.floor(arrow.getBbWidth() + 1.0F);
		this.entityHeight = Mth.floor(arrow.getBbHeight() + 1.0F);
		this.entityDepth = Mth.floor(arrow.getBbWidth() + 1.0F);
	}

	@Override
	public Node getStart() {
		BlockPos pos = this.arrow.blockPosition();
		return super.getNode(pos);
	}

	@Override
	public Target getGoal(double pX, double pY, double pZ) {
		return new Target(super.getNode(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ)));
	}

	@Nullable
	protected Node getNode(int pX, int pY, int pZ) {
		Node node = super.getNode(pX, pY, pZ);

		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		BlockPathTypes blockpathtypes = getBlockPathTypeRaw(this.level, blockpos$mutableblockpos.set(pX, pY, pZ));
		if (blockpathtypes != BlockPathTypes.OPEN) {
			node.closed = true;
		}
		return node;
	}

	@Override
	public int getNeighbors(Node[] neighbours, Node thisNode) {
		int i = 0;
		Node node = this.getNode(thisNode.x, thisNode.y, thisNode.z + 1);
		if (this.isOpen(node)) {
			neighbours[i++] = node;
		}

		Node node1 = this.getNode(thisNode.x - 1, thisNode.y, thisNode.z);
		if (this.isOpen(node1)) {
			neighbours[i++] = node1;
		}

		Node node2 = this.getNode(thisNode.x + 1, thisNode.y, thisNode.z);
		if (this.isOpen(node2)) {
			neighbours[i++] = node2;
		}

		Node node3 = this.getNode(thisNode.x, thisNode.y, thisNode.z - 1);
		if (this.isOpen(node3)) {
			neighbours[i++] = node3;
		}

		Node node4 = this.getNode(thisNode.x, thisNode.y + 1, thisNode.z);
		if (this.isOpen(node4)) {
			neighbours[i++] = node4;
		}

		Node node5 = this.getNode(thisNode.x, thisNode.y - 1, thisNode.z);
		if (this.isOpen(node5)) {
			neighbours[i++] = node5;
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

	private boolean isOpen(@Nullable Node pNode) {
		return pNode != null && !pNode.closed;
	}

	public static BlockPathTypes getBlockPathTypeRaw(BlockGetter pLevel, BlockPos pPos) {
		BlockState blockstate = pLevel.getBlockState(pPos);
		if (blockstate.isAir() || blockstate.is(BlockTP.ARROW_NOCLIP)) {
			return BlockPathTypes.OPEN;
		} else {
			return BlockPathTypes.BLOCKED;
		}
	}

	@Override
	public BlockPathTypes getBlockPathType(BlockGetter pBlockaccess, int pX, int pY, int pZ, Mob pEntityliving,
			int pXSize, int pYSize, int pZSize, boolean pCanBreakDoors, boolean pCanEnterDoors) {
		return null;
	}

	@Override
	public BlockPathTypes getBlockPathType(BlockGetter pLevel, int pX, int pY, int pZ) {
		return null;
	}
}
