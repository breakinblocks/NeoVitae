package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.SigilStats;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for all (static) sigils.
 */
public class ItemSigil extends Item implements IBindable, ISigil {
    private final int defaultLpUsed;

    public ItemSigil(Properties prop, int lpUsed) {
        super(prop);
        this.defaultLpUsed = lpUsed;
    }

    public boolean isUnusable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getOrDefault(BMDataComponents.SIGIL_UNUSABLE.get(), false);
    }

    public ItemStack setUnusable(ItemStack stack, boolean unusable) {
        if (!stack.isEmpty()) {
            stack.set(BMDataComponents.SIGIL_UNUSABLE.get(), unusable);
        }
        return stack;
    }

    /**
     * Gets the LP cost for this sigil, checking the datamap first.
     * Falls back to the default value if not in datamap.
     */
    public int getLpUsed() {
        SigilStats stats = BuiltInRegistries.ITEM.wrapAsHolder(this).getData(BMDataMaps.SIGIL_STATS);
        if (stats != null) {
            return stats.lpCost();
        }
        return defaultLpUsed;
    }

    /**
     * Gets the full SigilStats for this sigil from the datamap.
     * @return SigilStats or null if not configured
     */
    public SigilStats getSigilStats() {
        return BuiltInRegistries.ITEM.wrapAsHolder(this).getData(BMDataMaps.SIGIL_STATS);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Binding binding = getBinding(stack);
        if (binding != null) {
            tooltip.add(Component.translatable("tooltip.neovitae.currentOwner", binding.name())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    /**
     * Resolves the sigil stack for use, handling Sigil of Holding.
     * Returns null if the player is a fake player (automation not allowed).
     *
     * @param player The player using the sigil
     * @param hand The hand holding the sigil
     * @return The resolved ItemStack, or null if the action should fail
     */
    @Nullable
    protected ItemStack resolveStackForUse(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof ISigil.Holding holding) {
            stack = holding.getHeldItem(stack, player);
        }

        if (PlayerHelper.isFakePlayer(player)) {
            return null;
        }

        return stack;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        // Prevent the attack/swing animation when using sigils
        return true;
    }
}
