package com.quartzshard.aasb.client.model.lab;

import net.minecraft.resources.ResourceLocation;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.block.lab.te.starters.DistillationTE;

import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DistillationModel extends AnimatedGeoModel<DistillationTE> {
	private static final ResourceLocation modelResource = new ResourceLocation(AsAboveSoBelow.MODID, "geo/lab/distillation-retort.geo.json");
	private static final ResourceLocation textureResource = new ResourceLocation(AsAboveSoBelow.MODID, "textures/block/lab/distillation-retort.png");
	private static final ResourceLocation animationResource = new ResourceLocation(AsAboveSoBelow.MODID, "animations/lab/distillation-retort.animation.json");

	@Override
	public ResourceLocation getAnimationFileLocation(DistillationTE animatable) {
		return animationResource;
	}

	@Override
	public ResourceLocation getModelLocation(DistillationTE object) {
		return modelResource;
	}

	@Override
	public ResourceLocation getTextureLocation(DistillationTE object) {
		return textureResource;
	}

}
