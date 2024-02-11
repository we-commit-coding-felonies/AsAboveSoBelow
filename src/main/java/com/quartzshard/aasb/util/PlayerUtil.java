package com.quartzshard.aasb.util;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.util.MathUtil.Constants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class PlayerUtil {
	
	/**
	 * subclass containing things related to experience points & levels
	 * @author solunareclipse1
	 */
	public class Xp {
		/**
		 * gets a players current xp, in points
		 * @param player
		 * @return xp points player has
		 */
		public static long getXp(Player player) {
			// levels (as points) + (experience progress * amount for next level)
			return xpLvlToPoints(player.experienceLevel) + (long)(player.experienceProgress * player.getXpNeededForNextLevel());
		}
		
		/**
		 * inserts xp points into the player <br>
		 * will discard xp if the player cant hold any more
		 * @param player
		 * @param amount of xp in points to insert
		 */
		public static void insertXp(Player player, long amount) {
			long newXp = getXp(player) + amount;
			if (newXp >= Constants.Xp.VANILLA_MAX_POINTS) {
				player.experienceLevel = Constants.Xp.VANILLA_MAX_LVL;
				player.experienceProgress = 0;
				player.totalExperience = Integer.MAX_VALUE;
			} else {
				player.experienceLevel = xpPointsToLvl(newXp);
				long extra = newXp - xpLvlToPoints(player.experienceLevel);
				player.experienceProgress = (float) extra / (float) player.getXpNeededForNextLevel();
				// xp bar seems to not visually update without changing this
				player.totalExperience = (int) newXp;
			}
		}
		
		/**
		 * extracts xp from the given player
		 * @param player
		 * @param amount how much xp points to extract
		 * @return actual extracted amount
		 */
		public static long extractXp(Player player, long amount) {
			long newXp = getXp(player) - amount;
			
			if (newXp <= 0) {
				player.totalExperience = 0;
				player.experienceLevel = 0;
				player.experienceProgress = 0;
				return newXp + amount; // dont need to call getXp twice here
			}
			
			// 
			player.experienceLevel = xpPointsToLvl(newXp);
			long extra = newXp - xpLvlToPoints(player.experienceLevel);
			player.experienceProgress = (float) extra / (float) player.getXpNeededForNextLevel();
			player.totalExperience = (int) newXp;
			return amount;
		}
		
		/**
		 * Extracts a certain amount of levels from a player <br>
		 * Will extract extra points (experienceProgress) first
		 * @param player
		 * @param lvls the amount of levels to extract
		 * @return the extracted amount of points
		 */
		public static int extractLvl(Player player, int lvls) {
			int extracted = 0;
			for (int i = 0; i < lvls; i++) {
				
				if (player.experienceProgress <= 0 && player.experienceLevel <= 0) break;
				
				if (player.experienceProgress > 0) {
					extracted += (int) (player.experienceProgress * player.getXpNeededForNextLevel());
					player.experienceProgress = 0;
				} else if (player.experienceLevel > 0) {
					player.experienceLevel--;
					extracted += player.getXpNeededForNextLevel();
				}
			}
			
			player.totalExperience = Math.max(0, player.totalExperience - extracted);
			return extracted;
		}
		
		/**
		 * Extracts *all* xp from the player
		 * @param player
		 * @return
		 */
		public static long extractAll(Player player) {
			long extracted = 0;
			
			if (player.experienceProgress > 0) {
				extracted += (long) (player.experienceProgress * player.getXpNeededForNextLevel());
				player.experienceProgress = 0;
			}
			
			if (player.experienceLevel > 0) {
				extracted += xpLvlToPoints(player.experienceLevel);
				player.experienceLevel = 0;
			}
			
			player.totalExperience = 0;
			
			return extracted;
		}
		
		/**
		 * Converts XP Level to XP Points <br>
		 * Uses a long so we can work with larger amounts of xp
		 * @param lvl
		 * @return xp
		 */
		public static long xpLvlToPoints(long lvl) {
			if (lvl <= 0) return 0;
			
			if (lvl <= 16) {
				// x^2 + 6x
				return (lvl * lvl + 6 * lvl);
			} else if (lvl <= 31) {
				// 2.5x^2 - 40.5x + 360
				return (long) (2.5 * lvl * lvl - 40.5 * lvl + 360);
			} else {
				// 4.5x^2 - 162.5x + 2220
				return (long) (4.5 * lvl * lvl - 162.5 * lvl + 2220);
			}
		}
		
		/**
		 * Brute-forces the opposite of xpLvlToPoints <br>
		 * Is decently fast, even with very large numbers
		 * <p>
		 * Max return value is 1,431,655,783 (max level when using long)
		 * 
		 * @param xp
		 * @return lvl
		 */
		public static int xpPointsToLvl(long xp) {
			// out of range means we can skip the brute force
			// saves time, also handles negatives
			if (xp < 7) {
				return 0;
			} else if (xp >= Constants.Xp.TRANSFER_MAX_POINTS) {
				return Constants.Xp.TRANSFER_MAX_LVL;
			}
			
			// we set the initial tick value based on some precalculated values
			// lets us quickly handle big numbers, while not slowing down small ones
			int tick = 1;
			for (int i = 1; i < Constants.Xp.LVL_POWS_OF_TEN.length; i++) {
				if (xp < Constants.Xp.LVL_POWS_OF_TEN[i]) {
					tick = (int) Math.pow(10, i-1);
				}
			}
			
			int lvl = 0;
			long curPts = 0;
			boolean found = false;
			boolean dir = true; // true means we tick up, false means tick down
			
			while (!found) {
				if (dir) {
					lvl += tick;
				} else {
					lvl -= tick;
				}
				curPts = xpLvlToPoints(lvl);
				// if equal, we can stop here
				if (curPts == xp) {
					// set dir to false so we dont return lvl-1
					dir = false;
					found = true;
				} else {
					// check if we overshot
					if (dir ? curPts > xp : curPts < xp) {
						// if possible, increase precision
						// otherwise, weve found
						if (tick != 1) {
							dir = !dir;
							tick /= 10;
						}
						else found = true;
					}
				}
			}
			// we never want to return a level worth more than the input
			// this uses dir to check if we were > or <= when we found the lvl
			// if we were >, we need to -1 so that the level is worth less than input
			return dir ? lvl-1 : lvl;
		}
		
		/**
		 * Runs the same logic as {@link Player#getXpNeededForNextLevel()} using an arbitrary current level, rather than the players current level
		 * 
		 * @param curLvl the xp level
		 * @return xp needed to curLvl++
		 */
		public static int xpNeededToLevelUpFrom(int curLvl) {
			if (curLvl >= 30) {
				return 112 + (curLvl - 30) * 9;
			} else {
				return curLvl >= 15 ? 37 + (curLvl - 15) * 5 : 7 + curLvl * 2;
			}
		}
	}
	
	public static void swingArm(Player player, Level level, InteractionHand hand) {
		if (level instanceof ServerLevel lvl) {
			lvl.getChunkSource().broadcastAndSend(player, new ClientboundAnimatePacket(player, hand == InteractionHand.MAIN_HAND ? 0 : 3));
		}
	}
	
	public static void doSweepAttackParticle(Player culprit, ServerLevel level) {
		double rot1 = (-Mth.sin(culprit.getYRot() * ((float)Math.PI / 180f)));
		double rot2 = Mth.cos(culprit.getYRot() * ((float)Math.PI / 180f));
		level.sendParticles(ParticleTypes.SWEEP_ATTACK, culprit.getX()+rot1, culprit.getY(0.5), culprit.getZ()+rot2, 1, 0, 0, 0, 0);
	}

	public static boolean hasBreakPermission(ServerPlayer player, BlockPos pos) {
		return hasEditPermission(player, pos)
				&& ForgeHooks.onBlockBreakEvent(player.getCommandSenderWorld(), player.gameMode.getGameModeForPlayer(), player, pos) != -1;
	}

	public static boolean hasEditPermission(ServerPlayer player, BlockPos pos) {
		if (ServerLifecycleHooks.getCurrentServer().isUnderSpawnProtection((ServerLevel) player.getCommandSenderWorld(), pos, player))
			return false;
		return Arrays.stream(Direction.values()).allMatch(e -> player.mayUseItemAt(pos, e, ItemStack.EMPTY));
	}
	
	public static void coolDown(Player player, Item item, int ticks) {
		player.getCooldowns().addCooldown(item, ticks);
	}
	
	public static boolean onCooldown(Player player, Item item) {
		return player.getCooldowns().isOnCooldown(item);
	}

	/**
	 * Tries placing a block and fires an event for it.
	 * https://github.com/sinkillerj/ProjectE/blob/mc1.18.x/src/main/java/moze_intel/projecte/utils/PlayerHelper.java#L52
	 * @return Whether the block was successfully placed
	 */
	public static boolean checkedPlaceBlock(ServerPlayer player, BlockPos pos, BlockState state) {
		if (!hasEditPermission(player, pos)) {
			return false;
		}
		Level level = player.getCommandSenderWorld();
		BlockSnapshot before = BlockSnapshot.create(level.dimension(), level, pos);
		level.setBlockAndUpdate(pos, state);
		BlockEvent.EntityPlaceEvent evt = new BlockEvent.EntityPlaceEvent(before, Blocks.AIR.defaultBlockState(), player);
		MinecraftForge.EVENT_BUS.post(evt);
		if (evt.isCanceled()) {
			level.restoringBlockSnapshots = true;
			before.restore(true, false);
			level.restoringBlockSnapshots = false;
			return false;
		}
		return true;
	}

	@Nullable
	public static IItemHandler getCuriosInv(Player player) {
		return CuriosApi.getCuriosInventory(player).lazyMap(ICuriosItemHandler::getEquippedCurios).resolve().orElse(null);
	}

	@Nullable
	public static Tuple<ItemStack,SlotContext> getCurio(Player player, String type, int idx) {
		return CuriosApi.getCuriosInventory(player).lazyMap(handler -> {
			Tuple<ItemStack,SlotContext> curio = new Tuple<>(null,null);
			handler.findCurio(type, idx).ifPresent(result -> {
				curio.setA(result.stack());
				curio.setB(result.slotContext());
			});
			return curio;
		}).resolve().orElse(null);
	}
	

	// Next 2: https://github.com/sinkillerj/ProjectE/blob/68fbb2dea0cf8a6394fa6c7c084063046d94cee5/src/main/java/moze_intel/projecte/utils/PlayerHelper.java#L109C3-L127C3
	public static BlockHitResult getBlockLookingAt(Player player, double maxDistance) {
		Tuple<Vec3, Vec3> vecs = getLookVec(player, maxDistance);
		ClipContext ctx = new ClipContext(vecs.getA(), vecs.getB(), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
		return player.level().clip(ctx);
	}
	public static Tuple<Vec3, Vec3> getLookVec(Player player, double maxDistance) {
		// Thank you ForgeEssentials
		Vec3 look = player.getViewVector(1.0F);
		Vec3 playerPos = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
		Vec3 src = playerPos.add(0, player.getEyeHeight(), 0);
		Vec3 dest = src.add(look.x * maxDistance, look.y * maxDistance, look.z * maxDistance);
		return new Tuple<>(src, dest);
	}
	
	public static class PlayerSelectedHand {
		public static final String TK_HAND = "ActiveAlchemyHand";
		private InteractionHand hand = InteractionHand.MAIN_HAND;

		public boolean main() {
			return hand == InteractionHand.MAIN_HAND;
		}
		public boolean off() {
			return hand == InteractionHand.OFF_HAND;
		}
		
		public InteractionHand getHand() {
			return hand;
		}
		
		public void swapHand() {
			hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		}
		
		public void copyFrom(PlayerSelectedHand other) {
			hand = other.getHand();
		}

		public void serialize(CompoundTag tag) {
			tag.putBoolean(TK_HAND, hand == InteractionHand.MAIN_HAND);
		}
		public void deserialize(CompoundTag tag) {
			hand = tag.getBoolean(TK_HAND) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		}
	}
	
	public static class PlayerSelectedHandProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
		public static Capability<PlayerSelectedHand> PLAYER_SELECTED_HAND = CapabilityManager.get(new CapabilityToken<>(){});
		
		private final LazyOptional<PlayerSelectedHand> lo = LazyOptional.of(this::make);
		
		@Nullable private PlayerSelectedHand hand = null;
		
		private PlayerSelectedHand make() {
			// fuck you
			PlayerSelectedHand hand = new PlayerSelectedHand();
			if (this.hand == null) {
				this.hand = hand;
			}
			hand = this.hand;
			return hand;
		}
		
		@Override
		public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
			if (cap == PLAYER_SELECTED_HAND) {
				return lo.cast();
			}
			return LazyOptional.empty();
		}

		@Override
		public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
			return getCapability(cap);
		}
		
		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tag = new CompoundTag();
			make().serialize(tag);
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
