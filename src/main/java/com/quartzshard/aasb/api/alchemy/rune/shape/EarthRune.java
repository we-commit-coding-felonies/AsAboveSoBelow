package com.quartzshard.aasb.api.alchemy.rune.shape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.item.IDigStabilizer;
import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.data.tags.BlockTP;
import com.quartzshard.aasb.init.object.BlockInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.BoxUtil;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WayUtil;
import com.quartzshard.aasb.util.WorldUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

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
	public boolean combatAbility(ItemStack stack, @NotNull ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			BlockHitResult hitRes = PlayerUtil.getTargetedBlock(player, strong ? player.getBlockReach()-0.5 : 32);
			if (hitRes.getType() != HitResult.Type.MISS) {
				long held = WayUtil.getAvaliableWay(player);
				if (strong && held >= 72) {
					long limit = (int) (held/72);
					if (limit > Integer.MAX_VALUE || limit < 0) limit = 64;
					WayUtil.consumeAvaliableWay(player, 72*destructionCatalyst(player, level, hitRes.getBlockPos(), hitRes.getDirection(), (int) Math.min(64,limit)));
					PlayerUtil.coolDown(player, stack.getItem(), 20);
					return true;
				} else if (!strong && held >= 8) {
					BlockPos bPos = hitRes.getBlockPos();
					if (PlayerUtil.hasBreakPermission(player, bPos)) {
						BlockState bstate = level.getBlockState(bPos);
						if (!bstate.is(BlockTP.WAYBLAST_RESIST) && !bstate.is(BlockTP.WAYBLAST_IMMUNE) && bstate.getDestroySpeed(level, bPos) >= 0) {
							@NotNull ItemStack breakerStack = new ItemStack(ItemInit.THE_PHILOSOPHERS_STONE.get());
							breakerStack.enchant(Enchantments.SILK_TOUCH, 1);
							List<ItemStack> drops = Block.getDrops(level.getBlockState(bPos), level, bPos, WorldUtil.getBlockEntity(level, bPos), player, breakerStack);
							if (!drops.isEmpty()) LootBallItem.dropBalls(player, drops);
							level.removeBlock(bPos, false);
							level.sendParticles(ParticleTypes.EXPLOSION, bPos.getX()+0.5, bPos.getY()+0.5, bPos.getZ()+0.5, 1, 0,0,0, 0);
							WayUtil.consumeAvaliableWay(player, 8);
							return true;
						}
					}
				}
				
			}
		}
		return false;
	}

	/**
	 * Normal: Conjure temp block <br>
	 * Strong: Conjure temp angel block
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, @NotNull ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED && WayUtil.hasWay(player)) {
			BlockHitResult hitRes = PlayerUtil.getTargetedBlock(player, strong ? player.getBlockReach()-0.5 : 32);
			boolean didDo = false;
			BlockPos bPos = BlockPos.ZERO;
			if (strong && hitRes.getType() == HitResult.Type.MISS) {
				bPos = BlockPos.containing(player.getEyePosition().add(player.getLookAngle().scale(player.getBlockReach()-0.5)));
				if (level.getBlockState(bPos).isAir()) {
					level.setBlockAndUpdate(bPos, BlockInit.CRUMBLING_STONE.get().defaultBlockState());
					didDo = true;
				}
			} else if (!didDo && hitRes.getType() == HitResult.Type.BLOCK) {
				bPos = hitRes.getBlockPos().relative(hitRes.getDirection());
				if (level.getBlockState(bPos).canBeReplaced()) {
					level.setBlockAndUpdate(bPos, BlockInit.CRUMBLING_STONE.get().defaultBlockState());
					didDo = true;
				}
			}
			
			if (didDo) {
				WayUtil.consumeAvaliableWay(player, 1);
				level.playSound(null, bPos, SoundEvents.SUSPICIOUS_GRAVEL_PLACE, SoundSource.BLOCKS, 1, 0.5f);
				return true;
			}
		}
		return false;
	}

	/**
	 * Normal: Autofeed with Way <br>
	 * Strong: MekaSuit style potion resistance (incl Transmuting!)
	 */
	//@Override
	//public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
	//	return false;
	//}
	@Override
	public void tickPassive(ItemStack stack, @NotNull ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		if (this.passiveEnabled(stack)) {
			@NotNull FoodData hunger = player.getFoodData();
			long held = WayUtil.getAvaliableWay(player),
					toConsume = 0;
			if (held-toConsume >= 1 && hunger.getFoodLevel() < 20) {
				hunger.eat(1, 0);
				toConsume++;
			}
			if (strong) {
				if (held-toConsume >= 8 && hunger.getSaturationLevel() < 20 && hunger.getLastFoodLevel() >= 20) {
					 hunger.eat(1, 1);
					 toConsume += 8;
				}
				for (MobEffectInstance effect : player.getActiveEffects().stream().filter(this::canHandle).toList()) {
					if (held-toConsume >= 16) {
						speedupEffect(player, effect);
						toConsume += 16;
					} else break;
				}
			}
			if (toConsume > 0) {
				WayUtil.consumeAvaliableWay(player, toConsume);
			}
		}
	}
	@Override
	public void tickPassiveClient(ItemStack stack, Player player, Level level, boolean strong, boolean unequipped) {
		if (this.passiveEnabled(stack)) {
			//FoodData hunger = player.getFoodData();
			long held = WayUtil.getAvaliableWay(player),
					toConsume = 0;
			//if (held-toConsume >= 1 && hunger.getFoodLevel() < 20) {
			//	hunger.eat(1, 0);
			//	toConsume++;
			//}
			if (strong) {
				//if (held-toConsume >= 8 && hunger.getSaturationLevel() < 20 && hunger.getLastFoodLevel() >= 20) {
				//	 hunger.eat(0, 1);
				//	 toConsume += 8;
				//}
				for (@NotNull MobEffectInstance effect : player.getActiveEffects().stream().filter(this::canHandle).toList()) {
					if (held-toConsume >= 16) {
						speedupEffect(player, effect);
						toConsume += 16;
					} else break;
				}
			}
			if (toConsume > 0) {
				//WayUtil.consumeAvaliableWay(player, toConsume);
			}
		}
	}

	// https://github.com/mekanism/Mekanism/blob/9dc4221bcadd81a18c18205c069d5d4c6e7f4695/src/main/java/mekanism/common/content/gear/mekasuit/ModuleInhalationPurificationUnit.java#L97
	private static void speedupEffect(Player player, MobEffectInstance effect) {
		int extraTicks = effect.getEffect() == EntityInit.BUFF_TRANSMUTING.get() ? 4 : 9;
		for (int i = 0; i < extraTicks; i++) {
			speedUpEffectSafely(player, effect);
		}
	}
	private boolean canHandle(@NotNull MobEffectInstance effect) {
		return effect.getEffect() == EntityInit.BUFF_TRANSMUTING.get()
				|| effect.getEffect().getCategory() != MobEffectCategory.BENEFICIAL;
	}
	private static void speedUpEffectSafely(@NotNull LivingEntity entity, MobEffectInstance effectInstance) {
		if (effectInstance.getDuration() > 0) {
			int remainingDuration = effectInstance.tickDownDuration();
			if (remainingDuration == 0 && effectInstance.hiddenEffect != null) {
				effectInstance.setDetailsFrom(effectInstance.hiddenEffect);
				effectInstance.hiddenEffect = effectInstance.hiddenEffect.hiddenEffect;
				onChangedPotionEffect(entity, effectInstance, true);
			}
		}
	}
	private static void onChangedPotionEffect(LivingEntity entity, @NotNull MobEffectInstance effectInstance, boolean reapply) {
		entity.effectsDirty = true;
		if (reapply && !entity.level().isClientSide) {
			MobEffect effect = effectInstance.getEffect();
			effect.removeAttributeModifiers(entity, entity.getAttributes(), effectInstance.getAmplifier());
			effect.addAttributeModifiers(entity, entity.getAttributes(), effectInstance.getAmplifier());
		}
		if (entity instanceof ServerPlayer player) {
			player.connection.send(new ClientboundUpdateMobEffectPacket(entity.getId(), effectInstance));
			CriteriaTriggers.EFFECTS_CHANGED.trigger(player, null);
		}
	}
	
	// https://github.com/sinkillerj/ProjectE/blob/68fbb2dea0cf8a6394fa6c7c084063046d94cee5/src/main/java/moze_intel/projecte/gameObjs/items/DestructionCatalyst.java#L33
	private static int destructionCatalyst(ServerPlayer player, ServerLevel level, @NotNull BlockPos clickPos, Direction face, int depth) {
		List<ItemStack> drops = new ArrayList<>();
		ItemStack breakerStack = new ItemStack(ItemInit.THE_PHILOSOPHERS_STONE.get());
		int ops = 0;
		for (BlockPos bPos : BoxUtil.allBlocksInBox(getDeepBox(clickPos, face, --depth))) {
			if (level.isEmptyBlock(bPos)) {
				continue;
			}
			@NotNull BlockState state = level.getBlockState(bPos);
			float hardness = state.getDestroySpeed(level, bPos);
			if (state.is(BlockTP.WAYBLAST_RESIST) || state.is(BlockTP.WAYBLAST_IMMUNE)) {
				continue;
			}
			ops++;
			//Ensure we are immutable so that changing blocks doesn't act weird
			bPos = bPos.immutable();
			if (PlayerUtil.hasBreakPermission(player, bPos)) {
				@NotNull List<ItemStack> list = Block.getDrops(state, level, bPos, WorldUtil.getBlockEntity(level, bPos), player, breakerStack);
				drops.addAll(list);
				level.removeBlock(bPos, false);
				if (level.random.nextInt(8) == 0) {
					level.sendParticles(level.random.nextBoolean() ? ParticleTypes.EXPLOSION : ParticleTypes.LARGE_SMOKE, bPos.getX(), bPos.getY(), bPos.getZ(), 2, 0, 0, 0, 0.05);
				}
			}
		}
		if (ops > 0 && !drops.isEmpty()) {
			LootBallItem.dropBalls(player, drops);
		}
		return ops > 0 ? Math.max(1, ops/9) : 0;
	}
	/**
	 * Returns in AABB that is always 3x3 orthogonal to the side hit, but varies in depth in the direction of the side hit
	 */
	private static AABB getDeepBox(BlockPos pos, @NotNull Direction direction, int depth) {
		return switch (direction) {
			case EAST -> new AABB(pos.getX() - depth, pos.getY() - 1, pos.getZ() - 1, pos.getX(), pos.getY() + 1, pos.getZ() + 1);
			case WEST -> new AABB(pos.getX(), pos.getY() - 1, pos.getZ() - 1, pos.getX() + depth, pos.getY() + 1, pos.getZ() + 1);
			case UP -> new AABB(pos.getX() - 1, pos.getY() - depth, pos.getZ() - 1, pos.getX() + 1, pos.getY(), pos.getZ() + 1);
			case DOWN -> new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + depth, pos.getZ() + 1);
			case SOUTH -> new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ() - depth, pos.getX() + 1, pos.getY() + 1, pos.getZ());
			case NORTH -> new AABB(pos.getX() - 1, pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + depth);
		};
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
		
		public static @NotNull KillMode byID(byte id) {
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
