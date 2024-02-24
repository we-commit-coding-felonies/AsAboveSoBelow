package com.quartzshard.aasb.client.gui.tip;

import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.aspect.WayAspect;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.util.Colors;
import com.quartzshard.aasb.util.MathUtil;
import com.quartzshard.aasb.util.RenderUtil;
import com.quartzshard.aasb.util.TipUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;

public class AspectsClientTooltip implements ClientTooltipComponent {

	TipUtil.AspectTooltip dat;

	public AspectsClientTooltip(TipUtil.AspectTooltip dat) {
		this.dat = dat;
	}

	@Override
	public int getHeight() {
		return 53*2;
	}

	@Override
	public int getWidth(Font font) {
		return 64*2;//font.width("The Philosopher's Stone");// + dat.barWidth - 24;
	}

	@Override
	public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
		//font.drawInBatch(dat.normalText, (float)mouseX, (float)mouseY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
		x += 32;
		float size = 64;
		RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), dat.alchData().complexity().symbolTexture(), 0xffffff,
			x, y, 0, size, size, 0, 0, 1, 1);
		Vec2 pos = new Vec2(0,48);
		if (dat.alchData().complexity() == ComplexityAspect.UNKNOWN) {
			// TODO make actually query player knowledge
			// player has not learned this item so we just do question marks
			for (int i = 0; i < 3; i++) {
				RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), ComplexityAspect.UNKNOWN.symbolTexture(), 0xffffff,
					x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
				if (i != 2) pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			}
		} else {
			ResourceLocation nullTex = ComplexityAspect.NULLED.symbolTexture();
			ResourceLocation
				way = dat.alchData().way() == null ? nullTex : dat.alchData().way().symbolTexture(),
				shape = dat.alchData().shape() == null ? nullTex : dat.alchData().shape().symbolTexture(),
				form = dat.alchData().form() == null ? nullTex : dat.alchData().form().symbolTexture();
			int nullColor = 0x8e9e99, color = way == nullTex ? nullColor : 0xffff99;
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), way, color,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			color = shape == nullTex ? nullColor : dat.alchData().shape().color;
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), shape, color,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			color = form == nullTex ? nullColor : dat.alchData().form().getColor();
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), form, color,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
		}
		//RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), new WayAspect(7).symbolTexture(),
		//	x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
		//pos = MathUtil.rotate2DAroundOrigin(pos, 120);
		//RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), ShapeAspect.QUINTESSENCE.symbolTexture(),
		//	x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
		//pos = MathUtil.rotate2DAroundOrigin(pos, 120);
		//RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), AlchInit.MATERIA.get().symbolTexture(),
		//	x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
		//RenderUtil.drawHeatBarEnd(graphics.pose(), graphics.bufferSource(), offset + x + tooltip.barWidth - 8 - 26, y - 1, 0, 8, 10, 0.5f, 0, 1.0f, 0.625f);
	}
}
