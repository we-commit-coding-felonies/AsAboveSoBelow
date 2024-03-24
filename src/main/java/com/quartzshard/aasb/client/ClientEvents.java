package com.quartzshard.aasb.client;

import com.mojang.datafixers.util.Either;
import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.Phil;
import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.aspect.WayAspect;
import com.quartzshard.aasb.api.item.IWayHolder;
import com.quartzshard.aasb.client.sound.SentientWhispersAmbient;
import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.util.ClientUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = AASB.MODID, value = Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof SentientArrowEntity projectile && ClientUtil.mc().mouseHandler.isMouseGrabbed()) {
			ClientUtil.mc().getSoundManager().play(new SentientWhispersAmbient(projectile, 392));
		}
	}

	public static void onDisplayTooltip(RenderTooltipEvent.GatherComponents event) {
		if (Screen.hasAltDown()) { // TODO convert this to actual keybind. doing it the same way as the normal ones wasnt working
			// Holding down ALT, display aspects
			ItemStack stack = event.getItemStack();
			if (stack.isEmpty())
				return;
			AlchData aspects = Phil.getAspects(stack);
			List<Either<FormattedText,TooltipComponent>> tips = event.getTooltipElements();
			tips.get(0).ifLeft(txt -> {
				tips.clear(); // clear other tooltips, restore name with some extra text
				if (Screen.hasShiftDown() && stack.getItem() instanceof IWayHolder waystone && waystone.getStoredWay(stack) > 0) {
					// Also holding SHIFT, and we have stored way. display that
					tips.add(Either.left(LangData.tc(LangData.TIP_ASPECTJAR, txt)));
					//tips.add(Either.left(Component.empty())); // newline so render doesnt overlap
					tips.add(Either.right(new LangData.WayTooltip(waystone.getStoredWay(stack))));
				} else {
					tips.add(Either.left(LangData.tc(LangData.TIP_ASPECTS, txt)));
					tips.add(Either.left(Component.empty())); // newline so render doesnt overlap
					// TODO query player knowledge
					if (aspects == Phil.UNMAPPED) {
						tips.add(Either.left(LangData.tc(LangData.TIP_ASPECTS_UNMAPPED_1)));
						tips.add(Either.left(LangData.tc(LangData.TIP_ASPECTS_UNMAPPED_2)));
					} else {
						tips.add(Either.right(new LangData.AspectTooltip(aspects)));
					}
				}
			});
		}
	}
}
