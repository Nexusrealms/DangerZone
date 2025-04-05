package de.nexusrealms.de.zone.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.nexusrealms.de.DangerZone;
import de.nexusrealms.de.zone.Zone;
import de.nexusrealms.de.zone.ZoneManager;
import de.nexusrealms.de.zone.ZoneType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Handles saving and loading of danger zones to/from disk.
 */
public class ZonePersistence {
    private static final String ZONES_FILE = "danger_zones.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Saves all zones to disk.
     */
    public static void saveZones(MinecraftServer server) {
        // In 1.21.1, we use the server's getRunDirectory for storage
        File worldDirectory = new File(server.getRunDirectory(), "data");
        if (!worldDirectory.exists() && !worldDirectory.mkdirs()) {
            DangerZone.LOGGER.error("Failed to create data directory for saving zones");
            return;
        }
        
        File zonesFile = new File(worldDirectory, ZONES_FILE);
        Collection<Zone> zones = ZoneManager.getInstance().getAllZones();
        
        try (FileWriter writer = new FileWriter(zonesFile)) {
            JsonArray zonesArray = new JsonArray();
            
            for (Zone zone : zones) {
                JsonObject zoneObject = new JsonObject();
                zoneObject.addProperty("id", zone.getId().toString());
                zoneObject.addProperty("name", zone.getName());
                zoneObject.addProperty("type", zone.getType().getId());
                zoneObject.addProperty("dimension", zone.getDimension().getValue().toString());
                
                JsonArray chunksArray = new JsonArray();
                for (ChunkPos chunk : zone.getChunks()) {
                    JsonObject chunkObject = new JsonObject();
                    chunkObject.addProperty("x", chunk.x);
                    chunkObject.addProperty("z", chunk.z);
                    chunksArray.add(chunkObject);
                }
                
                zoneObject.add("chunks", chunksArray);
                zonesArray.add(zoneObject);
            }
            
            GSON.toJson(zonesArray, writer);
            DangerZone.LOGGER.info("Saved {} zones to disk", zones.size());
        } catch (IOException e) {
            DangerZone.LOGGER.error("Failed to save zones to disk", e);
        }
    }
    
    /**
     * Loads all zones from disk.
     */
    public static void loadZones(MinecraftServer server) {
        // In 1.21.1, we use the server's getRunDirectory for storage
        File worldDirectory = new File(server.getRunDirectory(), "data");
        File zonesFile = new File(worldDirectory, ZONES_FILE);
        
        if (!zonesFile.exists()) {
            DangerZone.LOGGER.info("No zones file found, starting with empty zones");
            return;
        }
        
        try (FileReader reader = new FileReader(zonesFile)) {
            JsonArray zonesArray = JsonParser.parseReader(reader).getAsJsonArray();
            int loaded = 0;
            
            // Clear existing zones
            List<UUID> existingZones = new ArrayList<>(ZoneManager.getInstance().getAllZones())
                    .stream()
                    .map(Zone::getId)
                    .toList();
            
            for (UUID zoneId : existingZones) {
                ZoneManager.getInstance().unregisterZone(zoneId);
            }
            
            // Load zones from file
            for (JsonElement element : zonesArray) {
                JsonObject zoneObject = element.getAsJsonObject();
                
                UUID id = UUID.fromString(zoneObject.get("id").getAsString());
                String name = zoneObject.get("name").getAsString();
                String typeId = zoneObject.get("type").getAsString();
                String dimensionId = zoneObject.get("dimension").getAsString();
                
                // Find the zone type
                ZoneType type = null;
                for (ZoneType zoneType : ZoneType.values()) {
                    if (zoneType.getId().equals(typeId)) {
                        type = zoneType;
                        break;
                    }
                }
                
                if (type == null) {
                    DangerZone.LOGGER.warn("Unknown zone type: {}, using CUSTOM", typeId);
                    type = ZoneType.CUSTOM;
                }
                
                // Parse the dimension
                RegistryKey<World> dimension;
                try {
                    // Split the dimension identifier correctly
                    String[] parts = dimensionId.split(":");
                    Identifier dimId;
                    if (parts.length == 2) {
                        dimId = new Identifier(parts[0], parts[1]);
                    } else {
                        dimId = new Identifier("minecraft", dimensionId);
                    }
                    
                    dimension = RegistryKey.of(RegistryKeys.WORLD, dimId);
                } catch (Exception e) {
                    DangerZone.LOGGER.warn("Invalid dimension: {}, using overworld", dimensionId);
                    dimension = World.OVERWORLD;
                }
                
                // Create the zone
                Zone zone = new Zone(name, dimension, type);
                
                // Add chunks
                JsonArray chunksArray = zoneObject.get("chunks").getAsJsonArray();
                for (JsonElement chunkElement : chunksArray) {
                    JsonObject chunkObject = chunkElement.getAsJsonObject();
                    int x = chunkObject.get("x").getAsInt();
                    int z = chunkObject.get("z").getAsInt();
                    
                    ChunkPos chunk = new ChunkPos(x, z);
                    zone.addChunk(chunk);
                }
                
                // Register the zone
                ZoneManager.getInstance().registerZone(zone);
                loaded++;
            }
            
            DangerZone.LOGGER.info("Loaded {} zones from disk", loaded);
        } catch (Exception e) {
            DangerZone.LOGGER.error("Failed to load zones from disk", e);
        }
    }
}