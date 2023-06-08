package com.quartzshard.aasb.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
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
		MultiBufferSource.BufferSource multisource = Minecraft.getInstance().renderBuffers().bufferSource();
		Vec3 viewPos = camera.getPosition();
		float lx1 = (float) (this.x - viewPos.x());
		float ly1 = (float) (this.y - viewPos.y());
		float lz1 = (float) (this.z - viewPos.z());
		float lx2 = (float) (pos2.x - viewPos.x());
		float ly2 = (float) (pos2.y - viewPos.y());
		float lz2 = (float) (pos2.z - viewPos.z());
		Vec3 line = new Vec3(x,y,z).subtract(pos2).normalize();
		Vector3f[] points = new Vector3f[]{new Vector3f(line), new Vector3f(line.reverse())};
		points[0].add(lx1, ly1, lz1);
		points[1].add(lx2, ly2, lz2);
		VertexConsumer buffer = multisource.getBuffer(RenderType.lines());
		float alpha = 1f - (float)age/(float)lifetime;
		float value = Math.max(0.75f, 1f - ((float)age/(float)lifetime)*2f);
		buffer.vertex(points[0].x(), points[0].y(), points[0].z()).color(value/2f, value/2f, value/2f, alpha/2f).normal(0, 0, 0).endVertex();
		buffer.vertex(points[1].x(), points[1].y(), points[1].z()).color(value, value, value, alpha).normal(0, 0, 0).endVertex();
		//for (int i = 0; i < points.length; i++) {
		//	buffer.vertex(points[i].x(), points[i].y(), points[i].z()).color(value, value, value, alpha).normal(0, 0, 0).endVertex();
		//}
		multisource.endBatch();
		return;
	}

	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		if (age++ >= this.lifetime) {
			this.remove();
		}
	}

	public ParticleRenderType getRenderType() {
		return ParticleRenderType.CUSTOM;
	}
	
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		public Provider(SpriteSet sprites) {}
		
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x1, double y1, double z1, double x2, double y2, double z2) {
			CutParticle particle = new CutParticle(level, x1,y1,z1, x2,y2,z2);
			return particle;
		}
	}

}
