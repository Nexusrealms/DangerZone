package de.nexusrealms.dangerzone.zone.effect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import de.nexusrealms.dangerzone.zone.Zone;
import net.minecraft.world.World;

/**
 * A zone effect that makes blocks slippery like ice.
 */
public class SlipperyZoneEffect implements ZoneEffect {
    private static final String ID = "slippery";
    
    @Override
    public void applyServer(PlayerEntity player, Zone zone, World world) {
        // TODO: Implement slippery effect (will need mixin to modify block friction)
        // This is just a placeholder to show a message for now
        player.sendMessage(Text.literal("§9You are on slippery ground in: " + zone.getName()), true);
    }
}