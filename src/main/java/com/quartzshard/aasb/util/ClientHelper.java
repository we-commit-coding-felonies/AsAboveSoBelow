package com.quartzshard.aasb.util;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;

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
		return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().options.keyJump.isDown(), () -> () -> false);
	}
}
