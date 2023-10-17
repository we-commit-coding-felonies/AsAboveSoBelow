package com.quartzshard.aasb.common.item.equipment.trinket;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.item.ITrinket;
import com.quartzshard.aasb.api.item.bind.ICanItemMode;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.RuneTicks;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.special.EmpowermentRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;
import com.quartzshard.aasb.init.AlchemyInit.TrinketRunes;
import com.quartzshard.aasb.util.NBTHelper;
import com.quartzshard.aasb.util.PlayerHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbilityTrinket extends Item implements ITrinket, ICanItemMode {
	public AbilityTrinket(Properties props) {
		super(props);
	}
	private static final String TAG_RUNE_1 = "rune1";
	private static final String TAG_RUNE_2 = "rune2";

	
	@Override // TODO: remove this debug code
	public boolean handle(PressContext ctx) {
		if (ITrinket.super.handle(ctx))
			return true;
		return ctx.bind() == ServerBind.ITEMMODE && randomizeRunes(ctx.player(), ctx.stack());
	}
	private boolean randomizeRunes(ServerPlayer player, ItemStack stack) {
		if (player.isShiftKeyDown()) {
			clearRunes(stack);
			setRune(stack, true, TrinketRunes.FIRE.get());
			setRune(stack, false, TrinketRunes.EMPOWERMENT.get());
		} else {
			TrinketRune[] runes = TrinketRunes.getReg().getValues().toArray(TrinketRune[]::new);
			for (int i = 0; i < 2; i++) {
				int idx = player.getRandom().nextInt(runes.length);
				TrinketRune chosen = runes[idx];
				if (chosen == null)
					chosen = runes[(idx+1)%runes.length];
				runes[idx] = null;
				setRune(stack, i == 0, chosen);
			}
		}
		return true;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (entity instanceof ServerPlayer plr && level instanceof ServerLevel lvl) {
			boolean strong = isStrong(stack);
			if (canUse(stack, plr, true)) {
				tryTickRune(getRune(stack, true), stack, plr, lvl, strong);
			}
			if (canUse(stack, plr, false)) {
				tryTickRune(getRune(stack, false), stack, plr, lvl, strong);
			}
		}
	}
	
	private <R extends TrinketRune> void tryTickRune(R rune, ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong) {
		@SuppressWarnings("null")
		RuneTicks tInfo = rune.getClass().getAnnotation(RuneTicks.class);
		tickRune(rune, tInfo, stack, player, level, strong);
	}
	
	public abstract <R extends TrinketRune> void tickRune(R rune, RuneTicks tInfo, ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong);
	
	public boolean canUse(ItemStack stack, ServerPlayer player) {
		return !PlayerHelper.onCooldown(player, stack.getItem()) && hasAnyRune(stack);
	}
	
	public boolean canUse(ItemStack stack, ServerPlayer player, boolean main) {
		return !PlayerHelper.onCooldown(player, stack.getItem()) && hasAnyRune(stack, main);
	}
	
	public boolean isStrong(ItemStack stack) {
		for (int i = 0; i < 2; i++) {
			if (getRune(stack, i == 0, (EmpowermentRune)TrinketRunes.EMPOWERMENT.get()) != null)
				return EmpowermentRune.hasBoost(stack);
		}
		return false;
	}
	
	public boolean hasAnyRune(ItemStack stack) {
		CompoundTag[] runeTags = {
				NBTHelper.Item.getCompound(stack, TAG_RUNE_1, true),
				NBTHelper.Item.getCompound(stack, TAG_RUNE_2, true)
		};
		for (int i = 0; i < 2; i++) {
			if (runeTags[i] != null) {
				String runeId = runeTags[i].getString("rl");
				if (runeId != "aasb:null" && runeId != "") {
					ResourceLocation rl = ResourceLocation.tryParse(runeId);
					if (rl != null && TrinketRunes.exists(rl)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean hasAnyRune(ItemStack stack, boolean main) {
		CompoundTag runeTag = NBTHelper.Item.getCompound(stack, main?TAG_RUNE_1:TAG_RUNE_2, true);
		if (runeTag != null) {
			String runeId = runeTag.getString("rl");
			if (runeId != "aasb:null" && runeId != "") {
				ResourceLocation rl = ResourceLocation.tryParse(runeId);
				if (rl != null && TrinketRunes.exists(rl)) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <R extends TrinketRune> boolean hasRune(ItemStack stack, @SuppressWarnings("unused") R expected) {
		CompoundTag[] runeTags = {
				NBTHelper.Item.getCompound(stack, TAG_RUNE_1, true),
				NBTHelper.Item.getCompound(stack, TAG_RUNE_2, true)
		};
		for (int i = 0; i < 2; i++) {
			if (runeTags[i] != null) {
				String runeId = runeTags[i].getString("rl");
				if (runeId != "aasb:null" && runeId != "") {
					ResourceLocation rl = ResourceLocation.tryParse(runeId);
					if (rl != null) {
						TrinketRune rune = TrinketRunes.get(rl);
						if (rune != null) {
							try {
								@SuppressWarnings("unused")
								R test = (R)rune;
								return true;
							} catch (ClassCastException e) {
								continue;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	@Nullable
	public TrinketRune getRune(ItemStack stack, boolean main) {
		CompoundTag runeTag = NBTHelper.Item.getCompound(stack, main?TAG_RUNE_1:TAG_RUNE_2, true);
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
	public <R extends TrinketRune> R getRune(ItemStack stack, boolean main, @SuppressWarnings("unused") R expected) {
		CompoundTag runeTag = NBTHelper.Item.getCompound(stack, main?TAG_RUNE_1:TAG_RUNE_2, true);
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

	@Nullable
	@SuppressWarnings("unchecked")
	public <R extends TrinketRune> R getRune(ItemStack stack, @SuppressWarnings("unused") R expected) {
		CompoundTag[] runeTags = {
				NBTHelper.Item.getCompound(stack, TAG_RUNE_1, true),
				NBTHelper.Item.getCompound(stack, TAG_RUNE_2, true)
		};
		for (int i = 0; i < 2; i++) {
			if (runeTags[i] != null) {
				String runeId = runeTags[i].getString("rl");
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
		}
		return null;
	}
	
	public <R extends TrinketRune> void setRune(ItemStack stack, boolean main, R rune) {
		CompoundTag runeTag = new CompoundTag();
		rune.save(runeTag);
		NBTHelper.Item.setCompound(stack, main?TAG_RUNE_1:TAG_RUNE_2, runeTag);
	}
	
	public  void clearRunes(ItemStack stack) {
		NBTHelper.Item.removeEntry(stack, TAG_RUNE_1);
		NBTHelper.Item.removeEntry(stack, TAG_RUNE_2);
	}
}
