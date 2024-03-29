package com.quartzshard.aasb.init;

import java.util.Random;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.api.item.IWayHolder;
import com.quartzshard.aasb.client.particle.CutParticle;
import com.quartzshard.aasb.client.render.AASBPlayerLayer;
import com.quartzshard.aasb.client.render.MustangRenderer;
import com.quartzshard.aasb.client.render.SentientArrowRenderer;
import com.quartzshard.aasb.common.item.MiniumStoneItem;
import com.quartzshard.aasb.common.item.equipment.WaystoneItem;
import com.quartzshard.aasb.init.object.EntityInit;
import com.quartzshard.aasb.init.object.ItemInit;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.Colors;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AASB.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {
	public static final ResourceLocation PRED_RUNES = AASB.rl("inscribed_runes");
	public static final ResourceLocation PRED_MINIUM = AASB.rl("minium_variant");
	public static final ResourceLocation PRED_WAY_HOLDER = AASB.rl("way_holder_status");
	//public static final ResourceLocation FLASK_STATUS = AASB.rl("flask_status");
	
	public static void init(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.registerGeneric(PRED_WAY_HOLDER, ClientInit::getWayHolderStatus);
			ItemProperties.registerGeneric(PRED_RUNES, ClientInit::getHermeticRunes);
			ItemProperties.register(ItemInit.MINIUM_STONE.get(), PRED_MINIUM, ClientInit::getMiniumVariant);
			//ItemProperties.register(ObjectInit.Items.FLASK_LEAD.get(), FLASK_STATUS, ClientInit::getFlaskStatus);
			//ItemProperties.register(ObjectInit.Items.FLASK_GOLD.get(), FLASK_STATUS, ClientInit::getFlaskStatus);
			//ItemProperties.register(ObjectInit.Items.FLASK_AETHER.get(), FLASK_STATUS, ClientInit::getFlaskStatus);
		});
	}
	
	@SubscribeEvent
	public static void addTints(final RegisterColorHandlersEvent.Item event) {
		event.register(ClientInit::getWayHolderColor,
				ItemInit.WAYSTONE.get(),
				ItemInit.AMULET.get());
		event.register(ClientInit::getWayUnstableColor, ItemInit.WAY_GRENADE.get());
		event.register(ClientInit::getRuneColor,
				ItemInit.GLOVE1.get(),
				ItemInit.BRACELET1.get(),
				ItemInit.CHARM1.get(),
				ItemInit.GLOVE2.get(),
				ItemInit.BRACELET2.get(),
				ItemInit.CHARM2.get()
		);
	}
	
	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.registerSpecial(FxInit.PTC_CUT.get(), new CutParticle.Provider());
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityInit.ENT_WAY_GRENADE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(EntityInit.ENT_SENTIENT_ARROW.get(), ctx -> new SentientArrowRenderer(ctx));
		//event.registerEntityRenderer(ObjectInit.Entities.HORROR.get(), ctx -> new HorrorRenderer(ctx));
		event.registerEntityRenderer(EntityInit.ENT_MUSTANG.get(), ctx -> new MustangRenderer(ctx));
		//event.registerBlockEntityRenderer(ObjectInit.TileEntities.DISTILLATION_TE.get(), DistillationRetortRenderer::new);
	}

	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (String skinName : event.getSkins()) {
			PlayerRenderer skin = event.getSkin(skinName);
			if (skin != null) {
				skin.addLayer(new AASBPlayerLayer(skin));
			}
		}
	}
	
	private static float getWayHolderStatus(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		if (stack.getItem() instanceof IWayHolder item) {
			return item.getStoredWay(stack) > 0 ? 1 : 0;
		}
		return 0;
	}
	private static int getWayHolderColor(ItemStack stack, int layer) {
		if (layer == 1 && stack.getItem() instanceof IWayHolder item) {
			return Colors.materiaGradient((float)item.getStoredWay(stack) / (float)item.getMaxWay(stack));
		}
		return -1;
	}
	private static int getWayUnstableColor(ItemStack stack, int layer) {
		if (layer == 1) {
			Random rng = new Random();
			return Colors.materiaGradient(rng.nextFloat());
		}
		return -1;
	}
	private static float getHermeticRunes(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		// TODO make this work generically for any IRuneable
		if (stack.getItem() instanceof IHermeticTool) {
			return IHermeticTool.getRunesVal(stack);
		}
		return 0;
	}
	private static int getRuneColor(ItemStack stack, int layer) {
		if (layer != 0 && stack.getItem() instanceof IRuneable item) {
			Rune rune = item.getInscribedRunes(stack).get(layer-1);
			if (rune != null) {
				return rune.color();
			}
		}
		return -1;
	}
	private static float getFlaskStatus(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		//if (stack.getItem() instanceof FlaskItem flask) {
		//	if (flask.hasStored(stack)) {
		//		return 1;
		//	}
		//}
		return 0;
	}
	private static float getMiniumVariant(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		if (stack.getItem() instanceof MiniumStoneItem item) {
			byte b = item.getVariant(stack);
			return b >= 0 ? b : 0;
		}
		return 0;
	}
	
	


}
