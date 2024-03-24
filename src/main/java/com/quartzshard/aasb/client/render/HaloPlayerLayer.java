package com.quartzshard.aasb.client.render;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.client.render.AASBRenderType;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.AmuletItem;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.CircletItem;
import com.quartzshard.aasb.common.item.equipment.armor.jewellery.JewelleryArmorItem;
import com.quartzshard.aasb.util.Colors;
import com.quartzshard.aasb.util.WayUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

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

	public AASBPlayerLayer(@NotNull PlayerRenderer renderer) {
		super(renderer);
		this.renderer = renderer;

		// Special halos for special people below
		// <name> (<reason for specialness>)

		// solunareclipse1 (Lead developer)
		SPECIAL_HALOS.put(SOL_UUID,
				new MagnumOpusHaloData(AASB.rl(HALOS + "gear.png"), Colors.WHITE, Colors.MID_GRAY, HaloFadeType.VAL, 300, 75));
		
		// GantTheShinx (solunareclipse1's alt)
		SPECIAL_HALOS.put(GANT_UUID,
				new MagnumOpusHaloData(AASB.rl(HALOS + "gear_gant.png"), Colors.WHITE, Colors.MID_GRAY, HaloFadeType.VAL, 300, 75));

		// quartzshard / craft_of_mining (Mapper nerd)
		SPECIAL_HALOS.put(SHARD_UUID,
				new MagnumOpusHaloData(AASB.rl(HALOS + "pentagonal.png"), Colors.MATERIA_MAJOR, Colors.PHILOSOPHERS, HaloFadeType.HUE, 2000, 50));

		// ShowdownFreddy (Some textures, friend)
		SPECIAL_HALOS.put(FRED_UUID,
				new MagnumOpusHaloData(AASB.rl(HALOS+"fred.png"), Colors.WHITE, Colors.WHITE, HaloFadeType.NONE, 2000, 50));

		// sinkillerj (Inspiration, lots of borrowed code)
		SPECIAL_HALOS.put(SIN_UUID,
				new MagnumOpusHaloData(AASB.rl(HALOS + "yue.png"), Colors.MATERIA_INFIRMA, Colors.MATERIA_INFIRMA, HaloFadeType.NONE, 2000, 50));

		// Clarissa (Because I feel like being nice)
		SPECIAL_HALOS.put(CLAR_UUID,
				new MagnumOpusHaloData(AASB.rl(HALOS + "heart.png"), Colors.PHILOSOPHERS, Colors.PHILOSOPHERS, HaloFadeType.NONE, 2000, 50));
	}

	private final PlayerRenderer renderer;
	private static final Map<UUID, MagnumOpusHaloData> SPECIAL_HALOS = new HashMap<>();
	private static final String HALOS = "textures/models/halo/";
	private static final ResourceLocation DEFAULT_HALO = AASB.rl(HALOS + "normal.png");
	private static final MagnumOpusHaloData DEFAULT_HALO_DATA = new MagnumOpusHaloData(DEFAULT_HALO, Colors.MATERIA_INFIRMA, Colors.MATERIA_PRIMA, HaloFadeType.HUE, 2000, 50);

	/**
	 * This is used for silly people who ask to be given an offensive custom halo
	 */
	private static final MagnumOpusHaloData DOOFUS_HALO_DATA = new MagnumOpusHaloData(AASB.rl(HALOS+"doofus.png"), Colors.BROWN, Colors.BROWN, HaloFadeType.NONE, 1, 1);

	// special people uuids
	private static final UUID
		SOL_UUID = UUID.fromString("89b9a7d2-daa3-48cc-903c-96d125106a6b"),
		GANT_UUID = UUID.fromString("c6cea672-5842-4d85-b6a1-6060b0495c5c"),
		SHARD_UUID = UUID.fromString("b9d0673f-51af-446e-a4d0-512eab478561"),
		FRED_UUID = UUID.fromString("7c48a895-c35f-42c4-ab94-aeba35de0217"),
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
		@NotNull String debugStr = "7c48a895-c35f-42c4-ab94-aeba35de0217";//DebugCfg.HALO_UUID.get();
		@Nullable UUID debugUUID = null;
		if (!FMLEnvironment.production) {
			try {
				debugUUID = UUID.fromString(debugStr);
			} catch (IllegalArgumentException e) {
				// if its not a valid UUID, thats fine
				debugUUID = null;
			}
		}
		float chargeLevel = 0;
		boolean isDev = debugUUID != null && player.getScoreboardName().equals("Dev") && SPECIAL_HALOS.containsKey(debugUUID);
		boolean hasWay = isDev;
		for (ItemStack stack : player.getArmorSlots()) {
			if (!(stack.getItem() instanceof JewelleryArmorItem))
				return;
			if (stack.getItem() instanceof AmuletItem item) {
				long stored = item.getStoredWay(stack);
				hasWay = stored > 0;
				chargeLevel = (float)stored/(float)item.getMaxWay(stack);
			}
		}
		if (!hasWay)
			return;
		int timer = Math.round(ageInTicks);
		poseStack.pushPose();
		renderer.getModel().jacket.translateAndRotate(poseStack);
		poseStack.mulPose(Axis.XP.rotationDegrees(270)); // rotate upright
		poseStack.scale(1.5f, 1.5f, 1.5f); // bigger!
		UUID uuid = isDev ? debugUUID : player.getUUID(); // getting UUID
		float spinAge = ageInTicks;
		if (uuid != null && uuid.getLeastSignificantBits()%2==0) {
			spinAge *= -1f;
		}
		poseStack.mulPose(Axis.YP.rotationDegrees(spinAge * 0.6f % 360)); // spinny
		poseStack.translate(-0.5, -0.25, -0.5); // positioning behind the head

		// set up data about halo
		MagnumOpusHaloData data = SPECIAL_HALOS.get(uuid);
		if (data == null) {
			data = DEFAULT_HALO_DATA;
		}
		ResourceLocation texture = data.texture();
		Colors c1 = data.color1();
		Colors c2 = data.color2();
		boolean fixed = c1 == c2;

		VertexConsumer buffer = renderBuffer.getBuffer(AASBRenderType.MAGNUM_OPUS_HALO.apply(texture));
		@NotNull Matrix4f pose = poseStack.last().pose();

		// blinks faster when low EMC
		int alpha = Math.round(Colors.loopFade(timer, Math.round(Math.max(280 * chargeLevel, 10)), 0, 96, 192));
		if (fixed || data.fadeType == HaloFadeType.NONE) {
			// fixed color mode, saves processing time by not doing fade stuff
			int @NotNull [] color = { c1.R, c1.G, c1.B };
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
				f1 = Colors.rgbFromInt(Mth.hsvToRgb(Colors.loopFade(timer, dur, 0, i, j), k, l));
				f2 = Colors.rgbFromInt(Mth.hsvToRgb(Colors.loopFade(timer, dur, spd, i, j), k, l));
				f3 = Colors.rgbFromInt(Mth.hsvToRgb(Colors.loopFade(timer, dur, spd * 2, i, j), k, l));
				f4 = Colors.rgbFromInt(Mth.hsvToRgb(Colors.loopFade(timer, dur, spd * 3, i, j), k, l));
				break;
			case SAT:
				i = c1.S / 100f; j = c2.S / 100f; // fades
				k = c1.H / 360f; l = c1.V / 100f; // statics
				f1 = Colors.rgbFromInt(Mth.hsvToRgb(k, Colors.loopFade(timer, dur, 0, i, j), l));
				f2 = Colors.rgbFromInt(Mth.hsvToRgb(k, Colors.loopFade(timer, dur, spd, i, j), l));
				f3 = Colors.rgbFromInt(Mth.hsvToRgb(k, Colors.loopFade(timer, dur, spd * 2, i, j), l));
				f4 = Colors.rgbFromInt(Mth.hsvToRgb(k, Colors.loopFade(timer, dur, spd * 3, i, j), l));
				break;
			case VAL:
				i = c1.V / 100f; j = c2.V / 100f; // fades
				k = c1.H / 360f; l = c1.S / 100f; // statics
				f1 = Colors.rgbFromInt(Mth.hsvToRgb(k, l, Colors.loopFade(timer, dur, 0, i, j)));
				f2 = Colors.rgbFromInt(Mth.hsvToRgb(k, l, Colors.loopFade(timer, dur, spd, i, j)));
				f3 = Colors.rgbFromInt(Mth.hsvToRgb(k, l, Colors.loopFade(timer, dur, spd * 2, i, j)));
				f4 = Colors.rgbFromInt(Mth.hsvToRgb(k, l, Colors.loopFade(timer, dur, spd * 3, i, j)));
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
	private record MagnumOpusHaloData(ResourceLocation texture, Colors color1, Colors color2, HaloFadeType fadeType, int fadeDuration, int fadeSpeed) {}
	
	private enum HaloFadeType {
		HUE, SAT, VAL, NONE
	}
}
