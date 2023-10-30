package com.quartzshard.aasb.api.item;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.quartzshard.aasb.api.item.bind.ICanEmpower;
import com.quartzshard.aasb.api.item.bind.ICanItemFunc1;
import com.quartzshard.aasb.api.item.bind.ICanItemFunc2;
import com.quartzshard.aasb.api.item.bind.ICanItemMode;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.ColorsHelper;
import com.quartzshard.aasb.util.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * common stuff for the herm tool set
 * @author solunareclipse1
 */
public interface IHermeticTool extends IShapeRuneItem, IStaticSpeedBreaker, ICanEmpower, ICanItemMode, ICanItemFunc1, ICanItemFunc2, IEnchantmentSynergizer {
	public static final String TAG_EMPOWERMENT = "empowerment_charge";
	
	default int getMaxCharge(ItemStack stack) {
		return 128;
	}
	
	default int getCharge(@NotNull ItemStack stack) {
		return NBTHelper.Item.getInt(stack, TAG_EMPOWERMENT, 0);
	}
	
	default void setCharge(ItemStack stack, int amount) {
		NBTHelper.Item.setInt(stack, TAG_EMPOWERMENT, amount);
	}
	
	default float getChargePercent(@NotNull ItemStack stack) {
		return (float)getCharge(stack) / (float)getMaxCharge(stack);
	}
	
	default int chargeBarWidth(ItemStack stack) {
		return Math.round(getCharge(stack) * 13f / getMaxCharge(stack));
	}
	default int chargeBarColor(ItemStack stack) {
		return ColorsHelper.covalenceGradient(this.getChargePercent(stack));
	}
	
	@Override
	default boolean onHeldEmpower(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (getRunesVal(stack) == 0) return false;
		int charge = getCharge(stack);
		boolean shouldTry = !player.getCooldowns().isOnCooldown(stack.getItem()) && charge <= getMaxCharge(stack)-4;
		if (shouldTry) {
			setCharge(stack, charge+4);
			level.playSound(null, player, EffectInit.Sounds.WAY_CHARGE.get(), SoundSource.PLAYERS, 0.25f, 0.48f + 0.5f * getChargePercent(stack));
			return true;
		}
		return false;
	}
	
	/**
	 * @param oldStack
	 * @param newStack
	 * @return true if the only change was empowerment charge
	 */
	default boolean onlyChargeHasChanged(ItemStack oldStack, ItemStack newStack) {
		if (!newStack.is(oldStack.getItem()))
			return true;

		CompoundTag newTag = newStack.getTag();
		CompoundTag oldTag = oldStack.getTag();

		if (newTag == null || oldTag == null)
			return !(newTag == null && oldTag == null);
		Set<String> newKeys = new HashSet<>(newTag.getAllKeys());
		Set<String> oldKeys = new HashSet<>(oldTag.getAllKeys());

		newKeys.remove(TAG_EMPOWERMENT);
		oldKeys.remove(TAG_EMPOWERMENT);

		if (!newKeys.equals(oldKeys))
			return true;

		return !newKeys.stream().allMatch(key -> Objects.equals(newTag.get(key), oldTag.get(key)));
	}
	
	@Override
	default int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		if (stack.isCorrectToolForDrops(state) && getCharge(stack) > 0) {
			return getDigState(stack) ? 2 : 0;
		}
		return 0;
	}

	@Override
	default boolean onPressedItemMode(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (hasRune(stack, ShapeRune.EARTH)) {
			toggleDigState(stack);
			player.displayClientMessage(new TranslatableComponent(
					AASBLang.TIP_GENERIC_MODE,
					AASBLang.tc(AASBLang.TIP_HERM_TOOL_STATICDIG),
					AASBLang.tc(getDigState(stack) ? AASBLang.TIP_GENERIC_ON : AASBLang.TIP_GENERIC_OFF)
					
			), true);
			return true;
		}
		return false;
	}
	

	default boolean getDigState(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, TAG_BREAKSPEED, false);
	}
	default void toggleDigState(ItemStack stack) {
		NBTHelper.Item.setBoolean(stack, TAG_BREAKSPEED, !getDigState(stack));
	}
	
	default void appendEnchText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(AASBLang.NL);
		tips.add(AASBLang.tc(AASBLang.TIP_HERM_TOOL_ENCHBONUS_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
		tips.add(AASBLang.tc(AASBLang.TIP_HERM_TOOL_ENCHBONUS_DESC));
		double maxPow = getBonusStrength(stack);
		if (hasRune(stack, ShapeRune.AIR)) maxPow *= Math.PI + Math.E;
		tips.add(AASBLang.tc(AASBLang.TIP_HERM_TOOL_ENCHBONUS_VAL, ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(maxPow)));
	}
	
	default void appendRuneText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		Tuple<ShapeRune,ShapeRune> runes = getRunes(stack);
		Component runeText;
		if (Mth.isPowerOfTwo(getRunesVal(runes))) {
			// power of 2 means theres only 1 rune
			runeText = AASBLang.tc(AASBLang.TIP_HERM_RUNE, runes.getA().fLoc()).copy().withStyle(ChatFormatting.GRAY);
		} else {
			// if its not a power of 2, then we know we have 2 runes
			runeText = AASBLang.tc(AASBLang.TIP_HERM_RUNE_MULTI,
					runes.getA().fLoc(),
					runes.getB().fLoc()
			).copy().withStyle(ChatFormatting.GRAY);
		}
		tips.add(runeText);
	}
	
	default void appendEmpowerText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(AASBLang.NL);
		Component empowerKeyText = AASBKeys.Bind.EMPOWER.fLoc();
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_TOOL_EMPOWER_DESC)); // Info
		tips.add(new TranslatableComponent(AASBLang.TIP_HERM_TOOL_EMPOWER_GUIDE, empowerKeyText)); // Key help
	}
	
	default void appendDigStabilizerText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(AASBLang.NL);
		Component modeKeyText = AASBKeys.Bind.ITEMMODE.fLoc();
		Component stateText = AASBLang.tc(getDigState(stack) ? AASBLang.TIP_GENERIC_ON : AASBLang.TIP_GENERIC_OFF);
		tips.add(AASBLang.tc(AASBLang.TIP_HERM_TOOL_STATICDIG_DESC)); // Info
		tips.add(AASBLang.tc(AASBLang.TIP_HERM_TOOL_STATICDIG_STATE, stateText, modeKeyText)); // Key help
	}
	
	default void appendMoreInfoText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(AASBLang.NL);
		Component shiftKeyText = AASBKeys.fLoc(ClientHelper.mc().options.keyShift);
		tips.add(AASBLang.tc(AASBLang.TIP_GENERIC_MOREINFO, shiftKeyText)); // Key help
	}
	
	default void attemptChargeLeak(ItemStack stack, Level level, Entity entity) {
		int effRune = hasRune(stack, ShapeRune.AIR) ? -2 :
			hasRune(stack, ShapeRune.EARTH) ? 2 : 0;
		int charge = getCharge(stack);
		if (charge > 0) {
			int toLeak = level.getGameTime() % (3+effRune) == 0 ? 1 : 0;
			if (toLeak > 0) {
				setCharge(stack, charge-toLeak);
				if (toLeak == 1)
					level.playSound(null, entity.blockPosition(), EffectInit.Sounds.WAY_LEAK.get(), entity.getSoundSource(), 1, 1);
			}
		}
	}
	
	@Override
	default double calculateBonus(ItemStack stack) {
		double chargeMod = getChargePercent(stack);
		if (hasRune(stack, ShapeRune.AIR)) chargeMod *= Math.PI + Math.E;
		return chargeMod * getBonusStrength(stack);
	}
	
	default float calcDestroySpeed(ItemStack stack, float baseSpeed) {
		float speed = baseSpeed;
		if (speed > 1) {
			double bonus = calculateBonus(stack) / 10d;
			if (bonus > 0 && shouldApplyBonus(stack)) {
				speed += speed*bonus;
			}
		}
		return speed;
	}

	public static final UUID BASE_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID BASE_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
	default Multimap<Attribute,AttributeModifier> enchAttribMods(EquipmentSlot slot, ItemStack stack, Multimap<Attribute,AttributeModifier> attribs) {
		double bonusDamage = calculateBonus(stack);
		if (bonusDamage > 0 && shouldApplyBonus(stack) && slot == EquipmentSlot.MAINHAND) {
			double bonusSpeed = bonusDamage/10d;
			double baseDamage = 0;
			double baseSpeed = 0;
			// we filter out base damage & attack speed, which we modify then apply later
			Builder<Attribute,AttributeModifier> extra = ImmutableMultimap.builder();
			for (Entry<Attribute,Collection<AttributeModifier>> attr : attribs.asMap().entrySet()) {
				if (attr.getKey() == Attributes.ATTACK_DAMAGE) {
					for (AttributeModifier mod : attr.getValue()) {
						if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
							baseDamage += mod.getAmount();
						} else {
							extra.put(Attributes.ATTACK_DAMAGE, mod);
						}
					}
				} else if (attr.getKey() == Attributes.ATTACK_SPEED) {
					for (AttributeModifier mod : attr.getValue()) {
						if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
							baseSpeed += mod.getAmount();
						} else {
							extra.put(Attributes.ATTACK_SPEED, mod);
						}
					}
				} else {
					extra.putAll(attr.getKey(), attr.getValue());
				}
			}
			Builder<Attribute,AttributeModifier> modAttr = ImmutableMultimap.builder();
			modAttr.put(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(BASE_DAMAGE_UUID, "enchantment synergy - attack damage", baseDamage + bonusDamage, AttributeModifier.Operation.ADDITION));
			modAttr.putAll(extra.build());
			modAttr.put(Attributes.ATTACK_SPEED,
					new AttributeModifier(BASE_SPEED_UUID, "enchantment synergy - swing speed", baseSpeed + Math.abs(baseSpeed*bonusSpeed), AttributeModifier.Operation.ADDITION));
			attribs = modAttr.build();
		}
		return attribs;
	}

	@Override
	default boolean handle(PressContext ctx) {
		ServerBind b = ctx.bind();
		BindState s = ctx.state();
		if (s == BindState.PRESSED) {
			switch (b) {
			case ITEMFUNC_1:
				return onPressedFunc1(ctx.stack(), ctx.player(), ctx.level());
			case ITEMFUNC_2:
				return onPressedFunc2(ctx.stack(), ctx.player(), ctx.level());
			case ITEMMODE:
				return onPressedItemMode(ctx.stack(), ctx.player(), ctx.level());
			default:
				return false;
			}
		} else if (s == BindState.HELD && b == ServerBind.EMPOWER)
			return onHeldEmpower(ctx.stack(), ctx.player(), ctx.level());
		return false;
	}
}
