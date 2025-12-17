package com.breakinblocks.neovitae.impl;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.altar.rune.EnumAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.common.block.BMBlocks;

/**
 * Registers Blood Magic's built-in rune blocks with the unified rune registry.
 *
 * <p>This class is called during mod common setup to register all altar rune
 * blocks with their corresponding rune types and amounts.</p>
 */
public final class AltarRuneBlockRegistry {

    private AltarRuneBlockRegistry() {
        // Utility class
    }

    /**
     * Registers all built-in rune blocks with the rune registry.
     * Should be called during FMLCommonSetupEvent via enqueueWork.
     */
    public static void init() {
        IAltarRuneRegistry registry = AltarRuneRegistryImpl.INSTANCE;

        NeoVitae.LOGGER.info("Registering built-in altar rune blocks...");

        // Tier 1 Runes (amount = 1)
        registerTier1Runes(registry);

        // Tier 2 Runes (amount = 2)
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
        // Speed Rune - increases LP consumption rate during crafting
        registry.registerRuneBlock(BMBlocks.RUNE_SPEED.block().get(), EnumAltarRuneType.SPEED, 1);

        // Sacrifice Rune - increases LP gained from mob sacrifice
        registry.registerRuneBlock(BMBlocks.RUNE_SACRIFICE.block().get(), EnumAltarRuneType.SACRIFICE, 1);

        // Self-Sacrifice Rune - increases LP gained from player self-sacrifice
        registry.registerRuneBlock(BMBlocks.RUNE_SELF_SACRIFICE.block().get(), EnumAltarRuneType.SELF_SACRIFICE, 1);

        // Displacement Rune - increases fluid I/O rate for piping
        registry.registerRuneBlock(BMBlocks.RUNE_DISLOCATION.block().get(), EnumAltarRuneType.DISPLACEMENT, 1);

        // Capacity Rune - increases altar blood capacity (additive)
        registry.registerRuneBlock(BMBlocks.RUNE_CAPACITY.block().get(), EnumAltarRuneType.CAPACITY, 1);

        // Augmented Capacity Rune - increases altar blood capacity (multiplicative)
        registry.registerRuneBlock(BMBlocks.RUNE_CAPACITY_AUGMENTED.block().get(), EnumAltarRuneType.AUGMENTED_CAPACITY, 1);

        // Orb Rune - increases soul network capacity bonus when filling orbs
        registry.registerRuneBlock(BMBlocks.RUNE_ORB.block().get(), EnumAltarRuneType.ORB, 1);

        // Acceleration Rune - reduces ticks between altar operations
        registry.registerRuneBlock(BMBlocks.RUNE_ACCELERATION.block().get(), EnumAltarRuneType.ACCELERATION, 1);

        // Charging Rune - enables pre-charging LP for instant crafting
        registry.registerRuneBlock(BMBlocks.RUNE_CHARGING.block().get(), EnumAltarRuneType.CHARGING, 1);

        // Efficiency Rune - reduces LP loss when altar runs out mid-craft
        registry.registerRuneBlock(BMBlocks.RUNE_EFFICIENCY.block().get(), EnumAltarRuneType.EFFICIENCY, 1);
    }

    private static void registerTier2Runes(IAltarRuneRegistry registry) {
        // Tier 2 runes provide double the effect (amount = 2)

        registry.registerRuneBlock(BMBlocks.RUNE_2_SPEED.block().get(), EnumAltarRuneType.SPEED, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_SACRIFICE.block().get(), EnumAltarRuneType.SACRIFICE, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_SELF_SACRIFICE.block().get(), EnumAltarRuneType.SELF_SACRIFICE, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_DISLOCATION.block().get(), EnumAltarRuneType.DISPLACEMENT, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_CAPACITY.block().get(), EnumAltarRuneType.CAPACITY, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_CAPACITY_AUGMENTED.block().get(), EnumAltarRuneType.AUGMENTED_CAPACITY, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_ORB.block().get(), EnumAltarRuneType.ORB, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_ACCELERATION.block().get(), EnumAltarRuneType.ACCELERATION, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_CHARGING.block().get(), EnumAltarRuneType.CHARGING, 2);
        registry.registerRuneBlock(BMBlocks.RUNE_2_EFFICIENCY.block().get(), EnumAltarRuneType.EFFICIENCY, 2);
    }
}
