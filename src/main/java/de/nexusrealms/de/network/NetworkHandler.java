package de.nexusrealms.de.network;

import de.nexusrealms.de.DangerZone;
import de.nexusrealms.de.zone.Zone;
import de.nexusrealms.de.zone.ZoneType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Handles network communication between server and client
 * for zone-related data.
 */
public class NetworkHandler {
    // Channel identifiers for the different packet types
    public static final Identifier ZONE_ENTER_PACKET_ID = Identifier.of(DangerZone.MOD_ID, "zone_enter");
    public static final Identifier ZONE_EXIT_PACKET_ID = Identifier.of(DangerZone.MOD_ID, "zone_exit");
    public static final Identifier ZONE_EFFECT_UPDATE_PACKET_ID = Identifier.of(DangerZone.MOD_ID, "zone_effect_update");
    
    /**
     * Registers all network handlers
     */
    public static void registerHandlers() {
        // Server side doesn't need receiver handlers
    }
    
    /**
     * Sends a packet to notify a player they entered a zone
     */
    public static void sendZoneEnterPacket(ServerPlayerEntity player, Zone zone) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(zone.getName());
        buf.writeString(zone.getType().getId());
        buf.writeUuid(zone.getId());
        ServerPlayNetworking.send(player, ZONE_ENTER_PACKET_ID, buf);
    }
    
    /**
     * Sends a packet to notify a player they exited a zone
     */
    public static void sendZoneExitPacket(ServerPlayerEntity player, Zone zone) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(zone.getId());
        ServerPlayNetworking.send(player, ZONE_EXIT_PACKET_ID, buf);
    }
    
    /**
     * Sends updated effect parameters for a zone
     */
    public static void sendZoneEffectUpdate(ServerPlayerEntity player, Zone zone, String effectId, float intensity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(zone.getId());
        buf.writeString(effectId);
        buf.writeFloat(intensity);
        ServerPlayNetworking.send(player, ZONE_EFFECT_UPDATE_PACKET_ID, buf);
    }
}