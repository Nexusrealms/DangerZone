package de.nexusrealms.dangerzone.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nexusrealms.dangerzone.network.EnterZonePacket;
import de.nexusrealms.dangerzone.network.ExitZonePacket;
import de.nexusrealms.dangerzone.zone.Zone;
import de.nexusrealms.dangerzone.zone.ZoneComponent;
import de.nexusrealms.dangerzone.zone.ZoneType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.registry.entry.RegistryEntry;
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
                                .then(CommandManager.argument("type", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, ZoneType.KEY))
                                        .executes(ZoneCommands::createZone))))

                // Add current chunk to a zone: /zone add <name>
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(ZoneCommands::addChunkToZone)))

                // Remove current chunk from a zone: /zone remove <name>
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(ZoneCommands::removeChunkFromZone)))

                // Enter zone: /zone enter
                .then(CommandManager.literal("enter")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .requires(ServerCommandSource::isExecutedByPlayer)
                                .executes(ZoneCommands::enterZone)))

                // Enter zone: /zone enter
                .then(CommandManager.literal("exit")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .requires(ServerCommandSource::isExecutedByPlayer)
                                .executes(ZoneCommands::exitZone)))

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
        RegistryEntry.Reference<ZoneType> type = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "type", ZoneType.KEY);
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);
        if(component.findZoneByName(name).isPresent()){
            source.sendFeedback(() -> Text.literal("§cA zone with this name already exists"), false);
            return 0;
        }

        // Create the zone
        component.createZone(name, type.value());
        source.sendFeedback(() -> Text.literal("§aCreated new " + type.registryKey().getValue() + " zone: " + name), true);
        return 1;
    }

    private static int addChunkToZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);

        // Find the zone
        Optional<Zone> targetZone = component.findZoneByName(name);
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
        zone.addChunk(chunkPos);
        ZoneComponent.KEY.sync(context.getSource().getWorld());
        source.sendFeedback(() -> Text.literal("§aAdded chunk " + chunkPos.x + ", " + chunkPos.z + " to zone: " + name), true);
        return 1;
    }
    private static int enterZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);

        // Find the zone
        Optional<Zone> targetZone = component.findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }

        Zone zone = targetZone.get();
        zone.onPlayerEnter(context.getSource().getPlayer());
        ServerPlayNetworking.send(context.getSource().getPlayer(), new EnterZonePacket(zone.getId()));
        source.sendFeedback(() -> Text.literal("§aEntered zone " + name), true);
        return 1;
    }
    private static int exitZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);

        // Find the zone
        Optional<Zone> targetZone = component.findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }

        Zone zone = targetZone.get();
        zone.onPlayerExit(context.getSource().getPlayer());
        ServerPlayNetworking.send(context.getSource().getPlayer(), new ExitZonePacket(zone.getId()));
        source.sendFeedback(() -> Text.literal("§cExited zone " + name), true);
        return 1;
    }
    private static int removeChunkFromZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);

        // Find the zone
        Optional<Zone> targetZone = component.findZoneByName(name);
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
        zone.removeChunk(chunkPos);
        ZoneComponent.KEY.sync(context.getSource().getWorld());

        source.sendFeedback(() -> Text.literal("§aRemoved chunk " + chunkPos.x + ", " + chunkPos.z + " from zone: " + name), true);
        return 1;
    }

    private static int listZones(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);

        // Get all zones
        java.util.Collection<Zone> zones = component.getAllZones();

        if (zones.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§eNo zones have been created yet"), false);
            return 0;
        }

        // Display all zones
        source.sendFeedback(() -> Text.literal("§6===== Danger Zones ====="), false);
        for (Zone zone : zones) {
            source.sendFeedback(() -> Text.literal("§e- " + zone.getName() + " (" + zone.getType().toString() + ") - "
                    + zone.getChunks().size() + " chunks"), false);
        }

        return zones.size();
    }

    private static int deleteZone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);

        // Find the zone
        Optional<Zone> targetZone = component.findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }

        // Remove the zone
        Zone zone = targetZone.get();
        component.removeZone(zone.getId());
        source.sendFeedback(() -> Text.literal("§aDeleted zone: " + name), true);
        return 1;
    }

    private static int zoneInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");
        ZoneComponent component = source.getWorld().getComponent(ZoneComponent.KEY);

        // Find the zone
        Optional<Zone> targetZone = component.findZoneByName(name);
        if (targetZone.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§cZone not found: " + name), false);
            return 0;
        }

        // Display zone information
        Zone zone = targetZone.get();
        source.sendFeedback(() -> Text.literal("§6===== Zone: " + zone.getName() + " ====="), false);
        source.sendFeedback(() -> Text.literal("§eID: " + zone.getId()), false);
        source.sendFeedback(() -> Text.literal("§eType: " + zone.getType().toString() + " - " + zone.getType().description()), false);
        source.sendFeedback(() -> Text.literal("§eChunks: " + zone.getChunks().size()), false);

        return 1;
    }
}