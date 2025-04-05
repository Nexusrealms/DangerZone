package de.nexusrealms.dangerzone;

import com.mojang.serialization.Codec;
import de.nexusrealms.dangerzone.command.ZoneCommands;
import de.nexusrealms.dangerzone.network.ModPackets;
import de.nexusrealms.dangerzone.zone.ZoneComponent;
import de.nexusrealms.dangerzone.zone.ZoneType;
import de.nexusrealms.dangerzone.zone.effect.ZoneEffect;
import de.nexusrealms.dangerzone.zone.effect.ZoneEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DangerZone implements ModInitializer, WorldComponentInitializer {
	public static final String MOD_ID = "dangerzone";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Identifier id(String name){
		return Identifier.of(MOD_ID, name);
	}
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing DangerZone mod...");
		ZoneEffects.init();
		ZoneType.init();
		ModPackets.init();
		// Register commands
		CommandRegistrationCallback.EVENT.register(ZoneCommands::register);
		LOGGER.info("DangerZone mod initialized!");
	}
	public static <T> Codec<Set<T>> mutableSetOf(Codec<T> codec){
		return codec.listOf().xmap(HashSet::new, List::copyOf);
	}
	public static <T> Codec<List<T>> mutableListOf(Codec<T> codec){
		return codec.listOf().xmap(ArrayList::new, Function.identity());
	}

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry worldComponentFactoryRegistry) {
		worldComponentFactoryRegistry.register(ZoneComponent.KEY, ZoneComponent::new);
	}
}