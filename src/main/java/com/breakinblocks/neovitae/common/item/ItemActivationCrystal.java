package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;

import java.util.List;
import java.util.Locale;

public class ItemActivationCrystal extends Item implements IBindable {
    private final CrystalType type;

    public ItemActivationCrystal(CrystalType type) {
        super(new Item.Properties().stacksTo(1).component(BMDataComponents.BINDING.get(), Binding.EMPTY));
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Only trigger on sneak+right-click for binding
        if (player.isShiftKeyDown()) {
            // Swing arm for visual feedback
            player.swing(hand);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.activationcrystal." + type.name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public int getCrystalLevel(ItemStack stack) {
        return this.type.equals(CrystalType.CREATIVE) ? Integer.MAX_VALUE : type.ordinal() + 1;
    }

    public CrystalType getCrystalType() {
        return type;
    }

    public enum CrystalType {
        WEAK,
        AWAKENED,
        CREATIVE;

        public static ItemStack getStack(int level) {
            if (level < 0) {
                level = 0;
            }
            return switch (level) {
                case 0 -> new ItemStack(BMItems.ACTIVATION_CRYSTAL_WEAK.get());
                case 1 -> new ItemStack(BMItems.ACTIVATION_CRYSTAL_AWAKENED.get());
                default -> new ItemStack(BMItems.ACTIVATION_CRYSTAL_CREATIVE.get());
            };
        }
    }
}
