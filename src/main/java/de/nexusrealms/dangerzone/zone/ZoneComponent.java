package de.nexusrealms.dangerzone.zone;

import de.nexusrealms.dangerzone.DangerZone;
import de.nexusrealms.dangerzone.zone.effect.ZoneEffect;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZoneComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {

    public static final ComponentKey<ZoneComponent> KEY = ComponentRegistryV3.INSTANCE.getOrCreate(DangerZone.id("zones"), ZoneComponent.class);
    private List<Zone> zones = new ArrayList<>();
    private final World world;

    public ZoneComponent(World world) {
        this.world = world;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        zones = DangerZone.mutableListOf(Zone.CODEC).parse(wrapperLookup.getOps(NbtOps.INSTANCE), nbtCompound.get("zones")).getOrThrow();
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.put("zones", Zone.CODEC.listOf().encodeStart(wrapperLookup.getOps(NbtOps.INSTANCE), zones).getOrThrow());
    }
    public void addZone(Zone zone){
        zones.add(zone);
        KEY.sync(world);
    }
    public UUID createZone(String name, ZoneType type){
        Zone zone = Zone.create(name, type);
        addZone(zone);
        return zone.getId();
    }
    public void removeZone(UUID uuid){
        zones.removeIf(zone -> zone.matchesId(uuid));
        KEY.sync(world);
    }
    public Optional<Zone> getZone(UUID id) {
        return streamZones().filter(z -> z.matchesId(id)).findFirst();
    }

    /**
     * Returns all existing zones.
     */
    public Collection<Zone> getAllZones() {
        return zones;
    }
    /**
     * Returns all existing zones as a stream.
     */
    public Stream<Zone> streamZones() {
        return zones.stream();
    }
    public Set<Zone> getZonesForChunk(ChunkPos chunk) {
        return streamZones().filter(zone -> zone.containsChunk(chunk)).collect(Collectors.toSet());
    }
    public Set<Zone> getZonesForPlayer(PlayerEntity player) {
        ChunkPos chunkPos = new ChunkPos(player.getBlockPos());
        return getZonesForChunk(chunkPos);
    }
    public Optional<Zone> findZoneByName(String name) {
        return streamZones()
                .filter(zone -> zone.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void serverTick() {
        world.getPlayers().forEach(player -> {
            Set<Zone> zones = getZonesForPlayer(player);
            Pair<Set<Zone>, Set<Zone>> pair = DangerZone.nonShared(zones, (player.hasAttached(DangerZone.PLAYER_ZONE_ATTACHMENT) ? player.getAttached(DangerZone.PLAYER_ZONE_ATTACHMENT) : List.<UUID>of()).stream().map(this::getZone).flatMap(Optional::stream).collect(Collectors.toSet()));

            pair.getLeft().forEach(zone -> zone.onPlayerEnter((ServerPlayerEntity) player));
            pair.getRight().forEach(zone -> zone.onPlayerExit((ServerPlayerEntity) player));
            if(!pair.getRight().isEmpty()){
                player.setAttached(DangerZone.PLAYER_ZONE_ATTACHMENT, pair.getLeft().stream().map(Zone::getId).toList());
            }
            zones.forEach(zone -> zone.performEffectActions(player, ZoneEffect::tickServer));
        });
    }

    @Override
    public void clientTick() {
        world.getPlayers().forEach(player -> {
            Set<Zone> zones = getZonesForPlayer(player);
            zones.forEach(zone -> zone.performEffectActions(player, ZoneEffect::tickClient));
        });
    }
}
