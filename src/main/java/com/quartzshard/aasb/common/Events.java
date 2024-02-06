package com.quartzshard.aasb.common;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.IDigStabilizer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * some misc events that dont really have a better place to be
 */
@Mod.EventBusSubscriber(modid = AASB.MODID)
public class Events {
	
	@SubscribeEvent
	public static void staticSpeedBreakerHandler(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();
		if (stack.getItem() instanceof IDigStabilizer tool) {
			Level level = player.level();
			BlockState state = event.getState();
			int breakTicks = tool.blockBreakSpeedInTicks(stack, state);
			if (event.getPosition().isPresent()) {
				float blockStrength = event.getState().getDestroySpeed(level, event.getPosition().get());
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
