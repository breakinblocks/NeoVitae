package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectLight;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class LightArrayTests {

    private LightArrayTests() {}

    private static final BlockPos ARRAY_POS = new BlockPos(2, 1, 3);

    public static void register(NVTestRegistrar r) {
        r.add("light_array/fills_its_radius", 120, helper -> {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    helper.setBlock(ARRAY_POS.offset(dx, -1, dz), Blocks.STONE.defaultBlockState());
                }
            }
            helper.setBlock(ARRAY_POS, NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState());

            AlchemyArrayBlockEntity array = helper.getBlockEntity(ARRAY_POS, AlchemyArrayBlockEntity.class);
            if (array == null) {
                helper.fail("No alchemy array placed");
                return;
            }
            array.inv.setStackInSlot(0, new ItemStack(Items.GLOWSTONE_DUST));
            array.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
            int expected = 0;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > 3) continue;
                    if (helper.getBlockState(ARRAY_POS.offset(dx, 1, dz)).isAir()) expected++;
                }
            }
            final int expectedLights = expected;
            array.attemptCraft();

            helper.runAfterDelay(40, () -> {
                int lights = 0;
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        if (Math.abs(dx) + Math.abs(dz) > 3) continue;
                        if (helper.getBlockState(ARRAY_POS.offset(dx, 1, dz)).is(Blocks.LIGHT)) {
                            lights++;
                        }
                    }
                }
                if (lights != expectedLights) {
                    helper.fail("Light array should light every open spot in its diamond, placed "
                            + lights + " of " + expectedLights);
                    return;
                }
                BlockState center = helper.getBlockState(ARRAY_POS.above());
                if (!center.is(Blocks.LIGHT) || center.getValue(LightBlock.LEVEL) != 15) {
                    helper.fail("The light above the array should be level 15, got " + center);
                    return;
                }
                helper.succeed();
            });
        });
        r.add("light_array/redstone_toggles_lights", 200, helper -> {
            floor(helper);
            AlchemyArrayBlockEntity array = start(helper);
            if (array == null) return;

            helper.runAfterDelay(40, () -> {
                if (countLights(helper) == 0) {
                    helper.fail("The array should have lit its diamond before the redstone test");
                    return;
                }
                helper.setBlock(ARRAY_POS.offset(0, 0, -1), Blocks.REDSTONE_BLOCK.defaultBlockState());

                helper.runAfterDelay(10, () -> {
                    if (countLights(helper) != 0) {
                        helper.fail("A powered light array should take its lights back down, "
                                + countLights(helper) + " left");
                        return;
                    }
                    helper.setBlock(ARRAY_POS.offset(0, 0, -1), Blocks.AIR.defaultBlockState());

                    helper.runAfterDelay(10, () -> {
                        if (countLights(helper) == 0) {
                            helper.fail("Cutting the signal should light the array again");
                            return;
                        }
                        helper.succeed();
                    });
                });
            });
        });

        r.add("light_array/persistence_keeps_lights_on_break", 160, helper -> {
            floor(helper);
            AlchemyArrayBlockEntity array = start(helper);
            if (array == null) return;

            helper.runAfterDelay(40, () -> {
                if (!(array.arrayEffect instanceof AlchemyArrayEffectLight lightEffect)) {
                    helper.fail("Expected a light effect, got " + array.arrayEffect);
                    return;
                }
                int lit = countLights(helper);
                if (lit == 0) {
                    helper.fail("The array should have lit its diamond first");
                    return;
                }
                lightEffect.setPersistent(true);
                helper.setBlock(ARRAY_POS, Blocks.AIR.defaultBlockState());

                helper.runAfterDelay(5, () -> {
                    if (countLights(helper) != lit) {
                        helper.fail("Persistent lights should survive the array being broken, "
                                + countLights(helper) + " of " + lit + " left");
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("light_array/lights_go_with_a_plain_array", 160, helper -> {
            floor(helper);
            AlchemyArrayBlockEntity array = start(helper);
            if (array == null) return;

            helper.runAfterDelay(40, () -> {
                if (countLights(helper) == 0) {
                    helper.fail("The array should have lit its diamond first");
                    return;
                }
                helper.setBlock(ARRAY_POS, Blocks.AIR.defaultBlockState());

                helper.runAfterDelay(5, () -> {
                    if (countLights(helper) != 0) {
                        helper.fail("Breaking a plain light array should clear its lights, "
                                + countLights(helper) + " left");
                        return;
                    }
                    helper.succeed();
                });
            });
        });

        r.add("light_array/fresh_array_reclaims_persistent_lights", 200, helper -> {
            floor(helper);
            AlchemyArrayBlockEntity array = start(helper);
            if (array == null) return;

            helper.runAfterDelay(40, () -> {
                if (!(array.arrayEffect instanceof AlchemyArrayEffectLight lightEffect)) {
                    helper.fail("Expected a light effect, got " + array.arrayEffect);
                    return;
                }
                int lit = countLights(helper);
                lightEffect.setPersistent(true);
                helper.setBlock(ARRAY_POS, Blocks.AIR.defaultBlockState());

                helper.runAfterDelay(5, () -> {
                    if (countLights(helper) != lit) {
                        helper.fail("The persistent lights should still be standing");
                        return;
                    }
                    if (start(helper) == null) return;

                    helper.runAfterDelay(40, () -> {
                        if (countLights(helper) != lit) {
                            helper.fail("A fresh array should adopt the leftover lights, "
                                    + countLights(helper) + " of " + lit);
                            return;
                        }
                        helper.setBlock(ARRAY_POS, Blocks.AIR.defaultBlockState());

                        helper.runAfterDelay(5, () -> {
                            if (countLights(helper) != 0) {
                                helper.fail("Breaking the fresh array should clear the adopted lights, "
                                        + countLights(helper) + " left");
                                return;
                            }
                            helper.succeed();
                        });
                    });
                });
            });
        });
    }

    private static void floor(GameTestHelper helper) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                helper.setBlock(ARRAY_POS.offset(dx, -1, dz), Blocks.STONE.defaultBlockState());
            }
        }
    }

    private static AlchemyArrayBlockEntity start(GameTestHelper helper) {
        helper.setBlock(ARRAY_POS, NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState());
        AlchemyArrayBlockEntity array = helper.getBlockEntity(ARRAY_POS, AlchemyArrayBlockEntity.class);
        if (array == null) {
            helper.fail("No alchemy array placed");
            return null;
        }
        array.inv.setStackInSlot(0, new ItemStack(Items.GLOWSTONE_DUST));
        array.inv.setStackInSlot(1, new ItemStack(Items.GOLD_INGOT));
        array.attemptCraft();
        return array;
    }

    private static int countLights(GameTestHelper helper) {
        int lights = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > 3) continue;
                if (helper.getBlockState(ARRAY_POS.offset(dx, 1, dz)).is(Blocks.LIGHT)) lights++;
            }
        }
        return lights;
    }
}
