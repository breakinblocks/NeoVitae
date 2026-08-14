package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.List;
import java.util.UUID;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class TabulaVitaeTests {

    private static final UUID DRAINED_UUID = UUID.fromString("0f4d2ab3-5e6c-4f1a-9d8b-7c2e4a6b0d31");
    private static final Binding DRAINED_BINDING = new Binding(DRAINED_UUID, "DrainedTestPlayer");

    private static TabulaVitaeBlockEntity placeTable(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.TABULA_VITAE.block().get().defaultBlockState());
        BlockEntity be = helper.getBlockEntity(pos);
        if (!(be instanceof TabulaVitaeBlockEntity table)) {
            helper.fail("Expected TabulaVitaeBlockEntity at " + pos);
            return null;
        }
        return table;
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tablePlacesAndInitializes(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableDoesNotCraftWithoutOrb(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            // Place random items but no orb
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableDoesNotCraftWithInvalidRecipe(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            // Random items that don't form any recipe
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void tableFlaskLengtheningNoCrash(GameTestHelper helper) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void tableDoesNotCraftWithUnboundOrb(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            table.inv.setStackInSlot(0, new ItemStack(Items.GRAVEL));
            table.inv.setStackInSlot(1, new ItemStack(Items.FLINT));
            table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, new ItemStack(NVItems.ORB_MAGICIAN.get()));

            helper.runAfterDelay(80, () -> {
                ItemStack output = table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    helper.fail("Table crafted " + output + " with an unbound orb");
                    return;
                }
                if (table.burnTime > 0) {
                    helper.fail("Table should not progress with an unbound orb, burnTime=" + table.burnTime);
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void tableDoesNotCraftWithEmptyNetwork(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            Anima anima = AnimaHelper.getAnima(DRAINED_UUID);
            while (anima.getCurrentEV() > 0) {
                anima.syphon(AnimaTicket.create(anima.getCurrentEV()));
            }

            ItemStack orb = new ItemStack(NVItems.ORB_MAGICIAN.get());
            orb.set(NVDataComponents.BINDING, DRAINED_BINDING);

            table.inv.setStackInSlot(0, new ItemStack(Items.GRAVEL));
            table.inv.setStackInSlot(1, new ItemStack(Items.FLINT));
            table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, orb);

            helper.runAfterDelay(80, () -> {
                ItemStack output = table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    helper.fail("Table crafted " + output + " with an empty soul network");
                    return;
                }
                if (anima.getCurrentEV() != 0) {
                    helper.fail("Table should not have moved EV, network has " + anima.getCurrentEV());
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void tableSyphonsFullRecipeCost(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            UUID uuid = UUID.fromString("0f4d2ab3-5e6c-4f1a-9d8b-7c2e4a6b0d32");
            Anima anima = AnimaHelper.getAnima(uuid);
            while (anima.getCurrentEV() > 0) {
                anima.syphon(AnimaTicket.create(anima.getCurrentEV()));
            }
            anima.add(AnimaTicket.create(10000), 100000);
            int before = anima.getCurrentEV();

            ItemStack orb = new ItemStack(NVItems.ORB_MAGICIAN.get());
            orb.set(NVDataComponents.BINDING, new Binding(uuid, "SyphonTestPlayer"));

            table.inv.setStackInSlot(0, new ItemStack(Items.GRAVEL));
            table.inv.setStackInSlot(1, new ItemStack(Items.FLINT));
            table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, orb);

            helper.runAfterDelay(60, () -> {
                ItemStack output = table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT);
                if (output.isEmpty()) {
                    helper.fail("Table should have crafted with a funded orb (burnTime=" + table.burnTime + ")");
                    return;
                }
                int spent = before - anima.getCurrentEV();
                if (spent < 50) {
                    helper.fail("Flint recipe costs 50 EV, only " + spent + " was syphoned");
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableRejectsNonOrbInOrbSlot(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            boolean valid = table.inv.isItemValid(TabulaVitaeBlockEntity.ORB_SLOT, new ItemStack(Items.DIAMOND));
            if (valid) {
                helper.fail("Orb slot should reject non-orb items");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 60)
    public void tableRejectsItemsInOutputSlot(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        TabulaVitaeBlockEntity table = placeTable(helper, new BlockPos(3, 1, 2));

        helper.runAfterDelay(1, () -> {
            if (table == null) return;

            boolean valid = table.inv.isItemValid(TabulaVitaeBlockEntity.OUTPUT_SLOT, new ItemStack(Items.DIAMOND));
            if (valid) {
                helper.fail("Output slot should reject items");
            }
            helper.succeed();
        });
    }
}
