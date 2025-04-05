package de.nexusrealms.de.client.effect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.nexusrealms.de.client.ClientZone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.util.math.MathHelper;

/**
 * Client-side effect for foggy zones.
 */
public class ClientFogEffect implements ClientZoneEffect {
    private static final String ID = "foggy";
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
        // No regular updates needed for fog effect
    }
    
    /**
     * Applies fog during rendering. This should be called from a fog render event.
     */
    public void applyFog(BackgroundRenderer.FogData fogData, Camera camera, float viewDistance, FogType fogType) {
        if (activeEffects.isEmpty() || fogType != FogType.FOG_TERRAIN) {
            return;
        }
        
        // Calculate maximum fog intensity across all active zones
        float maxIntensity = 0.0f;
        for (float intensity : activeEffects.values()) {
            maxIntensity = Math.max(maxIntensity, intensity);
        }
        
        if (maxIntensity <= 0.0f) {
            return;
        }
        
        // Apply fog based on intensity
        float fogStart = 0.0f;
        float fogEnd = MathHelper.lerp(maxIntensity, viewDistance, viewDistance * 0.25f);
        
        // Set fog parameters
        fogData.fogStart = fogStart;
        fogData.fogEnd = fogEnd;
        fogData.shape = FogShape.SPHERE;
        
        // Set fog color (slightly bluish mist)
        if (maxIntensity > 0.5f) {
            float[] fogColor = {0.7f, 0.8f, 0.9f};
            fogData.red = fogColor[0];
            fogData.green = fogColor[1];
            fogData.blue = fogColor[2];
        }
    }
}