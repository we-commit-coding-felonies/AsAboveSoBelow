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
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.BoxHelper;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.PlayerHelper;
import com.quartzshard.aasb.util.WorldHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.Tags;

public class HermeticShovelItem extends ShovelItem implements IHermeticTool {
	public HermeticShovelItem(Tier tier, int damage, float speed, Properties props) {
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
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_SHOVEL_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_SHOVEL_DESC, AASBKeys.Bind.ITEMFUNC_1.fLoc()));
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
			Vec3 pos1 = player.getEyePosition();
			Vec3 ray = player.getLookAngle().scale(player.getReachDistance()-0.5);
			Vec3 pos2 = pos1.add(ray);
			BlockHitResult hitRes = player.level.clip(new ClipContext(pos1, pos2, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
			if (hitRes.getType() != HitResult.Type.MISS) {
				if (hasRune(stack, ShapeRune.AIR)) power += (power)*(power);
				float size = 1f + 4f*power;
				didDo = areablast(player, hitRes.getBlockPos(), hitRes.getDirection(), size, level, stack);
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
		}
		return didDo;
	}

	/**
	 * insurance claim generator
	 */
	public static boolean areablast(Player player, BlockPos pos, Direction dir, float size, Level level, ItemStack stack) {
		boolean air = ((IHermeticTool)ObjectInit.Items.HERMETIC_SHOVEL.get()).hasRune(stack, ShapeRune.AIR);
		AABB box = BoxHelper.getCubeForAoeInFront(pos, dir, size);
		if (player.isOnGround() && !dir.getAxis().isVertical()) {
			// move the area up so that it matches with player feet
			double shift = box.getSize()/2d;
			if (size > 6.5) shift++;
			box = box.move(0, (int)shift, 0);
		}
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		ItemStack breakerStack = new ItemStack(ObjectInit.Items.OMNITOOL.get());
		for (BlockPos newPos : BoxHelper.allBlocksInBox(box)) {
			if (level.isEmptyBlock(newPos)) {
				continue;
			}
			BlockState state = level.getBlockState(newPos);
			if (state.getDestroySpeed(level, newPos) != -1 /*&& breakerStack.isCorrectToolForDrops(state)*/) {
				if (level.isClientSide) {
					return true;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				newPos = newPos.immutable();
				if (PlayerHelper.hasBreakPermission((ServerPlayer) player, newPos)) {
					if (!air || player.getRandom().nextInt() % 5 == 0) {
						drops.addAll(Block.getDrops(state, (ServerLevel) level, newPos, WorldHelper.getBlockEntity(level, newPos), player, breakerStack));
						if (player.getRandom().nextInt() % 5 == 0)
							((ServerLevel)level).sendParticles(ParticleTypes.EXPLOSION, newPos.getX()+0.5, newPos.getY()+0.5, newPos.getZ()+0.5, 1, 0, 0, 0, 0);
					}
					level.removeBlock(newPos, false);
					hasAction = true;
				}
			}
		}
		if (hasAction) {
			LootBallItem.dropBalls(player, drops);
			level.playSound(null, new BlockPos(box.getCenter()), EffectInit.Sounds.WAY_EXPLODE.get(), SoundSource.PLAYERS, 2, 1.5f);
			if (level instanceof ServerLevel lvl) {
				lvl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, box.getCenter().x, box.getCenter().y, box.getCenter().z, 1, 0, 0, 0, 0);
			}
			return true;
		}
		return false;
	}
}
