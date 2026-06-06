// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IRitualStone;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;

public class BlockRitualStone extends Block implements IRitualStone {
    private final EnumRuneType type;

    public BlockRitualStone(EnumRuneType type) {
        super(BlockBehaviour.Properties.of()
                .strength(2.0F, 5.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
        this.type = type;
    }

    public EnumRuneType getRuneType() {
        return type;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean isRuneType(Level world, BlockPos pos, EnumRuneType runeType) {
        return type.equals(runeType);
    }

    @Override
    public void setRuneType(Level world, BlockPos pos, EnumRuneType runeType) {
        setRuneType(world, pos, runeType, null);
    }

    /**
     * @param player The player responsible (null skips protection checks)
     */
    public boolean setRuneType(Level world, BlockPos pos, EnumRuneType runeType, @Nullable Player player) {
        Block runeBlock = this;
        switch (runeType) {
            case AIR:
                runeBlock = NVBlocks.AIR_RITUAL_STONE.block().get();
                break;
            case BLANK:
                runeBlock = NVBlocks.BLANK_RITUAL_STONE.block().get();
                break;
            case DAWN:
                runeBlock = NVBlocks.DAWN_RITUAL_STONE.block().get();
                break;
            case DUSK:
                runeBlock = NVBlocks.DUSK_RITUAL_STONE.block().get();
                break;
            case EARTH:
                runeBlock = NVBlocks.EARTH_RITUAL_STONE.block().get();
                break;
            case FIRE:
                runeBlock = NVBlocks.FIRE_RITUAL_STONE.block().get();
                break;
            case WATER:
                runeBlock = NVBlocks.WATER_RITUAL_STONE.block().get();
                break;
        }

        BlockState newState = runeBlock.defaultBlockState();

        if (player != null) {
            return BlockProtectionHelper.tryPlaceBlock(world, pos, newState, player);
        }

        world.setBlockAndUpdate(pos, newState);
        return true;
    }
}
