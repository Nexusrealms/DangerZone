package de.nexusrealms.de.zone;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a danger zone that consists of chunks and can have various effects.
 */
public class Zone {
    private final UUID id;
    private String name;
    private final RegistryKey<World> dimension;
    private final Set<ChunkPos> chunks;
    private ZoneType type;
    
    public Zone(String name, RegistryKey<World> dimension, ZoneType type) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.dimension = dimension;
        this.chunks = new HashSet<>();
        this.type = type;
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public RegistryKey<World> getDimension() {
        return dimension;
    }
    
    public Set<ChunkPos> getChunks() {
        return chunks;
    }
    
    public void addChunk(ChunkPos chunk) {
        chunks.add(chunk);
    }
    
    public void removeChunk(ChunkPos chunk) {
        chunks.remove(chunk);
    }
    
    public boolean containsChunk(ChunkPos chunk) {
        return chunks.contains(chunk);
    }
    
    public ZoneType getType() {
        return type;
    }
    
    public void setType(ZoneType type) {
        this.type = type;
    }
    
    /**
     * Checks if the given chunk position is within this zone.
     */
    public boolean isInZone(ChunkPos pos, RegistryKey<World> worldKey) {
        return dimension.equals(worldKey) && chunks.contains(pos);
    }
}