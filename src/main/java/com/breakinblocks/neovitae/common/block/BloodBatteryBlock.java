package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.BloodBatteryBlockEntity;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import java.util.List;

public class BloodBatteryBlock extends Block implements EntityBlock {
    protected static final VoxelShape BOX = Block.box(3, 0, 3, 13, 14, 13);

    public BloodBatteryBlock() {
        super(BlockBehaviour.Properties.of()
                .requiresCorrectToolForDrops()
                .strength(2.0F, 5.0F)
                .sound(SoundType.METAL)
                .forceSolidOn()
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, components, tooltipFlag);
        components.add(Component.translatable("tooltip.neovitae.blood_battery.capacity",
                String.format("%,d", BloodBatteryBlockEntity.CAPACITY)).withStyle(ChatFormatting.DARK_RED));
        components.add(Component.translatable("tooltip.neovitae.blood_battery.creative").withStyle(ChatFormatting.DARK_PURPLE));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BOX;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BloodBatteryBlockEntity(blockPos, blockState);
    }
}
