package com.quartzshard.aasb.client.render.layer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.client.render.AASBRenderType;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.CircletItem;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.JewelryArmor;
import com.quartzshard.aasb.config.DebugCfg;
import com.quartzshard.aasb.util.ColorsHelper;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.ColorsHelper.Color;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class AASBPlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

	public AASBPlayerLayer(PlayerRenderer renderer) {
		super(renderer);
		this.renderer = renderer;

		// Special halos for special people below
		// <name> (<reason for specialness>)

		// solunareclipse1 (Lead developer)
		SPECIAL_HALOS.put(SOL_UUID,
				new MagnumOpusHaloData(AsAboveSoBelow.rl(HALOS + "gear.png"), Color.WHITE, Color.MID_GRAY, HaloFadeType.VAL, 300, 75));
		
		// GantTheShinx (solunareclipse1's alt)
		SPECIAL_HALOS.put(GANT_UUID,
				new MagnumOpusHaloData(AsAboveSoBelow.rl(HALOS + "gear_gant.png"), Color.WHITE, Color.MID_GRAY, HaloFadeType.VAL, 300, 75));

		// quartzshard / craft_of_mining (Lead developer)
		SPECIAL_HALOS.put(SHARD_UUID,
				new MagnumOpusHaloData(AsAboveSoBelow.rl(HALOS + "pentagonal.png"), Color.COVALENCE_PURPLE, Color.PHILOSOPHERS, HaloFadeType.HUE, 2000, 50));

		// sinkillerj (Inspiration, lots of borrowed code)
		SPECIAL_HALOS.put(SIN_UUID,
				new MagnumOpusHaloData(AsAboveSoBelow.rl(HALOS + "yue.png"), Color.COVALENCE_GREEN, Color.COVALENCE_GREEN, HaloFadeType.NONE, 1, 1));

		// Clarissa (Because I feel like being nice)
		SPECIAL_HALOS.put(CLAR_UUID,
				new MagnumOpusHaloData(AsAboveSoBelow.rl(HALOS + "heart.png"), Color.PHILOSOPHERS, Color.PHILOSOPHERS, HaloFadeType.NONE, 1, 1));
	}

	private final PlayerRenderer renderer;
	private static final Map<UUID, MagnumOpusHaloData> SPECIAL_HALOS = new HashMap<>();
	private static final String HALOS = "textures/models/halo/";
	private static final ResourceLocation DEFAULT_HALO = AsAboveSoBelow.rl(HALOS + "normal.png");
	private static final MagnumOpusHaloData DEFAULT_HALO_DATA = new MagnumOpusHaloData(DEFAULT_HALO, Color.COVALENCE_GREEN, Color.COVALENCE_MAGENTA, HaloFadeType.HUE, 2000, 50);

	// special people uuids
	private static final UUID
			SOL_UUID = UUID.fromString("89b9a7d2-daa3-48cc-903c-96d125106a6b"),
			GANT_UUID = UUID.fromString("c6cea672-5842-4d85-b6a1-6060b0495c5c"),
			SHARD_UUID = UUID.fromString("b9d0673f-51af-446e-a4d0-512eab478561"),
			SIN_UUID = UUID.fromString("5f86012c-ca4b-451a-989c-8fab167af647"),
			CLAR_UUID = UUID.fromString("e5c59746-9cf7-4940-a849-d09e1f1efc13");

	@Override
	public void render(PoseStack poseStack, MultiBufferSource renderBuffer, int bakedLight,
			AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick,
			float ageInTicks, float netHeadYaw, float headPitch) {
		renderMagnumOpusHalo(poseStack, renderBuffer, player, ageInTicks);
	}

	/** for the alchemical barrier */
	private void renderMagnumOpusHalo(PoseStack poseStack, MultiBufferSource renderBuffer, AbstractClientPlayer player, float ageInTicks) {
		float emcLevel = 1;
		String debugStr = DebugCfg.HALO_UUID.get();
		UUID debugUUID = null;
		if (!FMLEnvironment.production) {
			try {
				debugUUID = UUID.fromString(debugStr);
			} catch (IllegalArgumentException e) {
				// if its not a valid UUID, thats fine
				debugUUID = null;
			}
		}
		// in production, this should always be false, as debugUUID will never not be
		// null
		boolean isDev = debugUUID != null && player.getScoreboardName() == "Dev" && SPECIAL_HALOS.containsKey(debugUUID);
		if (!isDev) {
			// checks if the halo should render
			for (ItemStack stack : player.getArmorSlots())
				if (!(stack.getItem() instanceof JewelryArmor))
					return;
			if (emcLevel <= 0)
				return;
		}
		int timer = Math.round(ageInTicks);
		poseStack.pushPose();
		renderer.getModel().jacket.translateAndRotate(poseStack);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(270)); // rotate upright
		poseStack.scale(1.5f, 1.5f, 1.5f); // bigger!
		UUID uuid = isDev ? debugUUID : player.getUUID(); // getting UUID
		float spinAge = ageInTicks;
		if (uuid != null && uuid.getLeastSignificantBits()%2==0) {
			spinAge *= -1f;
		}
		poseStack.mulPose(Vector3f.YP.rotationDegrees(spinAge * 0.6f % 360)); // spinny
		poseStack.translate(-0.5, -0.25, -0.5); // positioning behind the head

		// set up data about halo
		MagnumOpusHaloData data = SPECIAL_HALOS.get(uuid);
		if (data == null) {
			data = DEFAULT_HALO_DATA;
		}
		ResourceLocation texture = data.texture();
		Color c1 = data.color1();
		Color c2 = data.color2();
		boolean fixed = c1 == c2;

		VertexConsumer buffer = renderBuffer.getBuffer(AASBRenderType.MAGNUM_OPUS_HALO.apply(texture));
		Matrix4f pose = poseStack.last().pose();

		// blinks faster when low EMC
		int alpha = Math.round(ColorsHelper.loopFade(timer, Math.round(Math.max(280 * emcLevel, 10)), 0, 96, 192));
		if (fixed || data.fadeType == HaloFadeType.NONE) {
			// fixed color mode, saves processing time by not doing fade stuff
			int[] color = { c1.R, c1.G, c1.B };
			buffer.vertex(pose, 0, 0, 0).color(color[0], color[1], color[2], alpha).uv(0, 0).endVertex();
			buffer.vertex(pose, 0, 0, 1).color(color[0], color[1], color[2], alpha).uv(0, 1).endVertex();
			buffer.vertex(pose, 1, 0, 1).color(color[0], color[1], color[2], alpha).uv(1, 1).endVertex();
			buffer.vertex(pose, 1, 0, 0).color(color[0], color[1], color[2], alpha).uv(1, 0).endVertex();
		} else {
			int[] f1, f2, f3, f4;
			float i,j,k,l;
			int dur = data.fadeDuration, spd = data.fadeSpeed;
			switch (data.fadeType) {
			default:
			case NONE:
				throw new RuntimeException("Magnum Opus halo for player with UUID " + uuid + " tried to fade in an invalid way!");
			
			case HUE:
				i = c1.H / 360f; j = c2.H / 360f; // fades
				k = c1.S / 100f; l = c1.V / 100f; // statics
				f1 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.loopFade(timer, dur, 0, i, j), k, l));
				f2 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.loopFade(timer, dur, spd, i, j), k, l));
				f3 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.loopFade(timer, dur, spd * 2, i, j), k, l));
				f4 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(ColorsHelper.loopFade(timer, dur, spd * 3, i, j), k, l));
				break;
			case SAT:
				i = c1.S / 100f; j = c2.S / 100f; // fades
				k = c1.H / 360f; l = c1.V / 100f; // statics
				f1 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, ColorsHelper.loopFade(timer, dur, 0, i, j), l));
				f2 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, ColorsHelper.loopFade(timer, dur, spd, i, j), l));
				f3 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, ColorsHelper.loopFade(timer, dur, spd * 2, i, j), l));
				f4 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, ColorsHelper.loopFade(timer, dur, spd * 3, i, j), l));
				break;
			case VAL:
				i = c1.V / 100f; j = c2.V / 100f; // fades
				k = c1.H / 360f; l = c1.S / 100f; // statics
				f1 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, l, ColorsHelper.loopFade(timer, dur, 0, i, j)));
				f2 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, l, ColorsHelper.loopFade(timer, dur, spd, i, j)));
				f3 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, l, ColorsHelper.loopFade(timer, dur, spd * 2, i, j)));
				f4 = ColorsHelper.rgbFromInt(Mth.hsvToRgb(k, l, ColorsHelper.loopFade(timer, dur, spd * 3, i, j)));
				break;
			}
			buffer.vertex(pose, 0, 0, 0).color(f1[0], f1[1], f1[2], alpha).uv(0, 0).endVertex();
			buffer.vertex(pose, 0, 0, 1).color(f2[0], f2[1], f2[2], alpha).uv(0, 1).endVertex();
			buffer.vertex(pose, 1, 0, 1).color(f3[0], f3[1], f3[2], alpha).uv(1, 1).endVertex();
			buffer.vertex(pose, 1, 0, 0).color(f4[0], f4[1], f4[2], alpha).uv(1, 0).endVertex();
		}
		poseStack.popPose();
	}

	/**
	 * stores info for a custom magnum opus halo
	 * 
	 * @author solunareclipse1
	 */
	private record MagnumOpusHaloData(ResourceLocation texture, Color color1, Color color2, HaloFadeType fadeType, int fadeDuration, int fadeSpeed) {}
	
	private enum HaloFadeType {
		HUE, SAT, VAL, NONE
	}

	/** for the circlet */
	private void renderAllSeeingEye(PoseStack poseStack, MultiBufferSource renderBuffer, AbstractClientPlayer player, float ageInTicks) {
		ItemStack circlet = player.getItemBySlot(EquipmentSlot.HEAD);
		if (circlet.getItem() instanceof CircletItem ci) {
			// nyi TODO this
		}
	}

	private void renderAfterimage() {
		// nyi TODO this
	}
}
