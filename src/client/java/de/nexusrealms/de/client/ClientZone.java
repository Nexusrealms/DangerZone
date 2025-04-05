package de.nexusrealms.de.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a danger zone on the client side.
 */
public class ClientZone {
    private final UUID id;
    private final String name;
    private final String type;
    private final Map<String, Float> effectIntensities = new HashMap<>();
    
    public ClientZone(UUID id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    /**
     * Gets the unique ID of the zone.
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Gets the name of the zone.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the type of the zone.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the intensity of an effect.
     */
    public void setEffectIntensity(String effectId, float intensity) {
        effectIntensities.put(effectId, intensity);
    }
    
    /**
     * Gets the intensity of an effect.
     */
    public float getEffectIntensity(String effectId) {
        return effectIntensities.getOrDefault(effectId, 0.0f);
    }
    
    /**
     * Removes an effect intensity.
     */
    public void removeEffectIntensity(String effectId) {
        effectIntensities.remove(effectId);
    }
}