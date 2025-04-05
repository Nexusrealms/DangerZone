package de.nexusrealms.dangerzone.zone.effect;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import de.nexusrealms.dangerzone.zone.Zone;
import net.minecraft.world.World;

/**
 * A zone effect that damages players over time.
 */
public class DamageZoneEffect implements ZoneEffect {
    private static final float DEFAULT_DAMAGE = 1.0f;
    private final float damageAmount;
    private final RegistryKey<DamageType> damageType;
    public DamageZoneEffect() {
        this(DEFAULT_DAMAGE, DamageTypes.MAGIC);
    }
    
    public DamageZoneEffect(float damageAmount, RegistryKey<DamageType> damageType) {
        this.damageAmount = damageAmount;
        this.damageType = damageType;
    }
    
    @Override
    public void apply(PlayerEntity player, Zone zone, World world) {
        // This is just a placeholder for now
        if(!(world instanceof ServerWorld)) return;
        player.sendMessage(Text.literal("§cYou are taking damage in the " + zone.getName() + " danger zone!"), true);
        DamageSource damageSource = world.getDamageSources().create(damageType);
        player.damage(damageSource, damageAmount);
    }
}