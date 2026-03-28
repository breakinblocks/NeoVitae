package com.breakinblocks.neovitae.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

public final class BloodLightHelper {

    private BloodLightHelper() {}

    public static BlockState createBlockState(int brightness) {
        return NVBlocks.BLOOD_LIGHT.get().defaultBlockState()
                .setValue(BloodLightBlock.BRIGHTNESS, brightness)
                .setValue(BloodLightBlock.POWERED, true);
    }

    public static void setBlockEntityColor(Level level, BlockPos pos, DyeColor color) {
        if (level.getBlockEntity(pos) instanceof BloodLightBlockEntity ble) {
            ble.setColor(color);
        }
    }

    public static void playSound(Level level, BlockPos pos) {
        level.playSound(null, pos, NVSounds.BLOOD_LAMP.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    public static int cycleBrightness(int current, boolean reverse) {
        if (reverse) {
            return current <= 1 ? 15 : current - 1;
        }
        return current >= 15 ? 1 : current + 1;
    }

    public static boolean isRainbow(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.BLOOD_LIGHT_RAINBOW.get(), false);
    }

    public static DyeColor getColor(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.BLOOD_LIGHT_COLOR.get(), DyeColor.RED);
    }

    public static DyeColor getColorAndCycleIfRainbow(ItemStack stack) {
        if (isRainbow(stack)) {
            DyeColor[] colors = DyeColor.values();
            DyeColor next = colors[RandomSource.create().nextInt(colors.length)];
            stack.set(NVDataComponents.BLOOD_LIGHT_COLOR.get(), next);
            return next;
        }
        return getColor(stack);
    }

    public static int getBrightness(ItemStack stack, int defaultValue) {
        return stack.getOrDefault(NVDataComponents.BLOOD_LIGHT_BRIGHTNESS.get(), defaultValue);
    }
}
