package com.quartzshard.aasb.client.gui.tip;

import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.api.alchemy.aspect.ShapeAspect;
import com.quartzshard.aasb.api.alchemy.aspect.WayAspect;
import com.quartzshard.aasb.init.AlchInit;
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
		System.out.println("render :3");
		x += 32;
		//
		//float x1 = x + offset + 3;
		//float x2 = x + dat.barWidth - 3;
		//x2 = x1 + (x2 - x1) * (tooltip.heat / tooltip.maxHeat);
		//for (float j = 0; j < 10; j++) {
		//	float coeff = j / 10.0f;
		//	float coeff2 = (j + 1.0f) / 10.0f;
		//	for (float k = 0; k < 4; k += 0.5f) {
		//		float thick = (float) (k / 4.0f) * (tooltip.heat >= tooltip.maxHeat ? (float) Math.sin(EmbersClientEvents.ticks * 0.5f) * 2 + 3 : 1);
		//		RenderUtil.drawColorRectBatched(graphics.pose(), graphics.bufferSource(), x1 * (1.0f - coeff) + x2 * (coeff), y + k, 0, ((x2 - x1) / 10.0f), 8.0f - 2.0f * k,
		//			1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + k))),
		//			1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + k))),
		//			1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + (8.0 - k)))),
		//			1.0f, 0.25f, 0.0625f, Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + (8.0 - k)))));
		//	}
		//}
		//x1 = x + offset + 3;
		//x2 = x + dat.barWidth - 3;
		//float point = x1 + (x2 - x1) * (tooltip.heat / tooltip.maxHeat);
		//
		//for (float k = 0; k < 4; k += 0.5) {
		//	float thick = (float) (k / 4.0);
		//	RenderUtil.drawColorRectBatched(graphics.pose(), graphics.bufferSource(), point, y + k, 0, Math.min((x2 - point), ((x2 - x1) / 10.0f)), 8.0f - 2.0f * k,
		//		1.0f, 0.25f, 0.0625f, 1.0f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (point)), 4 * (int) (y + k))),
		//		0.25f, 0.0625f, 0.015625f, 0.0f,
		//		0.25f, 0.0625f, 0.015625f, 0.0f,
		//		1.0f, 0.25f, 0.0625f, 1.0f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (point)), 4 * (int) (y + (8.0 - k)))));
		//}
		//x1 = x + offset + 3;
		//x2 = x + tooltip.barWidth - 3;
		//x1 = x2 - (x2 - x1) * (1.0f - (tooltip.heat / tooltip.maxHeat));
		//for (float j = 0; j < 10; j++) {
		//	float coeff = j / 10.0f;
		//	float coeff2 = (j + 1.0f) / 10.0f;
		//	for (float k = 0; k < 4; k += 0.5f) {
		//		float thick = (float) (k / 4.0);
		//		RenderUtil.drawColorRectBatched(graphics.pose(), graphics.bufferSource(), x1 * (1.0f - coeff) + x2 * (coeff), y + k, 0, ((x2 - x1) / 10.0f), 8.0f - 2.0f * k,
		//			0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + k))),
		//			0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + k))),
		//			0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff2) + x2 * (coeff2))), 4 * (int) (y + (8.0 - k)))),
		//			0.25f, 0.0625f, 0.015625f, 0.75f * Math.min(1.0f, thick * 0.25f + thick * EmberGenUtil.getEmberDensity(6, (int) (EmbersClientEvents.ticks * 12 + 4 * (x1 * (1.0f - coeff) + x2 * (coeff))), 4 * (int) (y + (8.0 - k)))));
		//	}
		//}
		float size = 64;
		//ResourceLocation tex = AlchInit.TERRAIN.get().symbolTexture();
		RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), dat.alchData().complexity().symbolTexture(),
			x, y, 0, size, size, 0, 0, 1, 1);
		Vec2 pos = new Vec2(0,48);
		if (dat.alchData().complexity() == ComplexityAspect.UNKNOWN) {
			// TODO make actually query player knowledge
			// player has not learned this item so we just do question marks
			for (int i = 0; i < 3; i++) {
				RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), ComplexityAspect.UNKNOWN.symbolTexture(),
					x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
				if (i != 2) pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			}
		} else {
			ResourceLocation nullTex = ComplexityAspect.NULLED.symbolTexture();
			ResourceLocation
				way = dat.alchData().way() == null ? nullTex : dat.alchData().way().symbolTexture(),
				shape = dat.alchData().shape() == null ? nullTex : dat.alchData().shape().symbolTexture(),
				form = dat.alchData().form() == null ? nullTex : dat.alchData().form().symbolTexture();
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), way,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), shape,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), form,
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
