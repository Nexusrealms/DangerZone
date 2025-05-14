package de.nexusrealms.dangerzone.zone.effect;

import de.nexusrealms.dangerzone.zone.ZoneComponent;
import net.minecraft.entity.player.PlayerEntity;
import de.nexusrealms.dangerzone.zone.Zone;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Interface for effects that can be applied to players in a danger zone.
 */
public interface ZoneEffect {
    /**
     * Applies the effect to a player in a specific zone.
     */
    @FunctionalInterface
    interface ZoneEffectApplicator {
        void apply(ZoneEffect effect, PlayerEntity player, Zone zone, World world);
    }
    default void applyServer(PlayerEntity player, Zone zone, World world){}
    default void applyClient(PlayerEntity player, Zone zone, World world){}

    default void unapplyServer(PlayerEntity player, Zone zone, World world){}
    default void unapplyClient(PlayerEntity player, Zone zone, World world){}

    default void tickServer(PlayerEntity player, Zone zone, World world){}
    default void tickClient(PlayerEntity player, Zone zone, World world){}

    static <T extends ZoneEffect> Collection<T> getEffectsForPlayer(PlayerEntity player, Class<T> clazz){
        return player.getWorld().getComponent(ZoneComponent.KEY).getZonesForPlayer(player).stream().flatMap(zone -> zone.getEffects(clazz).stream()).collect(Collectors.toSet());
    }
}