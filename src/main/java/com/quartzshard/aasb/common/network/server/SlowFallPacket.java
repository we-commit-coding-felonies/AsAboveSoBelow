package com.quartzshard.aasb.common.network.server;

import java.util.function.Supplier;

import com.quartzshard.aasb.api.item.bind.ICanHandleKeybind;
import com.quartzshard.aasb.common.item.equipment.armor.jewelry.AnkletItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

/**
 * tells the server to update velocity and reset fall distance <br>
 * does a couple checks to prevent cheating
 * @author solunareclipse1
 */
public record SlowFallPacket(double newY) {
	
	public void enc(FriendlyByteBuf buffer) {
		buffer.writeDouble(newY);
	}

	public static SlowFallPacket dec(FriendlyByteBuf buffer) {
		return new SlowFallPacket(buffer.readDouble());
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
    		ServerPlayer player = ctx.getSender();
    		if (player != null) {
    			ServerLevel level = player.getLevel();
    			Vec3 oldVel = player.getDeltaMovement();
    			ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
    			if (oldVel.y < newY && newY < -0.5
    					&& !stack.isEmpty()
    					&& stack.getItem() instanceof AnkletItem anklet) {
    				player.setDeltaMovement(oldVel.x, newY, oldVel.z);
    				player.fallDistance = 0;
    			}
    		}
        });
        return true;
    }
}



