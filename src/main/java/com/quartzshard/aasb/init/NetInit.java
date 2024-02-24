package com.quartzshard.aasb.init;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.net.client.*;
import com.quartzshard.aasb.net.server.*;
import com.quartzshard.aasb.util.ClientUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;

public class NetInit {

	private static final String VERSION = "1";
	private static SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(AASB.MODID, "packets"))
			.networkProtocolVersion(() -> VERSION)
			.clientAcceptedVersions(VERSION::equals)
			.serverAcceptedVersions(VERSION::equals)
			.simpleChannel();

	private static int id = 0;

	public static void register() {
		// client -> server
		CHANNEL.messageBuilder(KeybindPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(KeybindPacket::enc)
				.decoder(KeybindPacket::dec)
				.consumerMainThread(KeybindPacket::handle)
				.add();
		CHANNEL.messageBuilder(SlowFallPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(SlowFallPacket::enc)
				.decoder(SlowFallPacket::dec)
				.consumerMainThread(SlowFallPacket::handle)
				.add();
		
		
		// server -> client
		CHANNEL.messageBuilder(PresetParticlePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PresetParticlePacket::enc)
				.decoder(PresetParticlePacket::dec)
				.consumerMainThread(PresetParticlePacket::handle)
				.add();
		CHANNEL.messageBuilder(CutParticlePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(CutParticlePacket::enc)
				.decoder(CutParticlePacket::dec)
				.consumerMainThread(CutParticlePacket::handle)
				.add();
		CHANNEL.messageBuilder(DrawParticleLinePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(DrawParticleLinePacket::enc)
				.decoder(DrawParticleLinePacket::dec)
				.consumerMainThread(DrawParticleLinePacket::handle)
				.add();
		CHANNEL.messageBuilder(DrawParticleAABBPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(DrawParticleAABBPacket::enc)
				.decoder(DrawParticleAABBPacket::dec)
				.consumerMainThread(DrawParticleAABBPacket::handle)
				.add();
		CHANNEL.messageBuilder(MapperPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(MapperPacket::enc)
				.decoder(MapperPacket::dec)
				.consumerMainThread(MapperPacket::handle)
				.add();
		CHANNEL.messageBuilder(ModifyPlayerVelocityPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ModifyPlayerVelocityPacket::enc)
				.decoder(ModifyPlayerVelocityPacket::dec)
				.consumerMainThread(ModifyPlayerVelocityPacket::handle)
				.add();
		CHANNEL.messageBuilder(FreecamPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(FreecamPacket::enc)
				.decoder(FreecamPacket::dec)
				.consumerMainThread(FreecamPacket::handle)
				.add();
	}
	
	public static <PKT> void toServer(PKT packet) {
		if (ClientUtil.mc().getConnection() == null)
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
	public static <PKT> void toNearbyClients(PKT message, ServerLevel level, @NotNull Vec3 sendPos, double sendRange) {
		for (@NotNull ServerPlayer player : level.players()) {
			if (player.position().closerThan(sendPos, sendRange)) {
				toClient(message, player);
			}
		}
	}

	/**
	 * sends a packet to clients within a certain distance of a point <br>
	 * this version lets you specify a "sender", who will always recieve the packet
	 * @param <PKT>
	 * @param level
	 * @param sendPos the point with which to do the distance check
	 * @param sendRange max distace, in blocks
	 * @param message
	 */
	public static <PKT> void toNearbyClients(PKT message, ServerPlayer sender, ServerLevel level, Vec3 sendPos, double sendRange) {
		for (ServerPlayer player : level.players()) {
			if (player.is(sender) || player.position().closerThan(sendPos, sendRange)) {
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
	public static <PKT> void toAllClients(@NotNull ServerLevel level, PKT message) {
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
	public static <PKT> void toClients(PKT message, ServerPlayer @NotNull ... players) {
		for (ServerPlayer player : players) {
			toClient(message, player);
		}
	}
}