package com.breakinblocks.neovitae;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
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
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.breakinblocks.neovitae.ritual.harvest.NVHarvestHandlers;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;

import com.breakinblocks.neovitae.structures.ModRoomPools;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.impl.AltarRuneBlockRegistry;
import com.breakinblocks.neovitae.impl.NeoVitaeAPIImpl;

@Mod(NeoVitae.MODID)
public class NeoVitae {
    public static final String MODID = "neovitae";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation TYPE_PROPERTY = rl("will_type");
    public static final ResourceLocation INCENSE_PROPERTY = rl("incense_type");

    public static final ServerConfig SERVER_CONFIG;
    private static final ModConfigSpec SERVER_CONFIG_SPEC;

    static {
        Pair<ServerConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_CONFIG = pair.getLeft();
        SERVER_CONFIG_SPEC = pair.getRight();
    }

    public NeoVitae(IEventBus modBus, ModContainer container) {
        com.breakinblocks.neovitae.api.routing.RoutingChannelRegistry.register(new com.breakinblocks.neovitae.common.routing.ItemRoutingChannel());
        com.breakinblocks.neovitae.api.routing.RoutingChannelRegistry.register(new com.breakinblocks.neovitae.common.routing.FluidRoutingChannel());
        com.breakinblocks.neovitae.api.routing.RoutingChannelRegistry.register(new com.breakinblocks.neovitae.common.routing.EnergyRoutingChannel());

        try {
            Class<?> testSetup = Class.forName("com.breakinblocks.neovitae.gametest.NVGameTestSetup");
            testSetup.getMethod("register", IEventBus.class).invoke(null, modBus);
        } catch (ClassNotFoundException ignored) {
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Failed to register game test setup", e);
        }

        com.breakinblocks.neovitae.common.material.MaterialRegistry.register(modBus);
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
        NVMultiblock.register(NeoForge.EVENT_BUS);
        NVMenus.register(modBus);
        NVTabs.register(modBus);

        CuriosCompat.init(modBus);

        container.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG_SPEC);

        modBus.addListener(this::commonSetup);
        modBus.addListener(NVPayloads::register);
        NeoForge.EVENT_BUS.addListener(NVCommands::register);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NeoVitaeAPI.setInstance(NeoVitaeAPIImpl.INSTANCE);
        event.enqueueWork(NVHarvestHandlers::init);
        event.enqueueWork(AnointmentRegistrar::init);
        event.enqueueWork(ModRoomPools::init);
        event.enqueueWork(AltarRuneBlockRegistry::init);

        if (ModList.get().isLoaded("modonomicon")) {
            event.enqueueWork(() -> {
                com.klikli_dev.modonomicon.data.LoaderRegistry.registerPredicate(
                        rl("non_air_solid"),
                        (blockGetter, blockPos, blockState) ->
                                !blockState.isAir() && blockState.getFluidState().isEmpty()
                );
                com.breakinblocks.neovitae.compat.modonomicon.NVModonomiconCompat.registerPageLoaders();
            });
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
