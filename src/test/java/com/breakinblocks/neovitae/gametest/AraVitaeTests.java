package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.recipe.AraVitaeInput;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

import java.util.UUID;

public final class AraVitaeTests {

    private AraVitaeTests() {}

    private static AraVitaeTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        AraVitaeTile altar = helper.getBlockEntity(pos, AraVitaeTile.class);
        if (altar == null) {
            helper.fail("Expected AraVitaeTile at " + pos);
        }
        return altar;
    }

    public static void register(NVTestRegistrar r) {
        r.add("ara_vitae/places_and_initializes", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                if (altar.getTier() != 0) {
                    helper.fail("Standalone altar should be tier 0, got " + altar.getTier());
                }
                helper.succeed();
            });
        });

        r.add("ara_vitae/accepts_fluid_input", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                ResourceHandler<FluidResource> handler = altar.getFluidHandler();
                int filled;
                try (Transaction tx = Transaction.openRoot()) {
                    filled = handler.insert(1, FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get()), 500, tx);
                    tx.commit();
                }
                if (filled <= 0) {
                    helper.fail("Altar should accept Life Essence at input slot, inserted=" + filled);
                }
                helper.succeed();
            });
        });

        r.add("ara_vitae/crafts_blank_slate", 300, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                altar.addSacrificeEV(2000, false);
                altar.inv.setStackInSlot(0, new ItemStack(Items.DEEPSLATE));

                helper.runAfterDelay(250, () -> {
                    ItemStack result = altar.inv.getStackInSlot(0);
                    if (!result.is(NVItems.TABULA_RASA.get())) {
                        helper.fail("Expected blank slate, got " + result + " (progress=" + altar.getProgress() + ", mainTank=" + altar.getMainTank() + ")");
                    }
                    helper.succeed();
                });
            });
        });

        r.add("ara_vitae/does_not_craft_without_lp", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                altar.inv.setStackInSlot(0, new ItemStack(Items.STONE));

                helper.runAfterDelay(40, () -> {
                    ItemStack result = altar.inv.getStackInSlot(0);
                    if (!result.is(Items.STONE)) {
                        helper.fail("Altar should not craft without EV, but item changed to " + result);
                    }
                    helper.succeed();
                });
            });
        });

        r.add("ara_vitae/does_not_craft_with_empty_slot", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                altar.addSacrificeEV(2000, false);

                helper.runAfterDelay(40, () -> {
                    if (altar.isActive()) {
                        helper.fail("Altar should not be active with empty slot");
                    }
                    helper.succeed();
                });
            });
        });

        r.add("ara_vitae/slot_caps_at_one", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(5, () -> {
                if (altar == null) return;
                int inserted;
                try (Transaction tx = Transaction.openRoot()) {
                    inserted = altar.inv.insert(0, ItemResource.of(new ItemStack(Items.DEEPSLATE)), 64, tx);
                    tx.commit();
                }
                int held = altar.inv.getStackInSlot(0).getCount();
                if (held != 1) {
                    helper.fail("Altar slot should cap at 1 item, holds " + held);
                }
                if (inserted != 1) {
                    helper.fail("Altar should accept only 1 of an inserted stack, inserted " + inserted);
                }
                helper.succeed();
            });
        });

        r.add("ara_vitae/awakened_crystal_keeps_binding", 40, helper -> {
            ItemStack bound = new ItemStack(NVItems.ACTIVATION_CRYSTAL_WEAK.get());
            bound.set(NVDataComponents.BINDING.get(),
                    new Binding(UUID.fromString("0fded6b6-1111-2222-3333-444455556666"), "TestVitaemancer"));
            AraVitaeInput input = new AraVitaeInput(bound, 4);

            var match = helper.getLevel().recipeAccess()
                    .getRecipeFor(NVRecipes.ARA_VITAE_TYPE.get(), input, helper.getLevel());
            if (match.isEmpty()) {
                helper.fail("A bound Weak Activation Crystal must still match the awakened recipe");
                return;
            }
            ItemStack result = match.get().value().assemble(input);
            if (!result.is(NVItems.ACTIVATION_CRYSTAL_AWAKENED.get())) {
                helper.fail("Expected an Awakened Activation Crystal, got " + result);
                return;
            }
            Binding carried = result.get(NVDataComponents.BINDING.get());
            if (carried == null || carried.isEmpty()) {
                helper.fail("The awakened crystal must carry the input binding, got " + carried);
                return;
            }
            helper.succeed();
        });
    }
}
