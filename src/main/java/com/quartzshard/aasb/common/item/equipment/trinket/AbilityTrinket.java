package com.quartzshard.aasb.common.item.equipment.trinket;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.item.ITrinket;
import com.quartzshard.aasb.api.item.bind.ICanEmpower;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.init.AlchemyInit.TrinketRunes;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class AbilityTrinket extends Item implements ITrinket {
	public AbilityTrinket(Properties props) {
		super(props);
	}
	private static final String TAG_RUNE_1 = "rune1";
	private static final String TAG_RUNE_2 = "rune2";
	
	@Override
	public boolean onPressedFunc2(ItemStack stack, ServerPlayer player, ServerLevel level) {
		int i = player.getRandom().nextInt(4);
		switch (i) {
		case 0:
			setRune(stack, TrinketRunes.WATER.get());
			break;
		case 1:
			setRune(stack, TrinketRunes.EARTH.get());
			break;
		case 2:
			setRune(stack, TrinketRunes.FIRE.get());
			break;
		case 3:
			setRune(stack, TrinketRunes.AIR.get());
			break;
		}
		return true;
	}
	
	public boolean hasAnyRune(ItemStack stack) {
		CompoundTag runeTag = NBTHelper.Item.getCompound(stack, TAG_RUNE_1, true);
		if (runeTag != null) {
			String runeId = runeTag.getString("rl");
			if (runeId != "aasb:null" && runeId != "") {
				ResourceLocation rl = ResourceLocation.tryParse(runeId);
				if (rl != null) {
					return TrinketRunes.exists(rl);
				}
			}
		}
		return false;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <R extends TrinketRune> boolean hasRune(ItemStack stack, R expected) {
		CompoundTag runeTag = NBTHelper.Item.getCompound(stack, TAG_RUNE_1, true);
		if (runeTag != null) {
			String runeId = runeTag.getString("rl");
			if (runeId != "aasb:null" && runeId != "") {
				ResourceLocation rl = ResourceLocation.tryParse(runeId);
				if (rl != null) {
					TrinketRune rune = TrinketRunes.get(rl);
					if (rune != null) {
						try {
							R test = (R)rune;
							return true;
						} catch (ClassCastException e) {
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Nullable
	public TrinketRune getRune(ItemStack stack) {
		CompoundTag runeTag = NBTHelper.Item.getCompound(stack, TAG_RUNE_1, true);
		if (runeTag != null) {
			String runeId = runeTag.getString("rl");
			if (runeId != "aasb:null" && runeId != "") {
				ResourceLocation rl = ResourceLocation.tryParse(runeId);
				if (rl != null) {
					return TrinketRunes.get(rl);
				}
			}
		}
		return null;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <R extends TrinketRune> R getRune(ItemStack stack, R expected) {
		CompoundTag runeTag = NBTHelper.Item.getCompound(stack, TAG_RUNE_1, true);
		if (runeTag != null) {
			String runeId = runeTag.getString("rl");
			if (runeId != "aasb:null" && runeId != "") {
				ResourceLocation rl = ResourceLocation.tryParse(runeId);
				if (rl != null) {
					TrinketRune rune = TrinketRunes.get(rl);
					if (rune != null) {
						try {
							return (R)rune;
						} catch (ClassCastException e) {}
					}
				}
			}
		}
		return null;
	}
	
	public <R extends TrinketRune> void setRune(ItemStack stack, R rune) {
		CompoundTag runeTag = new CompoundTag();
		rune.save(runeTag);
		NBTHelper.Item.setCompound(stack, TAG_RUNE_1, runeTag);
	}
}
