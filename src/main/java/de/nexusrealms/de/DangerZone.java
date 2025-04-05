package de.nexusrealms.de;

import de.nexusrealms.de.command.ZoneCommands;
import de.nexusrealms.de.zone.ZoneManager;
import de.nexusrealms.de.zone.ZoneTracker;
import de.nexusrealms.de.zone.persistence.ZonePersistence;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DangerZone implements ModInitializer {
	public static final String MOD_ID = "dangerzone";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing DangerZone mod...");
		
		// Initialize the zone manager (will be a no-op if already initialized)
		ZoneManager.getInstance();
		
		// Initialize the zone tracker
		ZoneTracker.getInstance().initialize();
		
		// Register commands
		CommandRegistrationCallback.EVENT.register(ZoneCommands::register);
		
		// Register server lifecycle events for loading and saving zones
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			LOGGER.info("Loading danger zones from disk...");
			ZonePersistence.loadZones(server);
		});
		
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			LOGGER.info("Saving danger zones to disk...");
			ZonePersistence.saveZones(server);
		});
		
		LOGGER.info("DangerZone mod initialized!");
	}
}