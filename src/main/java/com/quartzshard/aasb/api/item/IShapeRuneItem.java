package com.quartzshard.aasb.api.item;

import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

/**
 * applied runes is stored as a 4 bit number in nbt <br>
 * funky bitwise shenanigans make working with it painless
 * 
 * @author solunareclipse1
 */
public interface IShapeRuneItem {
	static final String TAG_RUNES = "shape_runes";
	public enum ShapeRune {
		WATER(AASBLang.ASPECT_SHAPE_WATER, ChatFormatting.DARK_AQUA, 8),
		EARTH(AASBLang.ASPECT_SHAPE_EARTH, ChatFormatting.DARK_GREEN, 4),
		FIRE(AASBLang.ASPECT_SHAPE_FIRE, ChatFormatting.DARK_RED, 2),
		AIR(AASBLang.ASPECT_SHAPE_AIR, ChatFormatting.GOLD, 1),
		NONE("None, this is a bug, please report it!", ChatFormatting.RESET, 0);
		
		public final byte digit;
		public final String langKey;
		private final Component loc, fLoc;
		private ShapeRune(String langKey, ChatFormatting color, int digit) {
			this.digit = (byte)digit;
			this.langKey = langKey;
			loc = new TranslatableComponent(langKey);
			fLoc = loc.copy().withStyle(color);
		}
		
		public Component loc() {
			return loc.copy();
		}
		
		public Component fLoc() {
			return fLoc.copy();
		}
		
		public static ShapeRune byDigit(int digit) {
			switch (digit) {
			case 8:
				return WATER;
			case 4:
				return EARTH;
			case 2:
				return FIRE;
			case 1:
				return AIR;
			default:
				return NONE;
			}
		}
	}

	default byte getRunesVal(ItemStack stack) {
		return NBTHelper.Item.getByte(stack, TAG_RUNES, (byte) 0);
	}
	default int getRunesVal(Tuple<ShapeRune,ShapeRune> runes) {
		return runes.getA().digit | runes.getB().digit;
	}

	default Tuple<ShapeRune,ShapeRune> getRunes(ItemStack stack) {
		return getRunes(getRunesVal(stack));
	}
	default Tuple<ShapeRune,ShapeRune> getRunes(int runesVal) {
		return getRunes((byte)runesVal);
	}
	default Tuple<ShapeRune,ShapeRune> getRunes(byte runesVal) {
		Tuple<ShapeRune,ShapeRune> pair = new Tuple<>(ShapeRune.NONE, ShapeRune.NONE);
		if (validateRunes(runesVal)) {
			for (int i = 8; i > 0; i = i>>1) {
				if ((runesVal & i) > 0) {
					ShapeRune rune = ShapeRune.byDigit(i);
					if (pair.getA() == ShapeRune.NONE)
						pair.setA(rune);
					else {
						pair.setB(rune);
						break;
					}
				}
			}
		}
		return pair;
	}

	default boolean validateRunes(ItemStack stack) {
		return validateRunes(getRunesVal(stack));
	}
	default boolean validateRunes(Tuple<ShapeRune,ShapeRune> runes) {
		return validateRunes(getRunesVal(runes));
	}
	default boolean validateRunes(int runesVal) {
		return validateRunes((byte)runesVal);
	}
	default boolean validateRunes(byte runesVal) {
		if (runesVal <= 12) {
			return runesVal >= 0 && (runesVal % 3 == 0 || Mth.isPowerOfTwo(runesVal));
		}
		return false;
	}
	
	default boolean hasRune(ItemStack stack, ShapeRune rune) {
		return hasRune(getRunesVal(stack), rune);
	}
	default boolean hasRune(Tuple<ShapeRune,ShapeRune> runes, ShapeRune rune) {
		return hasRune(getRunesVal(runes), rune);
	}
	default boolean hasRune(int runesVal, ShapeRune rune) {
		return hasRune((byte)runesVal, rune);
	}
	default boolean hasRune(byte runesVal, ShapeRune rune) {
		if (validateRunes(runesVal)) {
			return (runesVal & rune.digit) > 0;
		}
		return false;
	}

	default void setRunes(ItemStack stack, Tuple<ShapeRune,ShapeRune> runes) {
		setRunes(stack, runes.getA().digit | runes.getB().digit);
	}
	default void setRunes(ItemStack stack, int runes) {
		setRunes(stack, (byte)runes);
	}
	default void setRunes(ItemStack stack, byte runes) {
		NBTHelper.Item.setByte(stack, TAG_RUNES, runes);
	}
	
	default void clearRunes(ItemStack stack) {
		setRunes(stack, 0);
	}

	default void addRune(ItemStack stack, ShapeRune rune) {
		int runesVal = getRunesVal(stack);
		int newRunesVal = addRune(runesVal, rune);
		if (newRunesVal == runesVal) {
			LogHelper.warn("IShapeRuneItem.addRune()", "CannotAdd", "Could not add rune " + rune + " to " + stack);
		} else setRunes(stack, newRunesVal);
	}
	default Tuple<ShapeRune,ShapeRune> addRune(Tuple<ShapeRune,ShapeRune> runes, ShapeRune rune) {
		return getRunes(addRune(getRunesVal(runes), rune));
	}
	default int addRune(int runesVal, ShapeRune rune) {
		return addRune((byte)runesVal, rune);
	}
	default byte addRune(byte runesVal, ShapeRune rune) {
		int newRunesVal = runesVal | rune.digit;
		if (!validateRunes(newRunesVal)) {
			newRunesVal = runesVal;
		}
		return (byte)newRunesVal;
	}
	
	default void removeRune(ItemStack stack, ShapeRune rune) {
		if (hasRune(stack, rune)) {
			setRunes(stack, getRunesVal(stack) - rune.digit);
		}
	}
}
