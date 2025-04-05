package de.nexusrealms.de.client.effect;

import java.util.Collection;

import de.nexusrealms.de.client.ClientZone;

/**
 * Interface for client-side zone effects.
 */
public interface ClientZoneEffect {
    /**
     * Gets the unique identifier for this effect.
     */
    String getId();
    
    /**
     * Applies the effect to a zone.
     */
    void applyEffect(ClientZone zone, float intensity);
    
    /**
     * Updates the intensity of the effect for a zone.
     */
    void updateIntensity(ClientZone zone, float intensity);
    
    /**
     * Removes the effect from a zone.
     */
    void removeEffect(ClientZone zone);
    
    /**
     * Called each tick to update the effect.
     */
    void tick(Collection<ClientZone> activeZones);
}