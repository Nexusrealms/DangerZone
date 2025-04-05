package de.nexusrealms.dangerzone.zone;

import com.mojang.serialization.Codec;
import de.nexusrealms.dangerzone.DangerZone;
import de.nexusrealms.dangerzone.zone.effect.ZoneEffect;
import de.nexusrealms.dangerzone.zone.effect.ZoneEffects;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.List;

/**
 * Defines different types of danger zones with unique effects.
 */
public record ZoneType(String description, List<ZoneEffect> effects) {
    public static final RegistryKey<Registry<ZoneType>> KEY = RegistryKey.ofRegistry(DangerZone.id("zone_type"));
    public static final Registry<ZoneType> REGISTRY = FabricRegistryBuilder.createSimple(KEY).buildAndRegister();
    public static final Codec<ZoneType> CODEC = REGISTRY.getCodec();
    private static ZoneType create(String name, ZoneType type){
        return Registry.register(REGISTRY, DangerZone.id(name), type);
    }
    public static final ZoneType DAMAGE = create("damage", new ZoneType("Applies damage over time", List.of(ZoneEffects.DAMAGE)));
    public static final ZoneType FOGGY = create("foggy", new ZoneType("Reduces visibility with fog", List.of(ZoneEffects.FOGGY)));
    public static final ZoneType SLIPPERY = create("slippery", new ZoneType("Makes blocks slippery like ice", List.of(ZoneEffects.SLIPPERY)));
    public static void init(){}

    @Override
    public String toString() {
        return REGISTRY.getKey(this).toString();
    }
}