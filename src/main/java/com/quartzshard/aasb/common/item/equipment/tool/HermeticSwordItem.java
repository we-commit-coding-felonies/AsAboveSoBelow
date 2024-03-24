package com.quartzshard.aasb.common.item.equipment.tool;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune;
import com.quartzshard.aasb.api.alchemy.rune.ToolRune.ToolStyle;
import com.quartzshard.aasb.api.alchemy.rune.shape.EarthRune;
import com.quartzshard.aasb.api.alchemy.rune.shape.EarthRune.KillMode;
import com.quartzshard.aasb.api.alchemy.rune.shape.WaterRune;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.client.Keybinds;
import com.quartzshard.aasb.common.item.ItemTraits.Tier;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HermeticSwordItem extends SwordItem implements IHermeticTool {
	public HermeticSwordItem(int damage, float speed, Properties props) {
		super(Tier.HERMETIC, damage, speed, props);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		
		int runesVal = IHermeticTool.getRunesVal(stack);
		if (IHermeticTool.validateRunesVal(runesVal) && runesVal > 0) {
			appendRuneText(stack, level, tips, flags);
			if (ClientUtil.shiftHeld()) {
				if (runesVal > 1) {
					boolean tryEarth = false;
					if (hasRune(stack, AlchInit.RUNE_WATER.get())) {
						tips.add(LangData.NL);
						tips.add(LangData.tc(LangData.TIP_SWORD_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
						tips.add(LangData.tc(LangData.TIP_SWORD_DESC, Keybinds.Bind.ITEMFUNC_1.fLoc()));
						tryEarth = true;
					} else if (hasRune(stack, AlchInit.RUNE_FIRE.get())) {
						appendEnchText(stack, level, tips, flags, runesAreStrong(stack));
					}
					
					if (tryEarth && hasRune(stack, AlchInit.RUNE_EARTH.get())) {
						tips.add(LangData.tc(LangData.TIP_SWORD_MODE_DESC, getKillMode(stack).fLoc(), Keybinds.Bind.ITEMMODE.fLoc()));
					}
				}
				appendEmpowerText(stack, level, tips, flags);
			} else {
				LangData.appendMoreInfoText(stack, level, tips, flags);
			}
			tips.add(LangData.NL);
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
	public boolean shouldCauseBlockBreakReset(@NotNull ItemStack oldStack, ItemStack newStack) {
		return stacksDifferentIgnoreWay(oldStack, newStack);
    }
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
		if (slotChanged) return true;
		return stacksDifferentIgnoreWay(oldStack, newStack);
	}

	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (!level.isClientSide) {
			if (entity instanceof ServerPlayer plr && WaterRune.isCurrentlySlashing(stack)) { // cost: 15 air, 13 otherwise
				boolean strong = this.runesAreStrong(stack);
				@NotNull InteractionHand hand = plr.getOffhandItem() == stack ?
						InteractionHand.OFF_HAND :
						InteractionHand.MAIN_HAND;
				long toConsume = strong ? 15 : 10;
				boolean held = selected || hand == InteractionHand.OFF_HAND;
				if (held && getStoredWay(stack) >= toConsume) {
					float range = strong ? 35 : 18;
					range *= WaterRune.getSlashingPower(stack);
					boolean didDo = WaterRune.tickAutoSlash(plr, (ServerLevel)level,
							AABB.ofSize(plr.getBoundingBox().getCenter(), range, range, range),
							getKillMode(stack).test().and(ent -> WaterRune.isValidAutoslashTarget(ent, plr)),
							strong);
					//float power = WaterRune.getSlashingPower(stack);
					//int mod = strong ? 6 : 5;
					//float potency = (int) (mod * power);
					//mod++;
					//float range = mod+power*mod*(strong ? 4 : 2);
					//boolean didDo = WaterRune.tickAutoSlash(potency,
					//		AABB.ofSize(plr.getBoundingBox().getCenter(), range, range, range), (ServerLevel)level, plr,
					//		getKillMode(stack).test().and(ent -> WaterRune.isValidAutoslashTarget(ent, plr)));
					if (didDo) {
						if (!plr.isCreative()) {
							setStoredWay(stack, getStoredWay(stack) - toConsume);
						}
						plr.resetAttackStrengthTicker();
						PlayerUtil.swingArm(plr, level, hand);
					} else { // no targets
						WaterRune.ceaseSlashing(stack);
					}
				} else { // not held, or not enough Way
					WaterRune.ceaseSlashing(stack);
				}
			} else { 
				if (WaterRune.isCurrentlySlashing(stack)) WaterRune.ceaseSlashing(stack); // Not a player
				tickEmpower(stack, entity);
			}
		}
		
	}
	
	public KillMode getKillMode(ItemStack stack) {
		return KillMode.byID(EarthRune.getKillModeByte(stack));
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		Rune major = this.getMajorRune(stack);
		if (major instanceof ToolRune tr) {
			return tr.isEnchantable();
		}
		return false;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getStoredWay(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return empowerBarWidth(stack);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return empowerBarColor(stack);
	}

	@Override
	public ToolStyle getToolStyle(ItemStack stack) {
		return ToolStyle.SWORD;
	}
	
	@Override
	public boolean getDigState(ItemStack stack) {
		return false;
	}
	@Override
	public void toggleDigState(ItemStack stack) {}
}
