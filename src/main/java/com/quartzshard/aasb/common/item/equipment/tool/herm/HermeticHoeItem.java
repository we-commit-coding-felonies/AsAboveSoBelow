package com.quartzshard.aasb.common.item.equipment.tool.herm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.common.item.equipment.tool.AASBToolTier;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.data.AASBTags.BlockTP;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.BoxHelper;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.NBTHelper;
import com.quartzshard.aasb.util.PlayerHelper;
import com.quartzshard.aasb.util.WorldHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class HermeticHoeItem extends HoeItem implements IHermeticTool {
	public HermeticHoeItem(int damage, float speed, Properties props) {
		super(AASBToolTier.HERMETIC, damage, speed, props);
	}
	
	public static final String TAG_OPERATION = "hoe_operation";
	
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
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_HOE_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
						byte mode = NBTHelper.Item.getByte(stack, TAG_OPERATION, (byte) 0);
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_HOE_DESC, AASBKeys.Bind.ITEMFUNC_1.fLoc(), opLangLong(mode), AASBKeys.Bind.ITEMFUNC_2.fLoc()));
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

		InteractionHand hand = player.getOffhandItem() == stack ?
				InteractionHand.OFF_HAND :
				InteractionHand.MAIN_HAND;
		boolean didDo = false;
		if (charge > 0) {
			Vec3 pos1 = null;
			Vec3 ray = null;
			Vec3 pos2 = null;
			BlockHitResult hitRes = null;
			byte op = getOperation(stack);
			if (op != 2) {
				pos1 = player.getEyePosition();
				ray = player.getLookAngle().scale(player.getReachDistance()-0.5);
				pos2 = pos1.add(ray);
				hitRes = player.level.clip(new ClipContext(pos1, pos2, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
			}
			if (op == 2 || (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK)) {
				float power = getChargePercent(stack);
				if (hasRune(stack, ShapeRune.AIR)) power += (power+1)*power;
				float size = 5f + 20f*power;
				AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), size*1.75, size*0.25, size*1.75);
				didDo = hypersickle(player, hand, hitRes, area, level, stack, op);
			}
			if (didDo) {
				PlayerHelper.swingArm(player, level, hand);
				cd.addCooldown(stack.getItem(), 20);
				setCharge(stack, 0);
				level.playSound(null, player.blockPosition(), EffectInit.Sounds.WAY_SLASH.get(), SoundSource.PLAYERS, 1, 1.2f);
				PlayerHelper.doSweepAttackParticle(player, level);
			}
		}
		return didDo;
	}

	@Override
	public boolean onPressedFunc2(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (!hasRune(stack, ShapeRune.WATER)) return false;
		changeOperation(player, stack);
		return true;
	}
	
	public byte getOperation(ItemStack stack) {
		return hasRune(stack, ShapeRune.WATER) ? NBTHelper.Item.getByte(stack, TAG_OPERATION, (byte)0) : 0;
	}
	
	public boolean changeOperation(Player player, ItemStack stack) {
		byte mode = getOperation(stack);
		byte newMode = (byte) ((mode+1)%3);
		NBTHelper.Item.setByte( stack, TAG_OPERATION, newMode );
		Component hudText = AASBLang.tc(AASBLang.TIP_GENERIC_MODE, AASBLang.tc(AASBLang.TIP_HERM_HOE_MODE), opLang(newMode));
		player.displayClientMessage(hudText, true);
		return true;
	}
	
	private static Component opLang(byte mode) {
		switch (mode) {
		default:
		case 0:
			return AASBLang.tc(AASBLang.TIP_HERM_HOE_MODE_TILL).copy().withStyle(ChatFormatting.BLUE);
		case 1:
			return AASBLang.tc(AASBLang.TIP_HERM_HOE_MODE_PATH).copy().withStyle(ChatFormatting.BLUE);
		case 2:
			return AASBLang.tc(AASBLang.TIP_HERM_HOE_MODE_CULL).copy().withStyle(ChatFormatting.BLUE);
		}
	}
	
	private static Component opLangLong(byte mode) {
		switch (mode) {
		default:
		case 0:
			return AASBLang.tc(AASBLang.TIP_HERM_HOE_MODE_TILL_LONG).copy().withStyle(ChatFormatting.BLUE);
		case 1:
			return AASBLang.tc(AASBLang.TIP_HERM_HOE_MODE_PATH_LONG).copy().withStyle(ChatFormatting.BLUE);
		case 2:
			return AASBLang.tc(AASBLang.TIP_HERM_HOE_MODE_CULL_LONG).copy().withStyle(ChatFormatting.BLUE);
		}
	}
	
	public static boolean hypersickle(Player player, InteractionHand hand, BlockHitResult hitRes, AABB area, Level level, ItemStack stack, byte operation) {
		boolean didDo = false;
		boolean air = ((IHermeticTool)ObjectInit.Items.HERMETIC_HOE.get()).hasRune(stack, ShapeRune.AIR);
		BlockPos center = hitRes == null ?
				new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z) :
				hitRes.getBlockPos();
		BlockState centerBlock = level.getBlockState(center);
		UseOnContext ctx = null;
		ToolAction act = null;
		SoundEvent snd = null;
		switch (operation) {
		case 0: // till
			ctx = new UseOnContext(level, player, hand, stack, hitRes);
			act = ToolActions.HOE_TILL;
			snd = SoundEvents.HOE_TILL;
			break;
		case 1: // path
			ItemStack shovel = new ItemStack(ObjectInit.Items.HERMETIC_SHOVEL.get());
			ctx = new UseOnContext(level, player, hand, shovel, hitRes);
			act = ToolActions.SHOVEL_FLATTEN;
			snd = SoundEvents.SHOVEL_FLATTEN;
			break;
		default: // cull
			break;
		}
		BlockState modState = ctx == null ? null : centerBlock.getToolModifiedState(ctx, act, false);
		if (modState != null) {
			if (modState.getBlock() instanceof FarmBlock) {
				modState.setValue(FarmBlock.MOISTURE, 7);
			}
			level.setBlock(center, modState, Block.UPDATE_ALL_IMMEDIATE);
			level.playSound(null, center, snd, SoundSource.BLOCKS, 1, 1);
			// we move the box to the hit block, and make it 1 block tall
			area = BoxHelper.moveBoxTo(area, center).setMinY(center.getY()+0.1d).setMaxY(center.getY()+0.9d);
		} else if (operation == 0 || operation == 1) {
			// modification of the clicked block failed
			// and we arent culling plants, so we break out early
			return false;
		} else {
			// cull plants
			List<ItemStack> drops = new ArrayList<>();
			for (BlockPos pos : BoxHelper.allBlocksInBox(area)) {
				BlockState state = level.getBlockState(pos);
				if (state.is(BlockTP.HYPERSICKLE_CAN_CULL)) {
					//Ensure we are immutable so that changing blocks doesn't act weird
					pos = pos.immutable();
					if (!level.isClientSide && PlayerHelper.hasBreakPermission((ServerPlayer) player, pos)) {
						// air too strongk, destroys all
						if (!air) drops.addAll(Block.getDrops(state, (ServerLevel) level, pos, WorldHelper.getBlockEntity(level, pos), player, stack));
						level.addDestroyBlockEffect(pos, state);
						level.removeBlock(pos, false);
						didDo = true;
					}
				}
			}
			if (didDo) {
				if (!air)
					LootBallItem.dropBalls(player, drops);
				return true;
			}
			return false;
		}
		
		// remaining code is for till & path modes only
		for (BlockPos pos : BoxHelper.allBlocksInBox(area)) {
			if (pos.equals(center))
				continue; // we already did the center block
			BlockState above = level.getBlockState(pos.above());
			if ( above.isAir()
					|| (operation == 1 && (above.getMaterial() == Material.REPLACEABLE_PLANT || above.getMaterial() == Material.REPLACEABLE_FIREPROOF_PLANT)) ) {
				BlockState state = level.getBlockState(pos); // makes a new context for this interaction
				UseOnContext modCtx = new UseOnContext(level, ctx.getPlayer(), ctx.getHand(), ctx.getItemInHand(), new BlockHitResult(
						ctx.getClickLocation().add(pos.getX() - center.getX(), pos.getY() - center.getY(), pos.getZ() - center.getZ()),
						ctx.getClickedFace(), pos, ctx.isInside()));
				boolean same = isEffectivelySameState(modState, state.getToolModifiedState(modCtx, act, true));
				if (same) {
					//Ensure we are immutable so that changing blocks doesn't act weird
					pos = pos.immutable();
					state.getToolModifiedState(modCtx, act, true);
					level.setBlock(pos, modState, Block.UPDATE_ALL_IMMEDIATE);
				}
			}
		}
		return true;
	}
	
	private static boolean isEffectivelySameState(BlockState s1, BlockState s2) {
		if (s1 == null || s2 == null)
			return false;
		if (s1 == s2)
			return true;
		else if (s1.getBlock() instanceof FarmBlock
				&& s2.getBlock() instanceof FarmBlock) {
			return true;
		}
		
		return false;
	}
}
