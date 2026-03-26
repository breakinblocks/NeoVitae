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
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.structure.NVMultiblock;
import com.breakinblocks.neovitae.common.structure.MultiblockValidator;
import com.breakinblocks.neovitae.impl.AltarRuneRegistryImpl;

import java.util.*;

public class AltarUtil {

    public static BlockPos findAltar(Level level, BlockPos pos, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos testPos = pos.offset(x, y, z);
                    BlockState testState = level.getBlockState(testPos);
                    if (testState.is(NVBlocks.ARA_VITAE.block())) {
                        return testPos;
                    }
                }
            }
        }
        return null;
    }

    public static DamageSource sacrificeDamage(Player causer) {
        DamageSources sources = causer.level().damageSources();
        return sources.source(NVDamageSources.SELF_SACRIFICE, causer);
    }

    public static int getTier(Level level, BlockPos altarPos) {
        int tier = -1;
        for (int i = 0; i < NVMultiblock.TIER_VALIDATORS.length; i++) {
            MultiblockValidator validator = NVMultiblock.TIER_VALIDATORS[i];
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
     * Scans the altar structure for all runes and returns both aggregated counts
     * and individual rune instances, enabling addon mods with dynamic runes to
     * inspect rune state without re-scanning.
     */
    public static AltarScanResult scanForRunes(int tier, Level level, BlockPos altarPos) {
        Map<IAltarRuneType, Integer> upgrades = new HashMap<>();
        List<RuneInstance> instances = new ArrayList<>();

        // Tier -1 means no valid structure, so no upgrades
        if (tier < 0 || tier >= NVMultiblock.TIER_LIST.length) {
            NeoVitae.LOGGER.warn("scanForRunes: Invalid tier {} (TIER_LIST.length={})", tier, NVMultiblock.TIER_LIST.length);
            return AltarScanResult.empty();
        }

        AltarRuneRegistryImpl registry = AltarRuneRegistryImpl.INSTANCE;
        NeoVitae.LOGGER.info("scanForRunes: Scanning tier {} with {} components",
                tier, NVMultiblock.TIER_LIST[tier].components().size());

        int upgradeCount = 0;
        int foundRunes = 0;
        for (AltarComponent component : NVMultiblock.TIER_LIST[tier].components()) {
            if (component.isUpgrade()) {
                upgradeCount++;
                BlockPos runePos = altarPos.offset(component.pos());
                BlockState state = level.getBlockState(runePos);
                Block block = state.getBlock();

                Map<IAltarRuneType, Integer> blockRunes = registry.getRunesForBlock(block);
                if (!blockRunes.isEmpty()) {
                    foundRunes++;
                    for (Map.Entry<IAltarRuneType, Integer> entry : blockRunes.entrySet()) {
                        NeoVitae.LOGGER.debug("scanForRunes: Block {} contributes {} {} runes",
                                block, entry.getValue(), entry.getKey().getSerializedName());
                        upgrades.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }

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

        for (Map.Entry<IAltarRuneType, Integer> entry : upgrades.entrySet()) {
            NeoVitae.LOGGER.info("scanForRunes: Total {} = {} (amounts accumulated)",
                    entry.getKey().getSerializedName(), entry.getValue());
        }

        return new AltarScanResult(upgrades, instances);
    }

    public static int calculateSelfSacrificeLP(Player player, int healthSacrificed) {
        double conversion = NeoVitae.SERVER_CONFIG.SELF_SACRIFICE_CONVERSION.get();
        AttributeInstance attribute = player.getAttribute(NVAttributes.SELF_SACRIFICE_MULTIPLIER);
        double multiplier = attribute != null ? attribute.getValue() : 1.0;
        double bonusSelfSacrifice = player.getAttributeValue(NVAttributes.BONUS_SELF_SACRIFICE);
        double bonus = bonusSelfSacrifice > 0 ? (1 + bonusSelfSacrifice / 100) : 1;
        return (int) (healthSacrificed * conversion * multiplier * bonus);
    }

    public static int calculateSelfSacrificeLP(Player player, int healthSacrificed, double incenseBonus) {
        double conversion = NeoVitae.SERVER_CONFIG.SELF_SACRIFICE_CONVERSION.get();
        conversion *= (1 + incenseBonus);
        AttributeInstance attribute = player.getAttribute(NVAttributes.SELF_SACRIFICE_MULTIPLIER);
        double multiplier = attribute != null ? attribute.getValue() : 1.0;
        double bonusSelfSacrifice = player.getAttributeValue(NVAttributes.BONUS_SELF_SACRIFICE);
        double bonus = bonusSelfSacrifice > 0 ? (1 + bonusSelfSacrifice / 100) : 1;
        return (int) (healthSacrificed * conversion * multiplier * bonus);
    }
}
