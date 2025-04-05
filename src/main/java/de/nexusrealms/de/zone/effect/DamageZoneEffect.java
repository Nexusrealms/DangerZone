package de.nexusrealms.de.zone.effect;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import de.nexusrealms.de.DangerZone;
import de.nexusrealms.de.zone.Zone;

/**
 * A zone effect that damages players over time.
 */
public class DamageZoneEffect implements ZoneEffect {
    private static final String ID = "damage";
    private static final float DEFAULT_DAMAGE = 1.0f;
    private final float damageAmount;
    
    public DamageZoneEffect() {
        this(DEFAULT_DAMAGE);
    }
    
    public DamageZoneEffect(float damageAmount) {
        this.damageAmount = damageAmount;
    }
    
    @Override
    public void apply(PlayerEntity player, Zone zone, ServerWorld world) {
        // TODO: Add custom implementation for damage over time
        // This is just a placeholder for now
        player.sendMessage(Text.literal("§cYou are taking damage in the " + zone.getName() + " danger zone!"), true);
        
        // Get the registry for damage types
        RegistryKey<DamageType> damageTypeKey = RegistryKey.of(
                RegistryKeys.DAMAGE_TYPE,
                new Identifier(DangerZone.MOD_ID, "danger_zone")
        );
        
        // Get the damage source from the registry
        // We're using a fallback to regular damage if our type isn't registered
        DamageSource damageSource = world.getDamageSources().create(damageTypeKey);
        if (damageSource == null) {
            damageSource = world.getDamageSources().magic();
        }
        
        player.damage(damageSource, damageAmount);
    }
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    public String getDescription() {
        return "Applies " + damageAmount + " damage over time to players in the zone";
    }
}