package com.quartzshard.aasb.util;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IStaticSpeedBreaker;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class EventHelper {

	@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID)
	/**
	 * homeless shelter for event handlers that have nowhere else to go
	 * @author solunareclipse1
	 */
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
	}
}
