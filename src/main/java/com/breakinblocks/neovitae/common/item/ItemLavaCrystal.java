package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemLavaCrystal extends Item implements IBindable {

    private static final int FIRE_COST = 100;

    public ItemLavaCrystal(Item.Properties props) {
        super(props
                .stacksTo(1)
                .component(NVDataComponents.BINDING.get(), Binding.EMPTY));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            player.swing(hand);
            return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockPos firePos = pos.relative(context.getClickedFace());
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) {
            return InteractionResult.PASS;
        }

        Binding binding = stack.get(NVDataComponents.BINDING.get());
        if (binding == null || binding.isEmpty()) {
            if (!level.isClientSide()) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.crystal.notBound").withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }

        BlockState fireState = BaseFireBlock.getState(level, firePos);
        if (!level.getBlockState(firePos).canBeReplaced() || !fireState.canSurvive(level, firePos)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            Anima network = AnimaHelper.getAnima(binding.uuid());
            if (network == null) {
                player.sendOverlayMessage(
                        Component.translatable("chat.neovitae.notEnoughLP").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }

            int drained = network.syphon(AnimaTicket.create(FIRE_COST));
            if (drained < FIRE_COST) {
                network.hurtPlayer(player, FIRE_COST - drained);
            }

            level.setBlock(firePos, fireState, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, firePos);
        }

        return InteractionResult.SUCCESS;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.lavaCrystal.desc").withStyle(ChatFormatting.GRAY));}
}
