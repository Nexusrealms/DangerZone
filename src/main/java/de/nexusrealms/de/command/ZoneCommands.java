package de.nexusrealms.de.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nexusrealms.de.zone.Zone;
import de.nexusrealms.de.zone.ZoneManager;
import de.nexusrealms.de.zone.ZoneType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Optional;
import java.util.UUID;

/**
 * Commands for managing danger zones.
 */
public class ZoneCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        // Main command: /zone
        dispatcher.register(CommandManager.literal("zone")
                .requires(source -> source.hasPermissionLevel(2)) // Require operator permission level
                
                // Create a new zone: /zone create <name> <type>
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .then(CommandManager.argument("type", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            for (ZoneType type : ZoneType.values()) {
                                                builder.suggest(type.getId());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(ZoneCommands::createZone))))
                
                // Add current chunk to a zone: /zone add <name>
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(ZoneCommands::addChunkToZone)))
                
                // Remove current chunk from a zone: /zone remove <name>
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(ZoneCommands::removeChunkFromZone)))
                
                // List all zones: /zone list
                .then(CommandManager.literal("list")
                        .executes(ZoneCommands::listZones))
                
                // Delete a zone: /zone delete <name>
                .then(CommandManager.literal("delete")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(ZoneCommands::deleteZone)))
                
                // Info about a zone: /zone info <name>
                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(ZoneCommands::zoneInfo)))
        );
    }
    
    private static int createZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        String typeStr = StringArgumentType.getString(context, "type");
        
        // Find the zone type
        ZoneType type = null;
        for (ZoneType zoneType : ZoneType.values()) {
            if (zoneType.getId().equalsIgnoreCase(typeStr)) {
                type = zoneType;
                break;
            }
        }
        
        if (type == null) {
            source.sendFeedback(() -> Text.literal("§cInvalid zone type. Valid types: damage, foggy, slippery, custom"), false);
            return 0;
        }
        
        // Check if a zone with this name already exists
        for (Zone zone : ZoneManager.getInstance().getAllZones()) {
            if (zone.getName().equalsIgnoreCase(name)) {
                source.sendFeedback(() -> Text.literal("§cA zone with this name already exists"), false);
                return 0;
            }
        }
        
        // Create the zone
        final ZoneType finalType = type; // Make type effectively final for the lambda
        Zone zone = ZoneManager.getInstance().createZone(name, source.getWorld().getRegistryKey(), finalType);
        source.sendFeedback(() -> Text.literal("§aCreated new " + finalType.getId() + " zone: " + name), true);
        return 1;
    }
    
    private static int addChunkToZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        
        // Find the zone
        Optional<Zone> targetZone = findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }
        
        Zone zone = targetZone.get();
        
        // Get current chunk position using BlockPos instead of directly from Vec3d
        BlockPos blockPos = BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(blockPos);
        
        // Check if this chunk is already in the zone
        if (zone.containsChunk(chunkPos)) {
            source.sendFeedback(() -> Text.literal("§cThis chunk is already part of zone: " + name), false);
            return 0;
        }
        
        // Add the chunk to the zone
        ZoneManager.getInstance().addChunkToZone(zone.getId(), chunkPos);
        source.sendFeedback(() -> Text.literal("§aAdded chunk " + chunkPos.x + ", " + chunkPos.z + " to zone: " + name), true);
        return 1;
    }
    
    private static int removeChunkFromZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        
        // Find the zone
        Optional<Zone> targetZone = findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }
        
        Zone zone = targetZone.get();
        
        // Get current chunk position using BlockPos instead of directly from Vec3d
        BlockPos blockPos = BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(blockPos);
        
        // Check if this chunk is in the zone
        if (!zone.containsChunk(chunkPos)) {
            source.sendFeedback(() -> Text.literal("§cThis chunk is not part of zone: " + name), false);
            return 0;
        }
        
        // Remove the chunk from the zone
        ZoneManager.getInstance().removeChunkFromZone(zone.getId(), chunkPos);
        source.sendFeedback(() -> Text.literal("§aRemoved chunk " + chunkPos.x + ", " + chunkPos.z + " from zone: " + name), true);
        return 1;
    }
    
    private static int listZones(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        // Get all zones
        java.util.Collection<Zone> zones = ZoneManager.getInstance().getAllZones();
        
        if (zones.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§eNo zones have been created yet"), false);
            return 0;
        }
        
        // Display all zones
        source.sendFeedback(() -> Text.literal("§6===== Danger Zones ====="), false);
        for (Zone zone : zones) {
            final Zone finalZone = zone; // Make zone effectively final for the lambda
            source.sendFeedback(() -> Text.literal("§e- " + finalZone.getName() + " (" + finalZone.getType().getId() + ") - " 
                    + finalZone.getChunks().size() + " chunks"), false);
        }
        
        return zones.size();
    }
    
    private static int deleteZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        
        // Find the zone
        Optional<Zone> targetZone = findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }
        
        // Remove the zone
        Zone zone = targetZone.get();
        ZoneManager.getInstance().unregisterZone(zone.getId());
        source.sendFeedback(() -> Text.literal("§aDeleted zone: " + name), true);
        return 1;
    }
    
    private static int zoneInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        
        // Find the zone
        Optional<Zone> targetZone = findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }
        
        // Display zone information
        Zone zone = targetZone.get();
        final Zone finalZone = zone; // Make zone effectively final for the lambda
        source.sendFeedback(() -> Text.literal("§6===== Zone: " + finalZone.getName() + " ====="), false);
        source.sendFeedback(() -> Text.literal("§eID: " + finalZone.getId()), false);
        source.sendFeedback(() -> Text.literal("§eType: " + finalZone.getType().getId() + " - " + finalZone.getType().getDescription()), false);
        source.sendFeedback(() -> Text.literal("§eDimension: " + finalZone.getDimension().getValue()), false);
        source.sendFeedback(() -> Text.literal("§eChunks: " + finalZone.getChunks().size()), false);
        
        return 1;
    }
    
    private static Optional<Zone> findZoneByName(String name) {
        return ZoneManager.getInstance().getAllZones().stream()
                .filter(zone -> zone.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}