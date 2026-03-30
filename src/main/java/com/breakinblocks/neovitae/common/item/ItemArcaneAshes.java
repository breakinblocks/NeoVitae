package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemArcaneAshes extends Item implements IBindable {
    public ItemArcaneAshes() {
        super(new Item.Properties().stacksTo(1).durability(20).component(NVDataComponents.BINDING.get(), Binding.EMPTY));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.arcaneAshes").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        Binding binding = getBinding(stack);
        if (binding == null) {
            if (!context.getLevel().isClientSide && player != null) {
                player.displayClientMessage(Component.translatable("chat.neovitae.ash.notBound").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        BlockPos newPos = context.getClickedPos().relative(context.getClickedFace());
        Level world = context.getLevel();

        if (world.isEmptyBlock(newPos)) {
            if (!world.isClientSide) {
                Direction rotation = Direction.fromYRot(player.getYHeadRot());
                if (!BlockProtectionHelper.tryPlaceBlock(world, newPos, NVBlocks.ALCHEMY_ARRAY.get().defaultBlockState(), player)) {
                    return InteractionResult.FAIL;
                }
                BlockEntity tile = world.getBlockEntity(newPos);
                if (tile instanceof AlchemyArrayBlockEntity arrayTile) {
                    arrayTile.setRotation(rotation);
                    arrayTile.setOwnerBinding(binding);
                }

                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
