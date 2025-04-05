package de.nexusrealms.de.client.effect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.nexusrealms.de.client.ClientZone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Client-side effect for slippery zones.
 */
public class ClientSlipperyEffect implements ClientZoneEffect {
    private static final String ID = "slippery";
    private final Map<UUID, Float> activeEffects = new HashMap<>();
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    public void applyEffect(ClientZone zone, float intensity) {
        zone.setEffectIntensity(ID, intensity);
        activeEffects.put(zone.getId(), intensity);
    }
    
    @Override
    public void updateIntensity(ClientZone zone, float intensity) {
        zone.setEffectIntensity(ID, intensity);
        activeEffects.put(zone.getId(), intensity);
    }
    
    @Override
    public void removeEffect(ClientZone zone) {
        zone.removeEffectIntensity(ID);
        activeEffects.remove(zone.getId());
    }
    
    @Override
    public void tick(Collection<ClientZone> activeZones) {
        // Create visual slippery effect (particles, etc)
        // We'll leave actual movement control to the server
        
        if (activeEffects.isEmpty()) {
            return;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        
        if (player == null || !player.isOnGround()) {
            return;
        }
        
        // Calculate maximum slippery intensity across all active zones
        float maxIntensity = 0.0f;
        for (float intensity : activeEffects.values()) {
            maxIntensity = Math.max(maxIntensity, intensity);
        }
        
        if (maxIntensity <= 0.0f) {
            return;
        }
        
        // Apply a visual indicator for slippery surface
        // Here we could spawn particles under the player's feet
        // or add a special overlay to the screen
        if (client.world != null && client.world.getTime() % 10 == 0) {
            // Example: we could add blue particles under the feet
            // This is just a visual indicator, gameplay will be handled server-side
            Vec3d pos = player.getPos();
            // client.particleManager.addParticle(...); // We'd add appropriate particles here
        }
    }
}