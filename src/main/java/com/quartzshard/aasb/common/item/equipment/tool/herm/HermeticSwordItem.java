package com.quartzshard.aasb.common.item.equipment.tool.herm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.common.damage.source.AASBDmgSrc;
import com.quartzshard.aasb.common.item.equipment.tool.AASBToolTier;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.common.network.client.CutParticlePacket;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.util.BoxHelper;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.EntityHelper;
import com.quartzshard.aasb.util.NBTHelper;
import com.quartzshard.aasb.util.PlayerHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HermeticSwordItem extends SwordItem implements IHermeticTool {
	public HermeticSwordItem(int damage, float speed, Properties props) {
		super(AASBToolTier.HERMETIC, damage, speed, props);
	}
	
	public enum KillMode {
		HOSTILE(AASBLang.TIP_HERM_SWORD_KILLMODE_HOSTILE, ent -> ent instanceof Enemy),
		HOSTILE_PLAYER(AASBLang.TIP_HERM_SWORD_KILLMODE_HOSTILEPLAYER, ent -> ent instanceof Enemy || ent instanceof Player),
		NOT_PLAYER(AASBLang.TIP_HERM_SWORD_KILLMODE_NOTPLAYER, ent -> !(ent instanceof Player)),
		EVERYTHING(AASBLang.TIP_HERM_SWORD_KILLMODE_ALL, ent -> true);
		
		private final Predicate<LivingEntity> test;
		private final Component loc, fLoc;
		private KillMode(String langKey, Predicate<LivingEntity> test) {
			this.test = test;
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
			appendRuneText(stack, level, tips, flags);
			if (ClientHelper.shiftHeld()) {
				if (runesVal > 1) {
					boolean tryEarth = false;
					if (hasRune(runesVal, ShapeRune.WATER)) {
						tips.add(AASBLang.NL);
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_SWORD_FLAVOR).copy().withStyle(ChatFormatting.UNDERLINE));
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_SWORD_DESC, AASBKeys.Bind.ITEMFUNC_1.fLoc()));
						tryEarth = true;
					} else if (hasRune(runesVal, ShapeRune.FIRE)) {
						appendEnchText(stack, level, tips, flags);
					}
					
					if (tryEarth && hasRune(runesVal, ShapeRune.EARTH)) {
						tips.add(AASBLang.tc(AASBLang.TIP_HERM_SWORD_KILLMODE_DESC, getKillMode(stack).fLoc(), AASBKeys.Bind.ITEMMODE.fLoc()));
					}
				}
				appendEmpowerText(stack, level, tips, flags);
			} else {
				appendMoreInfoText(stack, level, tips, flags);
			}
		}
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack s, int a, T e, Consumer<T> b) {return 0;}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return calcDestroySpeed(stack, super.getDestroySpeed(stack, state));
	}
	
	@Override
	public Multimap<Attribute,AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return enchAttribMods(slot, stack, super.getAttributeModifiers(slot, stack));
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
		if (charge < 10 || isCurrentlySlashing(stack) || onCooldown)
			return false;
		
		float stage = getChargePercent(stack);
		startSlashing(stack, stage);
		cd.addCooldown(this, 1+(charge/10));
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
				InteractionHand hand = plr.getOffhandItem() == stack ?
						InteractionHand.OFF_HAND :
						InteractionHand.MAIN_HAND;
				boolean held = selected || hand == InteractionHand.OFF_HAND;
				if (hasRune(stack, ShapeRune.WATER) && active && held && charge >= (effRune == -2 ? 15 : 13)) {
					float power = getSlashingPower(stack);
					int mod = (effRune == -2 ? 6 : 5);
					float potency = (int) (mod * power);
					mod++;
					float range = mod+power*mod*(effRune == -2 ? 4 : 2);
					boolean didDo = autoSlash(potency,
							AABB.ofSize(plr.getBoundingBox().getCenter(), range, range, range), level, plr,
							getKillMode(stack).test().and(ent -> isValidAutoslashTarget(ent, plr)));
					if (didDo) {
						if (!plr.isCreative()) toLeak = 10;
						plr.resetAttackStrengthTicker();
						PlayerHelper.swingArm(plr, level, hand);
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
	
	public boolean autoSlash(float power, AABB area, Level level, Player culprit, Predicate<LivingEntity> validator) {
		List<LivingEntity> validTargets = level.getEntitiesOfClass(LivingEntity.class, area, validator);
		Map<Entity,Integer> hit = new HashMap<>();
		if (!validTargets.isEmpty()) {
			int limit = Mth.ceil(power);// + validTargets.size()/2;
			for (int i = 0; i < limit; i++) {
				LivingEntity victim = validTargets.get(level.random.nextInt(validTargets.size()));
				float damage = Math.min(13f, Mth.clamp(victim.getMaxHealth()/10f, 1f, 3f*power));
				//if (victim instanceof Player plr && GemJewelryBase.fullPristineSet(plr)) {
				//	damage = 16*power;
				//}
				EntityHelper.hurtNoDamI(victim, AASBDmgSrc.autoslash(culprit), damage);
				if (hit.containsKey(victim)) {
					hit.put(victim, hit.get(victim)+1);
				} else hit.put(victim, 1);
			}
			if (level instanceof ServerLevel lvl) {
				double rot1 = -Mth.sin(culprit.getYRot() * ((float)Math.PI / 180f));
				double rot2 = Mth.cos(culprit.getYRot() * ((float)Math.PI / 180f));
				Random rand = level.random;
				double maxNudge = 0.35;
				Vec3 offset = new Vec3(rand.nextDouble(-maxNudge, maxNudge),rand.nextDouble(-maxNudge, maxNudge),rand.nextDouble(-maxNudge, maxNudge));
				lvl.sendParticles(ParticleTypes.SWEEP_ATTACK, culprit.getX()+rot1+offset.x, culprit.getY(0.5)+offset.y, culprit.getZ()+rot2+offset.z, 1, 0, 0, 0, 0);
				for (Entry<Entity,Integer> hitEnt : hit.entrySet()) {
					// this kinda sucks and i should find a better way to do it
					AABB box = BoxHelper.growToCube(hitEnt.getKey().getBoundingBox());
					AASBNet.toNearbyClients(new CutParticlePacket(hitEnt.getValue(), box), lvl, culprit.position(), 128);
					level.playSound(null, hitEnt.getKey().blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 0.1f);
				}
			}
			level.playSound(null, culprit.blockPosition(), EffectInit.Sounds.WAY_SLASH.get(), SoundSource.PLAYERS, 0.7f, 1.5f);
			return true;
		}
		return false;
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
		return chargeBarWidth(stack);
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return chargeBarColor(stack);
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
	public boolean isDamageable(ItemStack stack) {
		return false;
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
					AASBLang.tc(AASBLang.TIP_HERM_SWORD_KILLMODE),
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
		}
		return false;
	}
}
