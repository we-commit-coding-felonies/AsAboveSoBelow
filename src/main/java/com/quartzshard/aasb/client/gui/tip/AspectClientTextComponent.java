package com.quartzshard.aasb.client.gui.tip;

import com.quartzshard.aasb.api.alchemy.aspect.ComplexityAspect;
import com.quartzshard.aasb.api.alchemy.aspect.IAspect;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.ClientInit;
import com.quartzshard.aasb.util.MathUtil;
import com.quartzshard.aasb.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

public class AspectClientTextComponent implements ClientTooltipComponent {

	IAspect<?>[] aspects;
	Component ogTxt;

	public AspectClientTextComponent(LangData.AspectTextComponent dat) {
		this.aspects = dat.aspects();
		this.ogTxt = dat.txt();
	}

	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public int getWidth(Font font) {
		StringBuilder str = new StringBuilder();
		int[] strInt = ogTxt.getString().codePoints().toArray();
		int extra = 0;
		for (int i = 0; i < strInt.length; i++) {
			if (ClientInit.getAspectForUnicode(strInt[i]) != null)
				extra += 13;
			else
				str.appendCodePoint(strInt[i]);
		}
		return font.width(Component.literal(str.toString()).withStyle(ogTxt.getStyle())) + extra;
		//for (int c : str.codePoints().toArray()) {
		//
		//}
		//int ogWidth = font.width(ogTxt);
	}

	@Override
	public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
		ClientInit.ASPECT_FONT.drawInBatch(ogTxt, (float)mouseX, (float)mouseY, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
	}
}
