package com.quartzshard.aasb.api.alchemy.rune.shape;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.item.IDigStabilizer;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EarthRune extends ShapeRune {
	public static final String
		TK_SWORDMODE = "SwordMode";

	public EarthRune() {
		super(ShapeAspect.EARTH);
	}

	/**
	 * Normal: 2D WorldEdit <br>
	 * Strong: 3D WorldEdit
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Conjure temp block <br>
	 * Strong: Conjure temp angel block
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Autofeed with Way <br>
	 * Strong: Potion resistance (incl Transmuting!)
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
		return false;
	}

	/**
	 * Normal: Change tool mode (Dig stabilizer / Entity hurt filter) <br>
	 * Strong: Dig stabilizer INSTAMINES!!!!
	 */
	@Override
	public boolean toolAbility(ItemStack stack, ToolStyle style, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		switch (style) {
			case SWORD:
				// TODO change what gets murdered
				break;
			default:
				if (state == BindState.PRESSED && stack.getItem() instanceof IDigStabilizer item) {
					item.setDigSpeed(stack, strong ? 1 : 2);
					item.toggleDigState(stack);
				}
		}
		return false;
	}

}
