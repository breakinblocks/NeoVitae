// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class HarvestHandlerBerryBush implements IHarvestHandler {

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel) || !test(level, pos, state)) {
            return false;
        }
        IntegerProperty age = ageProperty(state);
        if (age == null) {
            return false;
        }
        int min = age.getPossibleValues().stream().mapToInt(Integer::intValue).min().orElse(0);
        BlockState picked = state.setValue(age, min + 1);
        if (!BlockProtectionHelper.tryReplaceBlock(level, pos, picked, ownerUUID)) {
            return false;
        }
        drops.addAll(HarvestHelper.getDropsAt(serverLevel, pos, state, HarvestHelper.mockHoe()));
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
        return true;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        IntegerProperty age = ageProperty(state);
        if (age == null) {
            return false;
        }
        boolean berry = block instanceof SweetBerryBushBlock
                || (block instanceof BushBlock && BuiltInRegistries.BLOCK.getKey(block).getPath().contains("berry"));
        if (!berry) {
            return false;
        }
        int max = age.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(0);
        return state.getValue(age) >= max;
    }

    @Nullable
    private static IntegerProperty ageProperty(BlockState state) {
        for (Property<?> p : state.getProperties()) {
            if (p instanceof IntegerProperty ip && ip.getName().equals("age")) {
                return ip;
            }
        }
        return null;
    }
}
