package com.quartzshard.aasb.client.gui.tip;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.api.alchemy.aspect.WayAspect;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.util.MathUtil;
import com.quartzshard.aasb.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;

public class WayClientTooltip implements ClientTooltipComponent {
	private static final float
		WX = 21.5f,
		WY = 20;

	public static final ResourceLocation
		TEX_WAY = AASB.rl("textures/symbol/aspect/way/way.png"),
		TEX_KILOWAY = AASB.rl("textures/symbol/aspect/way/kiloway.png");

	long val;

	public WayClientTooltip(LangData.WayTooltip dat) {
		this.val = dat.way();
	}

	@Override
	public int getHeight() {
		return 50;
	}

	@Override
	public int getWidth(Font font) {
		return 50;//font.width("The Philosopher's Stone");// + dat.barWidth - 24;
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
		String valStr = val+"";
		float yb = mouseY + WY;
		if (val <= 999_999) {
			// <= 6 digits, single line
			font.drawInBatch(val+"", (float)mouseX+xOffForNumDigits(valStr.length()), yb, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
		} else if (val <= 999_999_999_999l) {
			// <= 12 digits, 2 lines
			int bDig = 0, i = 0;
			while (i < valStr.length()) {
				if (i % 2 != 0)
					bDig++;
				i++;
			}
			float offT = xOffForNumDigits(bDig), offB = xOffForNumDigits(valStr.length()-bDig);
			font.drawInBatch(valStr.substring(0,bDig), (float)mouseX+offT, yb-4f, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			font.drawInBatch(valStr.substring(bDig), (float)mouseX+offB, yb+4f, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
		} else if (val <= 9_999_999_999_999_999l) {
			// <= 16 digits, 3 lines
			int aDig = 0, bDig = 0;
			float offA = 0, offB = 0, offC = 0;
			switch (valStr.length()) {
				//case 11:
				//	aDig = 3;
				//	bDig = 7;
				//	offA = xOffForNumDigits(3);
				//	offB = offC = xOffForNumDigits(4);
				//	break;
				//case 12:
				//	aDig = 4;
				//	bDig = 8;
				//	offA = offB = offC = xOffForNumDigits(4);
				//	break;
				case 13:
					aDig = 4;
					bDig = aDig+5;
					offA = xOffForNumDigits(4);
					offB = xOffForNumDigits(5);
					offC = offA;
					break;
				case 14: aDig = 4;
					bDig = aDig+5;
					offA = xOffForNumDigits(4);
					offB = offC = xOffForNumDigits(5);
					break;
				case 15: aDig = 5;
					bDig = aDig+5;
					offA = offB = offC = xOffForNumDigits(5);
					break;
				case 16:
				default: aDig = 5;
					bDig = aDig+6;
					offA = xOffForNumDigits(5);
					offB = xOffForNumDigits(6);
					offC = offA;
					break;
			}
			font.drawInBatch(valStr.substring(0,aDig), (float)mouseX+offA, yb-8f, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			font.drawInBatch(valStr.substring(aDig,bDig), (float)mouseX+offB, yb, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
			font.drawInBatch(valStr.substring(bDig), (float)mouseX+offC, yb+8f, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
		} else if (val <= 999_999_999_999_999_999l) {
			// 17 or 18 digits, looks kinda dumb tbh
			switch (valStr.length()) {
				case 17:
					font.drawInBatch(valStr.substring(0,1), (float)mouseX+xOffForNumDigits(1), yb-16, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					font.drawInBatch(valStr.substring(1,6), (float)mouseX+xOffForNumDigits(5), yb-8, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					font.drawInBatch(valStr.substring(6,12), (float)mouseX+xOffForNumDigits(6), yb, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					font.drawInBatch(valStr.substring(12), (float)mouseX+xOffForNumDigits(5), yb+8, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					break;
				case 18: font.drawInBatch(valStr.substring(0,1), (float)mouseX+xOffForNumDigits(1), yb-16, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					font.drawInBatch(valStr.substring(1,6), (float)mouseX+xOffForNumDigits(5), yb-8, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					font.drawInBatch(valStr.substring(6,12), (float)mouseX+xOffForNumDigits(6), yb, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					font.drawInBatch(valStr.substring(12,17), (float)mouseX+xOffForNumDigits(5), yb+8, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					font.drawInBatch(valStr.substring(17), (float)mouseX+xOffForNumDigits(1), yb+16, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
					break;
			}
		} else {
			// too many digits! exponential notation. always 1 digit, e, 2 more digits (4 length)
			font.drawInBatch(valStr.charAt(0)+"e"+(valStr.length()-1), (float)mouseX+xOffForNumDigits(4), yb, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
		}

	}

	private static float xOffForNumDigits(int digits) {
		if (digits <= 1) return WX;
		return WX - 3f*(digits-1f);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
		//x += 40;
		//y += 8;
		float size = 48;
		//RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), dat.complexity().symbolTexture(), 0xffffff,
		//	x, y, 0, size, size, 0, 0, 1, 1);
		Vec2 pos = new Vec2(0,0);
		RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), AASB.rl("textures/placeholder.png"), 0x0000ff,
		//	x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			x, y, 0, size, size, 0, 0, 1, 1);
		RenderUtil.drawAspectSymbol(graphics.pose(), graphics.bufferSource(), val < 1000 ? TEX_WAY : TEX_KILOWAY, 0xffff99,
		//	x+pos.x, y+pos.y, 0, size, size, 0, 0, 1, 1);
			x, y, 0, size, size, 0, 0, 1, 1);
		//pos = MathUtil.rotate2DAroundOrigin(pos, 120);
	}
}
