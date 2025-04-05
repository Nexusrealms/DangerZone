package de.nexusrealms.dangerzone.network;

import de.nexusrealms.dangerzone.DangerZone;
import de.nexusrealms.dangerzone.zone.ZoneComponent;
import de.nexusrealms.dangerzone.zone.effect.ZoneEffect;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record EnterZonePacket(UUID zoneId) implements ReceiverPacket<ClientPlayNetworking.Context> {
    public static final Id<EnterZonePacket> ID = new Id<>(DangerZone.id("enter_zone"));
    public static final PacketCodec<ByteBuf, EnterZonePacket> PACKET_CODEC = Uuids.PACKET_CODEC.xmap(EnterZonePacket::new, EnterZonePacket::zoneId);
    @Override
    public void onReceive(ClientPlayNetworking.Context context) {
        context.player().getWorld().getComponent(ZoneComponent.KEY).getZone(zoneId).ifPresent(zone -> zone.onPlayerEnter(context.player()));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
