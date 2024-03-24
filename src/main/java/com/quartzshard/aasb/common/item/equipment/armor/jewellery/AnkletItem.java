package com.quartzshard.aasb.common.item.equipment.armor.jewellery;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.bind.ICanFeetMode;
import com.quartzshard.aasb.init.NetInit;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.net.server.SlowFallPacket;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class AnkletItem extends JewelleryArmorItem implements ICanFeetMode {
	public AnkletItem(Properties props) {
		super(Type.BOOTS, props);
	}
	
	public static final String TAG_UTILS = "DivinityState";
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		// TODO: COST
		if (utilsEnabled(stack)) {
			boolean flying = player.getAbilities().flying || player.isFallFlying();
			// slow fall
			if (!flying && !player.isShiftKeyDown() && !player.onGround() && !player.isSwimming()) {
				attemptSlowFall(player, level);
			}
			attemptLiquidWalk(player, player.isSprinting());
		}
	}
	
	private static void attemptSlowFall(Player player, Level level) {
		Vec3 vel = player.getDeltaMovement();
		if (vel.y <= 0) {
			Vec3 newVel = vel.multiply(1, 0.65, 1);
			if (level.isClientSide && ClientUtil.isJumpPressed()) {
				player.setDeltaMovement(newVel);
				player.fallDistance = 0;
				// we only send this every so often, because it doesnt need to be sent every tick to prevent fall damage
				if (level.getGameTime() % 3 == 0)
					NetInit.toServer(new SlowFallPacket(newVel.y));
			}
			if (newVel.y > -0.6) {
				//player.setDeltaMovement(newVel);
				player.fallDistance = 0;
			}
		}
	}
	
	private static boolean attemptLiquidWalk(Player player, boolean speed) {
		boolean didDo = false;
		Level level = player.level();
		boolean fly = player.getAbilities().flying || player.isFallFlying();
		int x = (int)Math.floor(player.getX());
		int y = (int)(player.getY() - player.getMyRidingOffset());
		int z = (int)Math.floor(player.getZ());
		BlockPos pos = new BlockPos(x, y, z);
		@NotNull Vec3 vel = player.getDeltaMovement();
		
		// rewrite
		AABB plrBox = player.getBoundingBox();
		@NotNull List<VoxelShape> list = level.getEntityCollisions(player, plrBox.expandTowards(vel));
		Vec3 cVel = vel.lengthSqr() == 0.0D ? vel : Player.collideBoundingBox(player, vel, plrBox, level, list);
		Vec3 nextPos = player.position().add(cVel);
		BlockPos nextBlockPos = BlockPos.containing(nextPos.x, nextPos.y, nextPos.z);
		FluidState fluid = level.getFluidState(nextBlockPos);
		if (player.level().isEmptyBlock(pos) && fluid != Fluids.EMPTY.defaultFluidState() && fluid.isSource()) {
			// the player will be in a liquid next tick
			if (!fly && vel.y <= 0 && !player.isShiftKeyDown()) {
				// we want to stop that and put them on the surface instead
				// so we will move upwards until we find a non-fluid block
				BlockPos surfacePos = nextBlockPos.above();
				while (level.getFluidState(surfacePos) != Fluids.EMPTY.defaultFluidState()) {
					if (level.isOutsideBuildHeight(surfacePos.getY())) {
						surfacePos = nextBlockPos;
						break;
					}
					surfacePos = surfacePos.above();
				}
				boolean performJesus = surfacePos != nextBlockPos && level.isEmptyBlock(surfacePos);
				if (performJesus) {
					// we found a valid surface position
					@NotNull Vec3 old = player.position();
					// we set the players position to the surface of the water
					player.setPos(new Vec3(old.x, surfacePos.getY()-0.11211, old.z));
					player.setDeltaMovement(vel.multiply(1,0,1)); // make them stop moving down
					Random r = AASB.RNG;
					int ix = Mth.floor(player.getX());
					int iy = Mth.floor(player.getY() - 0.2d);
					int iz = Mth.floor(player.getZ());
					BlockPos underPos = new BlockPos(ix, iy, iz);
					BlockState liquidUnder = level.getBlockState(underPos);
					boolean lava = liquidUnder.is(Blocks.LAVA);
					if (vel.y <= -0.1) {
						level.playSound(null, player.blockPosition(), lava ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.PLAYER_SPLASH, SoundSource.PLAYERS, 1f, r.nextFloat(0.5f, 1.5f));
						// damage if necessary, a bit of leeway because this is jank
						player.causeFallDamage(player.fallDistance, 1, EntityInit.dmg(EntityInit.DMG_SURFACE_TENSION_ENV, level));
						player.fallDistance = 0;
					}
					player.setOnGround(true);
					didDo = true;
					// particles code is huge so it gets its own function
					jesusFX(player, level, underPos, liquidUnder, speed, r);
				}
			}
		}	
		if (!level.isClientSide) {
			// sprint modifier
			AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
			if (attribute != null) {
				AttributeModifier boost = new AttributeModifier(UUID.fromString("4a109872-83ae-4781-9117-08fd2b8ce00b"), "Anklet of the Prophet - Sprinting on liquid", 0.05, AttributeModifier.Operation.ADDITION);
				if (speed && !fly && vel.y <= 0 && didDo && player.level().isEmptyBlock(pos)) {
					if (!attribute.hasModifier(boost)) {
						attribute.addTransientModifier(boost);
					}
				} else if (attribute.hasModifier(boost)) {
					attribute.removeModifier(boost);
				}
			}
		}
		return didDo;
	}
	
	private static void jesusFX(Player player, @NotNull Level level, BlockPos underPos, @NotNull BlockState liquidUnder, boolean speed, Random r) {
		double bbw = player.getBbWidth();
		boolean water = liquidUnder.is(Blocks.WATER);
		boolean lava = liquidUnder.is(Blocks.LAVA);
		
		// Idle particles
		level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, liquidUnder).setPos(underPos),
				player.getX() + bbw*3*(r.nextDouble()-0.5), player.getY(), player.getZ() + bbw*3*(r.nextDouble()-0.5),
				0, 20, 0);
		
		// Idle sound
		if (level.getGameTime() % 8 == 0)
			level.playSound(null, player.blockPosition(),
					lava ? SoundEvents.LAVA_POP : SoundEvents.PLAYER_SWIM,
					SoundSource.PLAYERS, 0.1f, r.nextFloat(0.5f, 0.7f));
		
		// Splashy step sound
		if (speed && level.getGameTime() % (lava?6:5) == 0)
			level.playSound(null, player.blockPosition(),
					lava ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.PLAYER_SPLASH_HIGH_SPEED,
					SoundSource.PLAYERS, lava?3:0.3f, r.nextFloat(0.5f, 1.5f));
		
		// Movement FX
		if (player.zza != 0 || player.xxa != 0) {
			if (!liquidUnder.addRunningEffects(level, underPos, player)) {
				for (int i = 0; i < 5; i++) {
					double pX = player.getX();
					double pY = player.getY();
					double pZ = player.getZ();
					double bubX = pX + (r.nextDouble() - 0.5) * bbw;
					double bubY = pY + (r.nextDouble() - 0.5) * bbw;
					double bubZ = pZ + (r.nextDouble() - 0.5) * bbw;
					Vec3 vel = player.getDeltaMovement();
					
					// "splash1" particle
					double pv1 = lava ? -0.25 : -4;
					if (speed || r.nextInt(3) == 0)
						level.addParticle(lava ? speed ? ParticleTypes.LAVA : ParticleTypes.SMALL_FLAME : ParticleTypes.RAIN, player.getX() + (r.nextDouble() - 0.5) * bbw, player.getY() + 0.1, player.getZ() + (r.nextDouble() - 0.5) * bbw, vel.x * pv1, lava ? 0.1 : 1.5, vel.z * pv1);
					
					// "splash2" particle
					if (speed || r.nextInt(3) == 0)
						level.addParticle(lava ? ParticleTypes.FALLING_LAVA : ParticleTypes.SPLASH, player.getX() + (r.nextDouble() - 0.5) * bbw, player.getY() + 0.1, player.getZ() + (r.nextDouble() - 0.5) * bbw, vel.x * -4d, 1.5, vel.z * -4d);
					
					// "bubble" particle
					if (speed || r.nextInt(3) == 0)
						level.addParticle(lava ? ParticleTypes.FLAME : ParticleTypes.BUBBLE, bubX, bubY, bubZ, 0, 0, 0);
					
					// movement wake particles
					if (speed || r.nextInt(3) == 0)
						for (int j = 0; j < (speed ? 3 : 1); j++) {
							@NotNull Block p = lava ? Blocks.LAVA : water ? Blocks.WATER : liquidUnder.getBlock();
							if (water || lava) // hardcoded fancier particles, we just use the blockstate for mod liquids
								switch (r.nextInt(10)) {
								default:
									break;
								case 1:
									p = lava ? Blocks.ORANGE_CONCRETE_POWDER : Blocks.LIGHT_BLUE_STAINED_GLASS;
									break;
								case 2:
									p = lava ? Blocks.RED_CONCRETE_POWDER : Blocks.LIGHT_GRAY_STAINED_GLASS;
									break;
								case 3:
									p = lava ? Blocks.YELLOW_CONCRETE_POWDER : Blocks.BLUE_STAINED_GLASS;
									break;
								case 4:
									p = lava ? Blocks.MAGMA_BLOCK : Blocks.ICE;
									break;
								case 5:
									p = lava ? Blocks.GRANITE : Blocks.BLUE_ICE;
									break;
								}
							Block blockParticle = p;
							level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockParticle.defaultBlockState()).setPos(underPos),
									player.getX() + (r.nextDouble() - 0.5) * bbw, player.getY() + r.nextDouble()*(player.getBbHeight()/3), player.getZ() + (r.nextDouble() - 0.5) * bbw,
									vel.x * -4d, 1.5, vel.z * -4d);
						}
				}
			}
		}
	}
	
	@Override
	public boolean onPressedFeetMode(@NotNull ItemStack stack, ServerPlayer player, ServerLevel level) {
		toggleUtils(stack);
		return true;
	}
	
	public boolean utilsEnabled(@NotNull ItemStack stack) {
		return NBTUtil.getBoolean(stack, TAG_UTILS, true);
	}
	
	public void toggleUtils(ItemStack stack) {
		setUtils(stack, !utilsEnabled(stack));
	}
	
	public void setUtils(ItemStack stack, boolean state) {
		NBTUtil.setBoolean(stack, TAG_UTILS, state);
	}

}
