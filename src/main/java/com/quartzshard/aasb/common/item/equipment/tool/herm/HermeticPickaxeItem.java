package com.quartzshard.aasb.common.item.equipment.tool.herm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.common.network.client.CutParticlePacket;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.BoxHelper;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.PlayerHelper;
import com.quartzshard.aasb.util.WorldHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.Tags;

public class HermeticPickaxeItem extends PickaxeItem implements IHermeticTool {
	public HermeticPickaxeItem(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		
		int runesVal = getRunesVal(stack);
		if (validateRunes(runesVal) && runesVal > 0) {
			appendRuneText(stack, level, tips, flags);
			if (ClientHelper.shiftHeld()) {
				if (runesVal > 1) {
					if (hasRune(runesVal, ShapeRune.WATER)) {
						tips.add(AASBLang.NL);
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_PICKAXE_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_PICKAXE_DESC, AASBKeys.Bind.ITEMFUNC_1.fLoc()));
					} else if (hasRune(runesVal, ShapeRune.FIRE)) {
						appendEnchText(stack, level, tips, flags);
					}
					
					if (hasRune(runesVal, ShapeRune.EARTH)) {
						appendDigStabilizerText(stack, level, tips, flags);
					}
				}
				appendEmpowerText(stack, level, tips, flags);
			} else {
				appendMoreInfoText(stack, level, tips, flags);
			}
			//tips.add(AASBLang.NL);
		}
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack s, int a, T e, Consumer<T> b) {return 0;}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float sup = super.getDestroySpeed(stack, state);
		float calced = calcDestroySpeed(stack, sup);
		return calced;
	}
	
	@Override
	public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return enchAttribMods(slot, stack, super.getAttributeModifiers(slot, stack));
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return onlyChargeHasChanged(oldStack, newStack);
    }
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (slotChanged) return true;
		return onlyChargeHasChanged(oldStack, newStack);
	}

	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		attemptChargeLeak(stack, level, entity);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return hasRune(stack, ShapeRune.FIRE);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getCharge(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return chargeBarWidth(stack);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return chargeBarColor(stack);
	}

	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (!hasRune(stack, ShapeRune.WATER)) return false;
		int charge = getCharge(stack);
		ItemCooldowns cd = player.getCooldowns();
		boolean onCooldown = player.getAttackStrengthScale(0) < 1 || cd.isOnCooldown(this);
		if (charge < 32 || onCooldown)
			return false;
		
		float power = getChargePercent(stack);
		boolean didDo = false;
		if (charge > 0) {
			if (hasRune(stack, ShapeRune.AIR)) power += (power+1)*power;
			float size = 5f + 20f*power;
			AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), size, size, size);
			didDo = proximine(player, area, level, stack);
			if (didDo) {
				InteractionHand hand = player.getOffhandItem() == stack ?
						InteractionHand.OFF_HAND :
						InteractionHand.MAIN_HAND;
				PlayerHelper.swingArm(player, level, hand);
				cd.addCooldown(stack.getItem(), 20);
				setCharge(stack, 0);
				level.playSound(null, player.blockPosition(), EffectInit.Sounds.WAY_SLASH.get(), SoundSource.PLAYERS, 1, 1.2f);
				PlayerHelper.doSweepAttackParticle(player, level);
			}
		}
		return didDo;
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
		ItemStack fortunePick = stack.copy();
		//fortunePick.enchant(Enchantments.BLOCK_FORTUNE, 1);
		for (BlockPos pos : BoxHelper.allBlocksInBox(area)) {
			if (level.isEmptyBlock(pos)) continue;
			BlockState state = level.getBlockState(pos);
			if (state.is(Tags.Blocks.ORES) && state.getDestroySpeed(level, pos) != -1 && stack.isCorrectToolForDrops(state)) {
				if (level.isClientSide)
					return true;
				
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				int oresMined = 0;
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
					oresMined++;
					drops.addAll(Block.getDrops(state, (ServerLevel) level, pos, WorldHelper.getBlockEntity(level, pos), player, stack));
					level.removeBlock(pos, false);
					AASBNet.toNearbyClients(new CutParticlePacket(8, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos))), level, Vec3.atCenterOf(pos), 64);
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
