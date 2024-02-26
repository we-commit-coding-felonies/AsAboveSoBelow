package com.quartzshard.aasb.client.gui.tip;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.api.alchemy.aspect.WayAspect;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.util.ClientUtil;
import com.quartzshard.aasb.util.MathUtil;
import com.quartzshard.aasb.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;

public class AspectsClientTooltip implements ClientTooltipComponent {

	AlchData dat;

	public AspectsClientTooltip(LangData.AspectTooltip dat) {
		this.dat = dat.alchData();
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
		// TODO find a way to make this always render with minecrafts default font
		// 1 = 61.5, 76
		// 2 = 58.5, 76
		// 3 = 55.5, 76
		// 4 = 52.5, 76
		// 5 = 49.5, 76
		//
		// 10 = ???, 72 & 80
		// 13 = ???, 68 & 76 & 84
		WayAspect way = dat.way();
		if (way != null) {
			long val = way.value();
			String valStr = val+"";
			if (val < 100_000) {
				// <= 5 digits, single line
				font.drawInBatch(val+"", (float)mouseX+xOffForNumDigits(valStr.length()), (float)mouseY+76, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			} else if (val < 10_000_000_000l) {
				// <= 10 digits, 2 lines
				int bDig = 0, i = 0;
				while (i < valStr.length()) {
					if (i % 2 != 0)
						bDig++;
					i++;
				}
				float offT = xOffForNumDigits(bDig), offB = xOffForNumDigits(valStr.length()-bDig);
				font.drawInBatch(valStr.substring(0,bDig), (float)mouseX+offT, (float)mouseY+72, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
				font.drawInBatch(valStr.substring(bDig), (float)mouseX+offB, (float)mouseY+80, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			} else if (val < 10_000_000_000_000l) {
				// <= 13 digits, 3 lines
				int aDig = 0, bDig = 0;
				float offA = 0, offB = 0, offC = 0;
				switch (valStr.length()) {
					case 11:
						aDig = 3;
						bDig = 7;
						offA = xOffForNumDigits(3);
						offB = offC = xOffForNumDigits(4);
						break;
					case 12:
						aDig = 4;
						bDig = 8;
						offA = offB = offC = xOffForNumDigits(4);
						break;
					default:
						aDig = 4;
						bDig = 9;
						offB = xOffForNumDigits(5);
						offA = offC = xOffForNumDigits(4);
						break;
				}
				font.drawInBatch(valStr.substring(0,aDig), (float)mouseX+offA, (float)mouseY+68, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
				font.drawInBatch(valStr.substring(aDig,bDig), (float)mouseX+offB, (float)mouseY+76, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
				font.drawInBatch(valStr.substring(bDig), (float)mouseX+offC, (float)mouseY+84, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			} else {
				// too many digits! exponential notation. always 1 digit, e, 2 more digits (4 length)
				font.drawInBatch(valStr.charAt(0)+"e"+(valStr.length()-1), (float)mouseX+52.5f, (float)mouseY+76, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			}
		}

	}

	private static float xOffForNumDigits(int digits) {
		if (digits <= 1) return 61.5f;
		return 61.5f - 3f*(digits-1f);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
		x += 32;
		float size = 64;
		RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), dat.complexity().symbolTexture(), 0xffffff,
			x, y, 0, size, size, 0, 0, 1, 1);
		Vec2 pos = new Vec2(0,48);
		if (dat.complexity() == ComplexityAspect.UNKNOWN) {
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
				way = dat.way() == null ? nullTex : dat.way().value() >= 1000 ? AASB.rl("textures/symbol/aspect/kiloway.png") : dat.way().symbolTexture(),
				shape = dat.shape() == null ? nullTex : dat.shape().symbolTexture(),
				form = dat.form() == null ? nullTex : dat.form().symbolTexture();
			int nullColor = 0x8e9e99, color = way == nullTex ? nullColor : 0xffff99;
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), way, color,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			color = shape == nullTex ? nullColor : dat.shape().color;
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), shape, color,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			pos = MathUtil.rotate2DAroundOrigin(pos, 120);
			color = form == nullTex ? nullColor : dat.form().getColor();
			RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), form, color,
				x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
		}
	}
}
