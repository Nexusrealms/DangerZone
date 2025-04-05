package de.nexusrealms.de.zone.effect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import de.nexusrealms.de.zone.Zone;

/**
 * A zone effect that creates fog for players.
 */
public class FoggyZoneEffect implements ZoneEffect {
    private static final String ID = "foggy";
    
    @Override
    public void apply(PlayerEntity player, Zone zone, ServerWorld world) {
        // TODO: Implement fog effect (will need client-side implementation)
        // This is just a placeholder to show a message for now
        player.sendMessage(Text.literal("§bYou are in a foggy area: " + zone.getName()), true);
    }
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    public String getDescription() {
        return "Creates a foggy environment in the zone";
    }
}