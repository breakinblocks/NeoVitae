package com.breakinblocks.neovitae.ritual.harvest;

/**
 * Initializes all Blood Magic harvest handlers.
 */
public class BMHarvestHandlers {

    private static boolean initialized = false;

    /**
     * Initialize all harvest handlers. This should be called during FMLCommonSetupEvent.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        // Register all default harvest handlers
        HarvestRegistry.registerHandler(new HarvestHandlerPlantable());
        HarvestRegistry.registerHandler(new HarvestHandlerNetherWart());
        HarvestRegistry.registerHandler(new HarvestHandlerBerryBush());
        HarvestRegistry.registerHandler(new HarvestHandlerTall());
        HarvestRegistry.registerHandler(new HarvestHandlerStem());
        HarvestRegistry.registerHandler(new HarvestHandlerGrowingPlant());
        HarvestRegistry.registerHandler(new HarvestHandlerVines());
    }
}
