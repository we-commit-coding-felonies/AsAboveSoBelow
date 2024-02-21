package com.quartzshard.aasb.api.alchemy.rune.form;

import java.util.ArrayList;
import java.util.List;

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
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			ItemStack handStack = player.getMainHandItem();
			InteractionHand hand = InteractionHand.MAIN_HAND;
			if (handStack.isEmpty() || !(handStack.getItem() instanceof BlockItem)) {
				handStack = player.getOffhandItem();
				hand = InteractionHand.OFF_HAND;
			}
			if (!handStack.isEmpty() && handStack.getItem() instanceof BlockItem item) {
				BlockHitResult hitRes = PlayerUtil.getTargetedBlockGrass(player, player.getBlockReach()-0.5);
				if (hitRes.getType() != HitResult.Type.MISS) {
					BlockPos pos2 = hitRes.getBlockPos();
					if (hasSelPos(stack)) {
						BlockPos pos1 = getSelPos(stack);
						AABB box = new AABB(pos1,pos2);
						if (box.getSize() <= 32) {
							List<ItemStack> drops = new ArrayList<>();
							ItemStack breakerStack = new ItemStack(ItemInit.THE_PHILOSOPHERS_STONE.get());
							BlockPlaceContext ctx = new BlockPlaceContext(player, hand, handStack, hitRes);
							BlockState bState = item.getPlacementState(ctx);
							if (bState != null && !bState.isAir()) {
								int ops = 0,
									limit = PlayerUtil.getTotalHeldCount(player, handStack);
								for (BlockPos pos : BoxUtil.allBlocksInBounds(getSelPos(stack), pos2)) {
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
								clearSelPos(stack);
								return true;
							}
						}
					}
					// if we fail out of the whole setting routine after a successful raycast
					// then we just set a new pos1 instead because last was invalid (too far away, or selected was too big)
					setSelPos(stack, pos2);
					return true;
				}
			}
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
