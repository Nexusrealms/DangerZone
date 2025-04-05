package de.nexusrealms.dangerzone.zone.effect;


import com.mojang.serialization.Codec;
import de.nexusrealms.dangerzone.DangerZone;
import de.nexusrealms.dangerzone.zone.ZoneType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * Manages and applies effects for danger zones.
 */
public class ZoneEffects {
    public static final RegistryKey<Registry<ZoneEffect>> KEY = RegistryKey.ofRegistry(DangerZone.id("zone_effect"));
    public static final Registry<ZoneEffect> REGISTRY = FabricRegistryBuilder.createSimple(KEY).buildAndRegister();
    public static final Codec<ZoneEffect> CODEC = REGISTRY.getCodec();
    public static final ZoneEffect DAMAGE = create("damage", new DamageZoneEffect());
    public static final ZoneEffect FOGGY = create("foggy", new FoggyZoneEffect());
    public static final ZoneEffect SLIPPERY = create("slippery", new SlipperyZoneEffect());

    private static <T extends ZoneEffect> T create(String name, T effect){
        return Registry.register(REGISTRY, DangerZone.id(name), effect);
    }
    public static void init(){}
}