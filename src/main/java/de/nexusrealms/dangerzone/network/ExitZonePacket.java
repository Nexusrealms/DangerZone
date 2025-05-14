package de.nexusrealms.dangerzone.network;

import de.nexusrealms.dangerzone.DangerZone;
import de.nexusrealms.dangerzone.zone.ZoneComponent;
import de.nexusrealms.dangerzone.zone.effect.ZoneEffect;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record ExitZonePacket(UUID zoneId) implements ReceiverPacket<ClientPlayNetworking.Context> {
    public static final Id<ExitZonePacket> ID = new Id<>(DangerZone.id("exit_zone"));
    public static final PacketCodec<ByteBuf, ExitZonePacket> PACKET_CODEC = Uuids.PACKET_CODEC.xmap(ExitZonePacket::new, ExitZonePacket::zoneId);
    @Override
    public void onReceive(ClientPlayNetworking.Context context) {
        context.player().getWorld().getComponent(ZoneComponent.KEY).getZone(zoneId).ifPresent(zone -> zone.performEffectActions(context.player(), ZoneEffect::unapplyClient));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
