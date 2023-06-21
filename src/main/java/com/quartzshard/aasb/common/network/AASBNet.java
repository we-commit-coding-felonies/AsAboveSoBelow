package com.quartzshard.aasb.common.network;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.network.client.*;
import com.quartzshard.aasb.common.network.server.*;
import com.quartzshard.aasb.util.ClientHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

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
		CHANNEL.messageBuilder(SlowFallPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(SlowFallPacket::enc)
				.decoder(SlowFallPacket::dec)
				.consumer(SlowFallPacket::handle)
				.add();
			
		// server -> client
		CHANNEL.messageBuilder(CutParticlePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(CutParticlePacket::enc)
				.decoder(CutParticlePacket::dec)
				.consumer(CutParticlePacket::handle)
				.add();
	}
	
	public static <PKT> void toServer(PKT packet) {
		if (ClientHelper.mc().getConnection() == null)
			return;
		CHANNEL.sendToServer(packet);
	}

	public static <PKT> void toClient(PKT message, ServerPlayer player) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	/**
	 * sends a packet to clients within a certain distance of a point
	 * @param <PKT>
	 * @param level
	 * @param sendPos the point with which to do the distance check
	 * @param sendRange max distace, in blocks
	 * @param message
	 */
	public static <PKT> void toNearbyClients(PKT message, ServerLevel level, Vec3 sendPos, double sendRange) {
		for (ServerPlayer player : level.players()) {
			if (player.position().closerThan(sendPos, sendRange)) {
				toClient(message, player);
			}
		}
	}

	/**
	 * Sends a packet to *all* clients in the level
	 * @param <PKT>
	 * @param level
	 * @param message
	 */
	public static <PKT> void toAllClients(ServerLevel level, PKT message) {
		for (ServerPlayer player : level.players()) {
			toClient(message, player);
		}
	}
	
	/**
	 * Sends a packet to multiple clients
	 * @param <PKT>
	 * @param message
	 * @param players
	 */
	public static <PKT> void toClients(PKT message, ServerPlayer... players) {
		for (ServerPlayer player : players) {
			toClient(message, player);
		}
	}
}