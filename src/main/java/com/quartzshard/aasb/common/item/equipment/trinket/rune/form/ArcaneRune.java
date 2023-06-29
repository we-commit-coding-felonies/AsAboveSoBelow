package com.quartzshard.aasb.common.item.equipment.trinket.rune.form;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.common.damage.source.AASBDmgSrc;
import com.quartzshard.aasb.common.entity.projectile.SentientArrow;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.AmuletItem;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.data.AASBTags.EntityTP;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.EntityHelper;
import com.quartzshard.aasb.util.PlayerHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// time accel
		return false;
	}

	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		// TODO: COST
		// gem of density???
		return false;
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
		if (!entity.getType().is(EntityTP.ITEMIZER_ENTITY_BLACKLIST)) {
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
}