package com.breakinblocks.neovitae.common.registry;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public final class NVLegacyNames {

    private static final Map<String, String> RITUAL_STONES = Map.of(
            "dusk_ritual_stone", "tenebrae_ritual_stone",
            "dawn_ritual_stone", "deus_ritual_stone");

    private static final Map<String, String> TOOLS = Map.of(
            "dusk_scribe_tool", "tenebrae_scribe_tool",
            "ritual_diviner_dusk", "ritual_diviner_tenebrae");

    private static final Map<String, String> CHARGES = Map.of(
            "shaped_charge_deep", "aug_shaped_charge",
            "deforester_charge_2", "deforester_charge",
            "veinmine_charge_2", "veinmine_charge",
            "fungal_charge_2", "fungal_charge");

    private NVLegacyNames() {
    }

    public static void register() {
        alias(NVBlocks.BLOCKS, RITUAL_STONES);
        alias(NVBlocks.BLOCK_ITEMS, RITUAL_STONES);
        alias(NVBlocks.BLOCKS, CHARGES);
        alias(NVBlocks.BLOCK_ITEMS, CHARGES);
        alias(NVItems.BASIC_ITEMS, TOOLS);
    }

    private static void alias(DeferredRegister<?> registry, Map<String, String> renames) {
        renames.forEach((from, to) -> registry.addAlias(NeoVitae.rl(from), NeoVitae.rl(to)));
    }
}
