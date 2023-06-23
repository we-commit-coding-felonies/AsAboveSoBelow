package com.quartzshard.aasb.util;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IStaticSpeedBreaker;
import com.quartzshard.aasb.common.item.equipment.tool.InternalOmnitool;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class EventHelper {

	/**
	 * homeless shelter for event handlers that have nowhere else to go
	 * @author solunareclipse1
	 */
	@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID)
	class Handlers {
		
		@SubscribeEvent
		public static void staticSpeedBreakerHandler(PlayerEvent.BreakSpeed event) {
			Player player = event.getPlayer();
			ItemStack stack = player.getMainHandItem();
			if (stack.getItem() instanceof IStaticSpeedBreaker tool) {
				Level level = player.level;
				BlockState state = event.getState();
				int breakTicks = tool.blockBreakSpeedInTicks(stack, state);
				float blockStrength = event.getState().getDestroySpeed(level, event.getPos());
				if (breakTicks > 0 && blockStrength >= 0) {
					if (breakTicks > 1 && blockStrength != 0) {
						event.setNewSpeed(32f / (breakTicks/blockStrength));
					} else {
						event.setNewSpeed(Float.POSITIVE_INFINITY);
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void devToolBreakUnbreakablesHandler(PlayerInteractEvent.LeftClickBlock event) {
			if (event.getPlayer() instanceof ServerPlayer player) {
				ItemStack stack = event.getItemStack();
				if (stack.getItem() instanceof InternalOmnitool tool && tool.isFoil(stack)) {
					BlockPos pos = event.getPos();
					Level level = player.level;
					BlockState block = level.getBlockState(pos);
					if (block.getDestroySpeed(level, pos) < 0 && tool.blockBreakSpeedInTicks(stack, block) == 1) {
						player.spawnAtLocation(block.getBlock()).setPos(Vec3.atCenterOf(pos));
						level.destroyBlock(pos, true);
					}
				}
			}
		}
	}
}
