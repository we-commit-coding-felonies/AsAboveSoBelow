package com.quartzshard.aasb.common.item.equipment.trinket.rune.form;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.common.damage.source.AASBDmgSrc;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.AmuletItem;
import com.quartzshard.aasb.common.item.equipment.trinket.RingItem;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.RuneTicks;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.common.network.client.DrawParticleAABBPacket;
import com.quartzshard.aasb.common.network.client.DrawParticleAABBPacket.AABBParticlePreset;
import com.quartzshard.aasb.common.network.client.ModifyPlayerVelocityPacket;
import com.quartzshard.aasb.common.network.client.ModifyPlayerVelocityPacket.VecOp;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.data.AASBTags.BlockTP;
import com.quartzshard.aasb.data.AASBTags.EntityTP;
import com.quartzshard.aasb.data.AASBTags.TETP;
import com.quartzshard.aasb.init.AlchemyInit.TrinketRunes;
import com.quartzshard.aasb.init.EffectInit;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.BoxHelper;
import com.quartzshard.aasb.util.EntityHelper;
import com.quartzshard.aasb.util.NBTHelper;
import com.quartzshard.aasb.util.PlayerHelper;
import com.quartzshard.aasb.util.WorldHelper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunk.BoundTickingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk.RebindableTickingBlockEntityWrapper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.IPlantable;

@RuneTicks(utility = true)
public class ArcaneRune extends TrinketRune {
	/** splits things into categories so that multiple similar items dont clog up drops */
	public static final Item[][] ITEMIZER_DEFAULTS = {
			{
				Items.APPLE,
				Items.ENCHANTED_GOLDEN_APPLE,
				Items.GOLDEN_APPLE
			},{
				Items.CLAY_BALL,
				Items.MAGMA_CREAM,
				Items.SLIME_BALL
			},{
				Items.BEETROOT_SEEDS,
				Items.MELON_SEEDS,
				Items.PUMPKIN_SEEDS,
				Items.WHEAT_SEEDS
			},{
				Items.GLOW_BERRIES,
				Items.SWEET_BERRIES
			},{
				Items.HONEY_BOTTLE,
				Items.MILK_BUCKET
			},{
				Items.ENDER_EYE,
				Items.ENDER_PEARL
			},{
				Items.ARROW,
				Items.BLAZE_ROD,
				Items.END_ROD,
				Items.LIGHTNING_ROD,
				Items.SPECTRAL_ARROW,
				Items.STICK,
				Items.TRIDENT
			},{
				Items.MUSIC_DISC_11,
				Items.MUSIC_DISC_13,
				Items.MUSIC_DISC_BLOCKS,
				Items.MUSIC_DISC_CAT,
				Items.MUSIC_DISC_CHIRP,
				Items.MUSIC_DISC_FAR,
				Items.MUSIC_DISC_MALL,
				Items.MUSIC_DISC_MELLOHI,
				Items.MUSIC_DISC_OTHERSIDE,
				Items.MUSIC_DISC_PIGSTEP,
				Items.MUSIC_DISC_STAL,
				Items.MUSIC_DISC_STRAD,
				Items.MUSIC_DISC_WAIT,
				Items.MUSIC_DISC_WARD
			}
	};
	public static final UUID TIME_ACCEL_UUID = UUID.fromString("311f77f1-5573-431d-8340-06511e72d28f");
	public static final String TAG_ACCELSTART = "TAStartTick";

	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		Vec3 ray = player.getLookAngle().scale(player.getReachDistance()-0.5);
		EntityHitResult hitRes = ProjectileUtil.getEntityHitResult(level, player, player.getEyePosition(), player.getEyePosition().add(ray), player.getBoundingBox().expandTowards(ray).inflate(1.0D), ArcaneRune::canItemize);
		if (hitRes != null && hitRes.getType() == HitResult.Type.ENTITY) {
			LivingEntity lEnt = (LivingEntity)hitRes.getEntity();
			if (strong) {
				BlockHitResult sightCheck = level.clip(new ClipContext(player.getEyePosition(), hitRes.getEntity().getBoundingBox().getCenter(), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
				if (sightCheck != null && sightCheck.getType() == HitResult.Type.MISS) {
					int hp = Math.max(1,(int)lEnt.getHealth());
					if (entityItemizer(lEnt, player, null)) {
						PlayerHelper.coolDown(player, stack.getItem(), hp*7);
						return true;
					}
				}
			}

			lEnt.hurt(AASBDmgSrc.strongTransmutation(player), Math.max(1, lEnt.getMaxHealth()/2));
			lEnt.addEffect(new MobEffectInstance(ObjectInit.MobEffects.TRANSMUTING.get(), 60, 0), player);
			//EmcHelper.consumeAvaliableEmc(player, Philo.TRANSMUTE.get());
			//level.playSound(null, player, EffectInit.PHILO_ATTACK.get(), SoundSource.PLAYERS, 1, 2);
			PlayerHelper.coolDown(player, stack.getItem(), 30);
			return true;
		}
		return false;
	}

	
	/**
	 * this is awful
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		boolean active = isActive(stack); // if we are considered active (TAStartTick >= 0)
		if (state == BindState.PRESSED) {
			// toggle code
			if (!shouldDoAccel(player)) {
				// since there is nothing active, we enable ourself
				enable(stack, level);
				return true;
			} else if (active) {
				// another is already active, disable ourself
				// dont need to check for anything else because
				// no matter which hand is enabled (main, off, or both)
				// turning off will mean the number enabled <= 1
				disable(stack);
				return true;
			}
		} else if (state == BindState.HELD) {
			// tick acceleration code
			boolean inOffhand = player.getItemBySlot(EquipmentSlot.OFFHAND) == stack; // flag for if we are in offhand slot
			boolean isHeld = inOffhand || player.getItemInHand(InteractionHand.MAIN_HAND) == stack; // true if we are being held
			if (isHeld) {
				InteractionHand myHand = inOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND; // the hand we are being held in
				InteractionHand otherHand = inOffhand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND; // the other hand that we are *NOT* being held in
				//boolean meValid = validHeldInHand(player, myHand);
				boolean otherValid = validHeldInHand(player, otherHand);
				if (!otherValid && validHeldInHand(player, myHand)) { // meValid && !otherValid
					// we are the only valid, so we perform the accel
					jojoReference(player, stack, strong ? 24 : 12, (int)accelTime(stack, level), 1200, strong ? 6 : 0, Long.MAX_VALUE);
					return true;
				} else if (!otherValid) {
					// neither is valid, so we remove speed boost
					endAccel(player, stack);
				}
			} else if (!shouldDoAccel(player)) {
				endAccel(player, stack);
			}
			// fall back on just disabling ourself
			disable(stack);
			// old
			/*boolean doing = meAccel || otherAccel;
			if (otherAccel) {
				// we are already accelerating with the other hand, so we stop to prevent stacking
				stopAccel(stack);
				resetTimeAccelSpeed(player);
				return false;
			} else if (!meAccel) {
				// no currently held accel item, so we stop
				if ()
				stopAccel(stack);
				resetTimeAccelSpeed(player);
				boolean held = offhand || player.getItemInHand(InteractionHand.MAIN_HAND) == stack;
			}
			int time = (int)accelTime(stack, level);*/
			//jojoReference(player, stack, strong ? 60 : 30, /*Integer.MAX_VALUE - */time, 1200, strong ? 24 : 0, Long.MAX_VALUE);
			//return true;
		}
		return false;
	}

	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// gem of density???
		return false;
	}
	

	public static boolean shouldDoAccel(Player player) {
		return validHeldInHand(player, InteractionHand.MAIN_HAND)
				|| validHeldInHand(player, InteractionHand.OFF_HAND);
	}
	
	public static boolean validHeldInHand(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof RingItem ring) {
			ArcaneRune rune = ring.getRune(stack, (ArcaneRune)TrinketRunes.ARCANE.get());
			if (rune != null) {
				long start = rune.getStartTime(stack);
				return 0 <= start && start <= player.level.getGameTime();
			}
		}
		return false;
	}
	
	public long getStartTime(ItemStack stack) {
		return NBTHelper.Item.getLong(stack, TAG_ACCELSTART, -1);
	}
	
	public boolean isActive(ItemStack stack) {
		return NBTHelper.Item.getLong(stack, TAG_ACCELSTART, -1) >= 0;
	}
	
	public long accelTime(ItemStack stack, Level level) {
		return level.getGameTime() - getStartTime(stack);
	}
	
	public void enable(ItemStack stack, Level level) {
		NBTHelper.Item.setLong(stack, TAG_ACCELSTART, level.getGameTime());
	}
	
	public void endAccel(Player player, ItemStack stack) {
		disable(stack);
		resetTimeAccelSpeed(player);
	}
	
	public void disable(ItemStack stack) {
		NBTHelper.Item.setLong(stack, TAG_ACCELSTART, -1);
	}
	
	/**
	 * removes all time accel attribute modifiers from a player
	 * @param player
	 */
	public void resetTimeAccelSpeed(Player player) {
		for (Attribute atr : getTimeAccelAttributes()) {
			player.getAttribute(atr).removeModifier(TIME_ACCEL_UUID);
		}
	}

	public static boolean canItemize(Entity victim) {
		return victim instanceof LivingEntity lEnt
				&& !lEnt.isDeadOrDying()
				&& !lEnt.isInvulnerableTo(AASBDmgSrc.TRANSMUTATION_POTION)
				&& !EntityHelper.isInvincible(lEnt);
	}
	/**
	 * Transforms an entity into a random item. Some entities are immune for technical reasons. <br>
	 * For most entities, possible items are chosen from their loot table, but there are some special exceptions. <br>
	 * There are also a few 'universal' items, which are always in the list of possible items.
	 * 
	 * @return if the entity was actually transformed
	 */
	public static boolean entityItemizer(LivingEntity entity, @Nullable Entity culprit, @Nullable Entity cause) {
		if (!entity.getType().is(EntityTP.ITEMIZER_BLACKLIST)) {
			if (entity instanceof Player player) {
				// TODO: something a bit more interesting than this
				if (player.getHealth() < player.getMaxHealth()/2 || !AmuletItem.isBarrierActive(player)) {
					player.setLastHurtByPlayer(null);
					player.hurt(AASBDmgSrc.strongTransmutation(culprit), Float.MAX_VALUE);
				} else return false;
			}
			List<ItemStack> possible = new ArrayList<>();
			
			for (Item[] cat : ITEMIZER_DEFAULTS) {
				// we take a random item from each category and make itemstack with it, which goes on the list
				possible.add( new ItemStack(cat[entity.getRandom().nextInt(cat.length)]) );
			}
			
			// getting things from entity loot table
			ResourceLocation resourcelocation = entity.getLootTable();
			LootTable loottable = entity.level.getServer().getLootTables().get(resourcelocation);
			LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)entity.level))
					.withRandom(entity.getRandom()).withParameter(LootContextParams.THIS_ENTITY, entity)
					.withParameter(LootContextParams.ORIGIN, entity.position())
					.withParameter(LootContextParams.DAMAGE_SOURCE, AASBDmgSrc.TRANSMUTATION_POTION)
					.withOptionalParameter(LootContextParams.KILLER_ENTITY, culprit)
					.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, cause);
			if (culprit instanceof Player player) {
				lootcontext$builder = lootcontext$builder
						.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
						.withLuck(player.getLuck());
			}
			LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
			for (ItemStack stack : loottable.getRandomItems(ctx)) {
				stack.setCount(1);
				possible.add(stack);
			}
			
			// entities with special drops
			/*if (entity instanceof EntityDoppleganger gaia) {
				if (gaia.isHardMode()) {
					possible.add(new ItemStack(ModItems.gaiaIngot));
					possible.add(new ItemStack(ModItems.dice));
					possible.add(ItemDice.RELIC_STACKS.get().get(entity.getRandom().nextInt(ItemDice.RELIC_STACKS.get().size())).copy());
				} else {
					possible.add(new ItemStack(ModItems.terrasteel));
				}
				possible.add(new ItemStack(ModItems.ancientWillAhrim));
				possible.add(new ItemStack(ModItems.ancientWillDharok));
				possible.add(new ItemStack(ModItems.ancientWillGuthan));
				possible.add(new ItemStack(ModItems.ancientWillKaril));
				possible.add(new ItemStack(ModItems.ancientWillTorag));
				possible.add(new ItemStack(ModItems.ancientWillVerac));
				possible.add(new ItemStack(ModItems.blackerLotus));
				possible.add(new ItemStack(ModBlocks.gaiaHead));
			} else*/ if (entity instanceof WitherBoss) {
				possible.add(new ItemStack(Items.NETHER_STAR));
				possible.add(new ItemStack(Items.WITHER_SKELETON_SKULL));
				possible.add(new ItemStack(Items.BEACON));
				possible.add(new ItemStack(Items.END_CRYSTAL));
			}
			
			if (entity.hasCustomName()) {
				possible.add(new ItemStack(Items.NAME_TAG).setHoverName(entity.getDisplayName()));
			}
			
			// we spawn one of the stacks in possible, chosen randomly, then delete entity
			ItemStack resultItem = possible.get(entity.getRandom().nextInt(possible.size()));

			//////////
			// ported from Bookshelf 1.12 code
			final ListTag loreList = new ListTag();
			String type = new TranslatableComponent(entity.getType().toString()).getString();
			if (entity instanceof Player || entity.hasCustomName() && !resultItem.is(Items.NAME_TAG)) {
				String name = entity.getDisplayName().getString();
				loreList.addTag(0, StringTag.valueOf("{\"text\":\"Formerly '"+name+"' ("+type+")\"}"));
			} else {
				loreList.addTag(0, StringTag.valueOf("{\"text\":\"Formerly "+type+"\"}"));
			}
	        if (!resultItem.hasTag()) {
	        	resultItem.setTag(new CompoundTag());
	        }
	        final CompoundTag tag = resultItem.getTag();
	        if (!tag.contains("display", 10)) {
	            tag.put("display", new CompoundTag());
	        }
	        
			final CompoundTag displayTag = tag.getCompound("display");
	        displayTag.put("Lore", loreList);
			//////////
			
			entity.spawnAtLocation(resultItem);
			entity.discard();
			return true;
		}
		return false;
	}
	
	private static boolean canBeSlowedBy(LivingEntity victim, Player culprit) {
		return !(victim.is(culprit) || EntityHelper.isImmuneToGravityManipulation(victim));
	}
	

	/**
	 * speeds up time near a player. gets stronger the longer it is active <br>
	 * should be called every tick while accelerating <br>
	 * effects of accelerated time include: <p>
	 * <li> movement / attack speed increase for player
	 * <li> player gets reduced i-frames
	 * <li> slowing of other nearby creatures (except players)
	 * <li> block entity tick acceleration
	 * <li> fast-forwarding of world time
	 * 
	 * @param player The player causing the acceleration
	 * @param stack item that is performing the acceleration
	 * @param potency A multiplier for the strength of the effects
	 * @param tick How long the effect has been active, in ticks
	 * @param max Number of ticks where the effect caps out (stops getting stronger)
	 * @param size side length of the AOE box. if <= 0 will disable all AOE effects
	 */
	@SuppressWarnings("unchecked")
	public void jojoReference(Player player, ItemStack stack, double potency, int tick, int max, int size, long plrEmc) {
		// TODO: COST
		Level level = player.level;
		ProfilerFiller profiler = level.getProfiler();
		profiler.push("AASBjojoReference"); // due to the nature of this function, it gets its own dedicated profiling section
		
		for (Attribute attribute : getTimeAccelAttributes()) {
			player.getAttribute(attribute).removeModifier(TIME_ACCEL_UUID);
		} // clearing old modifiers to make room for updated ones
		double curPow = Math.min(1d, (double)tick/(double)max);
		double selfSpeedMult = curPow * potency;// + (10d/3d-1d); // 10/3-1 cancels out the movespeed penalty when using an item
		for (Attribute attribute : getTimeAccelAttributes()) {
			player.getAttribute(attribute).addTransientModifier(new AttributeModifier(
				TIME_ACCEL_UUID,
				"aasb:time_acceleration",
				selfSpeedMult,
				Operation.MULTIPLY_TOTAL
			));
		}
		if (player.invulnerableTime > 0) { // decreased iframes when accelerating
			player.invulnerableTime = Math.max(0, player.invulnerableTime - (int)(10*(curPow)));
		}
		// sound interval approach 1 per tick
		if (tick % (int)(20 - (19*curPow)) == 0) {
			boolean highPitch = NBTHelper.Item.getBoolean(stack, "TickSoundPitch", false);
			stack.setPopTime(3); // funky hotbar anim
			level.playSound(null, player, EffectInit.Sounds.TICK.get(), SoundSource.PLAYERS, 1, highPitch ? 2f : 1);
			NBTHelper.Item.setBoolean(stack, "TickSoundPitch", !highPitch); // TODO: stop this nbt sound crap
		}
		// aoe stuff
		if (size > 0) {
			double mobSlow = Math.min(potency, 1 - curPow);
			Vec3 slowVec = new Vec3(mobSlow, mobSlow, mobSlow);
			Vec3 cent = player.position();
			if (player.isOnGround())
				cent = cent.add(0, size/2, 0);
			AABB aoe = AABB.ofSize(cent, size, size, size);
			AASBNet.toClient(new DrawParticleAABBPacket(BoxHelper.getMin(aoe), BoxHelper.getMax(aoe), AABBParticlePreset.DEBUG), (ServerPlayer)player);
			
			for ( LivingEntity ent : level.getEntitiesOfClass(LivingEntity.class, aoe, entity -> canBeSlowedBy(entity, player)) ) {
				Vec3 vel = ent.getDeltaMovement();
				if (ent instanceof ServerPlayer plr) {
					AASBNet.toClient(new ModifyPlayerVelocityPacket(slowVec, VecOp.MULTIPLY), plr);
				}
				ent.setDeltaMovement(vel.multiply(slowVec));
			}

			int extraTicks = (int)(selfSpeedMult/2d);//(int) (20 * (potency/30));// (ProjectEConfig.server.effects.timePedBonus.get() * (potency/30));
			//System.out.println(extraTicks);
			
			// world time acceleration
			// TODO: make the sun/moon not teleport
			if (level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
				long time = level.getDayTime();
				long newTime = time+extraTicks;
				if (newTime > 0 && level instanceof ClientLevel lvl) {
					lvl.setDayTime(newTime);
				} else if (level instanceof ServerLevel lvl) {
					lvl.setDayTime(newTime);
				}
			}
			
			if (!level.isClientSide()) {
			// most of the stuff inside this if() is taken directly from ProjectE's TimeWatch.java
				long toConsume = 0;
				for (BlockEntity blockEntity : WorldHelper.allTEInBox(level, aoe)) {
					if (toConsume > plrEmc) break;
					else if (!blockEntity.isRemoved() && !TETP.NO_TICKACCEL_LOOKUP.contains(blockEntity.getType())) {
						BlockPos pos = blockEntity.getBlockPos();
						if (level.shouldTickBlocksAt(ChunkPos.asLong(pos))) {
							LevelChunk chunk = level.getChunkAt(pos);
							RebindableTickingBlockEntityWrapper tickingWrapper = chunk.tickersInLevel.get(pos);
							if (tickingWrapper != null && !tickingWrapper.isRemoved()) {
								if (tickingWrapper.ticker instanceof BoundTickingBlockEntity tickingBE) {
									//In general this should always be the case, so we inline some of the logic
									// to optimize the calls to try and make extra ticks as cheap as possible
									if (chunk.isTicking(pos)) {
										profiler.push(tickingWrapper::getType);
										BlockState state = chunk.getBlockState(pos);
										if (blockEntity.getType().isValid(state)) {
											for (int i = 0; i < extraTicks/* && plrEmc >= WOFT.TICKACCEL.get()*/; i++) {
												//toConsume += WOFT.TICKACCEL.get();
												tickingBE.ticker.tick(level, pos, state, blockEntity);
											}
										}
										profiler.pop();
									}
								} else {
									//Fallback to just trying to make it tick extra
									for (int i = 0; i < extraTicks/* && plrEmc >= WOFT.TICKACCEL.get()*/; i++) {
										//toConsume += WOFT.TICKACCEL.get();
										tickingWrapper.tick();
									}
								}
							}
						}
					}
				}
				//plrEmc -= EmcHelper.consumeAvaliableEmc(player, toConsume);
				toConsume = 0;
				
				// random ticks brr
				for (BlockPos pos : BoxHelper.allBlocksInBox(aoe)) {
					/*if (plrEmc < WOFT.TICKACCEL.get()) break;
					else*/ if (WorldHelper.isBlockLoaded(level, pos)) {
						BlockState state = level.getBlockState(pos);
						Block block = state.getBlock();
						if (state.isRandomlyTicking() && !state.is(BlockTP.NO_TICKACCEL)
							&& !(block instanceof LiquidBlock) // Don't speed non-source fluid blocks - dupe issues
							&& !(block instanceof BonemealableBlock) && !(block instanceof IPlantable)) {// All plants should be sped using Harvest Goddess
							pos = pos.immutable();
							for (int i = 0; i < extraTicks/* && plrEmc >= WOFT.TICKACCEL.get()*/; i++) {
								//toConsume += WOFT.TICKACCEL.get();
								state.randomTick((ServerLevel)level, pos, level.random);
							}
						}
					}
				}
				//plrEmc -= EmcHelper.consumeAvaliableEmc(player, toConsume);
			}
		}
		
		profiler.pop();
	}
	
	/**
	 * Exists to make adding/removing attributes from time acceleration easy in the future <br>
	 * Would just make it a static final array but SWIM_SPEED is registered after items
	 * @return Attributes that TimeAccel modifies
	 */
	private static Attribute[] getTimeAccelAttributes() {
		Attribute[] attribs = {
				Attributes.ATTACK_SPEED,
				Attributes.MOVEMENT_SPEED,
				ForgeMod.SWIM_SPEED.get()
		};
		return attribs;
	}
}