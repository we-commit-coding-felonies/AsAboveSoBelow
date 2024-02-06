package com.quartzshard.aasb.api.alchemy.rune.shape;

import java.util.ArrayList;
import java.util.List;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.net.client.CutParticlePacket;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.BoxUtil;
import com.quartzshard.aasb.util.Logger;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WorldUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

public class WaterRune extends ShapeRune {

	public WaterRune() {
		super(ShapeAspect.WATER);
	}

	/**
	 * Normal: Quench nearby (put out fires, defuse bombs, solidify lava) <br>
	 * Strong: Freeze target (immobilized w/ damage resistance)
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Bottomless water bucket (works in nether) <br>
	 * Strong: Floodfill water (works in nether)
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Water breathing <br>
	 * Strong: Water breathing + Dolphin's Grace
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}
	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isEnchantable() {
		return false;
	}

	@Override
	public boolean hasToolAbility() {
		return true;
	}

	@Override
	public boolean isMajorToolRune() {
		return true;
	}

	@Override
	public boolean toolAbility(ItemStack stack, ToolStyle style, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		if (state != BindState.PRESSED) return false;
		// TODO make this less specific to hermetic stuff somehow
		Item item = stack.getItem();
		if (item instanceof IHermeticTool tool) {
			long charge = tool.getStoredWay(stack);
			ItemCooldowns cd = player.getCooldowns();
			boolean onCooldown = player.getAttackStrengthScale(0) < 1 || cd.isOnCooldown(item);
			if (charge < 32 || onCooldown)
				return false;
			
			float power = tool.getEmpowerPercent(stack);
			boolean didDo = false;
			if (charge > 0) {
				if (strong) power += (power+1)*power;
				float size = 5f + 20f*power;
				AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), size, size, size);
				switch (style) {
					case PICKAXE:
						didDo = proximine(player, area, level, stack);
						break;
					default:
						break;
				}
				if (didDo) {
					InteractionHand hand = player.getOffhandItem() == stack ?
							InteractionHand.OFF_HAND :
							InteractionHand.MAIN_HAND;
					PlayerUtil.swingArm(player, level, hand);
					cd.addCooldown(stack.getItem(), 20);
					tool.setStoredWay(stack, 0);
					level.playSound(null, player.blockPosition(), FxInit.SND_WAY_SLASH.get(), SoundSource.PLAYERS, 1, 1.2f);
					PlayerUtil.doSweepAttackParticle(player, level);
				}
			}
			return didDo;
		}
		return false;
	}

	/**
	 * collects all ores in the given AABB <br>
	 * based on ToolHelper.mineOreVeinsInAOE <br>
	 * https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/utils/ToolHelper.java <br>
	 * stores item drops in a "loot ball" to avoid making a mess
	 */
	public static boolean proximine(Player player, AABB area, ServerLevel level, ItemStack stack) {
		boolean didDo = false;
		List<ItemStack> drops = new ArrayList<>();
		//ItemStack fortunePick = stack.copy();
		//fortunePick.enchant(Enchantments.BLOCK_FORTUNE, 1);
		for (BlockPos pos : BoxUtil.allBlocksInBox(area)) {
			if (level.isEmptyBlock(pos)) continue;
			BlockState state = level.getBlockState(pos);
			if (state.is(Tags.Blocks.ORES) && state.getDestroySpeed(level, pos) != -1 && stack.isCorrectToolForDrops(state)) {
				if (level.isClientSide)
					return true;
				
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				int oresMined = 0;
				if (PlayerUtil.hasBreakPermission((ServerPlayer) player, pos)) {
					oresMined++;
					drops.addAll(Block.getDrops(state, level, pos, WorldUtil.getBlockEntity(level, pos), player, stack));
					level.removeBlock(pos, false);
					NetInit.toNearbyClients(new CutParticlePacket(8, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos))), level, Vec3.atCenterOf(pos), 64);
				}
				didDo = oresMined > 0;
				/*if (oresMined > 0) {
					if (level.random.nextInt(count) == 0) {
						// prevents extreme LOUD
						if (count < 50) count++;
						level.playSound(null, pos, PESoundEvents.DESTRUCT.get(), SoundSource.PLAYERS, 0.5f, 0.8f);
					}
					didDo = true;
				}*/
			}
		}
		if (didDo) {
			LootBallItem.dropBalls(player, drops);
			return true;
		}
		return false;
	}

}
