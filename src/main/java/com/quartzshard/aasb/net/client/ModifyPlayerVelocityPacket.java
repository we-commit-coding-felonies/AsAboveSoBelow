package com.quartzshard.aasb.net.client;

import java.util.function.Supplier;

import com.quartzshard.aasb.util.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

/**
 * allows changing player velocity serverside
 * @author solunareclipse1
 */
public record ModifyPlayerVelocityPacket(Vec3 mod, VecOp op) {
	
	public enum VecOp {
		ADD,
		SUBTRACT,
		MULTIPLY,
		CROSS,
		OVERRIDE;
		
		/**
		 * performs the operation on the input
		 * @param in the vector to modify
		 * @param mod the modifier
		 * @return the modified output: in.x(mod)
		 */
		public Vec3 perform(Vec3 in, Vec3 mod) {
			switch (this) {
			case ADD:
				return in.add(mod);
			case SUBTRACT:
				return in.subtract(mod);
			case MULTIPLY:
				return in.multiply(mod);
			case CROSS:
				return in.cross(mod);
			case OVERRIDE:
				return mod;
			default:
				Logger.warn("VecOp.perform()", "UnknownOperation", "Operation " + this.name() + " is unknown!");
				return in;
			}
		}
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(mod.x);
		buffer.writeDouble(mod.y);
		buffer.writeDouble(mod.z);
		buffer.writeEnum(op);
	}

	public static ModifyPlayerVelocityPacket dec(FriendlyByteBuf buffer) {
		return new ModifyPlayerVelocityPacket(
			new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()),
			buffer.readEnum(VecOp.class)
		);
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			@SuppressWarnings("resource")
			LocalPlayer player = Minecraft.getInstance().player;
			
			player.setDeltaMovement( op.perform(player.getDeltaMovement(), mod) );
		});
		return true;
	}
}
