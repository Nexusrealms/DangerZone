package de.nexusrealms.dangerzone.network;


import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ModPackets {
    public static void init(){
        initC2S();
        initS2C();
    }
    private static void initS2C(){
        registerClientReceiverPacket(EnterZonePacket.ID, EnterZonePacket.PACKET_CODEC);
        registerClientReceiverPacket(ExitZonePacket.ID, ExitZonePacket.PACKET_CODEC);

    }
    private static void initC2S(){
    }


    private static <T extends ReceiverPacket<ClientPlayNetworking.Context>> void registerClientReceiverPacket(CustomPayload.Id<T> packetId, PacketCodec<? super RegistryByteBuf, T> packetCodec){
        PayloadTypeRegistry.playS2C().register(packetId, packetCodec);
        ClientPlayNetworking.registerGlobalReceiver(packetId, ReceiverPacket::onReceive);
    }
    private static <T extends ReceiverPacket<ServerPlayNetworking.Context>> void registerServerReceiverPacket(CustomPayload.Id<T> packetId, PacketCodec<? super RegistryByteBuf, T> packetCodec){
        PayloadTypeRegistry.playC2S().register(packetId, packetCodec);
        ServerPlayNetworking.registerGlobalReceiver(packetId, ReceiverPacket::onReceive);
    }
}