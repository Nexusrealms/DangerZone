package de.nexusrealms.de.zone.effect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import de.nexusrealms.de.zone.Zone;

/**
 * Interface for effects that can be applied to players in a danger zone.
 */
public interface ZoneEffect {
    /**
     * Applies the effect to a player in a specific zone.
     */
    void apply(PlayerEntity player, Zone zone, ServerWorld world);
    
    /**
     * Gets the unique identifier for this effect.
     */
    String getId();
    
    /**
     * Gets a human-readable description of this effect.
     */
    String getDescription();
}