package com.breakinblocks.neovitae.ritual.harvest;

public class NVHarvestHandlers {

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        HarvestRegistry.registerHandler(new HarvestHandlerPlantable());
        HarvestRegistry.registerHandler(new HarvestHandlerNetherWart());
        HarvestRegistry.registerHandler(new HarvestHandlerBerryBush());
        HarvestRegistry.registerHandler(new HarvestHandlerTall());
        HarvestRegistry.registerHandler(new HarvestHandlerStem());
        HarvestRegistry.registerHandler(new HarvestHandlerGrowingPlant());
        HarvestRegistry.registerHandler(new HarvestHandlerVines());
    }
}
