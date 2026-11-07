package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.types.RitualDeusArmourEvolve;

import java.util.List;

public final class HellforgedHelper {

    public enum Readiness { READY, NOT_HELLFORGEABLE, ALREADY_HELLFORGED, NEEDS_DEUS_EVOLUTION }

    private HellforgedHelper() {}

    public static boolean isHellforged(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.HELLFORGED, false);
    }

    public static int maxUpgradePoints() {
        return RitualDeusArmourEvolve.DEUS_MAX_UPGRADE_POINTS + NeoVitae.SERVER_CONFIG.HELLFORGED_UPGRADE_POINTS.get();
    }

    public static Readiness readiness(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(NVTags.Items.HELLFORGEABLE)) return Readiness.NOT_HELLFORGEABLE;
        if (isHellforged(stack)) return Readiness.ALREADY_HELLFORGED;
        if (stack.is(NVItems.SENTIENT_PLATE.get())) {
            int points = stack.getOrDefault(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS,
                    NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get());
            if (points < RitualDeusArmourEvolve.DEUS_MAX_UPGRADE_POINTS) return Readiness.NEEDS_DEUS_EVOLUTION;
        }
        return Readiness.READY;
    }

    public static void apply(ItemStack stack, HolderLookup.Provider registries) {
        stack.set(NVDataComponents.HELLFORGED, true);
        if (NeoVitae.SERVER_CONFIG.HELLFORGED_FIRE_RESISTANT.get()) {
            stack.set(DataComponents.DAMAGE_RESISTANT,
                    new DamageResistant(registries.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypeTags.IS_FIRE)));
        }
        if (stack.is(NVItems.SENTIENT_PLATE.get())) {
            int points = stack.getOrDefault(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS,
                    NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get());
            stack.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, Math.max(points, maxUpgradePoints()));
        }
        if (NeoVitae.SERVER_CONFIG.HELLFORGED_NETHERITE_FLOOR.get() && stack.getItem() instanceof ISentientTool
                && !(stack.getItem() instanceof LexVitaeItem)) {
            applyNetheriteFloor(stack, registries.lookupOrThrow(Registries.BLOCK));
        }
    }

    private static void applyNetheriteFloor(ItemStack stack, HolderGetter<Block> blocks) {
        if (stack.getItem() instanceof SentientPickaxeItem) {
            stack.set(DataComponents.TOOL, netheriteTool(blocks, BlockTags.MINEABLE_WITH_PICKAXE));
        } else if (stack.getItem() instanceof SentientAxeItem) {
            stack.set(DataComponents.TOOL, netheriteTool(blocks, BlockTags.MINEABLE_WITH_AXE));
        } else if (stack.getItem() instanceof SentientShovelItem) {
            stack.set(DataComponents.TOOL, netheriteTool(blocks, BlockTags.MINEABLE_WITH_SHOVEL));
        }
        int netheriteUses = ToolMaterial.NETHERITE.durability();
        if (stack.getOrDefault(DataComponents.MAX_DAMAGE, 0) < netheriteUses) {
            stack.set(DataComponents.MAX_DAMAGE, netheriteUses);
        }
    }

    private static Tool netheriteTool(HolderGetter<Block> blocks, TagKey<Block> mineable) {
        return new Tool(List.of(
                Tool.Rule.deniesDrops(blocks.getOrThrow(ToolMaterial.NETHERITE.incorrectBlocksForDrops())),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(mineable), ToolMaterial.NETHERITE.speed())
        ), 1.0F, 1, true);
    }

    public static boolean hasNetheriteFloor(ItemStack stack) {
        return isHellforged(stack) && NeoVitae.SERVER_CONFIG.HELLFORGED_NETHERITE_FLOOR.get()
                && stack.getItem() instanceof ISentientTool && !(stack.getItem() instanceof LexVitaeItem);
    }

    public static double drainMultiplier(ItemStack stack) {
        return isHellforged(stack) ? NeoVitae.SERVER_CONFIG.HELLFORGED_DRAIN_MULTIPLIER.get() : 1.0;
    }

    public static double overchargeProgress(double souls) {
        double softCap = NeoVitae.SERVER_CONFIG.SENTIENT_SPIRITUS_SOFT_CAP.get();
        double max = NeoVitae.SERVER_CONFIG.SENTIENT_SPIRITUS_MAX.get();
        if (souls <= softCap || max <= softCap) return 0;
        double progress = Math.log(souls / softCap) / Math.log(max / softCap);
        return Math.min(1.0, progress);
    }

    public static double overchargeFactor(double souls) {
        return 1.0 + overchargeProgress(souls) * NeoVitae.SERVER_CONFIG.SENTIENT_OVERCHARGE_BONUS.get();
    }

    public static double overchargeFactor(ItemStack stack, double souls) {
        return isHellforged(stack) ? overchargeFactor(souls) : 1.0;
    }

    public static double drainFactor(ItemStack stack, double souls) {
        return NeoVitae.SERVER_CONFIG.SENTIENT_OVERCHARGE_DRAIN.get() ? overchargeFactor(stack, souls) : 1.0;
    }
}
