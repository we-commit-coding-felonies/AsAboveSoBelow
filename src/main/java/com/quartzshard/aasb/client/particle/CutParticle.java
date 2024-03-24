package com.quartzshard.aasb.client.particle;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * custom render particle <br>
 * draws a gray/white line between 2 points <br>
 * darkens and fades out over time <br>
 * uses its "speed" values as coords for 2nd point
 * @author solunareclipse1
 */
public class CutParticle extends Particle {
	private final Vec3 pos2;

	protected CutParticle(ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2) {
		super(level, x1, y1, z1);
		pos2 = new Vec3(x2, y2, z2);
		this.lifetime = 10;
		this.setBoundingBox(new AABB(x1,y1,z1, x2,y2,z2));
	}
	
	@Override
	public void render(VertexConsumer builder, Camera camera, float partialTicks) {
		// even though i wrote this, im not sure i fully understand it
		// will do my best to comment it, if im wrong please PR and correct
		
		// uses particles position as the center of the line
		MultiBufferSource.BufferSource multisource = Minecraft.getInstance().renderBuffers().bufferSource();
		Vec3 viewPos = camera.getPosition();
		// vec between line center and camera
		float lx1 = (float) (this.x - viewPos.x());
		float ly1 = (float) (this.y - viewPos.y());
		float lz1 = (float) (this.z - viewPos.z());
		
		// vec between endpoint and camera
		float lx2 = (float) (pos2.x - viewPos.x());
		float ly2 = (float) (pos2.y - viewPos.y());
		float lz2 = (float) (pos2.z - viewPos.z());
		
		// vec of half the line, other half is just this but .reverse()
		Vec3 halfLine = new Vec3(x,y,z).subtract(pos2).normalize();
		Vector3f[] points = new Vector3f[]{halfLine.toVector3f(), halfLine.reverse().toVector3f()};
		
		// add camera-vectors to convert from particle-local-space to camera-local-space so we can render
		// it also turns them back into points (rather than lines), which are our verticies
		points[0].add(lx1, ly1, lz1);
		points[1].add(lx2, ly2, lz2);
		
		// get the lines render buffer since thats what we will render to
		@NotNull VertexConsumer buffer = multisource.getBuffer(RenderType.lines());
		float alpha = 1f - (float)age/(float)lifetime; // fading out over time
		float value = Math.max(0.75f, 1f - ((float)age/(float)lifetime)*2f); // grayscale value, darkens over time
		
		// vertex defining
		buffer.vertex(points[0].x(), points[0].y(), points[0].z()).color(value/2f, value/2f, value/2f, alpha/2f).normal(0, 0, 0).endVertex();
		buffer.vertex(points[1].x(), points[1].y(), points[1].z()).color(value, value, value, alpha).normal(0, 0, 0).endVertex();
		
		// finish our render batch
		multisource.endBatch();
		return;
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		if (age++ >= this.lifetime) {
			this.remove();
		}
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}
	
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		@Override @NotNull
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2) {
			CutParticle particle = new CutParticle(level, x1,y1,z1, x2,y2,z2);
			return particle;
		}
	}

}
