package com.breakinblocks.neovitae.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.RuneInstance;
import com.breakinblocks.neovitae.common.attribute.BMAttributes;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.damagesource.BMDamageSources;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.structure.BMMultiblock;
import com.breakinblocks.neovitae.common.structure.MultiblockValidator;
import com.breakinblocks.neovitae.impl.AltarRuneRegistryImpl;

import java.util.*;

/**
 * Utility methods for Blood Altar operations.
 */
public class AltarUtil {

    /**
     * Finds a Blood Altar within a radius of a position.
     *
     * @param level The world level
     * @param pos The center position to search from
     * @param radius The search radius
     * @return The altar position, or null if not found
     */
    public static BlockPos findAltar(Level level, BlockPos pos, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos testPos = pos.offset(x, y, z);
                    BlockState testState = level.getBlockState(testPos);
                    if (testState.is(BMBlocks.BLOOD_ALTAR.block())) {
                        return testPos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Creates a self-sacrifice damage source for the given player.
     *
     * @param causer The player causing the sacrifice
     * @return The damage source
     */
    public static DamageSource sacrificeDamage(Player causer) {
        DamageSources sources = causer.level().damageSources();
        return sources.source(BMDamageSources.SELF_SACRIFICE, causer);
    }

    /**
     * Gets the tier of the altar structure at the given position.
     * Uses custom MultiblockValidator instead of Patchouli.
     *
     * @param level The world level
     * @param altarPos The position of the blood altar block
     * @return The tier (0-indexed), or -1 if no valid structure
     */
    public static int getTier(Level level, BlockPos altarPos) {
        int tier = -1;
        for (int i = 0; i < BMMultiblock.TIER_VALIDATORS.length; i++) {
            MultiblockValidator validator = BMMultiblock.TIER_VALIDATORS[i];
            if (validator != null) {
                // Pass the altar position - the validator has its own offset configured
                Rotation rot = validator.validate(level, altarPos);
                if (rot != null) {
                    tier = i;
                }
            }
        }
        return tier;
    }

    /**
     * Gets all rune upgrades from the altar structure using the unified rune registry.
     *
     * <p>This method scans the altar structure and returns a map of all rune types
     * (both built-in and custom) to their accumulated amounts.</p>
     *
     * @param tier The altar tier
     * @param level The world level
     * @param altarPos The position of the blood altar block
     * @return A map of rune types to their counts
     * @deprecated Use {@link #scanForRunes(int, Level, BlockPos)} instead for access to rune instances
     */
    @Deprecated
    public static Map<IAltarRuneType, Integer> getUpgrades(int tier, Level level, BlockPos altarPos) {
        return scanForRunes(tier, level, altarPos).runeCounts();
    }

    /**
     * Scans the altar structure for all runes and returns both aggregated counts
     * and individual rune instances.
     *
     * <p>This method provides full access to the scanned rune data, including the
     * position and block entity of each rune. This enables addon mods with dynamic
     * runes to inspect their rune state without re-scanning the structure.</p>
     *
     * @param tier The altar tier
     * @param level The world level
     * @param altarPos The position of the blood altar block
     * @return A scan result containing both rune counts and instances
     */
    public static AltarScanResult scanForRunes(int tier, Level level, BlockPos altarPos) {
        Map<IAltarRuneType, Integer> upgrades = new HashMap<>();
        List<RuneInstance> instances = new ArrayList<>();

        // Tier -1 means no valid structure, so no upgrades
        if (tier < 0 || tier >= BMMultiblock.TIER_LIST.length) {
            NeoVitae.LOGGER.warn("scanForRunes: Invalid tier {} (TIER_LIST.length={})", tier, BMMultiblock.TIER_LIST.length);
            return AltarScanResult.empty();
        }

        AltarRuneRegistryImpl registry = AltarRuneRegistryImpl.INSTANCE;
        NeoVitae.LOGGER.info("scanForRunes: Scanning tier {} with {} components",
                tier, BMMultiblock.TIER_LIST[tier].components().size());

        int upgradeCount = 0;
        int foundRunes = 0;
        for (AltarComponent component : BMMultiblock.TIER_LIST[tier].components()) {
            if (component.isUpgrade()) {
                upgradeCount++;
                BlockPos runePos = altarPos.offset(component.pos());
                BlockState state = level.getBlockState(runePos);
                Block block = state.getBlock();

                // Get all runes for this block from the unified registry
                Map<IAltarRuneType, Integer> blockRunes = registry.getRunesForBlock(block);
                if (!blockRunes.isEmpty()) {
                    foundRunes++;
                    // Log each block's rune contributions
                    for (Map.Entry<IAltarRuneType, Integer> entry : blockRunes.entrySet()) {
                        NeoVitae.LOGGER.debug("scanForRunes: Block {} contributes {} {} runes",
                                block, entry.getValue(), entry.getKey().getSerializedName());
                        upgrades.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }

                    // Create rune instance with block entity (may be null)
                    BlockEntity blockEntity = level.getBlockEntity(runePos);
                    instances.add(new RuneInstance(runePos, block, blockEntity));
                } else {
                    NeoVitae.LOGGER.warn("scanForRunes: Block {} (hash={}) at {} has no runes registered (hasRunes={})",
                            block, System.identityHashCode(block), runePos, registry.hasRunes(block));
                }
            }
        }

        NeoVitae.LOGGER.info("scanForRunes: Found {} runes out of {} upgrade positions, total rune types: {}",
                foundRunes, upgradeCount, upgrades.size());

        // Log each rune type's total count
        for (Map.Entry<IAltarRuneType, Integer> entry : upgrades.entrySet()) {
            NeoVitae.LOGGER.info("scanForRunes: Total {} = {} (amounts accumulated)",
                    entry.getKey().getSerializedName(), entry.getValue());
        }

        return new AltarScanResult(upgrades, instances);
    }

    /**
     * Calculates LP gained from self-sacrifice, applying the base conversion rate
     * and the player's self-sacrifice multiplier attribute.
     *
     * @param player The player performing the sacrifice
     * @param healthSacrificed The amount of health sacrificed (in half-hearts)
     * @return The amount of LP to add to the altar
     */
    public static int calculateSelfSacrificeLP(Player player, int healthSacrificed) {
        double conversion = NeoVitae.SERVER_CONFIG.SELF_SACRIFICE_CONVERSION.get();
        AttributeInstance attribute = player.getAttribute(BMAttributes.SELF_SACRIFICE_MULTIPLIER);
        double multiplier = attribute != null ? attribute.getValue() : 1.0;
        return (int) (healthSacrificed * conversion * multiplier);
    }

    /**
     * Calculates LP gained from self-sacrifice with an additional incense bonus.
     *
     * @param player The player performing the sacrifice
     * @param healthSacrificed The amount of health sacrificed (in half-hearts)
     * @param incenseBonus The incense bonus multiplier (0.0 = no bonus, 1.0 = +100%)
     * @return The amount of LP to add to the altar
     */
    public static int calculateSelfSacrificeLP(Player player, int healthSacrificed, double incenseBonus) {
        double conversion = NeoVitae.SERVER_CONFIG.SELF_SACRIFICE_CONVERSION.get();
        conversion *= (1 + incenseBonus);
        AttributeInstance attribute = player.getAttribute(BMAttributes.SELF_SACRIFICE_MULTIPLIER);
        double multiplier = attribute != null ? attribute.getValue() : 1.0;
        return (int) (healthSacrificed * conversion * multiplier);
    }
}
