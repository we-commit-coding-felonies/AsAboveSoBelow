package com.quartzshard.aasb.client.render;

import java.util.Random;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.entity.projectile.MustangEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/**
 * somewhat based on botanias red string renderer
 */
public class MustangRenderer extends EntityRenderer<MustangEntity> {
	public MustangRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(MustangEntity proj) {
		return new ResourceLocation("textures/block/fire_0.png");
	}

	public static void tick() {
		//Player player = Minecraft.getInstance().player;
		//boolean hasWand = player != null && PlayerHelper.hasHeldItemClass(player, ItemTwigWand.class);
		//if (transparency > 0 && !hasWand) {
		//	transparency--;
		//} else if (transparency < 10 && hasWand) {
		//	transparency++;
		//}
	}

	@Override
	public void render(MustangEntity proj, float yaw, float subTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
		Vec3 vel = proj.getDeltaMovement();
		Vec3 curPos = proj.position(),
			nextPos = proj.position().add(vel),
			diff = nextPos.subtract(curPos);
		
		Vec3 step = diff.normalize().scale(0.5);
		int stepCount = (int)(diff.length()/step.length());
		VertexConsumer buf = buffers.getBuffer(AASBRenderType.MUSTANG_LINES);
		int color = proj.getColor() | ((1 * 255) << 24);
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		float maxOff = 0.3f;
		boolean crazy = false;
		Random rand = crazy ? AASB.RNG : new Random(proj.tickCount ^ proj.position().hashCode());
		Supplier<Float> o = () -> rand.nextFloat(-maxOff, maxOff);

		Vec3 lastVert = step;
		Vec3 nextVert = lastVert.add(step.add(new Vec3(o.get(),o.get(),o.get())));
		Vec3 pt = step;
		poseStack.pushPose();
		for (int i = 0; i < stepCount; i++) {
			buf.vertex(poseStack.last().pose(), (float)pt.x, (float)pt.y, (float)pt.z)
				.color(r,g,b,a)
				.normal(poseStack.last().normal(), 0, 1, 0)
				.endVertex();
			if (i == stepCount-1)
				pt = nextVert;
			else
				pt = pt.add(step.add( new Vec3(o.get(),o.get(),o.get()).multiply(nextVert.subtract(pt).scale(2)) ));
			buf.vertex(poseStack.last().pose(), (float)pt.x,(float)pt.y,(float)pt.z)
				.color(r,g,b,a)
				.normal(poseStack.last().normal(), 0, 1, 0)
				.endVertex();
			lastVert = nextVert;
			nextVert = nextVert.add(step);
		}
		poseStack.popPose();
	}

}
