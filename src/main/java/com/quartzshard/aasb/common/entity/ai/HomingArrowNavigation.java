package com.quartzshard.aasb.common.entity.ai;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class HomingArrowNavigation {
	protected final AbstractArrow arrow;
	protected final Level level;
	@Nullable protected Path path;
	protected double speedModifier;
	protected int tick;
	protected int lastStuckCheck;
	protected Vec3 lastStuckCheckPos = Vec3.ZERO;
	protected Vec3i timeoutCachedNode = Vec3i.ZERO;
	protected long timeoutTimer;
	protected long lastTimeoutCheck;
	protected double timeoutLimit;
	protected float maxDistanceToWaypoint = 0.5F;
	/**
	 * Whether the path can be changed by {@link net.minecraft.pathfinding.PathNavigate#onUpdateNavigation()
	 * onUpdateNavigation()}
	 */
	protected boolean hasDelayedRecomputation;
	protected long timeLastRecompute;
	@Nullable protected HomingArrowNodeEvaluator nodeEvaluator;
	@Nullable private BlockPos targetPos;
	/** Distance in which a path point counts as target-reaching */
	private int reachRange;
	private float maxVisitedNodesMultiplier = 6.0F;
	private final @NotNull HomingArrowPathFinder pathFinder;
	private boolean isStuck;
	private static float FOLLOW_RANGE = 64;

	public HomingArrowNavigation(AbstractArrow arrow, Level level) {
		this.arrow = arrow;
		this.level = level;
		int i = Mth.floor(FOLLOW_RANGE * 64.0D);
		pathFinder = createPathFinder(i);
	}

	public void resetMaxVisitedNodesMultiplier() {
		maxVisitedNodesMultiplier = 16.0F;
	}

	public void setMaxVisitedNodesMultiplier(float mult) {
		maxVisitedNodesMultiplier = mult;
	}

	@Nullable
	public BlockPos getTargetPos() {
		return targetPos;
	}

	protected HomingArrowPathFinder createPathFinder(int maxNodes) {
		nodeEvaluator = new HomingArrowNodeEvaluator();
		nodeEvaluator.setCanPassDoors(true);
		return new HomingArrowPathFinder(nodeEvaluator, maxNodes);
	}

	/**
	 * Sets the speed
	 */
	public void setSpeedModifier(double speed) {
		speedModifier = speed;
	}

	public void recomputePath() {
		if (level.getGameTime() - timeLastRecompute > 20L) {
			if (targetPos != null) {
				path = null;
				path = createPath(targetPos, reachRange);
				timeLastRecompute = level.getGameTime();
				hasDelayedRecomputation = false;
			}
		} else {
			hasDelayedRecomputation = true;
		}

	}

	/**
	 * Returns path to given BlockPos
	 */
	@Nullable
	public final Path createPath(double x, double y, double z, int pAccuracy) {
		return createPath(BlockPos.containing(x, y, z), pAccuracy);
	}
	@Nullable
	public final Path createPath(Vec3 pos, int pAccuracy) {
		return createPath(BlockPos.containing(pos), pAccuracy);
	}

	/**
	 * Returns a path to one of the elements of the stream or null
	 */
	@Nullable
	public Path createPath(Stream<BlockPos> pTargets, int pAccuracy) {
		return createPath(pTargets.collect(Collectors.toSet()), 8, false, pAccuracy);
	}

	@Nullable
	public Path createPath(@NotNull Set<BlockPos> pPositions, int pDistance) {
		return createPath(pPositions, 8, false, pDistance);
	}

	/**
	 * Returns path to given BlockPos
	 */
	@Nullable
	public Path createPath(BlockPos pPos, int pAccuracy) {
		return createPath(ImmutableSet.of(pPos), 8, false, pAccuracy);
	}

	@Nullable
	public Path createPath(BlockPos pPos, int pRegionOffset, int pAccuracy) {
		return createPath(ImmutableSet.of(pPos), 8, false, pRegionOffset, pAccuracy);
	}

	/**
	 * Returns a path to the given entity or null
	 */
	@Nullable
	public Path createPath(@NotNull Entity pEntity, int pAccuracy) {
		return createPath(ImmutableSet.of(pEntity.blockPosition()), 16, true, pAccuracy);
	}

	/**
	 * Returns a path to one of the given targets or null
	 */
	@Nullable
	protected Path createPath(Set<BlockPos> pTargets, int pRegionOffset, boolean pOffsetUpward, int pAccuracy) {
		return createPath(pTargets, pRegionOffset, pOffsetUpward, pAccuracy, FOLLOW_RANGE);
	}

	@Nullable
	protected Path createPath(@NotNull Set<BlockPos> pTargets, int pRegionOffset, boolean pOffsetUpward, int pAccuracy, float pFollowRange) {
		if (pTargets.isEmpty()) {
			return null;
		} else if (arrow.getY() < level.getMinBuildHeight()) {
			return null;
		} else if (!canUpdatePath()) {
			return null;
		} else if (path != null && !path.isDone() && pTargets.contains(targetPos)) {
			return path;
		} else {
			level.getProfiler().push("pathfind");
			BlockPos blockpos = pOffsetUpward ? arrow.blockPosition().above() : arrow.blockPosition();
			int i = (int)(pFollowRange + pRegionOffset);
			PathNavigationRegion pathnavigationregion = new PathNavigationRegion(level, blockpos.offset(-i, -i, -i), blockpos.offset(i, i, i));
			Path path = pathFinder.findPath(pathnavigationregion, arrow, pTargets, pFollowRange, pAccuracy, maxVisitedNodesMultiplier);
			level.getProfiler().pop();
			if (path != null && path.getTarget() != null) {
				targetPos = path.getTarget();
				reachRange = pAccuracy;
				resetStuckTimeout();
			}

			return path;
		}
	}

	/**
	 * Try to find and set a path to XYZ. Returns true if successful. Args : x, y, z, speed
	 */
	public boolean moveTo(double x, double y, double z, double speed) {
		return moveTo(createPath(x,y,z, 1), speed);
	}

	/**
	 * Try to find and set a path to EntityLiving. Returns true if successful. Args : entity, speed
	 */
	public boolean moveTo(@NotNull Entity entity, double speed) {
		Path path = createPath(entity, 1);
		return path != null && moveTo(path, speed);
	}

	/**
	 * Sets a new path. If it's diferent from the old path. Checks to adjust path for sun avoiding, and stores start
	 * coords. Args : path, speed
	 */
	public boolean moveTo(@Nullable Path newPath, double speed) {
		if (newPath == null) {
			path = null;
			return false;
		}
		if (!newPath.sameAs(path)) {
			path = newPath;
		}

		if (isDone()) {
			return false;
		}
		trimPath();
		if (path.getNodeCount() <= 0) {
			return false;
		}
		speedModifier = speed;
		@NotNull Vec3 tmpPos = getTempArrowPos();
		lastStuckCheck = tick;
		lastStuckCheckPos = tmpPos;
		return true;
	}

	/**
	 * gets the actively used PathEntity
	 */
	@Nullable
	public Path getPath() {
		return path;
	}

	public void tick() {
		++tick;
		if (hasDelayedRecomputation) {
			recomputePath();
		}

		if (!isDone()) {
			if (canUpdatePath()) {
				followThePath();
			} else if (path != null && !path.isDone()) {
				@NotNull Vec3 tmpPos = getTempArrowPos();
				Vec3 nextPos = path.getNextEntityPos(arrow);
				if (tmpPos.y > nextPos.y && !arrow.onGround() && Mth.floor(tmpPos.x) == Mth.floor(nextPos.x) && Mth.floor(tmpPos.z) == Mth.floor(nextPos.z)) {
					path.advance();
				}
			}

			//DebugPackets.sendPathFindingPacket(this.level, this.arrow, this.path, this.maxDistanceToWaypoint);
			//if (!this.isDone()) {
			//	Vec3 vec32 = this.path.getNextEntityPos(this.arrow);
			//	this.arrow.getMoveControl().setWantedPosition(vec32.x, this.getGroundY(vec32), vec32.z, this.speedModifier);
			//}
		}
	}

	protected double getGroundY(@NotNull Vec3 testPos) {
		BlockPos pos = BlockPos.containing(testPos);
		return level.getBlockState(pos.below()).isAir() ? testPos.y : WalkNodeEvaluator.getFloorLevel(level, pos);
	}

	protected void followThePath() {
		Vec3 tmpPos = getTempArrowPos();
		maxDistanceToWaypoint = arrow.getBbWidth() > 0.75F ? arrow.getBbWidth() / 2.0F : 0.75F - arrow.getBbWidth() / 2.0F;
		Vec3i nextNodePos = path.getNextNodePos();
		double xDist = Math.abs(arrow.getX() - (nextNodePos.getX() + (arrow.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
		double yDist = Math.abs(arrow.getY() - nextNodePos.getY());
		double zDist = Math.abs(arrow.getZ() - (nextNodePos.getZ() + (arrow.getBbWidth() + 1) / 2D)); //Forge: Fix MC-94054
		boolean flag = xDist <= maxDistanceToWaypoint && zDist <= maxDistanceToWaypoint && yDist < 1.0D; //Forge: Fix MC-94054
		if (flag || /*this.arrow.canCutCorner(this.path.getNextNode().type) && */shouldTargetNextNodeInDirection(tmpPos)) {
			path.advance();
		}

		doStuckDetection(tmpPos);
	}

	private boolean shouldTargetNextNodeInDirection(@NotNull Vec3 curPos) {
		if (path.getNextNodeIndex() + 1 >= path.getNodeCount()) {
			return false;
		}
		@NotNull Vec3 nextNodePos = Vec3.atBottomCenterOf(path.getNextNodePos());
		if (!curPos.closerThan(nextNodePos, 2.0D)) {
			return false;
		} else if (canMoveDirectly(curPos, path.getNextEntityPos(arrow))) {
			return true;
		} else {
			Vec3 afterNextNodePos = Vec3.atBottomCenterOf(path.getNodePos(path.getNextNodeIndex() + 1));
			Vec3 nextToAfterNext = afterNextNodePos.subtract(nextNodePos);
			Vec3 curToNext = curPos.subtract(nextNodePos);
			return nextToAfterNext.dot(curToNext) > 0.0D;
		}
	}

	/**
	 * Checks if entity haven't been moved when last checked and if so, clears current {@link
	 * net.minecraft.pathfinding.PathEntity}
	 */
	protected void doStuckDetection(Vec3 curPos) {
		if (tick - lastStuckCheck > 100) {
			if (curPos.distanceToSqr(lastStuckCheckPos) < 2.25D) {
				isStuck = true;
				stop();
			} else {
				isStuck = false;
			}

			lastStuckCheck = tick;
			lastStuckCheckPos = curPos;
		}

		if (path != null && !path.isDone()) {
			@NotNull Vec3i nextNodePos = path.getNextNodePos();
			if (nextNodePos.equals(timeoutCachedNode)) {
				timeoutTimer += Util.getMillis() - lastTimeoutCheck;
			} else {
				timeoutCachedNode = nextNodePos;
				double distToCacheNode = curPos.distanceTo(Vec3.atBottomCenterOf(timeoutCachedNode));
				timeoutLimit = arrow.getDeltaMovement().length() > 0.0F ? distToCacheNode / arrow.getDeltaMovement().length() * 1000.0D : 0.0D;
			}

			if (timeoutLimit > 0.0D && timeoutTimer > timeoutLimit * 3.0D) {
				timeoutPath();
			}

			lastTimeoutCheck = Util.getMillis();
		}

	}

	private void timeoutPath() {
		resetStuckTimeout();
		stop();
	}

	private void resetStuckTimeout() {
		timeoutCachedNode = Vec3i.ZERO;
		timeoutTimer = 0;
		timeoutLimit = 0.0;
		isStuck = false;
	}

	/**
	 * If null path or reached the end
	 */
	public boolean isDone() {
		return path == null || path.isDone();
	}

	public boolean isInProgress() {
		return !isDone();
	}

	/**
	 * sets active PathEntity to null
	 */
	public void stop() {
		path = null;
	}

	protected Vec3 getTempArrowPos() {
		return arrow.position();
	}

	/**
	 * If on ground or swimming and can swim
	 */
	protected boolean canUpdatePath() {
		return true;
	}

	/**
	 * Returns true if the entity is in water or lava, false otherwise
	 */
	protected boolean isInLiquid() {
		return arrow.isInWaterOrBubble() || arrow.isInLava();
	}

	/**
	 * Trims path data from the end to the first sun covered block
	 */
	protected void trimPath() {
		if (path != null) {
			for (int i = 0; i < path.getNodeCount(); ++i) {
				Node node = path.getNode(i);
				Node nextNode = i + 1 < path.getNodeCount() ? path.getNode(i + 1) : null;
				@NotNull BlockState state = level.getBlockState(new BlockPos(node.x, node.y, node.z));
				if (state.is(BlockTags.CAULDRONS)) {
					path.replaceNode(i, node.cloneAndMove(node.x, node.y + 1, node.z));
					if (nextNode != null && node.y >= nextNode.y) {
						path.replaceNode(i + 1, node.cloneAndMove(nextNode.x, node.y + 1, nextNode.z));
					}
				}
			}
		}
	}

	/**
	 * Checks if the specified entity can safely walk to the specified location.
	 */
	protected boolean canMoveDirectly(Vec3 pos1, Vec3 pos2) {
		return false;
	}

	public boolean isStableDestination(BlockPos pos) {
		BlockPos below = pos.below();
		return level.getBlockState(below).isSolidRender(level, below);
	}
	
	@Nullable
	public NodeEvaluator getNodeEvaluator() {
		return nodeEvaluator;
	}

	public void setCanFloat(boolean canSwim) {
		nodeEvaluator.setCanFloat(canSwim);
	}

	public boolean canFloat() {
		return nodeEvaluator.canFloat();
	}

	public boolean shouldRecomputePath(@NotNull BlockPos pos) {
		if (hasDelayedRecomputation)
			return false;
		else if (path != null && !path.isDone() && path.getNodeCount() != 0) {
			@Nullable Node node = path.getEndNode();
			@NotNull Vec3 mid = new Vec3((node.x + arrow.getX()) / 2.0D, (node.y + arrow.getY()) / 2.0D, (node.z + arrow.getZ()) / 2.0D);
			return pos.closerToCenterThan(mid, path.getNodeCount() - path.getNextNodeIndex());
		} else
			return false;
		
	}

	public float getMaxDistanceToWaypoint() {
		return maxDistanceToWaypoint;
	}

	public boolean isStuck() {
		return isStuck;
	}
}
