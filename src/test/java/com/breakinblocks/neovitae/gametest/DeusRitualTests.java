package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.item.ItemRitualDiviner;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DeusRitualTests {

    private static final Map<String, String> DEUS_TO_BASE = Map.of(
            "deus_well_of_suffering", "well_of_suffering",
            "deus_torment_nexus", "torment_nexus",
            "deus_magnetism", "magnetism",
            "deus_crystallum_fractura", "crystallum_fractura",
            "deus_green_grove", "green_grove",
            "deus_placer", "placer",
            "deus_felling", "felling",
            "deus_meteor", "meteor",
            "deus_armour_evolve", "armour_evolve");

    private DeusRitualTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("deus/layouts_add_a_crown_of_deus_stones", 20, helper -> {
            for (Map.Entry<String, String> pair : DEUS_TO_BASE.entrySet()) {
                Ritual deus = RitualRegistry.getRitual(pair.getKey());
                Ritual base = RitualRegistry.getRitual(pair.getValue());
                if (deus == null || base == null) {
                    helper.fail("Missing ritual " + pair.getKey() + " or " + pair.getValue());
                    return;
                }
                List<RitualComponent> deusLayout = layout(deus);
                List<RitualComponent> baseLayout = layout(base);
                if (!deusLayout.containsAll(baseLayout)) {
                    helper.fail(pair.getKey() + " should contain every stone of " + pair.getValue());
                    return;
                }
                long deusStones = deusLayout.stream().filter(c -> c.runeType() == EnumRuneType.DEUS).count();
                if (deusStones != 16 || deusLayout.size() != baseLayout.size() + 16) {
                    helper.fail(pair.getKey() + " should add exactly 16 Deus stones, has " + deusStones
                            + " of " + deusLayout.size() + " total vs " + baseLayout.size());
                    return;
                }
            }
            helper.succeed();
        });

        r.add("deus/rituals_need_divinus_crystal", 20, helper -> {
            for (String name : DEUS_TO_BASE.keySet()) {
                Ritual deus = RitualRegistry.getRitual(name);
                if (deus.getCrystalLevel() != 2) {
                    helper.fail(name + " should need crystal level 2, needs " + deus.getCrystalLevel());
                    return;
                }
            }
            helper.succeed();
        });

        r.add("deus/only_deus_diviner_places_deus_stones", 20, helper -> {
            ItemStack tenebrae = new ItemStack(NVItems.RITUAL_DIVINER_TENEBRAE.get());
            ItemStack deus = new ItemStack(NVItems.RITUAL_DIVINER_DEUS.get());
            boolean tenebraeCan = ((ItemRitualDiviner) tenebrae.getItem()).canPlaceRitualStone(EnumRuneType.DEUS, tenebrae);
            boolean deusCan = ((ItemRitualDiviner) deus.getItem()).canPlaceRitualStone(EnumRuneType.DEUS, deus);
            helper.assertTrue(!tenebraeCan, "The Tenebrae diviner should not place Deus stones");
            helper.assertTrue(deusCan, "The Deus diviner should place Deus stones");
            helper.succeed();
        });

        r.add("deus/ores_only_meteor_layer_has_no_filler", 40, helper -> {
            BlockPos center = new BlockPos(2, 2, 2);
            MeteorLayer layer = new MeteorLayer(2, 0, Blocks.COBBLESTONE)
                    .addWeightedBlock(Blocks.DIAMOND_ORE, 100)
                    .setMinWeight(1000);
            layer.buildLayer(helper.getLevel(), helper.absolutePos(center), -1, true);

            int ores = 0;
            for (BlockPos rel : BlockPos.betweenClosed(center.offset(-2, -2, -2), center.offset(2, 2, 2))) {
                if (helper.getBlockState(rel).is(Blocks.COBBLESTONE)) {
                    helper.fail("Ores-only meteor placed filler at " + rel);
                    return;
                }
                if (helper.getBlockState(rel).is(Blocks.DIAMOND_ORE)) ores++;
            }
            helper.assertTrue(ores > 0, "Ores-only meteor layer should place ore");
            helper.succeed();
        });
    }

    private static List<RitualComponent> layout(Ritual ritual) {
        List<RitualComponent> components = new ArrayList<>();
        ritual.gatherComponents(components::add);
        return components;
    }
}
