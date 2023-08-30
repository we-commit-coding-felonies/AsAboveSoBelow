package com.quartzshard.aasb.common.network.server;

import java.util.function.Supplier;

import com.quartzshard.aasb.api.item.bind.ICanHandleKeybind;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkEvent;

/**
 * keybinds client -> server
 * @author solunareclipse1
 */
public record KeyPressPacket(ServerBind bind, BindState state) {
	
	/**
	 * exists to prevent client-code from being called on the server
	 * @author solunareclipse1
	 */
	public enum ServerBind {
		HEADMODE(EquipmentSlot.HEAD),
		CHESTMODE(EquipmentSlot.CHEST),
		LEGSMODE(EquipmentSlot.LEGS),
		FEETMODE(EquipmentSlot.FEET),
		
		ITEMMODE(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND),
		ITEMFUNC_1(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND),
		ITEMFUNC_2(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND),
		EMPOWER(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		
		private final EquipmentSlot[] slots;
		private ServerBind(EquipmentSlot... slots) {
			this.slots = slots;
		}
		
		public EquipmentSlot[] getSlotsToCheck() {
			return slots;
		}
	}
	public enum BindState {
		PRESSED,
		HELD,
		RELEASED
	}
	
	public void enc(FriendlyByteBuf buffer) {
		buffer
			.writeEnum(bind)
			.writeEnum(state);
	}

	public static KeyPressPacket dec(FriendlyByteBuf buffer) {
		return new KeyPressPacket(buffer.readEnum(ServerBind.class), buffer.readEnum(BindState.class));
	}
	
	public boolean handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        ctx.enqueueWork(() -> {
    		ServerPlayer player = ctx.getSender();
    		if (player != null) {
    			ServerLevel level = player.getLevel();
    			boolean didDo = false;
    			for (EquipmentSlot slot : bind.getSlotsToCheck()) {
    				ItemStack stack = player.getItemBySlot(slot);
    				if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ICanHandleKeybind item) {
    					didDo = item.handle(new PressContext(bind, state, stack, player, level));
    					if (didDo) break;
    				}
    			}
    		}
        });
        return true;
    }
	
	public record PressContext(ServerBind bind, BindState state, ItemStack stack, ServerPlayer player, ServerLevel level) {}
}



