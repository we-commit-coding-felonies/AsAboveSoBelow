package com.quartzshard.aasb.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.quartzshard.aasb.api.alchemy.aspect.IAspect;
import com.quartzshard.aasb.client.render.AASBRenderType;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2fc;
import org.joml.Vector2i;

/**
 * Abstracted rendering code, drawing things using particles, fun stuff like that <br>
 * Assume things in here only run on the client
 */
public class RenderUtil {
	/**
	 * Draws a line between 2 vectors using particles <br>
	 * 
	 * @param start point A
	 * @param end point B
	 * @param particle the particle to use
	 * @param stepSize lower = more particles
	 * @param level world/level particles are in
	 */
	public static void drawVectorWithParticles(Vec3 start, Vec3 end, ParticleOptions particle, double stepSize, @NotNull ClientLevel level) {
		Vec3 line = end.subtract(start);
		Vec3 step = line.normalize().scale(stepSize);
		int numSteps = (int) (line.length() / step.length());
		if (step.length() <= 0) {
			// avoids floating point nonsense
			numSteps = 1;
		}
		
		Vec3 curPos = start;
		for (int i = 0; i < numSteps; i++) {
			level.addParticle(particle, true, curPos.x, curPos.y, curPos.z, 0, 0, 0);
			curPos = curPos.add(step);
		}
	}

	/**
	 * Draws an AABB with particles
	 * 
	 * @param box the AABB to draw
	 * @param particle the particle to use
	 * @param stepSize lower = more particles
	 * @param level the level to put particles in
	 * @param fill if true, draws a solid box (filled with particles), instead of just an outline
	 */
	public static void drawAABBWithParticles(@NotNull AABB box, ParticleOptions particle, double stepSize, ClientLevel level, boolean fill, boolean infRange) {
		if (fill) {
			for (double i = box.minX; i < box.maxX; i += stepSize) {
	    		for (double j = box.minY; j < box.maxY; j += stepSize) {
	        		for (double k = box.minZ; k < box.maxZ; k += stepSize) {
	            		level.addParticle(particle, infRange, i, j, k, 0, 0, 0);
	        		}
	    		}
			}
		}
		
		for (double i = box.minX; i < box.maxX; i += stepSize) {
			level.addParticle(particle, infRange, i, box.minY, box.minZ, 0, 0, 0);
			level.addParticle(particle, infRange, i, box.minY, box.maxZ, 0, 0, 0);
		}
		for (double i = box.minY; i < box.maxY; i += stepSize) {
			level.addParticle(particle, infRange, box.minX, i, box.minZ, 0, 0, 0);
			level.addParticle(particle, infRange, box.minX, i, box.maxZ, 0, 0, 0);
		}
		for (double i = box.minZ; i < box.maxZ; i += stepSize) {
			level.addParticle(particle, infRange, box.minX, box.minY, i, 0, 0, 0);
			level.addParticle(particle, infRange, box.minX, box.maxY, i, 0, 0, 0);
		}
		for (double i = box.maxX; i > box.minX; i -= stepSize) {
			level.addParticle(particle, infRange, i, box.maxY, box.maxZ, 0, 0, 0);
			level.addParticle(particle, infRange, i, box.maxY, box.minZ, 0, 0, 0);
		}
		for (double i = box.maxY; i > box.minY; i -= stepSize) {
			level.addParticle(particle, infRange, box.maxX, i, box.maxZ, 0, 0, 0);
			level.addParticle(particle, infRange, box.maxX, i, box.minZ, 0, 0, 0);
		}
		for (double i = box.maxZ; i > box.minZ; i -= stepSize) {
			level.addParticle(particle, infRange, box.maxX, box.maxY, i, 0, 0, 0);
			level.addParticle(particle, infRange, box.maxX, box.minY, i, 0, 0, 0);
		}
	}

	public static void drawAspectSymbol(PoseStack pose, MultiBufferSource.BufferSource bufferSource, ResourceLocation symbol, int color,
			float x, float y, float zLevel, float widthIn, float heightIn, float minU, float minV, float maxU, float maxV) {
		Matrix4f matrix4f = pose.last().pose();
		VertexConsumer buffer = bufferSource.getBuffer(AASBRenderType.ASPECT_TOOLTIP.apply(symbol));
		int[] rgb = Colors.rgbFromInt(color);
		int a = 255;
		buffer.vertex(matrix4f, x + 0, y + 0, zLevel).color(rgb[0],rgb[1],rgb[2],a).uv(minU, minV).endVertex();
		buffer.vertex(matrix4f, x + 0, y + heightIn, zLevel).color(rgb[0],rgb[1],rgb[2],a).uv(minU, maxV).endVertex();
		buffer.vertex(matrix4f, x + widthIn, y + heightIn, zLevel).color(rgb[0],rgb[1],rgb[2],a).uv(maxU, maxV).endVertex();
		buffer.vertex(matrix4f, x + widthIn, y + 0, zLevel).color(rgb[0],rgb[1],rgb[2],a).uv(maxU, minV).endVertex();
	}
}
