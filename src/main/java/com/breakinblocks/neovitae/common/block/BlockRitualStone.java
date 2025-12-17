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
        tooltip.add(Component.translatable("tooltip.neovitae.decoration.safe").withStyle(ChatFormatting.GRAY));
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
     * Sets the rune type at the given position, with protection checks.
     *
     * @param world    The level
     * @param pos      The block position
     * @param runeType The rune type to set
     * @param player   The player responsible (null skips protection checks)
     * @return true if the rune was successfully placed
     */
    public boolean setRuneType(Level world, BlockPos pos, EnumRuneType runeType, @Nullable Player player) {
        Block runeBlock = this;
        switch (runeType) {
            case AIR:
                runeBlock = BMBlocks.AIR_RITUAL_STONE.block().get();
                break;
            case BLANK:
                runeBlock = BMBlocks.BLANK_RITUAL_STONE.block().get();
                break;
            case DAWN:
                runeBlock = BMBlocks.DAWN_RITUAL_STONE.block().get();
                break;
            case DUSK:
                runeBlock = BMBlocks.DUSK_RITUAL_STONE.block().get();
                break;
            case EARTH:
                runeBlock = BMBlocks.EARTH_RITUAL_STONE.block().get();
                break;
            case FIRE:
                runeBlock = BMBlocks.FIRE_RITUAL_STONE.block().get();
                break;
            case WATER:
                runeBlock = BMBlocks.WATER_RITUAL_STONE.block().get();
                break;
        }

        BlockState newState = runeBlock.defaultBlockState();

        // If player is provided, check protection before placing
        if (player != null) {
            return BlockProtectionHelper.tryPlaceBlock(world, pos, newState, player);
        }

        world.setBlockAndUpdate(pos, newState);
        return true;
    }
}
