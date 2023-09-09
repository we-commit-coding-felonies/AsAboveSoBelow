package com.quartzshard.aasb.client.render.entity;

import com.quartzshard.aasb.client.model.HorrorModel;
import com.quartzshard.aasb.common.entity.living.HorrorEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class HorrorRenderer extends AbstractZombieRenderer<HorrorEntity, HorrorModel> {
	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation("textures/entity/zombie/zombie.png"),
			new ResourceLocation("textures/entity/zombie/husk.png"),
			new ResourceLocation("textures/entity/zombie/drowned.png"),
			new ResourceLocation("textures/entity/steve.png"),
			new ResourceLocation("textures/entity/alex.png"),
			new ResourceLocation("textures/entity/end_portal.png"),
			new ResourceLocation("textures/environment/end_sky.png")
	};

	public HorrorRenderer(Context ctx) {
		this(ctx, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
	}

	public HorrorRenderer(Context ctx, ModelLayerLocation mainLayer, ModelLayerLocation inArmor, ModelLayerLocation outArmor) {
		super(ctx, new HorrorModel(ctx.bakeLayer(mainLayer)), new HorrorModel(ctx.bakeLayer(inArmor)), new HorrorModel(ctx.bakeLayer(outArmor)));
	}
	
	@Override
	public ResourceLocation getTextureLocation(Zombie ent) {
		return TEXTURES[ent.getRandom().nextInt(TEXTURES.length)];
	}
	
	@Override
	protected boolean isShaking(HorrorEntity horror) {
		return true;
	}

	@Override
	protected int getSkyLightLevel(HorrorEntity ent, BlockPos pos) {
		return ent.getRandom().nextInt(16);
	}
	
	@Override
	protected int getBlockLightLevel(HorrorEntity ent, BlockPos pos) {
		return ent.getRandom().nextInt(16);
	}

}
