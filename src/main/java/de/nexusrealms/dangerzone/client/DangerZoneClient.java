package de.nexusrealms.dangerzone.client;

import de.nexusrealms.dangerzone.DangerZone;
import de.nexusrealms.dangerzone.network.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DangerZoneClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(DangerZone.MOD_ID + "-client");
    public static boolean isInZoneFog = false;
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing DangerZone client...");
        LOGGER.info("DangerZone client initialized!");
    }
}