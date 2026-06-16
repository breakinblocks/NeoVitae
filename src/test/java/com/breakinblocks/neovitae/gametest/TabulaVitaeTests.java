package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

import java.util.List;

public final class TabulaVitaeTests {

    private TabulaVitaeTests() {}

    private static TabulaVitaeBlockEntity placeTable(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.TABULA_VITAE.block().get().defaultBlockState());
        TabulaVitaeBlockEntity table = helper.getBlockEntity(pos, TabulaVitaeBlockEntity.class);
        if (table == null) {
            helper.fail("Expected TabulaVitaeBlockEntity at " + pos);
        }
        return table;
    }

    public static void register(NVTestRegistrar r) {
        r.add("tabula_vitae/places_and_initializes", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (table == null) return;
                if (table.burnTime != 0) {
                    helper.fail("Fresh table should have 0 burn time, got " + table.burnTime);
                }
                if (table.isSlave()) {
                    helper.fail("Standalone table should not be slave");
                }
                helper.succeed();
            });
        });

        r.add("tabula_vitae/does_not_craft_without_orb", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (table == null) return;

                table.inv.setStackInSlot(0, new ItemStack(Items.SUGAR));
                table.inv.setStackInSlot(1, new ItemStack(Items.WATER_BUCKET));

                helper.runAfterDelay(40, () -> {
                    ItemStack output = table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT);
                    if (!output.isEmpty()) {
                        helper.fail("Table should not craft without orb, got " + output);
                    }
                    if (table.burnTime > 0) {
                        helper.fail("Table should not progress without valid recipe/orb");
                    }
                    helper.succeed();
                });
            });
        });

        r.add("tabula_vitae/does_not_craft_with_invalid_recipe", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (table == null) return;

                table.inv.setStackInSlot(0, new ItemStack(Items.DIRT));
                table.inv.setStackInSlot(1, new ItemStack(Items.COBBLESTONE));
                table.inv.setStackInSlot(2, new ItemStack(Items.GRAVEL));

                helper.runAfterDelay(40, () -> {
                    if (table.burnTime > 0) {
                        helper.fail("Table should not progress with invalid recipe, burnTime=" + table.burnTime);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("tabula_vitae/flask_lengthening_no_crash", 200, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (table == null) return;

                ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
                EffectHolder regen = EffectHolder.create(MobEffects.REGENERATION, 900, 0);
                ItemAlchemyFlask.setFlaskEffects(flask, new FlaskEffects(List.of(regen)));

                table.inv.setStackInSlot(0, flask);
                table.inv.setStackInSlot(1, new ItemStack(NVItems.MUNDANE_LENGTHENING_CATALYST.get()));
                table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, new ItemStack(NVItems.ORB_APPRENTICE.get()));

                helper.runAfterDelay(160, helper::succeed);
            });
        });

        r.add("tabula_vitae/rejects_non_orb_in_orb_slot", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (table == null) return;
                if (table.inv.isItemValid(TabulaVitaeBlockEntity.ORB_SLOT, new ItemStack(Items.DIAMOND))) {
                    helper.fail("Orb slot should reject non-orb items");
                }
                helper.succeed();
            });
        });

        r.add("tabula_vitae/rejects_items_in_output_slot", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(1, () -> {
                if (table == null) return;
                if (table.inv.isItemValid(TabulaVitaeBlockEntity.OUTPUT_SLOT, new ItemStack(Items.DIAMOND))) {
                    helper.fail("Output slot should reject items");
                }
                helper.succeed();
            });
        });
    }
}
