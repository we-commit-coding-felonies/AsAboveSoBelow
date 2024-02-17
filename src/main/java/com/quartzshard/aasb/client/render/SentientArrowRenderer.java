package com.quartzshard.aasb.client.render;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.entity.projectile.SentientArrowEntity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class SentientArrowRenderer extends ArrowRenderer<SentientArrowEntity> {
	public static final ResourceLocation ACTIVE_TEXTURE = AASB.rl("textures/entity/projectile/sentient_arrow/active.png");
	public static final ResourceLocation INERT_TEXTURE = AASB.rl("textures/entity/projectile/sentient_arrow/inert.png");

	public SentientArrowRenderer(Context ctx) {
		super(ctx);
	}
	
	@Override
	public ResourceLocation getTextureLocation(SentientArrowEntity arrow) {
		return !arrow.isInert() ? ACTIVE_TEXTURE : INERT_TEXTURE;
	}
	
	@Override
	protected int getBlockLightLevel(SentientArrowEntity arrow, BlockPos pos) {
		return !arrow.isInert() ? 15 : super.getBlockLightLevel(arrow, pos);
	}

}
