package de.nexusrealms.dangerzone.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.nexusrealms.dangerzone.client.DangerZoneClient;
import net.minecraft.client.render.FogShape;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer.FogType;

@Mixin(BackgroundRenderer.class)
public class FogRenderMixin {

    @Shadow private static float red;

    @Shadow private static float green;

    @Shadow private static float blue;

    @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"))
    private static void onSetupFog(Camera camera, FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Local BackgroundRenderer.FogData fogData) {
        // Apply our custom fog effect if the player is in a foggy zone
        if(DangerZoneClient.isInZoneFog){
            float maxIntensity = 1.0f;
            float fogStart = 0.0f;
            float fogEnd = MathHelper.lerp(maxIntensity, viewDistance, viewDistance * 0.25f);

            // Set fog parameters
            fogData.fogStart = 0.0F;
            fogData.fogEnd = 2.0F;
            //fogData.fogShape = FogShape.SPHERE;

            // Set fog color (slightly bluish mist)
            red = 0.7f;
            green = 0.8f;
            blue = 0.9f;
        }
    }
}