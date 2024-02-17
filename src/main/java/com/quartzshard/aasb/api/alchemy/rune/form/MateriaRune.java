package com.quartzshard.aasb.api.alchemy.rune.form;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * This rune allows the user to transmute their trinket into another
 */
public class MateriaRune extends FormRune {
	public MateriaRune() {
		super(AASB.rl("materia"));
	}

	/**
	 * Transform into Bracelet
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED && stack.getItem() instanceof IRuneable item) {
			@Nullable Item morphTarget = item.getMateriaRuneTarget(stack);
			if (morphTarget != null) {
				ItemStack newStack = new ItemStack(morphTarget);
				CompoundTag nbt = stack.getOrCreateTag();
				if (!nbt.isEmpty())
					newStack.setTag(nbt);
				PlayerUtil.forceSetCurio(player, slot, slotIdx(slot, player), newStack);
				System.out.println("idiot");
				return true;
			}
		}
		return false;
	}

	/**
	 * Transform into Charm
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED && stack.getItem() instanceof IRuneable item) {
			@Nullable Item morphTarget = item.getMateriaRuneTarget(stack);
			if (morphTarget != null) {
				ItemStack newStack = new ItemStack(morphTarget);
				CompoundTag nbt = stack.getOrCreateTag();
				if (!nbt.isEmpty())
					newStack.setTag(nbt);
				PlayerUtil.forceSetCurio(player, slot, slotIdx(slot, player), newStack);
				return true;
			}
		}
		return false;
	}

	/**
	 * Transform into Glove
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED && stack.getItem() instanceof IRuneable item) {
			@Nullable Item morphTarget = item.getMateriaRuneTarget(stack);
			if (morphTarget != null) {
				ItemStack newStack = new ItemStack(morphTarget);
				CompoundTag nbt = stack.getOrCreateTag();
				if (!nbt.isEmpty())
					newStack.setTag(nbt);
				PlayerUtil.forceSetCurio(player, slot, slotIdx(slot, player), newStack);
				return true;
			}
		}
		return false;
	}
	
	private int slotIdx(String slotType, ServerPlayer player) {
		return slotType == "charm" ? 0 : PlayerUtil.getActiveRuneHandVal(player);
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {}

}
