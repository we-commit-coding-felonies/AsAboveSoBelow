package com.quartzshard.aasb.api.alchemy.rune.shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Predicate;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.common.item.LootBallItem;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.data.tags.BlockTP;
import com.quartzshard.aasb.init.FxInit;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.net.client.CutParticlePacket;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.BoxUtil;
import com.quartzshard.aasb.util.EntUtil;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;
import com.quartzshard.aasb.util.WorldUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

/**
 * this file is bad and solunareclipse1 should feel bad <br>
 * TODO modernize & streamline most of this
 */
public class WaterRune extends ShapeRune {
	public static final String
		TK_SLASHING = "AutoslashActive",
		TK_HYPERSICKLE = "HypersickleOperation";

	public WaterRune() {
		super(ShapeAspect.WATER);
	}

	/**
	 * Normal: Quench nearby (put out fires, defuse bombs, solidify lava) <br>
	 * Strong: Freeze target (immobilized w/ damage resistance)
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Bottomless water bucket (works in nether) <br>
	 * Strong: Floodfill water (works in nether)
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}

	/**
	 * Normal: Water breathing <br>
	 * Strong: Water breathing + Dolphin's Grace
	 */
	@Override
	public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		return false;
	}
	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong) {
		// TODO Auto-generated method stub
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
		return true;
	}

	@Override
	public boolean toolAbility(ItemStack stack, ToolStyle style, ServerPlayer player, ServerLevel level, BindState state, boolean strong) {
		if (state != BindState.PRESSED) return false;
		// TODO make this less specific to hermetic stuff somehow
		// also make it NOT ACTUAL REAL SATAN
		Item item = stack.getItem();
		if (item instanceof IHermeticTool tool) {
			long charge = tool.getStoredWay(stack);
			ItemCooldowns cd = player.getCooldowns();
			boolean onCooldown = player.getAttackStrengthScale(0) < 1 || cd.isOnCooldown(item);
			if (charge < 32 || onCooldown)
				return false;
			
			float power = tool.getEmpowerPercent(stack);
			int cdTime = 0;
			boolean didDo = false;
			boolean consume = true;
			InteractionHand hand = player.getOffhandItem() == stack ?
					InteractionHand.OFF_HAND :
					InteractionHand.MAIN_HAND;
			if (charge > 0) {
				switch (style) {
					case SWORD:
						didDo = triggerAutoSlash(stack, player);
						cdTime = (int) (1+(charge/10));
						consume = false;
						break;
					case PICKAXE:
						if (strong) power += (power+1)*power;
						float sizePick = 5f + 20f*power;
						AABB areaPick = AABB.ofSize(player.getBoundingBox().getCenter(), sizePick, sizePick, sizePick);
						didDo = proximine(player, areaPick, level, stack, strong);
						cdTime = 20;
						break;
					case SHOVEL:
						Vec3 eyePos = player.getEyePosition();
						Vec3 rayShovel = player.getLookAngle().scale(player.getBlockReach()-0.5);
						Vec3 rayPos = eyePos.add(rayShovel);
						BlockHitResult hitResShovel = player.level().clip(new ClipContext(eyePos, rayPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
						if (hitResShovel.getType() != HitResult.Type.MISS) {
							if (strong) power += (power)*(power);
							float sizeShovel = 1f + 4f*power;
							didDo = areablast(player, hitResShovel.getBlockPos(), hitResShovel.getDirection(), sizeShovel, level, stack, strong);
							cdTime = 20;
						}
						break;
					case AXE:
						if (strong) power += (power+1)*power;
						float sizeAxe = 5f + 20f*power;
						AABB areaAxe = AABB.ofSize(player.getBoundingBox().getCenter(), sizeAxe*1.5, sizeAxe*0.75, sizeAxe*1.5);
						didDo = supercut(player, areaAxe, level, stack, strong);
						cdTime = 20;
						break;
					case HOE:
						Vec3 pos1 = null;
						Vec3 ray = null;
						Vec3 pos2 = null;
						BlockHitResult hitRes = null;
						byte op = getOperationByte(stack);
						if (op != 2) {
							pos1 = player.getEyePosition();
							ray = player.getLookAngle().scale(player.getBlockReach()-0.5);
							pos2 = pos1.add(ray);
							hitRes = player.level().clip(new ClipContext(pos1, pos2, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
						}
						if (op == 2 || (hitRes != null && hitRes.getType() == HitResult.Type.BLOCK)) {
							power = tool.getEmpowerPercent(stack);
							if (strong) power += (power+1)*power;
							float size = 5f + 20f*power;
							AABB area = AABB.ofSize(player.getBoundingBox().getCenter(), size*1.75, size*0.25, size*1.75);
							didDo = hypersickle(player, hand, hitRes, area, level, stack, HypersickleMode.byID(op), strong);
						}
						break;
					default:
						break;
				}
				if (didDo) {
					PlayerUtil.swingArm(player, level, hand);
					cd.addCooldown(stack.getItem(), cdTime);
					if (consume) {
						tool.setStoredWay(stack, 0);
						level.playSound(null, player.blockPosition(), FxInit.SND_WAY_SLASH.get(), SoundSource.PLAYERS, 1, 1.2f);
					}
					if (style != ToolStyle.SWORD) {
						PlayerUtil.doSweepAttackParticle(player, level);
					}
				}
			}
			return didDo;
		}
		return false;
	}

	public static boolean triggerAutoSlash(ItemStack stack, ServerPlayer player) {
		Item item = stack.getItem();
		if (item instanceof IHermeticTool tool) {
			long charge = tool.getStoredWay(stack);
			ItemCooldowns cd = player.getCooldowns();
			boolean onCooldown = player.getAttackStrengthScale(0) < 1 || cd.isOnCooldown(item);
			if (charge < 10 || isCurrentlySlashing(stack) || onCooldown)
				return false;
			
			float stage = tool.getEmpowerPercent(stack);
			startSlashing(stack, stage);
			cd.addCooldown(item, (int) (1+(charge/10)));
			return true;
		}
		return false;
	}
	
	public static boolean tickAutoSlash(ServerPlayer culprit, ServerLevel level, AABB area, Predicate<LivingEntity> validator, boolean strong) {
		List<LivingEntity> validTargets = level.getEntitiesOfClass(LivingEntity.class, area, validator);
		Map<Entity,Integer> hit = new HashMap<>();
		if (!validTargets.isEmpty()) {
			int numHits = strong ? validTargets.size() : 6;
			for (int i = 0; i < numHits; i++) {
				int victimIdx = strong ? i : AASB.RNG.nextInt(validTargets.size());
				LivingEntity victim = validTargets.get(victimIdx);
				float damage = strong ? 6 : 3;
				victim.hurt(EntityInit.dmg(EntityInit.DMG_AUTOSLASH, level, culprit), damage);
				if (hit.containsKey(victim)) {
					hit.put(victim, hit.get(victim)+1);
				} else hit.put(victim, 1);
			}
			double rot1 = -Mth.sin(culprit.getYRot() * ((float)Math.PI / 180f));
			double rot2 = Mth.cos(culprit.getYRot() * ((float)Math.PI / 180f));
			Random rand = AASB.RNG;
			double maxNudge = 0.35;
			Vec3 offset = new Vec3(rand.nextDouble(-maxNudge, maxNudge),rand.nextDouble(-maxNudge, maxNudge),rand.nextDouble(-maxNudge, maxNudge));
			level.sendParticles(ParticleTypes.SWEEP_ATTACK, culprit.getX()+rot1+offset.x, culprit.getY(0.5)+offset.y, culprit.getZ()+rot2+offset.z, 1, 0, 0, 0, 0);
			for (Entry<Entity,Integer> hitEnt : hit.entrySet()) {
				// this kinda sucks and i should find a better way to do it
				AABB box = BoxUtil.growToCube(hitEnt.getKey().getBoundingBox());
				NetInit.toNearbyClients(new CutParticlePacket(hitEnt.getValue(), box), level, culprit.position(), 128);
				level.playSound(null, hitEnt.getKey().blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 0.1f);
			}
			level.playSound(null, culprit.blockPosition(), FxInit.SND_WAY_SLASH.get(), SoundSource.PLAYERS, 0.7f, 1.5f);
			return true;
		}
		return false;
	}
	
	public static boolean tickAutoSlashOld(float power, AABB area, ServerLevel level, ServerPlayer culprit, Predicate<LivingEntity> validator) {
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
				victim.hurt(EntityInit.dmg(EntityInit.DMG_AUTOSLASH, level, culprit), damage);
				if (hit.containsKey(victim)) {
					hit.put(victim, hit.get(victim)+1);
				} else hit.put(victim, 1);
			}
			//if (level instanceof ServerLevel lvl) {
				double rot1 = -Mth.sin(culprit.getYRot() * ((float)Math.PI / 180f));
				double rot2 = Mth.cos(culprit.getYRot() * ((float)Math.PI / 180f));
				Random rand = AASB.RNG;
				double maxNudge = 0.35;
				Vec3 offset = new Vec3(rand.nextDouble(-maxNudge, maxNudge),rand.nextDouble(-maxNudge, maxNudge),rand.nextDouble(-maxNudge, maxNudge));
				level.sendParticles(ParticleTypes.SWEEP_ATTACK, culprit.getX()+rot1+offset.x, culprit.getY(0.5)+offset.y, culprit.getZ()+rot2+offset.z, 1, 0, 0, 0, 0);
				for (Entry<Entity,Integer> hitEnt : hit.entrySet()) {
					// this kinda sucks and i should find a better way to do it
					AABB box = BoxUtil.growToCube(hitEnt.getKey().getBoundingBox());
					NetInit.toNearbyClients(new CutParticlePacket(hitEnt.getValue(), box), level, culprit.position(), 128);
					level.playSound(null, hitEnt.getKey().blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1f, 0.1f);
				}
			//}
			level.playSound(null, culprit.blockPosition(), FxInit.SND_WAY_SLASH.get(), SoundSource.PLAYERS, 0.7f, 1.5f);
			return true;
		}
		return false;
	}

	/**
	 * based on projece's aoe ore veinmine 
	 * https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/utils/ToolHelper.java
	 * @param player who is doing the proximine
	 * @param area area to proximine
	 * @param level level to proximine in
	 * @param stack the item doing proximine
	 * @param strong if true, applies crazy fortune
	 * @return if the proximine happened
	 */
	public static boolean proximine(ServerPlayer player, AABB area, ServerLevel level, ItemStack stack, boolean strong) {
		boolean didDo = false;
		List<ItemStack> drops = new ArrayList<>();
		ItemStack mineWith = stack.copy();
		if (strong) {
			mineWith.enchant(Enchantments.BLOCK_FORTUNE, 6);
		}
		for (BlockPos pos : BoxUtil.allBlocksInBox(area)) {
			if (level.isEmptyBlock(pos)) continue;
			BlockState state = level.getBlockState(pos);
			if (state.is(Tags.Blocks.ORES) && state.getDestroySpeed(level, pos) != -1 && mineWith.isCorrectToolForDrops(state)) {
				if (level.isClientSide)
					return true;
				
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				int oresMined = 0;
				if (PlayerUtil.hasBreakPermission(player, pos)) {
					oresMined++;
					drops.addAll(Block.getDrops(state, level, pos, WorldUtil.getBlockEntity(level, pos), player, mineWith));
					level.removeBlock(pos, false);
					NetInit.toNearbyClients(new CutParticlePacket(8, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(pos))), level, Vec3.atCenterOf(pos), 64);
				}
				didDo = oresMined > 0;
			}
		}
		if (didDo) {
			LootBallItem.dropBalls(player, drops);
			return true;
		}
		return false;
	}
	/**
	 * insurance claim generator
	 * @param player idiot generating the insurance claim
	 * @param pos blast origin
	 * @param dir direction of the blast
	 * @param size blast box side length
	 * @param level world the blast will happen in
	 * @param stack the item generating the insurance claim
	 * @return if the areablast succeeded
	 */
	public static boolean areablast(ServerPlayer player, BlockPos pos, Direction dir, float size, ServerLevel level, ItemStack stack, boolean strong) {
		AABB box = BoxUtil.getCubeForAoeInFront(pos, dir, size);
		if (player.onGround() && !dir.getAxis().isVertical()) {
			// move the area up so that it matches with player feet
			double shift = box.getSize()/2d;
			if (size > 6.5) shift++;
			box = box.move(0, (int)shift, 0);
		}
		boolean hasAction = false;
		List<ItemStack> drops = new ArrayList<>();
		ItemStack breakerStack = new ItemStack(ItemInit.OMNITOOL.get());
		for (BlockPos newPos : BoxUtil.allBlocksInBox(box)) {
			if (level.isEmptyBlock(newPos)) {
				continue;
			}
			BlockState state = level.getBlockState(newPos);
			if (state.getDestroySpeed(level, newPos) != -1 /*&& breakerStack.isCorrectToolForDrops(state)*/) {
				if (level.isClientSide) {
					return true;
				}
				//Ensure we are immutable so that changing blocks doesn't act weird
				newPos = newPos.immutable();
				if (PlayerUtil.hasBreakPermission(player, newPos)) {
					if (!strong || player.getRandom().nextInt() % 5 == 0) {
						drops.addAll(Block.getDrops(state, level, newPos, WorldUtil.getBlockEntity(level, newPos), player, breakerStack));
						if (player.getRandom().nextInt() % 5 == 0)
							level.sendParticles(ParticleTypes.EXPLOSION, newPos.getX()+0.5, newPos.getY()+0.5, newPos.getZ()+0.5, 1, 0, 0, 0, 0);
					}
					level.removeBlock(newPos, false);
					hasAction = true;
				}
			}
		}
		if (hasAction) {
			LootBallItem.dropBalls(player, drops);
			level.playSound(null, BlockPos.containing(box.getCenter()), FxInit.SND_WAY_EXPLODE.get(), SoundSource.PLAYERS, 2, 1.5f);
			level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, box.getCenter().x, box.getCenter().y, box.getCenter().z, 1, 0, 0, 0, 0);
			return true;
		}
		return false;
	}
	
	/**
	 * deforestation
	 * @param player player doing the supercut
	 * @param area area to supercut
	 * @param level level to supercut in
	 * @param stack stack doing the supercut
	 * @param strong if true, destroys drops
	 * @return if the supercut happened
	 */
	public static boolean supercut(ServerPlayer player, AABB area, ServerLevel level, ItemStack stack, boolean strong) {
		boolean didDo = false;
		List<ItemStack> drops = new ArrayList<>();
		for (BlockPos pos : BoxUtil.allBlocksInBox(area)) {
			BlockState state = level.getBlockState(pos);
			if (state.is(BlockTP.SUPERCUT_HARVESTS)) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				if (!level.isClientSide && PlayerUtil.hasBreakPermission(player, pos)) {
					// too strongk, destroys all
					if (!strong) drops.addAll(Block.getDrops(state, level, pos, WorldUtil.getBlockEntity(level, pos), player, stack));
					level.removeBlock(pos, false);
					didDo = true;
				}
			}
		}
		if (didDo) {
			if (!strong) {
				LootBallItem.dropBalls(player, drops);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * have fun trying to read through this fucker
	 * @param player player sickling
	 * @param hand hand they have the scythe in
	 * @param hitRes block raycast result
	 * @param area area to sickle
	 * @param level world to sickle in
	 * @param stack item thats sickling
	 * @param operation hypersickle operation to perform
	 * @return if the hypersickle did the thing
	 */
	public static boolean hypersickle(Player player, InteractionHand hand, BlockHitResult hitRes, AABB area, Level level, ItemStack stack, HypersickleMode operation, boolean strong) {
		boolean didDo = false;
		BlockPos center = hitRes == null ?
				BlockPos.containing(area.getCenter()) :
				hitRes.getBlockPos();
		BlockState centerBlock = level.getBlockState(center);
		UseOnContext ctx = null;
		ToolAction act = null;
		SoundEvent snd = null;
		switch (operation) {
			case TILL: // 0
				ctx = new UseOnContext(level, player, hand, stack, hitRes);
				act = ToolActions.HOE_TILL;
				snd = SoundEvents.HOE_TILL;
				break;
			case PATH: // 1
				ItemStack shovel = new ItemStack(Items.NETHERITE_SHOVEL);
				ctx = new UseOnContext(level, player, hand, shovel, hitRes);
				act = ToolActions.SHOVEL_FLATTEN;
				snd = SoundEvents.SHOVEL_FLATTEN;
				break;
			case CULL:
			default: // 2?
				break;
		}
		BlockState modState = ctx == null ? null : centerBlock.getToolModifiedState(ctx, act, false);
		if (modState != null) {
			if (modState.getBlock() instanceof FarmBlock) {
				modState.setValue(FarmBlock.MOISTURE, 7);
			}
			level.setBlock(center, modState, Block.UPDATE_ALL_IMMEDIATE);
			level.playSound(null, center, snd, SoundSource.BLOCKS, 1, 1);
			// we move the box to the hit block, and make it 1 block tall
			area = BoxUtil.moveBoxTo(area, center).setMinY(center.getY()+0.1d).setMaxY(center.getY()+0.9d);
		} else if (operation != HypersickleMode.CULL) {
			// modification of the clicked block failed
			// and we arent culling plants, so we break out early
			return false;
		} else {
			// cull plants
			List<ItemStack> drops = new ArrayList<>();
			if (strong) area = area.deflate(0, 3, 0);
			System.out.println(area.minX +", "+ area.minY +", "+ area.minZ);
			System.out.println(area.maxX +", "+ area.maxY +", "+ area.maxZ);
			for (BlockPos pos : BoxUtil.allBlocksInBox(area)) {
				BlockState state = level.getBlockState(pos);
				if (!state.isAir() && state.is(BlockTP.HYPERSICKLE_CULLS)) {
					//Ensure we are immutable so that changing blocks doesn't act weird
					pos = pos.immutable();
					if (!level.isClientSide && PlayerUtil.hasBreakPermission((ServerPlayer) player, pos)) {
						// air too strongk, destroys all
						if (!strong) drops.addAll(Block.getDrops(state, (ServerLevel) level, pos, WorldUtil.getBlockEntity(level, pos), player, stack));
						level.addDestroyBlockEffect(pos, state);
						level.removeBlock(pos, false);
						didDo = true;
					}
				}
			}
			if (didDo) {
				if (!strong)
					LootBallItem.dropBalls(player, drops);
				return true;
			}
			return false;
		}
		
		// remaining code is for till & path modes only
		for (BlockPos pos : BoxUtil.allBlocksInBox(area)) {
			if (pos.equals(center))
				continue; // we already did the center block
			BlockState above = level.getBlockState(pos.above());
			if ( above.isAir()
					|| (operation == HypersickleMode.PATH && above.is(BlockTP.HYPERSICKLE_PATHUNDER)) ) {
				BlockState state = level.getBlockState(pos); // makes a new context for this interaction
				UseOnContext modCtx = new UseOnContext(level, ctx.getPlayer(), ctx.getHand(), ctx.getItemInHand(), new BlockHitResult(
						ctx.getClickLocation().add(pos.getX() - center.getX(), pos.getY() - center.getY(), pos.getZ() - center.getZ()),
						ctx.getClickedFace(), pos, ctx.isInside()));
				boolean same = isEffectivelySameState(modState, state.getToolModifiedState(modCtx, act, true));
				if (same) {
					//Ensure we are immutable so that changing blocks doesn't act weird
					pos = pos.immutable();
					state.getToolModifiedState(modCtx, act, true);
					level.setBlock(pos, modState, Block.UPDATE_ALL_IMMEDIATE);
				}
			}
		}
		return true;
	}
	
	private static boolean isEffectivelySameState(BlockState s1, BlockState s2) {
		if (s1 == null || s2 == null)
			return false;
		if (s1 == s2)
			return true;
		else if (s1.getBlock() instanceof FarmBlock
				&& s2.getBlock() instanceof FarmBlock) {
			return true;
		}
		
		return false;
	}
	
	public enum HypersickleMode {
		TILL, PATH, CULL;
		
		public static HypersickleMode byID(byte id) {
			switch (id) {
				case 0: return TILL;
				case 1: return PATH;
				default: return CULL;
			}
		}
	}
	
	public static HypersickleMode getOperation(ItemStack stack) {
		return HypersickleMode.byID(getOperationByte(stack));//hasRune(stack, ShapeRune.WATER) ? NBTHelper.Item.getByte(stack, TAG_OPERATION, (byte)0) : 0;
	}
	
	public static byte getOperationByte(ItemStack stack) {
		return NBTUtil.getByte(stack, TK_HYPERSICKLE, 0);//hasRune(stack, ShapeRune.WATER) ? NBTHelper.Item.getByte(stack, TAG_OPERATION, (byte)0) : 0;
	}
	
	public static boolean changeOperation(Player player, ItemStack stack) {
		byte mode = getOperationByte(stack);
		byte newMode = (byte) ((mode+1)%3);
		NBTUtil.setByte( stack, TK_HYPERSICKLE, newMode );
		Component hudText = LangData.tc(LangData.TIP_GENERIC_MODE, LangData.tc(LangData.TIP_HOE_MODE), opLang(newMode));
		player.displayClientMessage(hudText, true);
		return true;
	}
	
	public static Component opLang(byte mode) {
		switch (mode) {
		default:
		case 0:
			return LangData.tc(LangData.TIP_HOE_MODE_TILL).copy().withStyle(ChatFormatting.BLUE);
		case 1:
			return LangData.tc(LangData.TIP_HOE_MODE_PATH).copy().withStyle(ChatFormatting.BLUE);
		case 2:
			return LangData.tc(LangData.TIP_HOE_MODE_CULL).copy().withStyle(ChatFormatting.BLUE);
		}
	}
	
	public static Component opLangLong(byte mode) {
		switch (mode) {
		default:
		case 0:
			return LangData.tc(LangData.TIP_HOE_MODE_TILL_LONG).copy().withStyle(ChatFormatting.BLUE);
		case 1:
			return LangData.tc(LangData.TIP_HOE_MODE_PATH_LONG).copy().withStyle(ChatFormatting.BLUE);
		case 2:
			return LangData.tc(LangData.TIP_HOE_MODE_CULL_LONG).copy().withStyle(ChatFormatting.BLUE);
		}
	}
	
	public static void startSlashing(ItemStack stack, float power) {
		NBTUtil.setFloat(stack, TK_SLASHING, power);
	}
	
	public static void ceaseSlashing(ItemStack stack) {
		NBTUtil.setFloat(stack, TK_SLASHING, 0);
	}
	
	public static boolean isCurrentlySlashing(ItemStack stack) {
		return getSlashingPower(stack) > 0;
	}
	
	public static float getSlashingPower(ItemStack stack) {
		return NBTUtil.getFloat(stack, TK_SLASHING, 0);
	}
	
	public static boolean isValidAutoslashTarget(LivingEntity victim, Entity culprit) {
		return victim != null
				&& !victim.is(culprit)
				&& canHit(victim)
				&& !(culprit instanceof Player plr && EntUtil.isTamedByOrTrusts(victim, plr));
	}
	
	private static boolean canHit(LivingEntity victim) {
		if (!victim.isSpectator() && victim.isAlive() && victim.isPickable()) {
			return !EntUtil.isInvincible(victim);
		}
		return false;
	}
	

}
