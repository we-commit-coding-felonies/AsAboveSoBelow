package com.quartzshard.aasb.api.alchemy.rune.form;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.BoxUtil;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WorldUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TerrainRune extends FormRune {
	public static final String
		TK_POS1 = "SelectedPosition";

	public TerrainRune() {
		super(AASB.rl("terrain"));
	}

	/**
	 * normal: 2d worldedit //set <br>
	 * strong: 3d worldedit //set
	 */
	@Override
	public boolean combatAbility(ItemStack stack, @NotNull ServerPlayer player, @NotNull ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			@NotNull ItemStack handStack = player.getMainHandItem();
			@NotNull InteractionHand hand = InteractionHand.MAIN_HAND;
			if (handStack.isEmpty() || !(handStack.getItem() instanceof BlockItem)) {
				handStack = player.getOffhandItem();
				hand = InteractionHand.OFF_HAND;
			}
			BlockHitResult hitRes = PlayerUtil.getTargetedBlockGrass(player, player.getBlockReach()-0.5);
			if (hasSelPos(stack) && !handStack.isEmpty() && handStack.getItem() instanceof BlockItem item) {
				BlockPos pos1 = getSelPos(stack);
				@Nullable AABB box = null;
				@Nullable BlockState bState = null;
				if (!strong && hitRes.getType() == HitResult.Type.MISS) {
					box = boxFromPos1AndLook(pos1, player.getLookAngle().scale(48), player.getEyePosition());
					bState = item.getBlock().defaultBlockState();
				} else if (hitRes.getType() != HitResult.Type.MISS) {
					BlockPos pos2 = hitRes.getBlockPos();
					box = new AABB(pos1,pos2);
					BlockPlaceContext ctx = new BlockPlaceContext(player, hand, handStack, hitRes);
					bState = item.getPlacementState(ctx);
				}
				if (box != null && box.getSize() <= 32) {
					@NotNull List<ItemStack> drops = new ArrayList<>();
					ItemStack breakerStack = new ItemStack(ItemInit.THE_PHILOSOPHERS_STONE.get());
					if (bState != null && !bState.isAir()) {
						int ops = 0,
							limit = PlayerUtil.getTotalHeldCount(player, handStack);
						for (BlockPos pos : BoxUtil.allBlocksInBox(box)) {
							if (ops >= limit) break;
							pos.immutable();
							BlockState curState = level.getBlockState(pos);
							if (curState != bState && PlayerUtil.hasBreakPermission(player, pos)) {
								if (!curState.isAir()) {
									drops.addAll(Block.getDrops(curState, level, pos, WorldUtil.getBlockEntity(level, pos), player, breakerStack));
								}
								level.setBlockAndUpdate(pos, bState);
								ops++;
							}
						}
						Logger.printChat(PlayerUtil.consumeItems(player, handStack, ops)+"", player);
						if (!drops.isEmpty()) {
							LootBallItem.dropBalls(player, drops);
						}
						// Operation performed, clear position and report success
						clearSelPos(stack);
						return true;
					}
				}
			} else if (hitRes.getType() != HitResult.Type.MISS) {
				setSelPos(stack, hitRes.getBlockPos());
				return true;
			}
			// Box or BlockState was invalid, so we clear
			clearSelPos(stack);
			return false;
		}
		return false;
	}

	/**
	 * normal: 2d worldedit //replace <br>
	 * strong: 3d worldedit //replace
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: aqua + aerial affinity <br>
	 * strong: block reach extension
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Creates a "2d" AABB by walking along a ray until it intersects a valid corner BlockPos <br>
	 * used for pulling walls out of the ground with the glove
	 * @return
	 */
	@Nullable
	private static AABB boxFromPos1AndLook(@NotNull BlockPos pos1, Vec3 ray, Vec3 start) {
		@NotNull Vec3 step = ray.normalize().scale(0.5);
		int numSteps = (int) (ray.length() / step.length());
		if (step.length() <= 0) {
			// avoids floating point nonsense
			numSteps = 1;
		}
		
		Vec3 curPos = start;
		for (int i = 0; i < numSteps; i++) {
			BlockPos c = BlockPos.containing(curPos);
			if (c.getX() == pos1.getX() || c.getY() == pos1.getY() || c.getZ() == pos1.getZ()) {
				return new AABB(pos1, c);
			}
			
			curPos = curPos.add(step);
		}
		return null;
	}
	
	
	private static void setSelPos(ItemStack stack, BlockPos pos) {
		setSelPos(stack, pos.asLong());
	}
	private static void setSelPos(ItemStack stack, long packedPos) {
		NBTUtil.setLong(stack, TK_POS1, packedPos);
	}
	private static void clearSelPos(ItemStack stack) {
		setSelPos(stack, 6304);
	}
	private static long getSelPosVal(ItemStack stack) {
		return NBTUtil.getLong(stack, TK_POS1, 6304);
	}
	private static BlockPos getSelPos(ItemStack stack) {
		return BlockPos.of(getSelPosVal(stack));
	}
	private static boolean hasSelPos(ItemStack stack) {
		return getSelPosVal(stack) != 6304;
	}

}
