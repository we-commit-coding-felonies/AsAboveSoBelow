package com.quartzshard.aasb.api.alchemy.rune.shape;

import java.util.function.Predicate;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.item.IDigStabilizer;
import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EarthRune extends ShapeRune {
	public static final String
		TK_SWORDMODE = "SwordMode";

	public EarthRune() {
		super(ShapeAspect.EARTH);
	}

	/**
	 * Normal: Psi break spell <br>
	 * Strong: Destruction catalyst
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
	 * Strong: MekaSuit style potion resistance (incl Transmuting!)
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
				if (state == BindState.PRESSED) {
					Item item = stack.getItem();
					if (!WaterRune.isCurrentlySlashing(stack) && item instanceof IRuneable runed) {
						byte next = (byte)((getKillModeByte(stack)+1)%4);//(byte)(cur >= 3 ? 0 : cur+1);
						KillMode mode = KillMode.byID(next);
						NBTUtil.setByte(stack, TK_SWORDMODE, next);
						player.displayClientMessage(Component.translatable(
								LangData.TIP_GENERIC_MODE,
								LangData.tc(LangData.TIP_SWORD_MODE),
								mode.loc()
						), true);
						return true;
					}
				}
				break;
			default:
				if (state == BindState.PRESSED && stack.getItem() instanceof IDigStabilizer digStab) {
					digStab.setDigSpeed(stack, strong ? 1 : 2);
					digStab.toggleDigState(stack);
					player.displayClientMessage(Component.translatable(
							LangData.TIP_GENERIC_MODE,
							LangData.tc(LangData.TIP_TOOL_STATICDIG),
							LangData.tc(digStab.getDigState(stack) ? LangData.TIP_GENERIC_ON : LangData.TIP_GENERIC_OFF)
							
					), true);
					return true;
				}
				break;
		}
		return false;
	}

	public static byte getKillModeByte(ItemStack stack) {
		return NBTUtil.getByte(stack, TK_SWORDMODE, 3);
	}
	
	public enum KillMode {
		HOSTILE(LangData.TIP_SWORD_MODE_HOSTILE, ent -> ent instanceof Enemy),
		HOSTILE_PLAYER(LangData.TIP_SWORD_MODE_HOSTILEPLAYER, ent -> ent instanceof Enemy || ent instanceof Player),
		NOT_PLAYER(LangData.TIP_SWORD_MODE_NOTPLAYER, ent -> !(ent instanceof Player)),
		EVERYTHING(LangData.TIP_SWORD_MODE_ALL, ent -> true);
		
		private final Predicate<LivingEntity> test;
		private final Component loc, fLoc;
		private KillMode(String langKey, Predicate<LivingEntity> test) {
			this.test = test;
			loc = LangData.tc(langKey);
			fLoc = loc.copy().withStyle(ChatFormatting.LIGHT_PURPLE);
		}
		
		public Component loc() {
			return loc.copy();
		}
		
		public Component fLoc() {
			return fLoc.copy();
		}
		
		public Predicate<LivingEntity> test() {
			return this.test;
		}
		
		public static KillMode byID(byte id) {
			switch (id) {
			default:
			case 0:
				return HOSTILE;
			case 1:
				return HOSTILE_PLAYER;
			case 2:
				return NOT_PLAYER;
			case 3:
				return EVERYTHING;
			}
		}
	}

}
