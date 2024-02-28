package com.quartzshard.aasb.client.render.text;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.quartzshard.aasb.api.alchemy.aspect.IAspect;
import com.quartzshard.aasb.client.render.AASBRenderType;
import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.util.Colors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import org.joml.Matrix4f;

public class AspectFont extends Font {

	public AspectFont(Font base) {
		super(base.fonts, base.filterFishyGlyphs);
	}

	@Override
	public float renderText(String txt, float x, float y, int color, boolean shadow, Matrix4f pose, MultiBufferSource bufferSource, Font.DisplayMode mode, int bgColor, int light) {
		AspectRenderOutput rendOut = new AspectRenderOutput(bufferSource, x, y, color, shadow, pose, mode, light);
		StringDecomposer.iterateFormatted(txt, Style.EMPTY, rendOut);
		return rendOut.finish(bgColor, x);
	}

	@Override
	protected float renderText(FormattedCharSequence txt, float x, float y, int color, boolean shadow, Matrix4f pose, MultiBufferSource bufferSource, Font.DisplayMode mode, int bgColor, int light) {
		AspectRenderOutput rendOut = new AspectRenderOutput(bufferSource, x, y, color, shadow, pose, mode, light);
		txt.accept(rendOut);
		return rendOut.finish(bgColor, x);
	}

	class AspectRenderOutput extends StringRenderOutput {
		public AspectRenderOutput(MultiBufferSource bufferSource, float x, float y, int color, boolean hasShadow, Matrix4f pose, Font.DisplayMode mode, int light) {
			super(bufferSource, x, y, color, hasShadow, pose, mode, light);
		}

		/**
		 * Accepts a single code point from a {@link net.minecraft.util.FormattedCharSequence}.
		 * @return {@code true} to accept more characters, {@code false} to stop traversing the sequence.
		 * @param stringPos Contains the relative position of the character in the current sub-sequence.
		 * If multiple formatted char sequences have been combined, this value will reset to {@code 0} after each sequence
		 * has been fully consumed.
		 */
		public boolean accept(int stringPos, Style style, int unicodeIdx) {
			IAspect<?> aspect = ClientInit.getAspectForUnicode(unicodeIdx);
			if (aspect != null) {
				VertexConsumer buffer = this.bufferSource.getBuffer(AASBRenderType.ASPECT_GLYPH.apply(aspect.symbolTexture()).select(this.mode));
				int i = 3;
				float
					left = 0,
					right = 19,
					up = 0,
					down = 19;
				float x1 = this.x + (left-4f);
				float x2 = this.x + (right-4f);
				float y1 = this.y + (up-5.5f);
				float y2 = this.y + (down-5.5f);
				int[] rgb = Colors.rgbFromInt(aspect.getColor());
				int a = 255;
				buffer.vertex(this.pose, x1, y1, 0.0F).color(rgb[0],rgb[1],rgb[2],a).uv(0, 0).uv2(this.packedLightCoords).endVertex();
				buffer.vertex(this.pose, x1, y2, 0.0F).color(rgb[0],rgb[1],rgb[2],a).uv(0, 1).uv2(this.packedLightCoords).endVertex();
				buffer.vertex(this.pose, x2, y2, 0.0F).color(rgb[0],rgb[1],rgb[2],a).uv(1, 1).uv2(this.packedLightCoords).endVertex();
				buffer.vertex(this.pose, x2, y1, 0.0F).color(rgb[0],rgb[1],rgb[2],a).uv(1, 0).uv2(this.packedLightCoords).endVertex();
				this.x += right-7f;
				return true;
			}
			return super.accept(stringPos, style, unicodeIdx);
		}
	}
}