package com.quartzshard.aasb.client.render.entity.tile;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.quartzshard.aasb.client.model.lab.DistillationModel;
import com.quartzshard.aasb.common.block.lab.te.starters.DistillationTE;

import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class DistillationRetortRenderer extends GeoBlockRenderer<DistillationTE> {

	public DistillationRetortRenderer(BlockEntityRendererProvider.Context rendererProvider) {
		super(rendererProvider, new DistillationModel());
		// TODO Auto-generated constructor stub
	}

}
