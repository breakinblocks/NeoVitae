package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeHelper;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.util.AltarUtil;

public final class SacrificeTests {

    private SacrificeTests() {}

    private static AraVitaeTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.STONE.defaultBlockState());
        helper.setBlock(pos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        AraVitaeTile altar = helper.getBlockEntity(pos, AraVitaeTile.class);
        if (altar == null) {
            helper.fail("Expected AraVitaeTile at " + pos);
        }
        return altar;
    }

    public static void register(NVTestRegistrar r) {
        r.add("sacrifice/altar_util_finds_nearby_altar", 30, helper -> {
            BlockPos altarPos = new BlockPos(3, 1, 2);
            placeAltar(helper, altarPos);

            helper.runAfterDelay(3, () -> {
                BlockPos searchFrom = helper.absolutePos(new BlockPos(3, 1, 4));
                BlockPos absAltarPos = helper.absolutePos(altarPos);
                BlockPos found = AltarUtil.findAltar(helper.getLevel(), searchFrom, 3);
                if (found == null) {
                    helper.fail("AltarUtil.findAltar should find altar within radius 3");
                    return;
                }
                if (!found.equals(absAltarPos)) {
                    helper.fail("Found altar at wrong position: " + found + " expected " + absAltarPos);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sacrifice/altar_util_returns_null_when_too_far", 30, helper -> {
            BlockPos altarPos = new BlockPos(1, 1, 1);
            placeAltar(helper, altarPos);

            helper.runAfterDelay(3, () -> {
                BlockPos searchFrom = helper.absolutePos(new BlockPos(4, 1, 4));
                BlockPos found = AltarUtil.findAltar(helper.getLevel(), searchFrom, 2);
                if (found != null) {
                    helper.fail("AltarUtil.findAltar should return null when altar is out of range");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sacrifice/adds_to_main_tank", 30, helper -> {
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(3, () -> {
                if (altar == null) return;
                int before = altar.getMainTank();
                altar.addSacrificeEV(500, true);
                int after = altar.getMainTank();
                if (after - before < 500) {
                    helper.fail("addSacrificeEV(500, true) should add at least 500, added " + (after - before));
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sacrifice/self_sacrifice_adds_to_main_tank", 30, helper -> {
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(3, () -> {
                if (altar == null) return;
                int before = altar.getMainTank();
                altar.addSacrificeEV(200, false);
                int after = altar.getMainTank();
                if (after - before < 200) {
                    helper.fail("addSacrificeEV(200, false) should add at least 200, added " + (after - before));
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sacrifice/respects_capacity", 30, helper -> {
            AraVitaeTile altar = placeAltar(helper, new BlockPos(3, 1, 2));

            helper.runAfterDelay(3, () -> {
                if (altar == null) return;
                int capacity = altar.getMainCapacity();
                altar.addSacrificeEV(capacity + 10000, true);
                int after = altar.getMainTank();
                if (after > capacity) {
                    helper.fail("Main tank " + after + " should not exceed capacity " + capacity);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sacrifice/cow_has_sacrifice_value", 60, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());

            helper.runAfterDelay(3, () -> {
                BlockPos absPos = helper.absolutePos(pos);
                Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.TRIGGERED);
                if (cow == null) {
                    helper.fail("Failed to create cow");
                    return;
                }
                cow.setPos(absPos.getX() + 0.5, absPos.getY(), absPos.getZ() + 0.5);
                helper.getLevel().addFreshEntity(cow);

                int value = EntitySacrificeHelper.getEvPerDamage(cow);
                if (value <= 0) {
                    helper.fail("Cow should have a positive sacrifice value, got " + value);
                    return;
                }
                cow.discard();
                helper.succeed();
            });
        });

        r.add("sacrifice/chicken_has_sacrifice_value", 60, helper -> {
            BlockPos pos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());

            helper.runAfterDelay(3, () -> {
                BlockPos absPos = helper.absolutePos(pos);
                Chicken chicken = EntityType.CHICKEN.create(helper.getLevel(), EntitySpawnReason.TRIGGERED);
                if (chicken == null) {
                    helper.fail("Failed to create chicken");
                    return;
                }
                chicken.setPos(absPos.getX() + 0.5, absPos.getY(), absPos.getZ() + 0.5);
                helper.getLevel().addFreshEntity(chicken);

                int value = EntitySacrificeHelper.getEvPerDamage(chicken);
                if (value <= 0) {
                    helper.fail("Chicken should have a positive sacrifice value, got " + value);
                    return;
                }
                chicken.discard();
                helper.succeed();
            });
        });
    }
}
