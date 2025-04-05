package de.nexusrealms.de;

import de.nexusrealms.de.client.ClientZoneManager;
import de.nexusrealms.de.network.ClientNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DangerZoneClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(DangerZone.MOD_ID + "-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing DangerZone client...");
        
        // Register client network handlers for server communication
        ClientNetworkHandler.registerHandlers();
        
        // Register client tick event to update zone effects
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                ClientZoneManager.getInstance().tick();
            }
        });
        
        // Initialize client zone manager
        ClientZoneManager.getInstance();
        
        LOGGER.info("DangerZone client initialized!");
    }
}