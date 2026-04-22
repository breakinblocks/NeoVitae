package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.common.menu.SigilHoldingMenu;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Sigil of Holding - Can hold up to 5 other sigils and use them.
 * Shift+right-click opens the GUI for managing sigils and selecting the active one.
 */
public class ItemSigilHolding extends ItemSigilBase implements ISigil.Holding {

    public static final int INVENTORY_SIZE = 5;

    public ItemSigilHolding(Item.Properties props) {
        super(props, "holding");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {NonNullList<ItemStack> inv = getInternalInventory(stack);
        int currentSlot = getCurrentItemOrdinal(stack);

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack invStack = inv.get(i);
            if (!invStack.isEmpty()) {
                if (i == currentSlot) {
                    tooltip.accept(Component.translatable("tooltip.neovitae.sigil.holding.sigilInSlot",
                            i + 1, invStack.getHoverName().copy()
                                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.UNDERLINE)));
                } else {
                    tooltip.accept(Component.translatable("tooltip.neovitae.sigil.holding.sigilInSlot",
                            i + 1, invStack.getHoverName()));
                }
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (PlayerHelper.isFakePlayer(player)) {
            return InteractionResult.FAIL;
        }

        int currentSlot = getCurrentItemOrdinal(stack);
        NonNullList<ItemStack> inv = getInternalInventory(stack);
        ItemStack itemUsing = inv.get(currentSlot);

        if (itemUsing.isEmpty()) {
            return InteractionResult.CONSUME;
        }

        if (itemUsing.getItem() instanceof IBindable bindable) {
            Binding binding = bindable.getBinding(itemUsing);
            if (binding == null) {
                return InteractionResult.CONSUME;
            }
        }

        InteractionResult result = itemUsing.getItem().useOn(context);
        saveInventory(stack, inv);

        // Don't let PASS propagate to attack action
        return result == InteractionResult.PASS ? InteractionResult.CONSUME : result;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (PlayerHelper.isFakePlayer(player)) {
            return InteractionResult.FAIL;
        }

        if (player.isShiftKeyDown()) {
            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                int slot = hand == InteractionHand.MAIN_HAND
                        ? player.getInventory().getSelectedSlot()
                        : Inventory.SLOT_OFFHAND;
                openGui(serverPlayer, stack, slot);
            }
            return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
        }

        int currentSlot = getCurrentItemOrdinal(stack);
        NonNullList<ItemStack> inv = getInternalInventory(stack);
        ItemStack itemUsing = inv.get(currentSlot);

        if (itemUsing.isEmpty()) {
            return InteractionResult.CONSUME;
        }

        if (itemUsing.getItem() instanceof IBindable bindable) {
            Binding binding = bindable.getBinding(itemUsing);
            if (binding == null) {
                return InteractionResult.CONSUME;
            }
        }

        InteractionResult result = itemUsing.getItem().use(world, player, hand);
        saveInventory(stack, inv);

        return result;
    }

    private void openGui(ServerPlayer player, ItemStack holdingStack, int slot) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("item.neovitae.sigil_holding");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player menuPlayer) {
                return new SigilHoldingMenu(containerId, playerInventory, holdingStack, slot);
            }
        }, buf -> {
            buf.writeInt(slot);
            buf.writeInt(slot);
        });
    }

    @Nonnull
    @Override
    public ItemStack getHeldItem(ItemStack holdingStack, Player player) {
        return getInternalInventory(holdingStack).get(getCurrentItemOrdinal(holdingStack));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world,
                              Entity entity, EquipmentSlot slot) {
        tickInternalInventory(stack, world, entity, slot);
    }

    private void tickInternalInventory(ItemStack holdingStack, ServerLevel world,
                                       Entity entity, EquipmentSlot slot) {
        for (ItemStack stack : getInternalInventory(holdingStack)) {
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof IBindable)) continue;
            if (!(stack.getItem() instanceof ISigil)) continue;

            Binding binding = ((IBindable) stack.getItem()).getBinding(stack);
            if (binding == null) continue;

            stack.getItem().inventoryTick(stack, world, entity, slot);
        }
    }

    public void saveInventory(ItemStack itemStack, NonNullList<ItemStack> inventory) {
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory));
    }

    public static int getCurrentItemOrdinal(ItemStack stack) {
        if (stack.getItem() instanceof ItemSigilHolding) {
            int currentSigil = stack.getOrDefault(NVDataComponents.READER_STATE.get(), 0);
            return Math.max(0, Math.min(currentSigil, INVENTORY_SIZE - 1));
        }
        return 0;
    }

    public static NonNullList<ItemStack> getInternalInventory(ItemStack stack) {
        NonNullList<ItemStack> inv = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null) {
            for (int i = 0; i < Math.min(contents.getSlots(), INVENTORY_SIZE); i++) {
                inv.set(i, contents.getStackInSlot(i));
            }
        }
        return inv;
    }

    public static void cycleToNextSigil(ItemStack itemStack, int direction) {
        if (itemStack.getItem() instanceof ItemSigilHolding) {
            NonNullList<ItemStack> inv = getInternalInventory(itemStack);
            int currentIndex = getCurrentItemOrdinal(itemStack);
            int attempts = 0;

            do {
                currentIndex = (currentIndex + direction + INVENTORY_SIZE) % INVENTORY_SIZE;
                attempts++;
            } while (inv.get(currentIndex).isEmpty() && attempts < INVENTORY_SIZE);

            itemStack.set(NVDataComponents.READER_STATE.get(), currentIndex);
        }
    }
}
