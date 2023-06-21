package com.quartzshard.aasb.common.item.equipment.trinket;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.item.ITrinket;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.init.ObjectInit.TrinketRunes;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RingItem extends Item implements ITrinket {
	public RingItem(Properties props) {
		super(props);
	}
	private static final String TAG_RUNE_1 = "rune1";
	private static final String TAG_RUNE_2 = "rune2";
	
	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (hasAnyRune(stack)) {
			return getRune(stack).utilityAbility(stack, player, level, BindState.PRESSED);
		}
		return false;
	}
	
	@Override
	public boolean onPressedFunc2(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (player.getRandom().nextBoolean()) {
			System.out.println("Fire");
			setRune(stack, TrinketRunes.FIRE.get());
		} else {
			System.out.println("Water");
			setRune(stack, TrinketRunes.WATER.get());
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
