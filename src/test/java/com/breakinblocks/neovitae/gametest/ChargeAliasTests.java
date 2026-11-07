package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.BlockDeforesterCharge;

import java.util.Map;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class ChargeAliasTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void removedChargesResolveToTheirReplacements(GameTestHelper helper) {
        Map<String, Block> aliases = Map.of(
                "shaped_charge_deep", NVBlocks.AUG_SHAPED_CHARGE.block().get(),
                "deforester_charge_2", NVBlocks.DEFORESTER_CHARGE.block().get(),
                "veinmine_charge_2", NVBlocks.VEINMINE_CHARGE.block().get(),
                "fungal_charge_2", NVBlocks.FUNGAL_CHARGE.block().get());
        for (Map.Entry<String, Block> alias : aliases.entrySet()) {
            Block block = BuiltInRegistries.BLOCK.get(NeoVitae.rl(alias.getKey()));
            Item item = BuiltInRegistries.ITEM.get(NeoVitae.rl(alias.getKey()));
            helper.assertTrue(block == alias.getValue(), alias.getKey() + " should resolve to " + alias.getValue() + ", got " + block);
            helper.assertTrue(item == alias.getValue().asItem(), alias.getKey() + " item should resolve to its replacement, got " + item);
        }
        BlockDeforesterCharge deforester = (BlockDeforesterCharge) NVBlocks.DEFORESTER_CHARGE.block().get();
        helper.assertTrue(deforester.getMaxLogs() == 256, "The Deforester Charge should fell 256 logs, fells " + deforester.getMaxLogs());
        helper.succeed();
    }
}
