package de.nexusrealms.de.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.nexusrealms.de.client.ClientZoneManager;
import de.nexusrealms.de.client.effect.ClientFogEffect;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer.FogData;
import net.minecraft.client.render.BackgroundRenderer.FogType;

@Mixin(BackgroundRenderer.class)
public class FogRenderMixin {
    
    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void onSetupFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog, float tickDelta, FogData fogData, CallbackInfo ci) {
        // Apply our custom fog effect if the player is in a foggy zone
        ClientZoneEffect fogEffect = ClientZoneManager.getInstance().getEffect("foggy");
        if (fogEffect instanceof ClientFogEffect) {
            ((ClientFogEffect) fogEffect).applyFog(fogData, camera, viewDistance, fogType);
        }
    }
}