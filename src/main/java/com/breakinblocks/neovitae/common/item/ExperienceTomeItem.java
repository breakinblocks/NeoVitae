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

import java.util.List;

/**
 * Tome of Peritia - Allows players to store and retrieve experience.
 * Sneak + Use: Store one level of XP
 * Use: Retrieve one level of XP
 * Hold to store/retrieve multiple levels.
 */
public class ExperienceTomeItem extends Item {

    public ExperienceTomeItem() {
        super(new Properties().stacksTo(1).component(BMDataComponents.STORED_XP, 0));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        int storedXp = stack.getOrDefault(BMDataComponents.STORED_XP, 0);

        if (player.isShiftKeyDown()) {
            // Store XP
            int playerXp = getPlayerTotalXp(player);
            if (playerXp > 0) {
                int xpForCurrentLevel = getXpForLevel(player.experienceLevel);
                int xpToStore = Math.max(1, xpForCurrentLevel);

                if (playerXp >= xpToStore) {
                    addXpToPlayer(player, -xpToStore);
                    stack.set(BMDataComponents.STORED_XP, storedXp + xpToStore);
                    return InteractionResultHolder.success(stack);
                }
            }
        } else {
            // Retrieve XP
            if (storedXp > 0) {
                int xpForNextLevel = getXpForLevel(player.experienceLevel + 1) - getPlayerTotalXp(player);
                int xpToGive = Math.min(storedXp, Math.max(1, xpForNextLevel));

                addXpToPlayer(player, xpToGive);
                stack.set(BMDataComponents.STORED_XP, storedXp - xpToGive);
                return InteractionResultHolder.success(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int storedXp = stack.getOrDefault(BMDataComponents.STORED_XP, 0);
        tooltip.add(Component.translatable("tooltip.neovitae.experience_tome.stored", storedXp)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.experience_tome.sneak_use")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.experience_tome.use")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.STORED_XP, 0) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int storedXp = stack.getOrDefault(BMDataComponents.STORED_XP, 0);
        // Max bar at 1000 XP, scales logarithmically for visibility
        int displayXp = Math.min(storedXp, 10000);
        return (int) (13.0 * Math.log10(displayXp + 1) / 4.0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Bright green for XP
        return 0x7FFF00;
    }

    /**
     * Adds XP to the tome from an external source (like a ritual).
     * @param stack The tome stack
     * @param xpAmount The amount of XP to add
     */
    public static void addXpToTome(ItemStack stack, int xpAmount) {
        if (stack.getItem() instanceof ExperienceTomeItem) {
            int current = stack.getOrDefault(BMDataComponents.STORED_XP, 0);
            stack.set(BMDataComponents.STORED_XP, current + xpAmount);
        }
    }

    /**
     * Gets the stored XP in the tome.
     */
    public static int getStoredXp(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.STORED_XP, 0);
    }

    // XP calculation helpers
    private static int getPlayerTotalXp(Player player) {
        return getXpForLevel(player.experienceLevel) + (int) (player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static int getXpForLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }

    private static void addXpToPlayer(Player player, int amount) {
        player.giveExperiencePoints(amount);
    }
}
