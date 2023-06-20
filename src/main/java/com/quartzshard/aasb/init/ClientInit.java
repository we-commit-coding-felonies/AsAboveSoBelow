package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IHermeticTool;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.client.particle.CutParticle;
import com.quartzshard.aasb.client.render.layer.AASBPlayerLayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInit {
	public static final ResourceLocation EMPOWER_CHARGE = AsAboveSoBelow.rl("empowerment_charge");
	public static final ResourceLocation SHAPE_RUNE = AsAboveSoBelow.rl("shape_rune");
	
	public static void init(final FMLClientSetupEvent event) {
		AASBKeys.register();
		event.enqueueWork(() -> {
			ItemProperties.registerGeneric(EMPOWER_CHARGE, ClientInit::getEmpowerCharge);
			ItemProperties.registerGeneric(SHAPE_RUNE, ClientInit::getShapeRune);
		});
	}
	
	private static float getEmpowerCharge(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		if (stack.getItem() instanceof IHermeticTool item) {
			return item.getCharge(stack) > 0 ? 1 : 0;
		}
		return 0;
	}
	
	private static float getShapeRune(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		if (stack.getItem() instanceof IHermeticTool item) {
			return item.getRunesVal(stack);
		}
		return 0;
	}
	

	
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		provider(EffectInit.Particles.CUT_PARTICLE.get(), new CutParticle.Provider());
	}

	private static <T extends ParticleOptions> void provider(ParticleType<T> type, ParticleProvider<T> provider) {
		Minecraft.getInstance().particleEngine.register(type, provider);
	}
	private static <T extends ParticleOptions> void spriteProvider(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> provider) {
		Minecraft.getInstance().particleEngine.register(type, provider);
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
}
