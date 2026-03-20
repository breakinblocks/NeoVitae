package com.breakinblocks.neovitae.impl;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.altar.rune.EnumAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.common.block.NVBlocks;

/**
 * Registers NeoVitae's built-in rune blocks with the unified rune registry.
 *
 * <p>This class is called during mod common setup to register all altar rune
 * blocks with their corresponding rune types and amounts.</p>
 */
public final class AltarRuneBlockRegistry {

    private AltarRuneBlockRegistry() {}

    public static void init() {
        IAltarRuneRegistry registry = AltarRuneRegistryImpl.INSTANCE;

        NeoVitae.LOGGER.info("Registering built-in altar rune blocks...");

        registerTier1Runes(registry);
        registerTier2Runes(registry);

        NeoVitae.LOGGER.info("Registered {} built-in altar rune blocks", 20);

        // Debug: Log the registry state
        if (registry instanceof AltarRuneRegistryImpl impl) {
            var associations = impl.getAllBlockAssociations();
            NeoVitae.LOGGER.info("Rune registry state: {} block associations registered", associations.size());
            // Log first few registered blocks for verification
            int count = 0;
            for (var entry : associations.entrySet()) {
                if (count++ >= 5) {
                    NeoVitae.LOGGER.info("  ... and {} more", associations.size() - 5);
                    break;
                }
                NeoVitae.LOGGER.info("  - {} (hash={}) -> {}",
                        entry.getKey(), System.identityHashCode(entry.getKey()), entry.getValue().keySet());
            }
        }
    }

    private static void registerTier1Runes(IAltarRuneRegistry registry) {
        registry.registerRuneBlock(NVBlocks.RUNE_SPEED.block().get(), EnumAltarRuneType.SPEED, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_SACRIFICE.block().get(), EnumAltarRuneType.SACRIFICE, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_SELF_SACRIFICE.block().get(), EnumAltarRuneType.SELF_SACRIFICE, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_DISLOCATION.block().get(), EnumAltarRuneType.DISPLACEMENT, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_CAPACITY.block().get(), EnumAltarRuneType.CAPACITY, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_CAPACITY_AUGMENTED.block().get(), EnumAltarRuneType.AUGMENTED_CAPACITY, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_ORB.block().get(), EnumAltarRuneType.ORB, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_ACCELERATION.block().get(), EnumAltarRuneType.ACCELERATION, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_CHARGING.block().get(), EnumAltarRuneType.CHARGING, 1);
        registry.registerRuneBlock(NVBlocks.RUNE_EFFICIENCY.block().get(), EnumAltarRuneType.EFFICIENCY, 1);
    }

    private static void registerTier2Runes(IAltarRuneRegistry registry) {
        registry.registerRuneBlock(NVBlocks.RUNE_2_SPEED.block().get(), EnumAltarRuneType.SPEED, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_SACRIFICE.block().get(), EnumAltarRuneType.SACRIFICE, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_SELF_SACRIFICE.block().get(), EnumAltarRuneType.SELF_SACRIFICE, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_DISLOCATION.block().get(), EnumAltarRuneType.DISPLACEMENT, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_CAPACITY.block().get(), EnumAltarRuneType.CAPACITY, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_CAPACITY_AUGMENTED.block().get(), EnumAltarRuneType.AUGMENTED_CAPACITY, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_ORB.block().get(), EnumAltarRuneType.ORB, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_ACCELERATION.block().get(), EnumAltarRuneType.ACCELERATION, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_CHARGING.block().get(), EnumAltarRuneType.CHARGING, 2);
        registry.registerRuneBlock(NVBlocks.RUNE_2_EFFICIENCY.block().get(), EnumAltarRuneType.EFFICIENCY, 2);
    }
}
