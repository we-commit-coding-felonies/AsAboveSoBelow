package com.quartzshard.aasb.api.item;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.quartzshard.aasb.api.item.IShapeRuneItem.ShapeRune;
import com.quartzshard.aasb.api.item.bind.ICanEmpower;
import com.quartzshard.aasb.api.item.bind.ICanItemFunc1;
import com.quartzshard.aasb.api.item.bind.ICanItemMode;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * common stuff for the dm tool set
 * @author solunareclipse1
 */
public interface IDarkMatterTool extends IShapeRuneItem, IStaticSpeedBreaker, ICanEmpower, ICanItemMode, ICanItemFunc1 {
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
	
	@Override
	default boolean onHeldEmpower(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (getRunesVal(stack) == 0) return false;
		int charge = getCharge(stack);
		boolean shouldTry = !player.getCooldowns().isOnCooldown(stack.getItem()) && charge <= getMaxCharge(stack)-4;
		if (shouldTry) {
			setCharge(stack, charge+4);
			level.playSound(null, player, EffectInit.Sounds.WAY_CHARGE.get(), SoundSource.PLAYERS, 0.5f, 0.48f + 0.5f * getChargePercent(stack));
			return true;
		}
		return false;
	}
	
	default void appendEmpowerTooltip(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(new TextComponent(" "));
		Component empowerKeyText = AASBKeys.Bind.EMPOWER.fLoc();
		tips.add(new TranslatableComponent(AASBLang.TIP_DM_TOOL_EMPOWER_DESC)); // Info
		tips.add(new TranslatableComponent(AASBLang.TIP_DM_TOOL_EMPOWER_GUIDE, empowerKeyText)); // Key help
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
					AASBLang.tc(AASBLang.TIP_DM_TOOL_STATICDIG),
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
		tips.add(AASBLang.tc(AASBLang.TIP_DM_TOOL_ENCHBONUS_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
		tips.add(AASBLang.tc(AASBLang.TIP_DM_TOOL_ENCHBONUS_DESC));
		tips.add(AASBLang.tc(AASBLang.TIP_DM_TOOL_ENCHBONUS_VAL, AASBKeys.Bind.ITEMFUNC_1.fLoc()));
	}

	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.ITEMFUNC_1) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedFunc1(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldFunc1(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedFunc1(ctx.stack(), ctx.player(), ctx.level());
			}
		} else if (ctx.bind() == ServerBind.EMPOWER) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedEmpower(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldEmpower(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedEmpower(ctx.stack(), ctx.player(), ctx.level());
			}
		} else if (ctx.bind() == ServerBind.ITEMMODE) {
			switch (ctx.state()) {
			case PRESSED:
				return onPressedItemMode(ctx.stack(), ctx.player(), ctx.level());
			case HELD:
				return onHeldItemMode(ctx.stack(), ctx.player(), ctx.level());
			case RELEASED:
				return onReleasedItemMode(ctx.stack(), ctx.player(), ctx.level());
			}
		}
		return false;
	}
	
	
}
