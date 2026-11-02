package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.SanguineCasingBlock;
import com.breakinblocks.neovitae.common.block.SanguineCrafterBlock;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.ChunkLoading;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.Mode;
import com.breakinblocks.neovitae.common.blockentity.SanguineCrafterBlockEntity.Status;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SanguineCrafterTests {

    private SanguineCrafterTests() {}

    private static final BlockPos CENTER = new BlockPos(4, 3, 4);
    private static final BlockPos CONTROLLER = CENTER.north();

    private record Rig(SanguineCrafterBlockEntity crafter, Player owner, Anima network) {}

    private static Rig build(GameTestHelper helper, Block... upgrades) {
        BlockPos center = helper.absolutePos(CENTER);
        BlockPos controller = helper.absolutePos(CONTROLLER);
        Direction facing = Direction.getNearest(controller.getX() - center.getX(), controller.getY() - center.getY(),
                controller.getZ() - center.getZ(), Direction.NORTH);
        int upgradeIndex = 0;
        for (int dy = -1; dy <= 0; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (pos.equals(center) || pos.equals(controller)) continue;
                    Block block = upgradeIndex < upgrades.length && dy == -1
                            ? upgrades[upgradeIndex++]
                            : NVBlocks.SANGUINE_CASING.block().get();
                    helper.getLevel().setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
        BlockState state = NVBlocks.SANGUINE_CRAFTER.block().get().defaultBlockState().setValue(SanguineCrafterBlock.FACING, facing);
        helper.getLevel().setBlock(controller, state, Block.UPDATE_ALL);
        SanguineCrafterBlockEntity crafter = (SanguineCrafterBlockEntity) helper.getLevel().getBlockEntity(controller);
        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        Anima network = AnimaHelper.getAnima(owner.getUUID());
        network.add(AnimaTicket.create(10000), 100000);
        crafter.setOwner(owner);
        return new Rig(crafter, owner, network);
    }

    private static List<ItemStack> template(ItemStack... cells) {
        List<ItemStack> list = new ArrayList<>(Collections.nCopies(SanguineCrafterBlockEntity.TEMPLATE_SIZE, ItemStack.EMPTY));
        for (int i = 0; i < cells.length; i++) list.set(i, cells[i]);
        return list;
    }

    private static int outputCount(SanguineCrafterBlockEntity crafter, Item item) {
        int count = 0;
        for (int i = 0; i < SanguineCrafterBlockEntity.OUTPUT_COUNT; i++) {
            ItemStack stack = crafter.inv.getStackInSlot(SanguineCrafterBlockEntity.OUTPUT_START + i);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    private static List<ItemStack> sticksTemplate() {
        return template(new ItemStack(Items.OAK_PLANKS), ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.OAK_PLANKS));
    }

    public static void register(NVTestRegistrar r) {
        r.addIsolated("sanguine_crafter/packingmixedinputs", 180, helper -> {
        Rig rig = build(helper);
        rig.crafter().setMode(Mode.PACKING);
        rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.DIAMOND, 4));
        rig.crafter().inv.setStackInSlot(1, new ItemStack(Items.COAL, 9));
        rig.crafter().inv.setStackInSlot(2, new ItemStack(Items.DIAMOND, 5));
        helper.succeedWhen(() -> {
            helper.assertTrue(outputCount(rig.crafter(), Items.DIAMOND_BLOCK) == 1, "Expected diamond block from split stacks");
            helper.assertTrue(outputCount(rig.crafter(), Items.COAL_BLOCK) == 1, "Expected coal block from mixed buffer");
            helper.assertTrue(rig.network().getCurrentEV() == 9960, "Two crafts must cost 40 EV");
        });
        });
        r.addIsolated("sanguine_crafter/unpackingregisteredrecipes", 180, helper -> {
        Rig rig = build(helper);
        rig.crafter().setMode(Mode.UNPACKING);
        rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.DIAMOND_BLOCK));
        rig.crafter().inv.setStackInSlot(1, new ItemStack(Items.COAL_BLOCK));
        helper.succeedWhen(() -> {
            helper.assertTrue(outputCount(rig.crafter(), Items.DIAMOND) == 9, "Expected 9 diamonds");
            helper.assertTrue(outputCount(rig.crafter(), Items.COAL) == 9, "Expected 9 coal");
            helper.assertTrue(rig.network().getCurrentEV() == 9960, "Unpacking must pay normal crafting costs");
        });
        });
        r.addIsolated("sanguine_crafter/packingtwobytworoundtrip", 180, helper -> {
        Rig rig = build(helper);
        rig.crafter().setMode(Mode.PACKING);
        rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.ECHO_SHARD, 4));
        helper.runAfterDelay(60, () -> {
            helper.assertTrue(outputCount(rig.crafter(), Items.CALCITE) == 1, "Registered test recipe must pack 4 shards");
            rig.crafter().inv.setStackInSlot(SanguineCrafterBlockEntity.OUTPUT_START, ItemStack.EMPTY);
            rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.CALCITE));
            rig.crafter().setMode(Mode.UNPACKING);
            helper.succeedWhen(() -> helper.assertTrue(outputCount(rig.crafter(), Items.ECHO_SHARD) == 4, "Registered reverse must restore 4 shards"));
        });
        });
        r.addIsolated("sanguine_crafter/packingrejectsonewayandlossyrecipes", 180, helper -> {
        Rig rig = build(helper);
        rig.crafter().setMode(Mode.PACKING);
        rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.QUARTZ, 4));
        rig.crafter().inv.setStackInSlot(1, new ItemStack(Items.FLINT, 9));
        ItemStack named = new ItemStack(Items.DIAMOND, 9);
        named.set(DataComponents.CUSTOM_NAME, Component.literal("Keep this name"));
        rig.crafter().inv.setStackInSlot(2, named);
        helper.runAfterDelay(80, () -> {
            helper.assertTrue(rig.crafter().inv.getStackInSlot(0).getCount() == 4, "Quartz has no reverse recipe");
            helper.assertTrue(rig.crafter().inv.getStackInSlot(1).getCount() == 9, "Reverse returning 8 of 9 must be rejected");
            helper.assertTrue(rig.crafter().inv.getStackInSlot(2).getCount() == 9, "Packing must not erase components");
            helper.assertTrue(rig.network().getCurrentEV() == 10000, "Rejected recipes must not spend EV");
            helper.succeed();
        });
        });
        r.addIsolated("sanguine_crafter/unpackingrejectsonewayandlossyrecipes", 180, helper -> {
        Rig rig = build(helper);
        rig.crafter().setMode(Mode.UNPACKING);
        rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.OAK_LOG));
        rig.crafter().inv.setStackInSlot(1, new ItemStack(Items.ANDESITE));
        helper.runAfterDelay(80, () -> {
            helper.assertTrue(rig.crafter().inv.getStackInSlot(0).is(Items.OAK_LOG), "Planks cannot be packed back into a log");
            helper.assertTrue(rig.crafter().inv.getStackInSlot(1).is(Items.ANDESITE), "Lossy reverse must not unpack");
            helper.assertTrue(rig.network().getCurrentEV() == 10000, "Rejected recipes must not spend EV");
            helper.succeed();
        });
        });

        r.addIsolated("sanguine_crafter/forms_and_unforms", 120, helper -> {
            Rig rig = build(helper);
            BlockPos floorCenter = helper.absolutePos(CENTER.below());
            helper.runAfterDelay(25, () -> {
                helper.assertTrue(rig.crafter().isFormed(), "A complete structure should form");
                BlockState floor = helper.getLevel().getBlockState(floorCenter);
                helper.assertTrue(floor.getValue(SanguineCasingBlock.FORMED) && floor.getValue(SanguineCasingBlock.PX) == 1
                                && floor.getValue(SanguineCasingBlock.PY) == 0 && floor.getValue(SanguineCasingBlock.PZ) == 1,
                        "The floor center casing should be formed at 1,0,1, got " + floor);
                helper.getLevel().setBlock(helper.absolutePos(CENTER.east()), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
                helper.runAfterDelay(25, () -> {
                    helper.assertTrue(!rig.crafter().isFormed(), "Replacing a ring casing with stone should unform it");
                    helper.assertTrue(rig.crafter().getStatus() == Status.NOT_FORMED, "Status should be NOT_FORMED, got " + rig.crafter().getStatus());
                    helper.assertTrue(!helper.getLevel().getBlockState(floorCenter).getValue(SanguineCasingBlock.FORMED),
                            "Casings should drop their formed state when the structure breaks");
                    helper.succeed();
                });
            });
        });

        r.addIsolated("sanguine_crafter/active_state_follows_crafting", 140, helper -> {
            Rig rig = build(helper);
            rig.crafter().setTemplate(Mode.CRAFTING, sticksTemplate());
            rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.OAK_PLANKS, 2));
            helper.runAfterDelay(12, () -> {
                helper.assertTrue(rig.crafter().getBlockState().getValue(SanguineCrafterBlock.ACTIVE),
                        "The controller should be active while crafting, status " + rig.crafter().getStatus());
                helper.succeedWhen(() -> {
                    helper.assertTrue(outputCount(rig.crafter(), Items.STICK) == 4, "Expected 4 sticks");
                    helper.assertTrue(!rig.crafter().getBlockState().getValue(SanguineCrafterBlock.ACTIVE),
                            "The controller should go idle once the planks run out");
                });
            });
        });

        r.addIsolated("sanguine_crafter/center_block_can_be_anything", 80, helper -> {
            Rig rig = build(helper);
            helper.getLevel().setBlock(helper.absolutePos(CENTER), Blocks.STONE.defaultBlockState(), Block.UPDATE_ALL);
            helper.runAfterDelay(25, () -> {
                helper.assertTrue(rig.crafter().isFormed(), "A block in the center should not stop the structure forming");
                helper.succeed();
            });
        });

        r.addIsolated("sanguine_crafter/crafting_template_crafts_and_charges_ev", 120, helper -> {
            Rig rig = build(helper);
            rig.crafter().setTemplate(Mode.CRAFTING, sticksTemplate());
            rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.OAK_PLANKS, 2));
            helper.succeedWhen(() -> {
                helper.assertTrue(outputCount(rig.crafter(), Items.STICK) == 4, "Expected 4 sticks, status " + rig.crafter().getStatus());
                helper.assertTrue(rig.crafter().inv.getStackInSlot(0).isEmpty(), "Both planks should be used");
                helper.assertTrue(rig.network().getCurrentEV() == 9980, "One craft should cost 20 EV, network holds " + rig.network().getCurrentEV());
            });
        });

        r.addIsolated("sanguine_crafter/hellforged_casings_craft_in_parallel", 100, helper -> {
            Rig rig = build(helper, NVBlocks.HELLFORGED_SANGUINE_CASING.block().get(), NVBlocks.HELLFORGED_SANGUINE_CASING.block().get());
            rig.crafter().setTemplate(Mode.CRAFTING, sticksTemplate());
            rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.OAK_PLANKS, 6));
            helper.runAfterDelay(35, () -> {
                helper.assertTrue(rig.crafter().getParallel() == 3, "Two Hellforged casings should give 3 parallel crafts, got " + rig.crafter().getParallel());
                helper.assertTrue(outputCount(rig.crafter(), Items.STICK) == 12, "Three crafts should finish in one cycle, got " + outputCount(rig.crafter(), Items.STICK));
                helper.succeed();
            });
        });

        r.addIsolated("sanguine_crafter/tabula_mode_uses_orb", 300, helper -> {
            Rig rig = build(helper);
            ItemStack orb = new ItemStack(NVItems.ORB_APPRENTICE.get());
            orb.set(NVDataComponents.BINDING, new Binding(rig.owner().getUUID(), "CrafterOwner"));
            rig.crafter().setTemplate(Mode.TABULA, template(new ItemStack(Items.WHEAT), new ItemStack(Items.SUGAR)));
            rig.crafter().inv.setStackInSlot(SanguineCrafterBlockEntity.ORB_SLOT, orb);
            rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.WHEAT));
            rig.crafter().inv.setStackInSlot(1, new ItemStack(Items.SUGAR));
            helper.succeedWhen(() -> {
                helper.assertTrue(outputCount(rig.crafter(), Items.BREAD) == 1, "Expected bread, status " + rig.crafter().getStatus());
                helper.assertTrue(rig.network().getCurrentEV() == 9880, "Recipe 100 EV plus 20 base, network holds " + rig.network().getCurrentEV());
            });
        });

        r.addIsolated("sanguine_crafter/auto_mode_crafts_forge_recipe_from_gem", 300, helper -> {
            Rig rig = build(helper);
            ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_GREATER.get());
            gem.set(NVDataComponents.SPIRITUS_AMOUNT, 600.0);
            rig.crafter().inv.setStackInSlot(SanguineCrafterBlockEntity.GEM_SLOT, gem);
            rig.crafter().inv.setStackInSlot(0, new ItemStack(NVBlocks.HELLFIRE_FORGE.item().get()));
            rig.crafter().inv.setStackInSlot(1, new ItemStack(Items.DEEPSLATE));
            rig.crafter().inv.setStackInSlot(2, new ItemStack(Items.LAPIS_LAZULI));
            rig.crafter().inv.setStackInSlot(3, new ItemStack(Items.GLASS));
            helper.succeedWhen(() -> {
                helper.assertTrue(outputCount(rig.crafter(), NVBlocks.CRYSTALLARIUM_MALEFICUM.item().get()) == 1,
                        "Expected Crystallarium Maleficum, status " + rig.crafter().getStatus());
                double left = rig.crafter().inv.getStackInSlot(SanguineCrafterBlockEntity.GEM_SLOT).getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                helper.assertTrue(left == 500.0, "The gem should pay the 100 drain, holds " + left);
            });
        });

        r.addIsolated("sanguine_crafter/pushes_results_out_of_chosen_face", 120, helper -> {
            Rig rig = build(helper);
            BlockPos center = helper.absolutePos(CENTER);
            BlockPos barrelPos = center.east(2);
            helper.getLevel().setBlock(barrelPos, Blocks.BARREL.defaultBlockState(), Block.UPDATE_ALL);
            BarrelBlockEntity barrel = (BarrelBlockEntity) helper.getLevel().getBlockEntity(barrelPos);
            rig.crafter().setOutputFace(Direction.EAST);
            rig.crafter().setTemplate(Mode.CRAFTING, sticksTemplate());
            rig.crafter().inv.setStackInSlot(0, new ItemStack(Items.OAK_PLANKS, 2));
            helper.succeedWhen(() -> {
                helper.assertTrue(barrel.getItem(0).is(Items.STICK) && barrel.getItem(0).getCount() == 4,
                        "Barrel should receive the 4 sticks, holds " + barrel.getItem(0));
                helper.assertTrue(outputCount(rig.crafter(), Items.STICK) == 0, "Output buffer should be empty after pushing");
            });
        });

        r.addIsolated("sanguine_crafter/casing_faces_follow_side_groups", 80, helper -> {
            Rig rig = build(helper);
            helper.runAfterDelay(25, () -> {
                BlockPos casing = helper.absolutePos(CENTER.below());
                ResourceHandler<ItemResource> top = helper.getLevel().getCapability(Capabilities.Item.BLOCK, casing, Direction.UP);
                helper.assertTrue(top != null, "A formed casing should expose the crafter's inventory");
                try (Transaction tx = Transaction.openRoot()) {
                    int planks = top.insert(ItemResource.of(new ItemStack(Items.OAK_PLANKS)), 1, tx);
                    helper.assertTrue(planks == 1, "The top face should accept input by default");
                    int orb = top.insert(SanguineCrafterBlockEntity.ORB_SLOT, ItemResource.of(new ItemStack(NVItems.ORB_APPRENTICE.get())), 1, tx);
                    helper.assertTrue(orb == 0, "The orb slot should stay hidden from the top face by default");
                }
                helper.succeed();
            });
        });

        r.addIsolated("sanguine_crafter/chunk_loading_waits_for_owner_online", 80, helper -> {
            Rig rig = build(helper);
            helper.runAfterDelay(25, () -> {
                helper.assertTrue(rig.crafter().getChunkLoading() == ChunkLoading.OWNER_OFFLINE,
                        "A mock owner is not in the player list, expected OWNER_OFFLINE, got " + rig.crafter().getChunkLoading());
                helper.succeed();
            });
        });
    }
}
