package com.breakinblocks.neovitae;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import com.breakinblocks.neovitae.common.menu.NVMenus;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.command.NVCommands;
import com.breakinblocks.neovitae.common.creativetab.NVTabs;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.common.crafting.NVIngredientTypes;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.loot.NVLootFunctions;
import com.breakinblocks.neovitae.common.structure.NVMultiblock;
import com.breakinblocks.neovitae.common.structure.NVAltarBookSync;
import com.breakinblocks.neovitae.common.structure.NVRitualBookSync;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.breakinblocks.neovitae.ritual.harvest.NVHarvestHandlers;
import com.breakinblocks.neovitae.common.advancement.NVCriteriaTriggers;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;
import com.breakinblocks.neovitae.compat.ftbultimine.FTBUltimineCompat;

import com.breakinblocks.neovitae.structures.ModRoomPools;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.routing.RoutingChannelRegistry;
import com.breakinblocks.neovitae.client.render.item.SpiritusBarDecorator;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.breakinblocks.neovitae.common.routing.EnergyRoutingChannel;
import com.breakinblocks.neovitae.common.routing.FluidRoutingChannel;
import com.breakinblocks.neovitae.common.routing.ItemRoutingChannel;
import com.breakinblocks.neovitae.compat.modonomicon.NVModonomiconCompat;
import com.breakinblocks.neovitae.impl.AltarRuneBlockRegistry;
import com.breakinblocks.neovitae.impl.NeoVitaeAPIImpl;
import com.klikli_dev.modonomicon.registry.PredicateRegistry;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.lang.reflect.Method;

@Mod(NeoVitae.MODID)
public class NeoVitae {
    public static final String MODID = "neovitae";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Identifier TYPE_PROPERTY = rl("spiritus_type");
    public static final Identifier INCENSE_PROPERTY = rl("incense_type");

    public static final ServerConfig SERVER_CONFIG;
    private static final ModConfigSpec SERVER_CONFIG_SPEC;

    public static final ClientConfig CLIENT_CONFIG;
    private static final ModConfigSpec CLIENT_CONFIG_SPEC;

    static {
        Pair<ServerConfig, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = serverPair.getLeft();
        SERVER_CONFIG_SPEC = serverPair.getRight();

        Pair<ClientConfig, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
        CLIENT_CONFIG_SPEC = clientPair.getRight();
    }

    public NeoVitae(IEventBus modBus, ModContainer container) {
        RoutingChannelRegistry.register(new ItemRoutingChannel());
        RoutingChannelRegistry.register(new FluidRoutingChannel());
        RoutingChannelRegistry.register(new EnergyRoutingChannel());

        MaterialRegistry.register(modBus);
        NVRegistries.register(modBus);
        NVDataComponents.register(modBus);
        NVFluids.register(modBus);
        NVSounds.register(modBus);
        NVBlocks.register(modBus);
        DungeonBlocks.register(modBus);
        NVTiles.register(modBus);
        NVEntities.register(modBus);
        NVParticles.register(modBus);
        NVMaterialsAndTiers.register(modBus);
        NVItems.register(modBus);
        modBus.addListener(NVDataMaps::register);
        NVDataAttachments.register(modBus);
        NVAttributes.register(modBus);
        NVMobEffects.register(modBus);
        NVRecipes.register(modBus);
        NVIngredientTypes.register(modBus);
        NVLootFunctions.register(modBus);
        RitualRegistry.register(modBus);
        NVCriteriaTriggers.register(modBus);
        NVMultiblock.register(NeoForge.EVENT_BUS);
        if (ModList.get().isLoaded("modonomicon")) {
            NVAltarBookSync.register(NeoForge.EVENT_BUS);
            NVRitualBookSync.register(NeoForge.EVENT_BUS);
        }
        NVMenus.register(modBus);
        NVTabs.register(modBus);

        CuriosCompat.init(modBus);

        container.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC);

        modBus.addListener(this::commonSetup);
        modBus.addListener(NVPayloads::register);
        if (FMLLoader.getCurrentOrNull() != null && FMLLoader.getCurrentOrNull().getDist() == Dist.CLIENT) {
            modBus.addListener(SpiritusBarDecorator::registerAll);
        }
        NeoForge.EVENT_BUS.addListener(NVCommands::register);

        wireGameTests(modBus);
    }

    private static void wireGameTests(IEventBus modBus) {
        try {
            Class<?> setup = Class.forName("com.breakinblocks.neovitae.gametest.NVGameTestSetup");
            setup.getMethod("register", IEventBus.class).invoke(null, modBus);

            Class<?> reg = Class.forName("com.breakinblocks.neovitae.gametest.NVGameTestRegistration");
            Method handler = reg.getMethod("registerTests",
                    RegisterGameTestsEvent.class);
            modBus.addListener(RegisterGameTestsEvent.class, event -> {
                try {
                    handler.invoke(null, event);
                } catch (ReflectiveOperationException t) {
                    LOGGER.error("Failed to invoke NVGameTestRegistration.registerTests", t);
                }
            });
        } catch (ClassNotFoundException expected) {
            // Test source set not on classpath — production build
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Failed to wire gametest hooks", e);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NeoVitaeAPI.setInstance(NeoVitaeAPIImpl.INSTANCE);
        event.enqueueWork(NVHarvestHandlers::init);
        event.enqueueWork(AnointmentRegistrar::init);
        event.enqueueWork(ModRoomPools::init);
        event.enqueueWork(AltarRuneBlockRegistry::init);
        event.enqueueWork(FTBUltimineCompat::init);

        if (ModList.get().isLoaded("modonomicon")) {
            event.enqueueWork(() -> {
                PredicateRegistry.register(
                        rl("non_air_solid"),
                        (blockGetter, blockPos, blockState) ->
                                !blockState.isAir() && blockState.getFluidState().isEmpty()
                );
                NVModonomiconCompat.registerPageLoaders();
            });
        }
    }

    public static Identifier rl(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
