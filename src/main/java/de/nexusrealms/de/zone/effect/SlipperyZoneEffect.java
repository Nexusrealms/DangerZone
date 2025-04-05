package de.nexusrealms.de.zone.effect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import de.nexusrealms.de.zone.Zone;

/**
 * A zone effect that makes blocks slippery like ice.
 */
public class SlipperyZoneEffect implements ZoneEffect {
    private static final String ID = "slippery";
    
    @Override
    public void apply(PlayerEntity player, Zone zone, ServerWorld world) {
        // TODO: Implement slippery effect (will need mixin to modify block friction)
        // This is just a placeholder to show a message for now
        player.sendMessage(Text.literal("§9You are on slippery ground in: " + zone.getName()), true);
    }
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    public String getDescription() {
        return "Makes blocks slippery like ice in the zone";
    }
}