package de.nexusrealms.de.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nexusrealms.de.DangerZone;
import de.nexusrealms.de.client.effect.ClientFogEffect;
import de.nexusrealms.de.client.effect.ClientSlipperyEffect;
import de.nexusrealms.de.client.effect.ClientZoneEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Manages client-side zone information and effects.
 */
public class ClientZoneManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DangerZone.MOD_ID + "-client");
    private static ClientZoneManager instance;
    
    private final Map<UUID, ClientZone> activeZones = new HashMap<>();
    private final Map<String, ClientZoneEffect> effects = new HashMap<>();
    
    private ClientZoneManager() {
        // Register default client-side effects
        registerEffect(new ClientFogEffect());
        registerEffect(new ClientSlipperyEffect());
    }
    
    public static ClientZoneManager getInstance() {
        if (instance == null) {
            instance = new ClientZoneManager();
        }
        return instance;
    }
    
    /**
     * Called when the player enters a zone.
     */
    public void enterZone(UUID zoneId, String zoneName, String zoneType) {
        LOGGER.debug("Client entered zone: {} ({})", zoneName, zoneType);
        
        ClientZone zone = new ClientZone(zoneId, zoneName, zoneType);
        activeZones.put(zoneId, zone);
        
        // Display a message to the player
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§eYou have entered: " + zoneName + " (" + zoneType + ")"), false);
        }
        
        // Apply the appropriate client effect based on zone type
        applyEffectsForZoneType(zone, zoneType);
    }
    
    /**
     * Called when the player exits a zone.
     */
    public void exitZone(UUID zoneId) {
        ClientZone zone = activeZones.remove(zoneId);
        if (zone != null) {
            LOGGER.debug("Client exited zone: {}", zone.getName());
            
            // Display a message to the player
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§eYou have left: " + zone.getName()), false);
            }
            
            // Remove the zone's effects
            for (ClientZoneEffect effect : effects.values()) {
                effect.removeEffect(zone);
            }
        }
    }
    
    /**
     * Called when an effect parameter is updated for a zone.
     */
    public void updateZoneEffect(UUID zoneId, String effectId, float intensity) {
        ClientZone zone = activeZones.get(zoneId);
        if (zone != null) {
            ClientZoneEffect effect = effects.get(effectId);
            if (effect != null) {
                effect.updateIntensity(zone, intensity);
            }
        }
    }
    
    /**
     * Registers a client-side effect.
     */
    public void registerEffect(ClientZoneEffect effect) {
        effects.put(effect.getId(), effect);
    }
    
    /**
     * Gets a client-side effect by ID.
     */
    public ClientZoneEffect getEffect(String id) {
        return effects.get(id);
    }
    
    /**
     * Called each frame to update and apply active effects.
     */
    public void tick() {
        // Update all effects
        for (ClientZoneEffect effect : effects.values()) {
            effect.tick(activeZones.values());
        }
    }
    
    /**
     * Apply the appropriate effects based on zone type.
     */
    private void applyEffectsForZoneType(ClientZone zone, String zoneType) {
        switch (zoneType) {
            case "foggy":
                ClientZoneEffect fogEffect = getEffect("foggy");
                if (fogEffect != null) {
                    fogEffect.applyEffect(zone, 1.0f);
                }
                break;
            case "slippery":
                ClientZoneEffect slipperyEffect = getEffect("slippery");
                if (slipperyEffect != null) {
                    slipperyEffect.applyEffect(zone, 1.0f);
                }
                break;
            // Damage effect is handled server-side, no client-side effect needed
            default:
                // No effect for unknown types
                break;
        }
    }
    
    /**
     * Resets the manager, clearing all active zones.
     * Called when leaving a world.
     */
    public void reset() {
        activeZones.clear();
    }
}