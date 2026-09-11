package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.menu.ExperienceTomeMenu;

import java.util.List;

public class ExperienceTomeItem extends Item {

    public ExperienceTomeItem() {
        super(new Properties().stacksTo(1).component(NVDataComponents.STORED_XP, 0));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : Inventory.SLOT_OFFHAND;
        player.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, opener) -> new ExperienceTomeMenu(containerId, playerInventory, slot),
                stack.getHoverName()), buf -> buf.writeInt(slot));

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int storedXp = stack.getOrDefault(NVDataComponents.STORED_XP, 0);
        tooltip.add(Component.translatable("tooltip.neovitae.experience_tome.stored", storedXp)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.experience_tome.sneak_use")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.experience_tome.use")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.STORED_XP, 0) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int storedXp = stack.getOrDefault(NVDataComponents.STORED_XP, 0);
        // Max bar at 1000 XP, scales logarithmically for visibility
        int displayXp = Math.min(storedXp, 10000);
        return (int) (13.0 * Math.log10(displayXp + 1) / 4.0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x7FFF00;
    }

    public static void addXpToTome(ItemStack stack, int xpAmount) {
        if (stack.getItem() instanceof ExperienceTomeItem) {
            int current = stack.getOrDefault(NVDataComponents.STORED_XP, 0);
            stack.set(NVDataComponents.STORED_XP, (int) Math.clamp((long) current + xpAmount, 0, Integer.MAX_VALUE));
        }
    }

    public static int getStoredXp(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.STORED_XP, 0);
    }

    /** Levels < 0 mean "everything". Returns the experience actually moved. */
    public static int depositLevels(Player player, ItemStack tome, int levels) {
        int total = getPlayerTotalXp(player);
        int keep = levels < 0 ? 0 : getXpForLevel(Math.max(0, player.experienceLevel - levels));
        int moved = Math.min(Math.max(0, total - keep), Integer.MAX_VALUE - getStoredXp(tome));
        if (moved <= 0) {
            return 0;
        }
        setPlayerTotalXp(player, total - moved);
        addXpToTome(tome, moved);
        return moved;
    }

    /** Levels < 0 mean "everything". Returns the experience actually moved. */
    public static int withdrawLevels(Player player, ItemStack tome, int levels) {
        int stored = getStoredXp(tome);
        if (stored <= 0) {
            return 0;
        }
        int total = getPlayerTotalXp(player);
        int targetLevel = (int) Math.min((long) player.experienceLevel + levels, Integer.MAX_VALUE);
        int wanted = levels < 0 ? stored : Math.max(0, getXpForLevel(targetLevel) - total);
        int moved = Math.min(Math.min(stored, wanted), Integer.MAX_VALUE - total);
        if (moved <= 0) {
            return 0;
        }
        setPlayerTotalXp(player, total + moved);
        tome.set(NVDataComponents.STORED_XP, stored - moved);
        return moved;
    }

    public static int getLevelForXp(int xp) {
        int low = 0;
        // Level 65536 requires more XP than an int can hold. Compare in long
        // arithmetic so a full tome cannot saturate the threshold and loop forever.
        int high = 65536;
        while (low + 1 < high) {
            int middle = low + (high - low) / 2;
            if (getXpForLevelLong(middle) <= xp) {
                low = middle;
            } else {
                high = middle;
            }
        }
        return low;
    }

    private static void setPlayerTotalXp(Player player, int total) {
        player.experienceLevel = 0;
        player.experienceProgress = 0;
        player.totalExperience = 0;
        player.giveExperiencePoints(Math.max(0, total));
    }

    public static int getPlayerTotalXp(Player player) {
        return (int) Math.min(Integer.MAX_VALUE, (long) getXpForLevel(player.experienceLevel)
                + (int) (player.experienceProgress * player.getXpNeededForNextLevel()));
    }

    public static int getXpForLevel(int level) {
        return (int) Math.min(Integer.MAX_VALUE, getXpForLevelLong(Math.clamp(level, 0, 65536)));
    }

    private static long getXpForLevelLong(int level) {
        if (level <= 16) {
            return (long) level * level + 6L * level;
        } else if (level <= 31) {
            return (5L * level * level - 81L * level + 720) / 2;
        } else {
            return (9L * level * level - 325L * level + 4440) / 2;
        }
    }

    private static void addXpToPlayer(Player player, int amount) {
        player.giveExperiencePoints(amount);
    }
}
