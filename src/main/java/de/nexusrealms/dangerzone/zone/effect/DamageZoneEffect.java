package de.nexusrealms.dangerzone.zone.effect;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
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
    private final int i;
    public DamageZoneEffect() {
        this(DEFAULT_DAMAGE, DamageTypes.MAGIC, 0);
    }
    
    public DamageZoneEffect(float damageAmount, RegistryKey<DamageType> damageType, int i) {
        this.damageAmount = damageAmount;
        this.damageType = damageType;
        this.i = i;
    }
    
    @Override
    public void tickServer(PlayerEntity player, Zone zone, World world) {
        // This is just a placeholder for now
        if(i <= 0 || world.getTime() % i == 0){
            player.sendMessage(Text.literal("§cYou are taking damage in the " + zone.getName() + " danger zone!"), true);
            DamageSource damageSource = world.getDamageSources().create(damageType);
            player.damage(damageSource, damageAmount);
        }
    }
}