package de.nexusrealms.de.zone;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all danger zones in the game.
 */
public class ZoneManager {
    private static ZoneManager instance;
    private final Map<UUID, Zone> zones = new ConcurrentHashMap<>();
    private final Map<RegistryKey<World>, Map<ChunkPos, Set<UUID>>> chunkToZoneMap = new ConcurrentHashMap<>();
    
    private ZoneManager() {
        // Private constructor for singleton
    }
    
    public static ZoneManager getInstance() {
        if (instance == null) {
            instance = new ZoneManager();
        }
        return instance;
    }
    
    /**
     * Creates a new zone and registers it.
     */
    public Zone createZone(String name, RegistryKey<World> dimension, ZoneType type) {
        Zone zone = new Zone(name, dimension, type);
        zones.put(zone.getId(), zone);
        return zone;
    }
    
    /**
     * Adds a zone to the manager.
     */
    public void registerZone(Zone zone) {
        zones.put(zone.getId(), zone);
        
        // Update the chunk to zone mapping
        for (ChunkPos chunk : zone.getChunks()) {
            addChunkZoneMapping(zone.getDimension(), chunk, zone.getId());
        }
    }
    
    /**
     * Removes a zone from the manager.
     */
    public void unregisterZone(UUID zoneId) {
        Zone zone = zones.remove(zoneId);
        if (zone != null) {
            // Remove all chunk mappings for this zone
            for (ChunkPos chunk : zone.getChunks()) {
                removeChunkZoneMapping(zone.getDimension(), chunk, zoneId);
            }
        }
    }
    
    /**
     * Gets a zone by its ID.
     */
    public Optional<Zone> getZone(UUID id) {
        return Optional.ofNullable(zones.get(id));
    }
    
    /**
     * Gets all registered zones.
     */
    public Collection<Zone> getAllZones() {
        return zones.values();
    }
    
    /**
     * Adds a chunk to a zone.
     */
    public void addChunkToZone(UUID zoneId, ChunkPos chunk) {
        Optional<Zone> zoneOpt = getZone(zoneId);
        if (zoneOpt.isPresent()) {
            Zone zone = zoneOpt.get();
            zone.addChunk(chunk);
            addChunkZoneMapping(zone.getDimension(), chunk, zoneId);
        }
    }
    
    /**
     * Removes a chunk from a zone.
     */
    public void removeChunkFromZone(UUID zoneId, ChunkPos chunk) {
        Optional<Zone> zoneOpt = getZone(zoneId);
        if (zoneOpt.isPresent()) {
            Zone zone = zoneOpt.get();
            zone.removeChunk(chunk);
            removeChunkZoneMapping(zone.getDimension(), chunk, zoneId);
        }
    }
    
    /**
     * Gets all zones that contain a specific chunk.
     */
    public Set<Zone> getZonesForChunk(RegistryKey<World> dimension, ChunkPos chunk) {
        Set<Zone> result = new HashSet<>();
        Map<ChunkPos, Set<UUID>> dimensionMap = chunkToZoneMap.get(dimension);
        
        if (dimensionMap != null) {
            Set<UUID> zoneIds = dimensionMap.get(chunk);
            if (zoneIds != null) {
                for (UUID id : zoneIds) {
                    getZone(id).ifPresent(result::add);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Checks if a player is in any danger zone.
     */
    public Set<Zone> getZonesForPlayer(PlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld)) {
            return Collections.emptySet();
        }
        
        RegistryKey<World> dimension = player.getWorld().getRegistryKey();
        ChunkPos chunkPos = new ChunkPos(player.getBlockPos());
        return getZonesForChunk(dimension, chunkPos);
    }
    
    private void addChunkZoneMapping(RegistryKey<World> dimension, ChunkPos chunk, UUID zoneId) {
        chunkToZoneMap.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(chunk, k -> ConcurrentHashMap.newKeySet())
                .add(zoneId);
    }
    
    private void removeChunkZoneMapping(RegistryKey<World> dimension, ChunkPos chunk, UUID zoneId) {
        Map<ChunkPos, Set<UUID>> dimensionMap = chunkToZoneMap.get(dimension);
        if (dimensionMap != null) {
            Set<UUID> zoneIds = dimensionMap.get(chunk);
            if (zoneIds != null) {
                zoneIds.remove(zoneId);
                if (zoneIds.isEmpty()) {
                    dimensionMap.remove(chunk);
                }
            }
            if (dimensionMap.isEmpty()) {
                chunkToZoneMap.remove(dimension);
            }
        }
    }
}