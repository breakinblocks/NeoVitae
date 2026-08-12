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
            stack.set(NVDataComponents.STORED_XP, current + xpAmount);
        }
    }

    public static int getStoredXp(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.STORED_XP, 0);
    }

    /** Levels < 0 mean "everything". Returns the experience actually moved. */
    public static int depositLevels(Player player, ItemStack tome, int levels) {
        int total = getPlayerTotalXp(player);
        int keep = levels < 0 ? 0 : getXpForLevel(Math.max(0, player.experienceLevel - levels));
        int moved = Math.max(0, total - keep);
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
        int wanted = levels < 0 ? stored : Math.max(0, getXpForLevel(player.experienceLevel + levels) - total);
        int moved = Math.min(stored, wanted);
        if (moved <= 0) {
            return 0;
        }
        setPlayerTotalXp(player, total + moved);
        tome.set(NVDataComponents.STORED_XP, stored - moved);
        return moved;
    }

    public static int getLevelForXp(int xp) {
        int level = 0;
        while (getXpForLevel(level + 1) <= xp) {
            level++;
        }
        return level;
    }

    public static int getPlayerTotalXp(Player player) {
        return getXpForLevel(player.experienceLevel) + (int) (player.experienceProgress * player.getXpNeededForNextLevel());
    }

    private static void setPlayerTotalXp(Player player, int total) {
        player.experienceLevel = 0;
        player.experienceProgress = 0;
        player.totalExperience = 0;
        player.giveExperiencePoints(Math.max(0, total));
    }

    public static int getXpForLevel(int level) {
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
