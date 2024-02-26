package com.quartzshard.aasb.client.gui.tip;

import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.api.alchemy.aspect.IAspect;
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

public class AspectClientTextComponent implements ClientTooltipComponent {

	IAspect<?> dat;

	public AspectClientTextComponent(LangData.AspectTextComponent dat) {
		this.dat = dat.aspect();
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public int getWidth(Font font) {
		return 16;//font.width("The Philosopher's Stone");// + dat.barWidth - 24;
	}

	@Override
	public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
		//font.drawInBatch(dat.normalText, (float)mouseX, (float)mouseY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics graphics) {}
}
