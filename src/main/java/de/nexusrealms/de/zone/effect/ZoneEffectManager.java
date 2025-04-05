package de.nexusrealms.de.zone.effect;

import de.nexusrealms.de.zone.Zone;
import de.nexusrealms.de.zone.ZoneType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages and applies effects for danger zones.
 */
public class ZoneEffectManager {
    private static ZoneEffectManager instance;
    private final Map<String, ZoneEffect> registeredEffects = new HashMap<>();
    
    private ZoneEffectManager() {
        // Register default effects
        registerEffect(new DamageZoneEffect());
        registerEffect(new FoggyZoneEffect());
        registerEffect(new SlipperyZoneEffect());
    }
    
    public static ZoneEffectManager getInstance() {
        if (instance == null) {
            instance = new ZoneEffectManager();
        }
        return instance;
    }
    
    /**
     * Registers a new zone effect.
     */
    public void registerEffect(ZoneEffect effect) {
        registeredEffects.put(effect.getId(), effect);
    }
    
    /**
     * Gets a registered zone effect by ID.
     */
    public ZoneEffect getEffect(String id) {
        return registeredEffects.get(id);
    }
    
    /**
     * Applies effects to a player based on their current zone.
     */
    public void applyEffects(PlayerEntity player, Zone zone, ServerWorld world) {
        ZoneType type = zone.getType();
        
        // Apply effects based on zone type
        switch (type) {
            case DAMAGE:
                getOrCreateEffect("damage").apply(player, zone, world);
                break;
            case FOGGY:
                getOrCreateEffect("foggy").apply(player, zone, world);
                break;
            case SLIPPERY:
                getOrCreateEffect("slippery").apply(player, zone, world);
                break;
            case CUSTOM:
                // For custom zones, you might want to apply multiple effects
                // This would be expanded in the future
                break;
            default:
                // No effect for unknown types
        }
    }
    
    private ZoneEffect getOrCreateEffect(String id) {
        ZoneEffect effect = registeredEffects.get(id);
        if (effect == null) {
            // Create a default effect if not found
            switch (id) {
                case "damage":
                    effect = new DamageZoneEffect();
                    break;
                case "foggy":
                    effect = new FoggyZoneEffect();
                    break;
                case "slippery":
                    effect = new SlipperyZoneEffect();
                    break;
                default:
                    // Create a dummy effect if nothing matches
                    effect = new ZoneEffect() {
                        @Override
                        public void apply(PlayerEntity player, Zone zone, ServerWorld world) {
                            // Do nothing
                        }
                        
                        @Override
                        public String getId() {
                            return id;
                        }
                        
                        @Override
                        public String getDescription() {
                            return "Unknown effect: " + id;
                        }
                    };
            }
            
            // Register the newly created effect
            registerEffect(effect);
        }
        
        return effect;
    }
}