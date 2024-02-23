package com.quartzshard.aasb.client;

import com.mojang.datafixers.util.Either;
import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.Phil;
import com.quartzshard.aasb.client.sound.SentientWhispersAmbient;
import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.TipUtil;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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

	//@SubscribeEvent
	public static void toolTipEvent(RenderTooltipEvent.GatherComponents event) {
		System.out.println("event :O");
		ItemStack stack = event.getItemStack();
		if (stack.isEmpty()) {
			return;
		}
		Player clientPlayer = ClientUtil.mc().player;

		AlchData aspects = Phil.getAspects(stack);
		List<Either<FormattedText,TooltipComponent>> tips = event.getTooltipElements();
		if (aspects == Phil.UNMAPPED) {
			tips.add(Either.left(Component.translatable("This item cannot be")));
			tips.add(Either.left(Component.translatable("described by alchemy.")));
		} else {
			Either<FormattedText,TooltipComponent> name = tips.get(0);
			tips.clear(); // get item name before clearing, restore it
			tips.add(name);
			tips.add(Either.left(Component.empty()));
			tips.add(Either.right(new TipUtil.AspectTooltip(aspects)));
		}
			/*
			if (AugmentUtil.getLevel(event.getItemStack()) > 0) {
				event.getTooltipElements().add(Either.right(new GlowingTextTooltip(Component.translatable(Embers.MODID + ".tooltip.heat_level").withStyle(ChatFormatting.GRAY).getVisualOrderText(), Component.literal("" + AugmentUtil.getLevel(event.getItemStack())).getVisualOrderText())));
				int slots = AugmentUtil.getLevel(event.getItemStack()) - AugmentUtil.getTotalAugmentLevel(event.getItemStack());
				if (slots > 0)
					event.getTooltipElements().add(Either.right(new GlowingTextTooltip(Component.translatable(Embers.MODID + ".tooltip.augment_slots").withStyle(ChatFormatting.GRAY).getVisualOrderText(), Component.literal("" + slots).getVisualOrderText())));
			}
			event.getTooltipElements().add(Either.right(new HeatBarTooltip(Component.translatable(Embers.MODID + ".tooltip.heat_amount").withStyle(ChatFormatting.GRAY).getVisualOrderText(), AugmentUtil.getHeat(event.getItemStack()), AugmentUtil.getMaxHeat(event.getItemStack()))));
			List<IAugment> augments = AugmentUtil.getAugments(event.getItemStack()).stream().filter(x -> x.shouldRenderTooltip()).collect(Collectors.toList());
			if (augments.size() > 0) {
				event.getTooltipElements().add(Either.left(Component.translatable(Embers.MODID + ".tooltip.augments").withStyle(ChatFormatting.GRAY)));
				for (IAugment augment : augments) {
					int level = AugmentUtil.getAugmentLevel(event.getItemStack(), augment);
					event.getTooltipElements().add(Either.right(new GlowingTextTooltip(Component.translatable(Embers.MODID + ".tooltip.augment." + augment.getName().toLanguageKey(), Component.translatable(getFormattedModifierLevel(level))).getVisualOrderText())));
				}
			}
			 */

		/*
		if (ProjectEConfig.client.emcToolTips.get() && (!ProjectEConfig.client.shiftEmcToolTips.get() || Screen.hasShiftDown())) {
			long value = EMCHelper.getEmcValue(current);
			if (value > 0) {
				event.getToolTip().add(EMCHelper.getEmcTextComponent(value, 1));
				if (current.getCount() > 1) {
					event.getToolTip().add(EMCHelper.getEmcTextComponent(value, current.getCount()));
				}
				if (clientPlayer != null && (!ProjectEConfig.client.shiftLearnedToolTips.get() || Screen.hasShiftDown())) {
					if (clientPlayer.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).map(k -> k.hasKnowledge(current)).orElse(false)) {
						event.getToolTip().add(PELang.EMC_HAS_KNOWLEDGE.translateColored(ChatFormatting.YELLOW));
					} else {
						event.getToolTip().add(PELang.EMC_NO_KNOWLEDGE.translateColored(ChatFormatting.RED));
					}
				}
			}
		}
		 */
	}
}
