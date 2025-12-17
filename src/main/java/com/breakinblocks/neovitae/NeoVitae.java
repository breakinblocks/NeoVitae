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
import com.breakinblocks.neovitae.common.menu.BMMenus;
import com.breakinblocks.neovitae.common.entity.BMEntities;
import com.breakinblocks.neovitae.common.attribute.BMAttributes;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.command.BMCommands;
import com.breakinblocks.neovitae.common.creativetab.BMTabs;
import com.breakinblocks.neovitae.common.dataattachment.BMDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;
import com.breakinblocks.neovitae.common.fluid.BMFluids;
import com.breakinblocks.neovitae.common.BMSounds;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.item.BMMaterialsAndTiers;
import com.breakinblocks.neovitae.common.crafting.BMIngredientTypes;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;
import com.breakinblocks.neovitae.common.registry.BMRegistries;
import com.breakinblocks.neovitae.common.loot.BMLootFunctions;
import com.breakinblocks.neovitae.common.structure.BMMultiblock;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.breakinblocks.neovitae.ritual.harvest.BMHarvestHandlers;
import com.breakinblocks.neovitae.common.network.BMPayloads;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;
import com.breakinblocks.neovitae.compat.patchouli.RegisterPatchouliMultiblocks;
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
        BMRegistries.register(modBus);
        BMDataComponents.register(modBus);
        BMFluids.register(modBus);
        BMSounds.register(modBus);
        BMBlocks.register(modBus);
        DungeonBlocks.register(modBus);
        BMTiles.register(modBus);
        BMEntities.register(modBus);
        BMMaterialsAndTiers.register(modBus);
        BMItems.register(modBus);
        modBus.addListener(BMDataMaps::register);
        BMDataAttachments.register(modBus);
        BMAttributes.register(modBus);
        BMMobEffects.register(modBus);
        BMRecipes.register(modBus);
        BMIngredientTypes.register(modBus);
        BMLootFunctions.register(modBus);
        RitualRegistry.register(modBus);
        BMMultiblock.register(NeoForge.EVENT_BUS);
        BMMenus.register(modBus);
        BMTabs.register(modBus);

        // Initialize Curios compatibility (if Curios is loaded)
        CuriosCompat.init(modBus);

        container.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG_SPEC);

        modBus.addListener(this::commonSetup);
        modBus.addListener(BMPayloads::register);
        NeoForge.EVENT_BUS.addListener(BMCommands::register);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NeoVitaeAPI.setInstance(NeoVitaeAPIImpl.INSTANCE);
        event.enqueueWork(BMHarvestHandlers::init);
        event.enqueueWork(AnointmentRegistrar::init);
        event.enqueueWork(ModRoomPools::init);
        event.enqueueWork(AltarRuneBlockRegistry::init);

        // Initialize Patchouli multiblock registration if Patchouli is loaded
        if (ModList.get().isLoaded("patchouli")) {
            event.enqueueWork(RegisterPatchouliMultiblocks::new);
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
