package com.quartzshard.aasb.util;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;

import net.minecraftforge.fml.DistExecutor;

/**
 * helper code for clientside stuff
 * <p>
 * DO NOT CALL THESE FUNCTIONS SERVERSIDE
 * @author solunareclipse1
 */
public class ClientHelper {
	public static Minecraft mc() {
		return Minecraft.getInstance();
	}
	
	public static boolean shiftHeld() {
		return InputConstants.isKeyDown(mc().getWindow().getWindow(), mc().options.keyShift.getKey().getValue());
	}
	
	/**
	 * from projecte gem boots
	 * @return
	 */
	public static boolean isJumpPressed() {
		return DistExecutor.unsafeRunForDist(() -> () -> mc().options.keyJump.isDown(), () -> () -> false);
	}
	
	/**
	 * DO NOT CALL THIS SERVERSIDE!!!!!
	 * @return clients sound manager
	 */
	public static SoundManager getSoundManager() {
		return mc().getSoundManager();
	}
	
	public static ClientLevel level() {
		return mc().level;
	}
	
	/**
	 * only use this if you have to <br>
	 * functions similarly to level.addParticle, but it returns the partice it created
	 * @return
	 */
	@Nullable
	public static Particle hackyParticle(ParticleOptions particle, boolean alwaysRender, boolean decreased, double x, double y, double z, double vx, double vy, double vz) {
		Minecraft mc = mc();
		ClientLevel level = mc.level;
		ParticleEngine engine = mc.particleEngine;
		//LevelRenderer lr = mc.levelRenderer;
		try {
			Camera camera = mc.gameRenderer.getMainCamera();
			if (mc != null && camera.isInitialized() && engine != null) {
				ParticleStatus status = calculateParticleLevel(level, decreased);
				if (alwaysRender) {
					return engine.createParticle(particle, x, y, z, vx, vy, vz);
				} else if (camera.getPosition().distanceToSqr(x, y, z) > 1024) {
					return null;
				} else {
					return status == ParticleStatus.MINIMAL ? null : engine.createParticle(particle, x, y, z, vx, vy, vz);
				}
			}
			return null;
		} catch (Throwable crash) {
			CrashReport report = CrashReport.forThrowable(crash, "Exception while adding particle");
			CrashReportCategory category = report.addCategory("Particle being added");
			category.setDetail("ID", Registry.PARTICLE_TYPE.getKey(particle.getType()));
			category.setDetail("Parameters", particle.writeToString());
			category.setDetail("Position", () -> {
				return CrashReportCategory.formatLocation(level, x, y, z);
			});
			throw new ReportedException(report);
		}
	}
	

	private static ParticleStatus calculateParticleLevel(ClientLevel level, boolean pDecreased) {
		ParticleStatus particleSetting = mc().options.particles;
		if (pDecreased && particleSetting == ParticleStatus.MINIMAL && level.random.nextInt(10) == 0) {
			particleSetting = ParticleStatus.DECREASED;
		}

		if (particleSetting == ParticleStatus.DECREASED && level.random.nextInt(3) == 0) {
			particleSetting = ParticleStatus.MINIMAL;
		}

		return particleSetting;
	}
}
