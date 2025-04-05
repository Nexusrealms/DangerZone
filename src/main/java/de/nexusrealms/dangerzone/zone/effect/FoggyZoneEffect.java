package de.nexusrealms.dangerzone.zone.effect;

import de.nexusrealms.dangerzone.client.DangerZoneClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import de.nexusrealms.dangerzone.zone.Zone;
import net.minecraft.world.World;

/**
 * A zone effect that creates fog for players.
 */
public class FoggyZoneEffect implements ZoneEffect {
    private static final String ID = "foggy";
    
    @Override
    public void apply(PlayerEntity player, Zone zone, World world) {
        if(!(world instanceof ClientWorld)) return;
        DangerZoneClient.isInZoneFog = true;
    }

    @Override
    public void unapply(PlayerEntity player, Zone zone, World world) {
        if(!(world instanceof ClientWorld)) return;
        DangerZoneClient.isInZoneFog = false;
    }
}