package com.quartzshard.aasb.common.entity.ai;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;

public class HomingArrowPathFinder {
	//private static final float FUDGING = 1.5F;
	private final Node[] neighbors = new Node[32];
	private final int maxNodes;
	private final HomingArrowNodeEvaluator nodeEval;
	//private static final boolean DEBUG = false;
	private final BinaryHeap nearbyNodes = new BinaryHeap();

	public HomingArrowPathFinder(HomingArrowNodeEvaluator nodeEval, int maxNodes) {
		this.nodeEval = nodeEval;
		this.maxNodes = maxNodes;
	}

	/**
	 * Finds a path to one of the specified positions and post-processes it or
	 * returns null if no path could be found within given accuracy
	 */
	@Nullable
	public Path findPath(PathNavigationRegion region, AbstractArrow arrow, Set<BlockPos> targetPosSet, float maxRange, int accuracy,
			float searchDepthMult) {
		this.nearbyNodes.clear();
		this.nodeEval.prepare(region, arrow);
		Node node = this.nodeEval.getStart();
		Map<Target, BlockPos> map = targetPosSet.stream().collect(Collectors.toMap((pos) -> {
			return this.nodeEval.getGoal(pos.getX(), pos.getY(), pos.getZ());
		}, Function.identity()));
		Path path = this.findPath(region.getProfiler(), node, map, maxRange, accuracy, searchDepthMult);
		this.nodeEval.done();
		return path;
	}

	@Nullable
	private Path findPath(ProfilerFiller profiler, Node startNode, Map<Target, BlockPos> targetPosSet, float maxRange,
			int accuracy, float searchDepthMult) {
		profiler.push("find_path");
		profiler.markForCharting(MetricCategory.PATH_FINDING);
		Set<Target> targets = targetPosSet.keySet();
		startNode.g = 0;
		startNode.h = HomingArrowPathFinder.getBestH(startNode, targets);
		startNode.f = startNode.h;
		this.nearbyNodes.clear();
		this.nearbyNodes.insert(startNode);
		int i = 0;
		Set<Target> newTargets = Sets.newHashSetWithExpectedSize(targets.size());
		int j = (int) (this.maxNodes * searchDepthMult);

		while (!this.nearbyNodes.isEmpty()) {
			++i;
			if (i >= j) {
				break;
			}

			Node node = this.nearbyNodes.pop();
			node.closed = true;

			for (Target target : targets) {
				if (node.distanceManhattan(target) <= accuracy) {
					target.setReached();
					newTargets.add(target);
				}
			}

			if (!newTargets.isEmpty()) {
				break;
			}

			if (!(node.distanceTo(startNode) >= maxRange)) {
				int k = this.nodeEval.getNeighbors(this.neighbors, node);

				for (int l = 0; l < k; ++l) {
					Node adjNode = this.neighbors[l];
					float distToAdj = node.distanceTo(adjNode);
					adjNode.walkedDistance = node.walkedDistance + distToAdj;
					float newTotalCost = node.g + distToAdj + adjNode.costMalus;
					if (adjNode.walkedDistance < maxRange && (!adjNode.inOpenSet() || newTotalCost < adjNode.g)) {
						adjNode.cameFrom = node;
						adjNode.g = newTotalCost;
						adjNode.h = HomingArrowPathFinder.getBestH(adjNode, targets) * 1.5f;
						if (adjNode.inOpenSet()) {
							this.nearbyNodes.changeCost(adjNode, adjNode.g + adjNode.h);
						} else {
							adjNode.f = adjNode.g + adjNode.h;
							this.nearbyNodes.insert(adjNode);
						}
					}
				}
			}
		}

		Optional<Path> optPath = !newTargets.isEmpty() ? newTargets.stream().map((targ) -> {
			return HomingArrowPathFinder.reconstructPath(targ.getBestNode(), targetPosSet.get(targ), true);
		}).min(Comparator.comparingInt(Path::getNodeCount)) : targets.stream().map((targ) -> {
			return HomingArrowPathFinder.reconstructPath(targ.getBestNode(), targetPosSet.get(targ), false);
		}).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
		profiler.pop();
		return !optPath.isPresent() ? null : optPath.get();
	}

	private static float getBestH(Node pNode, Set<Target> pTargets) {
		float estCost = Float.MAX_VALUE;

		for (Target target : pTargets) {
			float newEstCost = pNode.distanceTo(target);
			target.updateBest(newEstCost, pNode);
			estCost = Math.min(newEstCost, estCost);
		}

		return estCost;
	}

	/**
	 * Converts a recursive path point structure into a path
	 */
	private static Path reconstructPath(Node startNode, BlockPos targetPos, boolean complete) {
		List<Node> nodes = Lists.newArrayList();
		Node node = startNode;
		nodes.add(0, startNode);

		while (node.cameFrom != null) {
			node = node.cameFrom;
			nodes.add(0, node);
		}

		return new Path(nodes, targetPos, complete);
	}
}