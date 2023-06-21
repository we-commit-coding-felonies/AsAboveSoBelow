package com.quartzshard.aasb.common.item.equipment.trinket.rune;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class TrinketRune extends ForgeRegistryEntry<TrinketRune> {
	public TrinketRune() {
	}

	/**
	 * Serializes the rune to NBT <br>
	 * override if you need to put extra data <br>
	 * dont forget to override load() as well
	 * @param tag
	 * @return
	 */
	public CompoundTag save(CompoundTag tag) {
		ResourceLocation rl = ObjectInit.TrinketRunes.REGISTRY_SUPPLIER.get().getKey(this);
		tag.putString("rl", rl == null ? "aasb:null" : rl.toString());
		return tag;
	}
	
	/**
	 * Gets the rune from NBT <br>
	 * override if you need to get extra data <br>
	 * dont forget to override save() as well
	 * @param tag
	 * @return
	 */
	@Nullable
	public TrinketRune load(CompoundTag tag) {
		String str = tag.getString("rl");
		if (str == "aasb:null")
			return null;
		ResourceLocation rl = ResourceLocation.tryParse(str);
		TrinketRune rune = ObjectInit.TrinketRunes.REGISTRY_SUPPLIER.get().getValue(rl);
		return rune;
	}
	
	
	/**
	 * Called by Gloves
	 * @return if the ability was used successfully
	 */
	public abstract boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state);
	
	/**
	 * Called by Rings
	 * @return if the ability was used successfully
	 */
	public abstract boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state);
	
	/**
	 * Called by Charms every tick
	 * @return if the ability was used successfully
	 */
	public abstract boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state);

	// TODO: onRuneAdded/Removed
	//public void onRuneAdded() {}
	//public void onRuneRemoved() {}
}
