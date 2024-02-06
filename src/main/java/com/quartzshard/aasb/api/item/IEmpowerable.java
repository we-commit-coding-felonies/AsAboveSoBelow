package com.quartzshard.aasb.api.item;

import java.util.List;

import com.quartzshard.aasb.api.item.bind.IHandleKeybind;
import com.quartzshard.aasb.client.Keybinds;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.net.server.KeybindPacket.PressContext;
import com.quartzshard.aasb.net.server.KeybindPacket.ServerBind;
import com.quartzshard.aasb.util.Colors;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Items that can be empowered on-demand with Way from the inventory
 */
public interface IEmpowerable extends IWayHolder, IHandleKeybind {
	public static final String
		TK_IS_EMPOWERING = "IsEmpowering";
	
	@Override
	default boolean canInsertWay(ItemStack stack) {
		return getStoredWay(stack) < getMaxWay(stack);
	}
	
	@Override
	default boolean canExtractWay(ItemStack stack) {
		return false;
	}
	
	@Override
	default long getMaxWay(ItemStack stack) {
		return 128;
	}
	
	/**
	 * Gets how much Way should be charged into the item every tick the button is held down <br>
	 * Also defines the minimum amount of Way required to do a charge tick on the item,
	 * as default impl doesnt do partial charge ticks
	 * @return How much Way/t the item gets charged with
	 */
	default long wayChargeRate(ItemStack stack) {
		return 4;
	}
	
	/**
	 * Gets how much Way is drained every time it leaks
	 * @return How much Way is lost per leak
	 */
	default long wayLeakSize(ItemStack stack) {
		return 1;
	}
	
	/**
	 * Gets how many ticks between leaks 
	 * @return How many ticks between leaks
	 */
	default int wayLeakRate(ItemStack stack) {
		return 3;
	}
	
	default float getEmpowerPercent(ItemStack stack) {
		return (float)getStoredWay(stack) / (float)getMaxWay(stack);
	}
	
	default int empowerBarWidth(ItemStack stack) {
		return Math.round(getStoredWay(stack) * 13f / getMaxWay(stack));
	}
	default int empowerBarColor(ItemStack stack) {
		return Colors.materiaGradient(getEmpowerPercent(stack));
	}

	/**
	 * Checks if this is currently empowering (charging up)
	 * @param stack
	 * @return True if the stack is being empowered
	 */
	default boolean isEmpowering(ItemStack stack) {
		return NBTUtil.getBoolean(stack, TK_IS_EMPOWERING, false);
	}
	/**
	 * Sets IsEmpowering
	 * @param stack
	 * @param state
	 */
	default void setEmpowering(ItemStack stack, boolean state) {
		NBTUtil.setBoolean(stack, TK_IS_EMPOWERING, state);
	}
	/**
	 * Toggles whether this is empowering
	 * @param stack
	 */
	default void toggleEmpowering(ItemStack stack) {
		setEmpowering(stack, !isEmpowering(stack));
	}
	
	/**
	 * Whether this should start/continue empowering <br>
	 * We should stop empowering (and also not start) when this is false
	 * @param stack
	 * @return False if the stack should stop empowering
	 */
	default boolean shouldEmpower(ItemStack stack) {
		return canInsertWay(stack, wayChargeRate(stack));
	}
	
	/**
	 * Ticks the empowerment logic (charging & leaking)
	 * @param stack
	 * @param entity
	 */
	default void tickEmpower(ItemStack stack, Entity entity) {
		if (isEmpowering(stack) && shouldEmpower(stack)) {
			// Charging!!! TODO extract from waystones
			insertWay(stack, wayChargeRate(stack));
			entity.level().playSound(null, entity, FxInit.SND_WAY_CHARGE.get(), SoundSource.PLAYERS, 0.25f, 0.48f + 0.5f * getEmpowerPercent(stack));
			return;
		} else if (!shouldEmpower(stack)) {
			setEmpowering(stack, false);
		}
		// Leaking...
		if (entity.level().getGameTime() % wayLeakRate(stack) == 0) {
			long stored = getStoredWay(stack),
				toLeak = wayLeakSize(stack);
			setStoredWay(stack, stored > toLeak ? stored - toLeak : 0);
			if (stored - toLeak >= 0) entity.level().playSound(null, entity.blockPosition(), FxInit.SND_WAY_LEAK.get(), entity.getSoundSource(), 1, 1);
		}
	}
	
	default void appendEmpowerText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		tips.add(LangData.NL);
		Component empowerKeyText = Keybinds.Bind.EMPOWER.fLoc();
		tips.add(LangData.tc(LangData.TIP_TOOL_EMPOWER_DESC)); // Info
		tips.add(LangData.tc(LangData.TIP_TOOL_EMPOWER_GUIDE, empowerKeyText)); // Key help
	}
	
	@Override
	default boolean handle(PressContext ctx) {
		if (ctx.bind() == ServerBind.EMPOWER) {
			// Sets the empowering state to:
			// state == PRESSED && shouldEmpower(stack)
			setEmpowering(ctx.stack(), ctx.state() == BindState.PRESSED && shouldEmpower(ctx.stack()));
			return true;
		}
		return false;
	}
}
