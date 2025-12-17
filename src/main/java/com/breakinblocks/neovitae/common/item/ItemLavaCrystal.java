package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.List;

/**
 * Lava Crystal - a bindable item that can place fire and works as furnace fuel.
 * Uses soul power from the owner's network for its operations.
 */
public class ItemLavaCrystal extends Item implements IBindable {

    private static final int FIRE_COST = 100;
    private static final int FUEL_COST = 50;
    private static final int BURN_TIME = 200;

    public ItemLavaCrystal() {
        super(new Item.Properties()
                .stacksTo(1)
                .component(BMDataComponents.BINDING.get(), Binding.EMPTY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Swing arm for binding visual feedback
        if (player.isShiftKeyDown()) {
            player.swing(hand);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
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

        // Check binding
        Binding binding = stack.get(BMDataComponents.BINDING.get());
        if (binding == null || binding.isEmpty()) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.crystal.notBound").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        // Check if we can place fire here
        BlockState fireState = BaseFireBlock.getState(level, firePos);
        if (!level.getBlockState(firePos).canBeReplaced() || !fireState.canSurvive(level, firePos)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            // Try to drain soul power
            SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding.uuid());
            if (network == null) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.notEnoughLP").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            int drained = network.syphon(SoulTicket.create(FIRE_COST));
            if (drained < FIRE_COST) {
                // Not enough LP - damage player for the difference
                network.hurtPlayer(player, FIRE_COST - drained);
            }

            // Place fire
            level.setBlock(firePos, fireState, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, firePos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        Binding binding = itemStack.get(BMDataComponents.BINDING.get());
        if (binding == null || binding.isEmpty()) {
            return 0;
        }

        // Check if network has enough LP
        SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding.uuid());
        if (network == null || network.getCurrentEssence() < FUEL_COST) {
            return 0;
        }

        return BURN_TIME;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        Binding binding = stack.get(BMDataComponents.BINDING.get());
        return binding != null && !binding.isEmpty();
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        Binding binding = stack.get(BMDataComponents.BINDING.get());
        if (binding == null || binding.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Try to drain LP
        SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding.uuid());
        if (network != null) {
            network.syphon(SoulTicket.create(FUEL_COST));
        }

        // Return the stack with binding preserved
        return stack.copy();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.lavaCrystal.desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
