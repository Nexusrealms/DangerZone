package de.nexusrealms.de.network;

import de.nexusrealms.de.DangerZone;
import de.nexusrealms.de.client.ClientZoneManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

/**
 * Client-side network handler for processing zone-related packets from the server.
 */
public class ClientNetworkHandler {
    /**
     * Registers all client-side network handlers
     */
    public static void registerHandlers() {
        // Register handler for zone enter notifications
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.ZONE_ENTER_PACKET_ID, (client, handler, buf, responseSender) -> {
            String zoneName = buf.readString();
            String zoneType = buf.readString();
            java.util.UUID zoneId = buf.readUuid();
            
            // Execute on the main client thread
            client.execute(() -> {
                ClientZoneManager.getInstance().enterZone(zoneId, zoneName, zoneType);
            });
        });
        
        // Register handler for zone exit notifications
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.ZONE_EXIT_PACKET_ID, (client, handler, buf, responseSender) -> {
            java.util.UUID zoneId = buf.readUuid();
            
            // Execute on the main client thread
            client.execute(() -> {
                ClientZoneManager.getInstance().exitZone(zoneId);
            });
        });
        
        // Register handler for zone effect updates
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.ZONE_EFFECT_UPDATE_PACKET_ID, (client, handler, buf, responseSender) -> {
            java.util.UUID zoneId = buf.readUuid();
            String effectId = buf.readString();
            float intensity = buf.readFloat();
            
            // Execute on the main client thread
            client.execute(() -> {
                ClientZoneManager.getInstance().updateZoneEffect(zoneId, effectId, intensity);
            });
        });
        
        DangerZone.LOGGER.info("Registered client network handlers for danger zones");
    }
}