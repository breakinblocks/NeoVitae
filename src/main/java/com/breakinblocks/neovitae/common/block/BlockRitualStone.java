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
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class BlockRitualStone extends Block implements IRitualStone {
    private final EnumRuneType type;

    public BlockRitualStone(BlockBehaviour.Properties props, EnumRuneType type) {
        super(props
                .strength(2.0F, 5.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
        this.type = type;
    }

    public EnumRuneType getRuneType() {
        return type;
    }

    // @Override (removed: not an override in 26.1)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {}

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
