package com.quartzshard.aasb.common.block.lab.te;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * TODO: turn this into a capability
 * <p>
 * provides a standardized way for transferring of aspect stacks <br>
 * everything operates on a push system, and should never attempt to pull
 * <p>
 * if you are an addon mod developer, and want to add more lab blocks, this is the class to extend
 */
public abstract class AspectTE extends BlockEntity {
	public AspectTE(BlockEntityType<? extends AspectTE> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	/**
	 * Serverside tick function. <br>
	 * Recommend you call super.tick() if overriding <br>
	 * Default contains some aspect pushing code
	 */
	public void tick() {
		if (this.canPush()) {
			for (Direction side : Direction.values()) {
				boolean tryWay = this.canPushWayTo(side);
				boolean tryShape = this.canPushShapeTo(side);
				boolean tryForm = this.canPushFormTo(side);
				if (tryWay || tryShape || tryForm) {
					BlockPos pushToPos = this.getBlockPos().relative(side);
					BlockEntity foundEnt = this.level.getBlockEntity(pushToPos);
					if (foundEnt != null && foundEnt instanceof AspectTE pushTarget) {
						Direction oSide = side.getOpposite();
						if (pushTarget.canAccept(oSide)) {
							if (tryWay) {
								LegacyWayStack toPush = this.getWayToPush(side);
								if (toPush != null && pushTarget.canAcceptWay(oSide, toPush)) {
									pushTarget.insertWay(toPush);
									toPush.setAmount(0);
								}
							}
							if (tryShape) {
								LegacyShapeStack toPush = this.getShapeToPush(side);
								if (toPush != null && pushTarget.canAcceptShape(oSide, toPush)) {
									pushTarget.insertShape(toPush);
									toPush.setAmount(0);
								}
							}
							if (tryForm) {
								LegacyFormStack toPush = this.getFormToPush(side);
								if (toPush != null && pushTarget.canAcceptForm(oSide, toPush)) {
									pushTarget.insertForm(toPush);
									toPush.setAmount(0);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Asks this TE if it can accept anything from the given side
	 * @param side
	 * @return
	 */
	public abstract boolean canAccept(Direction side);
	/**
	 * Asks this TE if it can accept a specific WayStack from the given side <br>
	 * Unlike shape and form, WayStacks should *never* be partially transferred as they stay separate, and this should be reflected here!
	 * @param side
	 * @return
	 */
	public abstract boolean canAcceptWay(Direction side, LegacyWayStack query);
	/**
	 * Asks this TE if it can accept a specific ShapeStack from the given side <br>
	 * A stack that only partially fits should still return true
	 * @param side
	 * @return
	 */
	public abstract boolean canAcceptShape(Direction side, LegacyShapeStack query);
	/**
	 * Asks this TE if it can accept a specific FormStack from the given side <br>
	 * A stack that only partially fits should still return true
	 * @param side
	 * @return
	 */
	public abstract boolean canAcceptForm(Direction side, LegacyFormStack query);

	/**
	 * Attempts to insert the given WayStack into the TE's input buffer
	 * Unlike shape and form, WayStacks should *never* be partially transferred as they stay separate, and this should be reflected here!
	 * @param toInsert
	 * @return if the insertion was successful
	 */
	public abstract boolean insertWay(LegacyWayStack toInsert);
	/**
	 * Attempts to insert the given ShapeStack into the TE's input buffer
	 * @param toInsert
	 * @return the remainder ShapeStack that could not be inserted
	 */
	public abstract LegacyShapeStack insertShape(LegacyShapeStack toInsert);
	/**
	 * Attempts to insert the given FormStack into the TE's input buffer
	 * @param toInsert
	 * @return the remainder ShapeStack that could not be inserted
	 */
	public abstract LegacyFormStack insertForm(LegacyFormStack toInsert);

	/**
	 * Attempts to insert the given ShapeStack into the TE's input buffer
	 * @param toInsert
	 * @return the remainder ShapeStack that could not be inserted
	 */
	public abstract boolean canPush();

	public abstract boolean canPushWayTo(Direction side);
	public abstract boolean canPushShapeTo(Direction side);
	public abstract boolean canPushFormTo(Direction side);

	/**
	 * gets the next waystack to attempt pushing <br>
	 * null means there is nothing left to push (empty output)
	 * @param side the side trying to be pushed to
	 * @return
	 */
	@Nullable
	protected abstract LegacyWayStack getWayToPush(Direction side);
	/**
	 * gets the next shapestack to attempt pushing <br>
	 * null means there is nothing left to push (empty output)
	 * @param side the side trying to be pushed to
	 * @return
	 */
	@Nullable
	protected abstract LegacyShapeStack getShapeToPush(Direction side);
	/**
	 * gets the next formstack to attempt pushing <br>
	 * null means there is nothing left to push (empty output)
	 * @param side the side trying to be pushed to
	 * @return
	 */
	@Nullable
	protected abstract LegacyFormStack getFormToPush(Direction side);
}
