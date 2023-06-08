package com.quartzshard.aasb.common.item.equipment.tool.dm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.item.IDarkMatterTool;
import com.quartzshard.aasb.api.item.IShapeRuneItem;
import com.quartzshard.aasb.api.item.IShapeRuneItem.ShapeRune;
import com.quartzshard.aasb.api.item.bind.ICanEmpower;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.ColorsHelper.Color;
import com.quartzshard.aasb.util.EntityHelper;
import com.quartzshard.aasb.util.ColorsHelper;
import com.quartzshard.aasb.util.MiscHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class DarkMatterSword extends SwordItem implements IDarkMatterTool {
	public DarkMatterSword(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
	}
	
	public enum KillMode {
		HOSTILE(AASBLang.TIP_DM_SWORD_KILLMODE_HOSTILE, ent -> ent instanceof Enemy),
		HOSTILE_PLAYER(AASBLang.TIP_DM_SWORD_KILLMODE_HOSTILEPLAYER, ent -> ent instanceof Enemy || ent instanceof Player),
		NOT_PLAYER(AASBLang.TIP_DM_SWORD_KILLMODE_NOTPLAYER, ent -> !(ent instanceof Player)),
		EVERYTHING(AASBLang.TIP_DM_SWORD_KILLMODE_ALL, ent -> true);
		
		private final Predicate<LivingEntity> test;
		private final String langKey;
		private final Component loc, fLoc;
		private KillMode(String langKey, Predicate<LivingEntity> test) {
			this.test = test;
			this.langKey = langKey;
			loc = new TranslatableComponent(langKey);
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
		
		public static KillMode byId(byte id) {
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

	private static final String TAG_KILLMODE = "kill_mode";
	private static final String TAG_SLASHING = "slash_power";
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		super.appendHoverText(stack, level, tips, flags);
		
		int runesVal = getRunesVal(stack);
		if (validateRunes(runesVal) && runesVal > 0) {
			Tuple<ShapeRune,ShapeRune> runes = getRunes(stack);
			Component runeText;
			if (Mth.isPowerOfTwo(runesVal)) {
				// power of 2 means theres only 1 rune
				runeText = AASBLang.tc(AASBLang.TIP_DM_RUNE, runes.getA().fLoc()).copy().withStyle(ChatFormatting.GRAY);
			} else {
				// if its not a power of 2, then we know we have 2 runes
				runeText = AASBLang.tc(AASBLang.TIP_DM_RUNE_MULTI,
						runes.getA().fLoc(),
						runes.getB().fLoc()
				).copy().withStyle(ChatFormatting.GRAY);
			}
			tips.add(runeText);
			if (runesVal > 1) {
				boolean tryEarth = false;
				if (hasRune(runesVal, ShapeRune.WATER)) {
					tips.add(AASBLang.NL);
					tips.add(AASBLang.tc(AASBLang.TIP_DM_SWORD_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
					tips.add(AASBLang.tc(AASBLang.TIP_DM_SWORD_DESC, AASBKeys.Bind.ITEMFUNC_1.fLoc()));
					tryEarth = true;
				} else if (hasRune(runesVal, ShapeRune.FIRE)) {
					appendEnchText(stack, level, tips, flags);
				}
				
				if (tryEarth && hasRune(runesVal, ShapeRune.EARTH)) {
					tips.add(AASBLang.tc(AASBLang.TIP_DM_SWORD_KILLMODE_DESC, getKillMode(stack).fLoc(), AASBKeys.Bind.ITEMMODE.fLoc()));
				}
			}
			appendEmpowerTooltip(stack, level, tips, flags);	
		}	
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (slotChanged) return true;
		return onlyChargeHasChanged(oldStack, newStack);
	}

	@Override
	public boolean onPressedFunc1(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (!hasRune(stack, ShapeRune.WATER)) return false;
		int charge = getCharge(stack);
		ItemCooldowns cd = player.getCooldowns();
		boolean onCooldown = player.getAttackStrengthScale(0) < 1 || cd.isOnCooldown(this);
		boolean air = hasRune(stack, ShapeRune.AIR);
		if (charge < (air ? 15 : 13) || isCurrentlySlashing(stack) || onCooldown)
			return false;
		
		float stage = getChargePercent(stack);
		startSlashing(stack, stage);
		cd.addCooldown(this, 1+(charge/(air ? 15 : 13)));
		return true;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		int effRune = hasRune(stack, ShapeRune.AIR) ? -2 :
			hasRune(stack, ShapeRune.EARTH) ? 2 : 0;
		int charge = getCharge(stack);
		boolean active = isCurrentlySlashing(stack);
		if (charge > 0) {
			int toLeak = level.getGameTime() % (3+effRune) == 0 ? 1 : 0;
			if (entity instanceof Player plr) {
				boolean held = selected || plr.getOffhandItem() == stack;
				if (hasRune(stack, ShapeRune.WATER) && active && held && charge >= (effRune == -2 ? 15 : 13)) {
					float power = getSlashingPower(stack);
					int mod = (effRune == -2 ? 6 : 5);
					float potency = (int) (mod * power);
					mod++;
					float range = mod+power*mod*(effRune == -2 ? 4 : 2);
					boolean didDo = MiscHelper.attackRandomInRange(potency,
							AABB.ofSize(plr.getBoundingBox().getCenter(), range, range, range), level, plr,
							getKillMode(stack).test().and(ent -> isValidAutoslashTarget(ent, plr)));
					if (didDo) {
						toLeak = effRune == -2 ? 15 : 13;
						plr.resetAttackStrengthTicker();
						if (plr.level instanceof ServerLevel lvl) {
							lvl.getChunkSource().broadcastAndSend(plr, new ClientboundAnimatePacket(plr, plr.getOffhandItem() == stack ? 3 : 0));
						}
					} else // no targets
						ceaseSlashing(stack);
				} else if (active) // doesnt meet slash requirements
					ceaseSlashing(stack);
			} else if (active) // has tag but not a player
				ceaseSlashing(stack);
			if (toLeak > 0) {
				setCharge(stack, charge-toLeak);
				if (toLeak == 1)
					level.playSound(null, entity.blockPosition(), EffectInit.Sounds.WAY_LEAK.get(), entity.getSoundSource(), 1, 1);
			}
		} else if (active) // no charge
			ceaseSlashing(stack);
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return hasRune(stack, ShapeRune.FIRE);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getCharge(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round((float)getCharge(stack) * 13f / (float)getMaxCharge(stack));
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return ColorsHelper.covalenceGradient(this.getChargePercent(stack));
	}

	public byte getMode(@NotNull ItemStack stack) {
		// if we dont have earth rune, we always target everything
		return hasRune(stack, ShapeRune.EARTH) ? NBTHelper.Item.getByte(stack, TAG_KILLMODE, (byte)0) : 3;
	}
	
	public KillMode getKillMode(ItemStack stack) {
		return KillMode.byId(getMode(stack));
	}
	
	@Override
	public int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		return 0;
	}

	@Override
	public boolean onPressedItemMode(ItemStack stack, ServerPlayer player, ServerLevel level) {
		if (!isCurrentlySlashing(stack) && hasRune(stack, ShapeRune.EARTH)) {
			//byte cur = getMode(stack);
			byte next = (byte)((getMode(stack)+1)%4);//(byte)(cur >= 3 ? 0 : cur+1);
			KillMode mode = KillMode.byId(next);
			NBTHelper.Item.setByte(stack, TAG_KILLMODE, next);
			player.displayClientMessage(new TranslatableComponent(
					AASBLang.TIP_GENERIC_MODE,
					AASBLang.tc(AASBLang.TIP_DM_SWORD_KILLMODE),
					mode.loc()
			), true);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean getDigState(ItemStack stack) {
		return false;
	}
	@Override
	public void toggleDigState(ItemStack stack) {}
	
	public boolean isCurrentlySlashing(ItemStack stack) {
		return getSlashingPower(stack) > 0;
	}
	
	public float getSlashingPower(ItemStack stack) {
		return NBTHelper.Item.getFloat(stack, TAG_SLASHING, 0);
	}
	
	public void startSlashing(ItemStack stack, float power) {
		NBTHelper.Item.setFloat(stack, TAG_SLASHING, power);
	}
	
	public void ceaseSlashing(ItemStack stack) {
		NBTHelper.Item.setFloat(stack, TAG_SLASHING, 0);
	}
	
	public boolean isValidAutoslashTarget(LivingEntity victim, Entity culprit) {
		return victim != null
				&& !victim.is(culprit)
				&& canHit(victim)
				&& !(culprit instanceof Player plr && EntityHelper.isTamedByOrTrusts(victim, plr));
	}
	
	private static boolean canHit(LivingEntity victim) {
		if (!victim.isSpectator() && victim.isAlive() && victim.isPickable()) {
			return !EntityHelper.isInvincible(victim);
		} else {
			return false;
		}
	}
}
