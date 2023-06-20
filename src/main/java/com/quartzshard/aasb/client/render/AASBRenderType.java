package com.quartzshard.aasb.client.render;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class AASBRenderType extends RenderType {

	// sorry nothing
	private AASBRenderType(String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}
	

	public static final Function<ResourceLocation, RenderType> MAGNUM_OPUS_HALO = Util.memoize(resourceLocation -> {
		RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.createCompositeState(true);
		return create("magnum_opus_halo", DefaultVertexFormat.POSITION_COLOR_TEX, Mode.QUADS, 256, true, false, state);
	});

}
