package de.nexusrealms.de.zone;

import de.nexusrealms.de.DangerZone;
import de.nexusrealms.de.network.NetworkHandler;
import de.nexusrealms.de.zone.effect.ZoneEffectManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

/**
 * Tracks players and applies zone effects to them when they're in a zone.
 */
public class ZoneTracker {
    private static ZoneTracker instance;
    private final Map<UUID, Set<UUID>> playerInZones = new HashMap<>();
    private boolean initialized = false;
    
    private ZoneTracker() {
        // Private constructor for singleton
    }
    
    public static ZoneTracker getInstance() {
        if (instance == null) {
            instance = new ZoneTracker();
        }
        return instance;
    }
    
    /**
     * Initializes the zone tracker with event listeners.
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        
        // Register server tick event to check player positions
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        
        initialized = true;
        DangerZone.LOGGER.info("Zone tracker initialized");
    }
    
    /**
     * Called at the end of each server tick.
     */
    private void onServerTick(MinecraftServer server) {
        // Only process every 20 ticks (approximately 1 second)
        if (server.getTicks() % 20 != 0) {
            return;
        }
        
        // Process all players
        for (ServerWorld world : server.getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                processPlayer(player, world);
            }
        }
    }
    
    /**
     * Processes a player to check if they're in any zones and applies effects.
     */
    private void processPlayer(ServerPlayerEntity player, ServerWorld world) {
        // Get zones the player is in
        Set<Zone> playerZones = ZoneManager.getInstance().getZonesForPlayer(player);
        UUID playerId = player.getUuid();
        
        // Get zones the player was previously in
        Set<UUID> previousZoneIds = playerInZones.getOrDefault(playerId, new HashSet<>());
        Set<UUID> currentZoneIds = new HashSet<>();
        
        // Process current zones
        for (Zone zone : playerZones) {
            UUID zoneId = zone.getId();
            currentZoneIds.add(zoneId);
            
            // Apply zone effects
            ZoneEffectManager.getInstance().applyEffects(player, zone, world);
            
            // If this is a new zone, send an enter message and notify client
            if (!previousZoneIds.contains(zoneId)) {
                onPlayerEnterZone(player, zone);
            }
        }
        
        // Check for zones the player has exited
        for (UUID zoneId : previousZoneIds) {
            if (!currentZoneIds.contains(zoneId)) {
                // Player has left this zone
                ZoneManager.getInstance().getZone(zoneId).ifPresent(zone -> 
                    onPlayerExitZone(player, zone)
                );
            }
        }
        
        // Update stored zones for the player
        if (currentZoneIds.isEmpty()) {
            playerInZones.remove(playerId);
        } else {
            playerInZones.put(playerId, currentZoneIds);
        }
    }
    
    /**
     * Called when a player enters a zone.
     */
    private void onPlayerEnterZone(PlayerEntity player, Zone zone) {
        // Send message on server side
        player.sendMessage(net.minecraft.text.Text.literal("§eYou have entered: " + zone.getName() + " (" + zone.getType().getId() + ")"), false);
        
        // Notify client to update visual effects
        if (player instanceof ServerPlayerEntity) {
            NetworkHandler.sendZoneEnterPacket((ServerPlayerEntity) player, zone);
            
            // Example: Send effect intensity updates based on zone type
            switch (zone.getType()) {
                case FOGGY:
                    NetworkHandler.sendZoneEffectUpdate((ServerPlayerEntity) player, zone, "foggy", 1.0f);
                    break;
                case SLIPPERY:
                    NetworkHandler.sendZoneEffectUpdate((ServerPlayerEntity) player, zone, "slippery", 1.0f);
                    break;
                default:
                    // Other zone types don't need client-side effects
                    break;
            }
        }
    }
    
    /**
     * Called when a player exits a zone.
     */
    private void onPlayerExitZone(PlayerEntity player, Zone zone) {
        // Send message on server side
        player.sendMessage(net.minecraft.text.Text.literal("§eYou have left: " + zone.getName()), false);
        
        // Notify client to remove visual effects
        if (player instanceof ServerPlayerEntity) {
            NetworkHandler.sendZoneExitPacket((ServerPlayerEntity) player, zone);
        }
    }
}