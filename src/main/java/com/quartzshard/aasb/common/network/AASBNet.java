package com.quartzshard.aasb.common.network;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.network.server.KeyPressPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class AASBNet {

	private static final String VERSION = "1";
    private static SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(AsAboveSoBelow.MODID, "packets"))
            .networkProtocolVersion(() -> VERSION)
            .clientAcceptedVersions(VERSION::equals)
            .serverAcceptedVersions(VERSION::equals)
            .simpleChannel();

    private static int id = 0;

    public static void register() {
    	// client -> server
        CHANNEL.messageBuilder(KeyPressPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
        		.encoder(KeyPressPacket::enc)
                .decoder(KeyPressPacket::dec)
                .consumer(KeyPressPacket::handle)
                .add();
        
        
        
        // server -> client
    }
    
    public static <PKT> void toServer(PKT packet) {
    	CHANNEL.sendToServer(packet);
    }

    public static <PKT> void toClient(PKT message, ServerPlayer player) {
    	CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}