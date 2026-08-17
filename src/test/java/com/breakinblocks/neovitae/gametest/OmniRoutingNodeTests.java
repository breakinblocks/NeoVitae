package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import com.breakinblocks.neovitae.common.block.BlockRoutingNode;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OmniRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.routing.FaceDirection;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.common.routing.RoutingTint;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class OmniRoutingNodeTests {

    private OmniRoutingNodeTests() {}

    private static final BlockPos MASTER_POS = new BlockPos(1, 1, 1);
    private static final BlockPos OMNI_POS = new BlockPos(3, 1, 1);
    private static final BlockPos SOURCE_POS = new BlockPos(3, 1, 0);
    private static final BlockPos TARGET_POS = new BlockPos(3, 1, 2);

    private static OmniRoutingNodeBlockEntity build(GameTestHelper helper) {
        helper.setBlock(MASTER_POS, NVBlocks.MASTER_ROUTING_NODE.block().get().defaultBlockState());
        helper.setBlock(OMNI_POS, NVBlocks.OMNI_ROUTING_NODE.block().get().defaultBlockState());
        helper.setBlock(SOURCE_POS, Blocks.CHEST.defaultBlockState());
        helper.setBlock(TARGET_POS, Blocks.CHEST.defaultBlockState());

        MasterRoutingNodeBlockEntity master = helper.getBlockEntity(MASTER_POS, MasterRoutingNodeBlockEntity.class);
        OmniRoutingNodeBlockEntity omni = helper.getBlockEntity(OMNI_POS, OmniRoutingNodeBlockEntity.class);
        if (master == null || omni == null) {
            helper.fail("Failed to place the omni routing network");
            return null;
        }

        RoutingLinkHelper.bindToMaster(helper.getLevel(), omni, helper.absolutePos(OMNI_POS),
                master, helper.absolutePos(MASTER_POS));
        return omni;
    }

    public static void register(NVTestRegistrar r) {
        r.add("omni_routing_node/faces_start_off", 40, helper -> {
            OmniRoutingNodeBlockEntity omni = build(helper);

            helper.runAfterDelay(1, () -> {
                if (omni == null) return;
                for (Direction side : Direction.values()) {
                    if (omni.getSideFilter(side).getDirection() != FaceDirection.OFF) {
                        helper.fail("A fresh omni face should be Off, got " + omni.getSideFilter(side).getDirection());
                        return;
                    }
                    if (omni.isInput(side) || omni.isOutput(side)) {
                        helper.fail("An Off face must neither pull nor push");
                        return;
                    }
                }
                helper.succeed();
            });
        });

        r.add("omni_routing_node/direction_governs_both_roles", 40, helper -> {
            OmniRoutingNodeBlockEntity omni = build(helper);

            helper.runAfterDelay(1, () -> {
                if (omni == null) return;
                int north = Direction.NORTH.get3DDataValue();
                int south = Direction.SOUTH.get3DDataValue();

                omni.getSideFilter(north).setDirection(FaceDirection.INPUT);
                omni.getSideFilter(south).setDirection(FaceDirection.OUTPUT);

                if (!omni.isInput(Direction.NORTH) || omni.isOutput(Direction.NORTH)) {
                    helper.fail("An Input face should pull only");
                    return;
                }
                if (omni.isInput(Direction.SOUTH) || !omni.isOutput(Direction.SOUTH)) {
                    helper.fail("An Output face should push only");
                    return;
                }
                if (!omni.isFluidInput(Direction.NORTH) || !omni.isFluidOutput(Direction.SOUTH)) {
                    helper.fail("Fluids should follow the same face directions");
                    return;
                }

                omni.getSideFilter(north).setDirection(FaceDirection.BOTH);
                if (!omni.isInput(Direction.NORTH) || !omni.isOutput(Direction.NORTH)) {
                    helper.fail("A Both face should pull and push");
                    return;
                }
                if (!omni.getSideFilter(north).isEnabled()) {
                    helper.fail("Any direction other than Off should leave the face enabled");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("omni_routing_node/moves_items_both_ways", 400, helper -> {
            OmniRoutingNodeBlockEntity omni = build(helper);

            helper.runAfterDelay(5, () -> {
                if (omni == null) return;
                ChestBlockEntity source = helper.getBlockEntity(SOURCE_POS, ChestBlockEntity.class);
                if (source == null) {
                    helper.fail("No source chest");
                    return;
                }
                source.setItem(0, Items.COBBLESTONE.getDefaultInstance().copyWithCount(16));

                var pull = omni.getSideFilter(Direction.NORTH.get3DDataValue());
                var push = omni.getSideFilter(Direction.SOUTH.get3DDataValue());
                pull.setDirection(FaceDirection.INPUT);
                push.setDirection(FaceDirection.OUTPUT);
                pull.setItemMode(FilterMode.BLACKLIST);
                push.setItemMode(FilterMode.BLACKLIST);

                helper.runAfterDelay(300, () -> {
                    ChestBlockEntity target = helper.getBlockEntity(TARGET_POS, ChestBlockEntity.class);
                    if (target == null) {
                        helper.fail("No target chest");
                        return;
                    }
                    int moved = 0;
                    for (int i = 0; i < target.getContainerSize(); i++) {
                        if (target.getItem(i).is(Items.COBBLESTONE)) {
                            moved += target.getItem(i).getCount();
                        }
                    }
                    if (moved <= 0) {
                        helper.fail("A single omni node should pull from one face and push out the other");
                        return;
                    }
                    helper.succeed();
                });
            });
        });
        r.add("omni_routing_node/tint_tracks_configured_faces", 200, helper -> {
            OmniRoutingNodeBlockEntity omni = build(helper);

            helper.runAfterDelay(5, () -> {
                if (omni == null) return;
                var pull = omni.getSideFilter(Direction.NORTH.get3DDataValue());
                pull.setDirection(FaceDirection.INPUT);
                pull.setItemMode(FilterMode.BLACKLIST);

                helper.runAfterDelay(30, () -> {
                    if (helper.getBlockState(OMNI_POS).getValue(BlockRoutingNode.TINT) != RoutingTint.INPUT) {
                        helper.fail("A pull-only omni node should tint as input, got "
                                + helper.getBlockState(OMNI_POS).getValue(BlockRoutingNode.TINT));
                        return;
                    }

                    var push = omni.getSideFilter(Direction.SOUTH.get3DDataValue());
                    push.setDirection(FaceDirection.OUTPUT);
                    push.setItemMode(FilterMode.BLACKLIST);

                    helper.runAfterDelay(30, () -> {
                        if (helper.getBlockState(OMNI_POS).getValue(BlockRoutingNode.TINT) != RoutingTint.BOTH) {
                            helper.fail("An omni node that pulls and pushes should tint as both, got "
                                    + helper.getBlockState(OMNI_POS).getValue(BlockRoutingNode.TINT));
                            return;
                        }
                        helper.succeed();
                    });
                });
            });
        });

        r.add("omni_routing_node/both_side_does_not_route_into_itself", 400, helper -> {
            OmniRoutingNodeBlockEntity omni = build(helper);

            helper.runAfterDelay(5, () -> {
                if (omni == null) return;
                ChestBlockEntity source = helper.getBlockEntity(SOURCE_POS, ChestBlockEntity.class);
                if (source == null) {
                    helper.fail("No source chest");
                    return;
                }
                source.setItem(0, Items.COBBLESTONE.getDefaultInstance().copyWithCount(64));

                var both = omni.getSideFilter(Direction.NORTH.get3DDataValue());
                var push = omni.getSideFilter(Direction.SOUTH.get3DDataValue());
                both.setDirection(FaceDirection.BOTH);
                both.setItemMode(FilterMode.BLACKLIST);
                push.setDirection(FaceDirection.OUTPUT);
                push.setItemMode(FilterMode.BLACKLIST);
                omni.priorities[Direction.NORTH.get3DDataValue()] = 5;

                helper.runAfterDelay(300, () -> {
                    ChestBlockEntity target = helper.getBlockEntity(TARGET_POS, ChestBlockEntity.class);
                    ChestBlockEntity src = helper.getBlockEntity(SOURCE_POS, ChestBlockEntity.class);
                    if (target == null || src == null) {
                        helper.fail("Missing a chest");
                        return;
                    }
                    int moved = count(target);
                    int left = count(src);
                    if (moved <= 0) {
                        helper.fail("A Both side starved the other output, source=" + left + " target=" + moved);
                        return;
                    }
                    if (moved + left != 64) {
                        helper.fail("Items lost! source=" + left + " target=" + moved);
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("omni_routing_node/broken_node_leaves_no_graph_entry", 80, helper -> {
            OmniRoutingNodeBlockEntity omni = build(helper);

            helper.runAfterDelay(5, () -> {
                if (omni == null) return;
                MasterRoutingNodeBlockEntity master = helper.getBlockEntity(MASTER_POS, MasterRoutingNodeBlockEntity.class);
                BlockPos absOmni = helper.absolutePos(OMNI_POS);
                if (master == null || !master.graphContains(absOmni)) {
                    helper.fail("Test setup failed: master never recorded the omni node");
                    return;
                }

                helper.setBlock(OMNI_POS, Blocks.AIR.defaultBlockState());

                helper.runAfterDelay(2, () -> {
                    MasterRoutingNodeBlockEntity reloaded = helper.getBlockEntity(MASTER_POS, MasterRoutingNodeBlockEntity.class);
                    if (reloaded == null || reloaded.graphContains(absOmni)) {
                        helper.fail("Master still holds a graph entry for the broken omni node");
                        return;
                    }
                    helper.succeed();
                });
            });
        });
    }

    private static int count(ChestBlockEntity chest) {
        int total = 0;
        for (int i = 0; i < chest.getContainerSize(); i++) {
            if (chest.getItem(i).is(Items.COBBLESTONE)) {
                total += chest.getItem(i).getCount();
            }
        }
        return total;
    }
}
