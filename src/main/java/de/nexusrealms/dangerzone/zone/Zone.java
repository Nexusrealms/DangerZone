package de.nexusrealms.dangerzone.zone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.nexusrealms.dangerzone.DangerZone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
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
    public static final Codec<ChunkPos> CHUNK_POS_CODEC = Codec.LONG.xmap(ChunkPos::new, ChunkPos::toLong);
    public static final Codec<ChunkPos> TWO_COORD_CHUNK_POS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(c -> c.x),
            Codec.INT.fieldOf("z").forGetter(c -> c.z)
    ).apply(instance, ChunkPos::new));
    private final UUID id;
    private String name;
    private final Set<ChunkPos> chunks;
    private ZoneType type;
    public static final Codec<Zone> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("id").forGetter(Zone::getId),
            Codec.STRING.fieldOf("name").forGetter(Zone::getName),
            DangerZone.mutableSetOf(CHUNK_POS_CODEC).fieldOf("chunks").forGetter(Zone::getChunks),
            ZoneType.CODEC.fieldOf("type").forGetter(Zone::getType)
    ).apply(instance, Zone::new));
    private Zone(UUID uuid, String name, Set<ChunkPos> chunks, ZoneType type){
        this.id = uuid;
        this.name = name;
        this.chunks = chunks;
        this.type = type;
    }
    public static Zone create(String name, ZoneType type){
        return new Zone(UUID.randomUUID(), name, new HashSet<>(), type);
    }
    public boolean matchesId(UUID id){
        return this.id.equals(id);
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
    public void onPlayerEnter(PlayerEntity player){
        getType().effects().forEach(zoneEffect -> zoneEffect.apply(player, this, player.getWorld()));
    }
    public void onPlayerExit(PlayerEntity player){
        getType().effects().forEach(zoneEffect -> zoneEffect.unapply(player, this, player.getWorld()));
    }
    /**
     * Checks if the given chunk position is within this zone.
     */
    public boolean isInZone(ChunkPos pos) {
        return chunks.contains(pos);
    }
}