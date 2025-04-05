package de.nexusrealms.dangerzone.zone.effect;

import net.minecraft.entity.player.PlayerEntity;
import de.nexusrealms.dangerzone.zone.Zone;
import net.minecraft.world.World;

/**
 * Interface for effects that can be applied to players in a danger zone.
 */
public interface ZoneEffect {
    /**
     * Applies the effect to a player in a specific zone.
     */
    void apply(PlayerEntity player, Zone zone, World world);
    default void unapply(PlayerEntity player, Zone zone, World world){}
}