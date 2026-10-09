package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.UUID;

public final class DeusToolTests {

    private static final BlockPos FLOOR = new BlockPos(3, 0, 2);
    private static final BlockPos MACHINE = new BlockPos(3, 1, 2);
    private static final int EV_PER_USE = 50;

    private DeusToolTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("deus/athanor_tool_draws_ev_and_stays", 600, helper -> {
            AthanorBlockEntity arc = placeAthanor(helper);
            Binding owner = fundedOwner("7d1e0c11-0000-4000-8000-000000000001", 1000);

            helper.runAfterDelay(1, () -> {
                ItemStack tool = bound(NVItems.DEUS_HYDRATION_CELL.get(), owner);
                loadHydration(arc, tool);

                helper.succeedWhen(() -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    helper.assertTrue(output.is(Items.CLAY), "Expected clay from terracotta, got " + output);
                    helper.assertTrue(arc.athanorInv.getStackInSlot(AthanorBlockEntity.TOOL_SLOT).is(NVItems.DEUS_HYDRATION_CELL.get()),
                            "Deus tool should stay in the tool slot");
                    int ev = AnimaHelper.getAnima(owner).getCurrentEV();
                    helper.assertTrue(ev == 1000 - EV_PER_USE, "Owner should have paid " + EV_PER_USE + " EV, has " + ev);
                });
            });
        });

        r.add("deus/athanor_tool_pauses_without_ev", 400, helper -> {
            AthanorBlockEntity arc = placeAthanor(helper);
            Binding owner = fundedOwner("7d1e0c11-0000-4000-8000-000000000002", 10);

            helper.runAfterDelay(1, () -> {
                loadHydration(arc, bound(NVItems.DEUS_HYDRATION_CELL.get(), owner));

                helper.runAfterDelay(320, () -> {
                    helper.assertTrue(arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT).isEmpty(),
                            "Athanor should not craft when the owner cannot pay");
                    helper.assertTrue(arc.getIdleReason() == AthanorBlockEntity.IdleReason.NOT_ENOUGH_EV,
                            "Expected NOT_ENOUGH_EV, got " + arc.getIdleReason());
                    helper.assertTrue(AnimaHelper.getAnima(owner).getCurrentEV() == 10, "Nothing should be drawn while paused");
                    helper.succeed();
                });
            });
        });

        r.add("deus/athanor_tool_unbound_pauses", 60, helper -> {
            AthanorBlockEntity arc = placeAthanor(helper);

            helper.runAfterDelay(1, () -> {
                loadHydration(arc, new ItemStack(NVItems.DEUS_HYDRATION_CELL.get()));

                helper.runAfterDelay(20, () -> {
                    helper.assertTrue(arc.getIdleReason() == AthanorBlockEntity.IdleReason.TOOL_UNBOUND,
                            "Expected TOOL_UNBOUND, got " + arc.getIdleReason());
                    helper.assertTrue(arc.athanorInv.getStackInSlot(AthanorBlockEntity.TOOL_SLOT).is(NVItems.DEUS_HYDRATION_CELL.get()),
                            "Unbound Deus tool should stay in the tool slot");
                    helper.succeed();
                });
            });
        });

        r.add("deus/lava_crystal_smelts_and_draws_ev", 600, helper -> {
            AthanorBlockEntity arc = placeAthanor(helper);
            Binding owner = fundedOwner("7d1e0c11-0000-4000-8000-000000000003", 1000);

            helper.runAfterDelay(1, () -> {
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, bound(NVItems.LAVA_CRYSTAL.get(), owner));
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON));

                helper.succeedWhen(() -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    helper.assertTrue(output.is(Items.IRON_INGOT), "Expected an iron ingot, got " + output);
                    helper.assertTrue(arc.athanorInv.getStackInSlot(AthanorBlockEntity.TOOL_SLOT).is(NVItems.LAVA_CRYSTAL.get()),
                            "Lava Crystal should stay in the tool slot");
                    int ev = AnimaHelper.getAnima(owner).getCurrentEV();
                    helper.assertTrue(ev == 1000 - EV_PER_USE, "Owner should have paid " + EV_PER_USE + " EV, has " + ev);
                });
            });
        });

        r.add("deus/unbound_lava_crystal_does_not_smelt", 60, helper -> {
            AthanorBlockEntity arc = placeAthanor(helper);

            helper.runAfterDelay(1, () -> {
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.LAVA_CRYSTAL.get()));
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.RAW_IRON));

                helper.runAfterDelay(20, () -> {
                    helper.assertTrue(arc.getIdleReason() == AthanorBlockEntity.IdleReason.TOOL_UNBOUND,
                            "Expected TOOL_UNBOUND, got " + arc.getIdleReason());
                    helper.assertTrue(arc.athanorInv.getStackInSlot(AthanorBlockEntity.TOOL_SLOT).is(NVItems.LAVA_CRYSTAL.get()),
                            "Unbound Lava Crystal should stay in the tool slot");
                    helper.assertTrue(arc.getProgressForGui() == 0, "Unbound Lava Crystal should not make progress");
                    helper.succeed();
                });
            });
        });

        r.add("deus/tabula_cutting_fluid_draws_ev_and_stays", 300, helper -> {
            TabulaVitaeBlockEntity table = placeTable(helper);
            Binding fluidOwner = fundedOwner("7d1e0c11-0000-4000-8000-000000000004", 1000);
            Binding orbOwner = fundedOwner("7d1e0c11-0000-4000-8000-000000000005", 20000);

            helper.runAfterDelay(1, () -> {
                table.inv.setStackInSlot(0, new ItemStack(Items.IRON_ORE));
                table.inv.setStackInSlot(1, bound(NVItems.DEUS_CUTTING_FLUID.get(), fluidOwner));
                table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, bound(NVItems.ORB_APPRENTICE.get(), orbOwner));

                helper.succeedWhen(() -> {
                    helper.assertTrue(!table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT).isEmpty(),
                            "Tabula Vitae should have crafted dust (idle=" + table.getIdleReason() + ")");
                    helper.assertTrue(table.inv.getStackInSlot(1).is(NVItems.DEUS_CUTTING_FLUID.get()),
                            "Deus Cutting Fluid should stay in its slot");
                    int ev = AnimaHelper.getAnima(fluidOwner).getCurrentEV();
                    helper.assertTrue(ev == 1000 - EV_PER_USE, "Fluid owner should have paid " + EV_PER_USE + " EV, has " + ev);
                });
            });
        });

        r.add("deus/prismatic_gem_crafts_and_keeps_orb", 600, helper -> {
            AthanorBlockEntity arc = placeAthanor(helper);

            helper.runAfterDelay(1, () -> {
                fillAllAspects(helper);
                loadPrismaticGem(arc);
                arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, new ItemStack(NVItems.ORB_TRANSCENDENT.get()));

                helper.succeedWhen(() -> {
                    ItemStack output = arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT);
                    helper.assertTrue(output.is(NVItems.PRISMATIC_SPIRITUS_GEM.get()), "Expected a Prismatic Spiritus Gem, got " + output);
                    helper.assertTrue(arc.athanorInv.getStackInSlot(AthanorBlockEntity.TOOL_SLOT).is(NVItems.ORB_TRANSCENDENT.get()),
                            "The orb should stay in the tool slot");
                });
            });
        });

        r.add("deus/prismatic_gem_needs_orb", 120, helper -> {
            AthanorBlockEntity arc = placeAthanor(helper);

            helper.runAfterDelay(1, () -> {
                fillAllAspects(helper);
                loadPrismaticGem(arc);

                helper.runAfterDelay(80, () -> {
                    helper.assertTrue(arc.athanorInv.getStackInSlot(AthanorBlockEntity.OUTPUT_SLOT).isEmpty(),
                            "The gem should not craft without an orb in the tool slot");
                    helper.assertTrue(arc.getProgressForGui() == 0, "No progress without an orb");
                    helper.succeed();
                });
            });
        });

        r.add("deus/forge_crafts_deus_tool_with_grand_gem", 300, helper -> {
            HellfireForgeBlockEntity forge = placeForge(helper);

            helper.runAfterDelay(1, () -> {
                loadDeusToolForge(forge, NVItems.SPIRITUS_GEM_GRAND.get(), 6000.0);

                helper.succeedWhen(() -> {
                    ItemStack output = forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT);
                    helper.assertTrue(output.is(NVItems.DEUS_CUTTING_FLUID.get()), "Expected a Deus Cutting Fluid, got " + output);
                });
            });
        });

        r.add("deus/forge_rejects_greater_gem", 200, helper -> {
            HellfireForgeBlockEntity forge = placeForge(helper);

            helper.runAfterDelay(1, () -> {
                loadDeusToolForge(forge, NVItems.SPIRITUS_GEM_GREATER.get(), 4096.0);

                helper.runAfterDelay(150, () -> {
                    helper.assertTrue(forge.inv.getStackInSlot(HellfireForgeBlockEntity.OUTPUT_SLOT).isEmpty(),
                            "A full Greater gem should not power a Deus craft");
                    helper.succeed();
                });
            });
        });

        r.add("deus/tabula_unbound_cutting_fluid_pauses", 60, helper -> {
            TabulaVitaeBlockEntity table = placeTable(helper);
            Binding orbOwner = fundedOwner("7d1e0c11-0000-4000-8000-000000000006", 20000);

            helper.runAfterDelay(1, () -> {
                table.inv.setStackInSlot(0, new ItemStack(Items.IRON_ORE));
                table.inv.setStackInSlot(1, new ItemStack(NVItems.DEUS_CUTTING_FLUID.get()));
                table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, bound(NVItems.ORB_APPRENTICE.get(), orbOwner));

                helper.runAfterDelay(20, () -> {
                    helper.assertTrue(table.getIdleReason() == TabulaVitaeBlockEntity.IdleReason.CUTTING_FLUID_UNBOUND,
                            "Expected CUTTING_FLUID_UNBOUND, got " + table.getIdleReason());
                    helper.assertTrue(table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT).isEmpty(),
                            "Tabula Vitae should not craft with an unbound Deus Cutting Fluid");
                    helper.succeed();
                });
            });
        });
    }

    private static HellfireForgeBlockEntity placeForge(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
        helper.setBlock(MACHINE, NVBlocks.HELLFIRE_FORGE.block().get().defaultBlockState());
        return helper.getBlockEntity(MACHINE, HellfireForgeBlockEntity.class);
    }

    private static void loadDeusToolForge(HellfireForgeBlockEntity forge, Item gemItem, double spiritus) {
        ItemStack gem = new ItemStack(gemItem);
        gem.set(NVDataComponents.SPIRITUS_AMOUNT, spiritus);
        forge.inv.setStackInSlot(HellfireForgeBlockEntity.GEM_SLOT, gem);
        forge.inv.setStackInSlot(HellfireForgeBlockEntity.SOUTH, new ItemStack(NVItems.ADVANCED_CUTTING_FLUID.get()));
        forge.inv.setStackInSlot(HellfireForgeBlockEntity.WEST, new ItemStack(NVItems.PRISMATIC_SPIRITUS_GEM.get()));
        forge.inv.setStackInSlot(HellfireForgeBlockEntity.NORTH, new ItemStack(NVItems.TABULA_AETHEREA.get()));
    }

    private static void fillAllAspects(GameTestHelper helper) {
        BlockPos abs = helper.absolutePos(MACHINE);
        for (SpiritusType type : SpiritusType.values()) {
            WorldSpiritusHandler.fillSpiritusToAmount(helper.getLevel(), abs, type, 100.0);
        }
    }

    private static void loadPrismaticGem(AthanorBlockEntity arc) {
        arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.NETHER_STAR));
        for (int i = 1; i <= 4; i++) {
            arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START + i, new ItemStack(Items.DIAMOND));
        }
    }

    private static AthanorBlockEntity placeAthanor(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
        helper.setBlock(MACHINE, NVBlocks.ATHANOR_BLOCK.block().get().defaultBlockState());
        return helper.getBlockEntity(MACHINE, AthanorBlockEntity.class);
    }

    private static TabulaVitaeBlockEntity placeTable(GameTestHelper helper) {
        helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
        helper.setBlock(MACHINE, NVBlocks.TABULA_VITAE.block().get().defaultBlockState());
        return helper.getBlockEntity(MACHINE, TabulaVitaeBlockEntity.class);
    }

    private static void loadHydration(AthanorBlockEntity arc, ItemStack tool) {
        arc.inputTank.setFluid(new FluidStack(Fluids.WATER, 1000));
        arc.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, tool);
        arc.athanorInv.setStackInSlot(AthanorBlockEntity.INPUT_START, new ItemStack(Items.TERRACOTTA));
    }

    private static Binding fundedOwner(String uuid, int ev) {
        UUID id = UUID.fromString(uuid);
        Anima network = AnimaHelper.getAnima(id);
        network.set(AnimaTicket.create(ev), Integer.MAX_VALUE);
        return new Binding(id, "DeusTestPlayer");
    }

    private static ItemStack bound(Item item, Binding binding) {
        ItemStack stack = new ItemStack(item);
        stack.set(NVDataComponents.BINDING, binding);
        return stack;
    }
}
