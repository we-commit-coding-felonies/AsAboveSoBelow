package com.quartzshard.aasb.api.alchemy.rune.form;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.client.gui.menu.PortableCraftingMenu;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.AmuletItem;
import com.quartzshard.aasb.common.item.equipment.curio.AbilityCurioItem;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.data.tags.BlockTP;
import com.quartzshard.aasb.data.tags.EntityTP;
import com.quartzshard.aasb.data.tags.ItemTP;
import com.quartzshard.aasb.data.tags.TileTP;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.net.client.ModifyPlayerVelocityPacket;
import com.quartzshard.aasb.net.client.ModifyPlayerVelocityPacket.VecOp;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.BoxUtil;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WayUtil;
import com.quartzshard.aasb.util.WorldUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import top.theillusivec4.curios.api.SlotContext;


public class ArcaneRune extends FormRune {
	public static final UUID UUID_TIMEACCEL = UUID.fromString("311f77f1-5573-431d-8340-06511e72d28f");
	public static final String
		TK_PITCH = "TimeAccelTickHighPitch",
		TK_TICKTIME = "TimeAccelTickCount";

	public ArcaneRune() {
		super(AASB.rl("arcane"));
	}

	/**
	 * normal: instant damage touch <br>
	 * strong: transmuting touch (itemizer)
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		double reach = player.getEntityReach();
		Vec3 checkVec = player.getLookAngle().scale(reach); 
		@Nullable EntityHitResult hitRes = ProjectileUtil.getEntityHitResult(
				level,
				player,
				player.getEyePosition(),
				player.getEyePosition().add(checkVec),
				player.getBoundingBox().expandTowards(checkVec).inflate(1),
				ent -> ent instanceof LivingEntity && !EntUtil.isInvincible(ent) && ent.isAlive());
		if (hitRes != null) {
			LivingEntity victim = (LivingEntity) hitRes.getEntity();
			if (strong) {
				if (victim.getHealth() <= victim.getMaxHealth()/3f || victim.getHealth() <= 10) {
					if (entityItemizer(victim, player, player)) {
						PlayerUtil.coolDown(player, stack.getItem(), 10);
						return true;
					}
				}
				if (victim instanceof Player plr && AmuletItem.isBarrierActive(plr)) {
					victim.hurt(EntityInit.dmg(EntityInit.DMG_TRANSMUTE, level, player), 4);
				} else if (!victim.addEffect(new MobEffectInstance(EntityInit.BUFF_TRANSMUTING.get(), 5, 1))) {
					victim.hurt(EntityInit.dmg(EntityInit.DMG_TRANSMUTE, level, player), victim.getMaxHealth() / 6f);
				}
				victim.playSound(FxInit.SND_TRANSMUTE_GENERIC.get(), 1, 1f);
			} else {
				MobEffects.HARM.applyInstantenousEffect(player, player, victim, 1, 1);
			}
			PlayerUtil.coolDown(player, stack.getItem(), 10);
			return true;
		}
		return false;
	}

	/**
	 * normal: portable crafting table <br>
	 * strong: portable transmutation
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (strong) {
			// TODO transmutation tablet
		}
		ContainerLevelAccess wp = ContainerLevelAccess.create(level, BlockPos.ZERO);
		player.openMenu(new SimpleMenuProvider((id, inv, plr) -> new PortableCraftingMenu(id, inv, wp), stack.getHoverName()));
		return true;
	}

	/**
	 * normal: jojo reference <br>
	 * strong: really op jojo reference
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		if (state != BindState.PRESSED) return false;
		if (passiveEnabled(stack)) {
			resetAccel(player, stack);
		} else {
			NBTUtil.setBoolean(stack, TK_ACTIVATED, true);
		}
		return true;
	}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		if (unequipped) { // FIXME cannot for the life of me get this thing to reset the time accel when its unequipped. fix later
			//System.out.println(stack.isEmpty());
			resetAccel(player, stack);
			//NBTUtil.setBoolean(stack, TK_JOJOACTIVE, false);
			//NBTUtil.setInt(stack, TK_TICKTIME, 0);
			//resetTimeAccelSpeed(player);
		} else if (!unequipped && NBTUtil.getBoolean(stack, TK_ACTIVATED, false)) {
			long plrEmc = WayUtil.getAvaliableWay(player);
			if (plrEmc >= (strong ? 64 : 8)) {
				NBTUtil.setInt(stack, TK_TICKTIME, timeTicking(stack)+1);
				jojoReference(player, stack, 60, timeTicking(stack), 1200, strong ? 24 : 0, plrEmc);
			} else {
				resetAccel(player, stack);
			}
		}
	}
	
	@Override @Nullable
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext ctx, UUID uuid, ItemStack stack) {
		if (NBTUtil.getBoolean(stack, TK_ACTIVATED, false) && stack.getItem() instanceof AbilityCurioItem item) {
			ImmutableMultimap.Builder<Attribute,AttributeModifier> map = ImmutableMultimap.builder();
			double curPow = Math.min(1d, (double)timeTicking(stack)/(double)1200);
			double selfSpeedMult = curPow * 10;// + (10d/3d-1d); // 10/3-1 cancels out the movespeed penalty when using an item
			for (Attribute attribute : getTimeAccelAttributes()) {
				map.put(attribute, new AttributeModifier(
					UUID_TIMEACCEL,
					AASB.rl("time_acceleration").toString(),
					selfSpeedMult,
					AttributeModifier.Operation.MULTIPLY_TOTAL
				));
			}
			return map.build();
		}
		return null;
	}
	
	private static int timeTicking(ItemStack stack) {
		return NBTUtil.getInt(stack, TK_TICKTIME, 0);
	}
	
	private static void resetAccel(Player player, ItemStack stack) {
		NBTUtil.setBoolean(stack, TK_ACTIVATED, false);
		NBTUtil.setInt(stack, TK_TICKTIME, 0);
		resetTimeAccelSpeed(player);
	}
	
	/**
	 * removes all time accel attribute modifiers from a player
	 * @param player
	 */
	private static void resetTimeAccelSpeed(Player player) {
		//for (Attribute atr : getTimeAccelAttributes()) {
		//	player.getAttribute(atr).removeModifier(UUID_TIMEACCEL);
		//}
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
	public void jojoReference(Player player, ItemStack stack, double potency, int tick, int max, int size, long plrEmc) {
		for (Attribute attribute : getTimeAccelAttributes()) {
			player.getAttribute(attribute).removeModifier(UUID_TIMEACCEL);
		} // clearing old modifiers to make room for updated ones
		double curPow = Math.min(1d, (double)tick/(double)max);
		double selfSpeedMult = curPow * potency;// + (10d/3d-1d); // 10/3-1 cancels out the movespeed penalty when using an item
		//for (Attribute attribute : getTimeAccelAttributes()) {
		//	player.getAttribute(attribute).addTransientModifier(new AttributeModifier(
		//		UUID_TIMEACCEL,
		//		AASB.rl("time_acceleration").toString(),
		//		selfSpeedMult,
		//		AttributeModifier.Operation.MULTIPLY_TOTAL
		//	));
		//}
		player.invulnerableTime = Math.max(0, player.invulnerableTime - (int)(10*(curPow)));
		// sound interval approach 1 per tick
		if (tick % (int)(20 - (19*curPow)) == 0) {
			boolean highPitch = NBTUtil.getBoolean(stack, TK_PITCH, false);
			stack.setPopTime(3);
			player.level().playSound(null, player, FxInit.SND_TICK.get(), SoundSource.PLAYERS, 1, highPitch ? 2f : 1);
			NBTUtil.setBoolean(stack, TK_PITCH, !highPitch); // storing an nbt tag to know what pitch the sound should be is *totally* the best way of doing it
		}
		// aoe stuff
		if (size > 0) {
			double mobSlow = Math.min(potency, 1 - curPow);
			Vec3 slowVec = new Vec3(mobSlow, mobSlow, mobSlow);
			AABB aoe = AABB.ofSize( player.position(), size, size, size);
			
			for (LivingEntity ent : player.level().getEntitiesOfClass(LivingEntity.class, aoe, ent -> !EntUtil.resistsSpacetimeShenanigans(ent))) {
				if (!ent.level().isClientSide && ent instanceof ServerPlayer plr && !plr.is(player)) {
					NetInit.toClient(new ModifyPlayerVelocityPacket(slowVec, VecOp.MULTIPLY), plr);
				}
				else if ( !(ent instanceof Player) ) {
					ent.setDeltaMovement(ent.getDeltaMovement().multiply(slowVec));
				}
			}
			Level level = player.level();
			if (!level.isClientSide()) {
			// most of the stuff inside this if() is taken directly from ProjectE's TimeWatch.java
			// https://github.com/sinkillerj/ProjectE/blob/68fbb2dea0cf8a6394fa6c7c084063046d94cee5/src/main/java/moze_intel/projecte/gameObjs/items/rings/TimeWatch.java#L130
				int extraTicks = (int) (18 * (potency/30));
				long toConsume = 0;
				for (BlockEntity te : WorldUtil.allTEInBox(level, aoe)) {
					if (toConsume > plrEmc) break;
					else if (!te.isRemoved() && !TileTP.L_NO_TICKACCEL.contains(te.getType())) {
						BlockPos pos = te.getBlockPos();
						if (level.shouldTickBlocksAt(ChunkPos.asLong(pos))) {
							LevelChunk chunk = level.getChunkAt(pos);
							LevelChunk.RebindableTickingBlockEntityWrapper tickingWrapper = chunk.tickersInLevel.get(pos);
							if (tickingWrapper != null && !tickingWrapper.isRemoved()) {
								if (tickingWrapper.ticker instanceof LevelChunk.BoundTickingBlockEntity tickingBE) {
									//In general this should always be the case, so we inline some of the logic
									// to optimize the calls to try and make extra ticks as cheap as possible
									if (chunk.isTicking(pos)) {
										ProfilerFiller profiler = level.getProfiler();
										profiler.push(tickingWrapper::getType);
										BlockState state = chunk.getBlockState(pos);
										if (te.getType().isValid(state)) {
											for (int i = 0; i < extraTicks && plrEmc >= 64; i++) {
												toConsume += 64;
												tickingBE.ticker.tick(level, pos, state, te);
											}
										}
										profiler.pop();
									}
								} else {
									//Fallback to just trying to make it tick extra
									for (int i = 0; i < extraTicks && plrEmc >= 64; i++) {
										toConsume += 64;
										tickingWrapper.tick();
									}
								}
							}
						}
					}
				}
				plrEmc -= WayUtil.consumeAvaliableWay(player, toConsume);
				toConsume = 0;
				
				// random ticks brr
				for (BlockPos pos : BoxUtil.allBlocksInBox(aoe)) {
					if (plrEmc < 64) break;
					else if (WorldUtil.isBlockLoaded(level, pos)) {
						BlockState state = level.getBlockState(pos);
						Block block = state.getBlock();
						if (state.isRandomlyTicking() && !state.is(BlockTP.NO_TICKACCEL)
							&& !(block instanceof LiquidBlock)) { // Don't speed non-source fluid blocks - dupe issues
							//&& !(block instanceof BonemealableBlock) && !(block instanceof IPlantable)) {// All plants should be sped using Harvest Goddess
							pos = pos.immutable();
							for (int i = 0; i < extraTicks && plrEmc >= 64; i++) {
								toConsume += 64;
								state.randomTick((ServerLevel)level, pos, level.random);
							}
						}
					}
				}
				plrEmc -= WayUtil.consumeAvaliableWay(player, toConsume);
				
				// world time acceleration
				// TODO: make the sun/moon not teleport
				if (level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
					ServerLevel serverWorld = (ServerLevel) level;
					serverWorld.setDayTime(level.getDayTime() + extraTicks);
				}
			}
		}
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
	
	/**
	 * Transforms an entity into a random item. Some entities are immune for technical reasons. <br>
	 * For most entities, possible items are chosen from their loot table, but there are some special exceptions. <br>
	 * There are also a few 'universal' items, which are always in the list of possible items.
	 * 
	 * @return if the entity was actually transformed
	 */
	public static boolean entityItemizer(LivingEntity entity, @Nullable Entity culprit, @Nullable Entity cause) {
		if (!entity.getType().is(EntityTP.ITEMIZER_LIST)) {
			//if (entity instanceof Player player) {
			//	// TODO: something a bit more interesting than this
			//	if (player.getHealth() < player.getMaxHealth()/2 || !AmuletItem.isBarrierActive(player)) {
			//		player.setLastHurtByPlayer(null);
			//		player.hurt(MGTKDmgSrc.strongTransmutation(culprit), Float.MAX_VALUE);
			//	} else return false;
			//}
			List<ItemStack> possible = new ArrayList<>();
			
			
			ItemStack[] stock = {
					ItemTP.L_ITEMIZER_APPLES.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_GUMMYS.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_ORBS.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_SEEDS.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_BERRIES.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_DRINKS.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_STICKS.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_DISCS.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
					ItemTP.L_ITEMIZER_JUNKS.tag().getRandomElement(culprit.level().random).map(ItemStack::new).orElse(ItemStack.EMPTY),
			};
			for (ItemStack stack : stock) {
				if (!stack.isEmpty())
					possible.add(stack);
			}
			
			// getting things from entity loot table
			ResourceLocation resourcelocation = entity.getLootTable();
			LootTable loottable = entity.level().getServer().getLootData().getLootTable(resourcelocation);
			//LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
			DamageSource dmgSrc = EntityInit.dmg(EntityInit.DMG_TRANSMUTE, culprit.level(), culprit);
			LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel)entity.level()))
					.withParameter(LootContextParams.THIS_ENTITY, entity)
					.withParameter(LootContextParams.ORIGIN, entity.position())
					.withParameter(LootContextParams.DAMAGE_SOURCE, dmgSrc)
					.withOptionalParameter(LootContextParams.KILLER_ENTITY, dmgSrc.getEntity())
					.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, dmgSrc.getDirectEntity());
					//.withRandom(entity.getRandom()).withParameter(LootContextParams.THIS_ENTITY, entity)
					//.withParameter(LootContextParams.ORIGIN, entity.position())
					//.withParameter(LootContextParams.DAMAGE_SOURCE, EntityInit.dmg(EntityInit.DMG_TRANSMUTE, culprit.level()))
					//.withOptionalParameter(LootContextParams.KILLER_ENTITY, culprit)
					//.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, cause);
			if (culprit instanceof Player player) {
				lootcontext$builder = lootcontext$builder
						.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
						.withLuck(player.getLuck() + 2);
			}
			LootParams lootparams = lootcontext$builder.create(LootContextParamSets.ENTITY);
			loottable.getRandomItems(lootparams, entity.getLootTableSeed());
			for (ItemStack stack : loottable.getRandomItems(lootparams, entity.getLootTableSeed())) {
				stack.setCount(1);
				possible.add(stack);
			}
			
			// entities with special drops
			if (entity instanceof WitherBoss) {
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
			String type = LangData.tc(entity.getType().toString()).getString();
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
}
