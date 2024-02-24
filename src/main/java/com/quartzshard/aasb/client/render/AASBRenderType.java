package com.quartzshard.aasb.client.render;

import java.util.OptionalDouble;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AASBRenderType extends RenderType {

	// sorry nothing
	private AASBRenderType(String name, @NotNull VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
		super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}
	

	public static final Function<ResourceLocation, RenderType>
		MAGNUM_OPUS_HALO = Util.memoize(rl -> {
			RenderType.CompositeState state = RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.setCullState(NO_CULL)
					.createCompositeState(true);
			return create("magnum_opus_halo", DefaultVertexFormat.POSITION_COLOR_TEX, Mode.QUADS, 256, true, false, state);
		}),
		ASPECT_TOOLTIP = Util.memoize(rl -> {
			RenderType.CompositeState state = RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_TEX_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setDepthTestState(LEQUAL_DEPTH_TEST)
				.createCompositeState(false);
			return create("aspect_tooltip", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false, state);
		});
	public static final RenderType
		MUSTANG_LINES = create("mustang_lines",
			DefaultVertexFormat.POSITION_COLOR_NORMAL, Mode.LINES, 128,false, false,
			lineState(3, false, false));


	/**
	 * vanilla LINES layer with line width defined (and optionally depth disabled)
	 * https://github.com/VazkiiMods/Botania/blob/1.18.x/Xplat/src/main/java/vazkii/botania/client/core/helper/RenderHelper.java#L220
	 * @param width
	 * @param direct
	 * @param noDepth
	 * @return
	 */
	private static CompositeState lineState(double width, boolean direct, boolean noDepth) {
		@NotNull var builder = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_LINES_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(width)))
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setWriteMaskState(noDepth ? COLOR_WRITE : COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL);
		if (!direct) {
			builder = builder.setOutputState(ITEM_ENTITY_TARGET);
		}
		if (noDepth) {
			builder = builder.setDepthTestState(NO_DEPTH_TEST);
		}
		return builder.createCompositeState(false);
	}

	private static CompositeState texTipState(double width, boolean direct, boolean noDepth) {
		@NotNull var builder = RenderType.CompositeState.builder()
			.setShaderState(RENDERTYPE_LINES_SHADER)
			.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(width)))
			.setLayeringState(VIEW_OFFSET_Z_LAYERING)
			.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
			.setWriteMaskState(noDepth ? COLOR_WRITE : COLOR_DEPTH_WRITE)
			.setCullState(NO_CULL);
		if (!direct) {
			builder = builder.setOutputState(ITEM_ENTITY_TARGET);
		}
		if (noDepth) {
			builder = builder.setDepthTestState(NO_DEPTH_TEST);
		}
		return builder.createCompositeState(false);
	}

}
