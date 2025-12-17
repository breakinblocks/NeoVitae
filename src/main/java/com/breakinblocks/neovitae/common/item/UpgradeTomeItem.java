package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.living.LivingHelper;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;

import java.util.List;

public class UpgradeTomeItem extends Item {
    public UpgradeTomeItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack tomeStack = player.getItemInHand(usedHand);
        UpgradeTome tome = tomeStack.get(BMDataComponents.UPGRADE_TOME_DATA);
        if (tome == null) {
            return InteractionResultHolder.pass(tomeStack);
        }

        XpFunc expAdder = LivingHelper::applyExp;
        if (player.isShiftKeyDown()) {
            expAdder = LivingHelper::applyExpToCap;
        }

        float consumed = expAdder.apply(player, tome.upgrade(), tome.exp(), true);
        if (player.hasInfiniteMaterials()) { // creative, no consume item/exp, only add >:
            return InteractionResultHolder.sidedSuccess(tomeStack, level.isClientSide);
        }

        if (consumed >= tome.exp()) {
            return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, level.isClientSide);
        }

        tomeStack.set(BMDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(tome.upgrade(), tome.exp() - consumed));
        return InteractionResultHolder.sidedSuccess(tomeStack, level.isClientSide);
    }

    @FunctionalInterface
    public interface XpFunc {
        Float apply(Player player, Holder<LivingUpgrade> upgrade, Float exp, boolean fromTome);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        UpgradeTome tome = stack.get(BMDataComponents.UPGRADE_TOME_DATA);
        return tome == null ? getDescriptionId() : getDescriptionId() + "." + tome.upgrade().getKey().location().getPath();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        UpgradeTome tome = stack.get(BMDataComponents.UPGRADE_TOME_DATA);
        if (tome != null) {
            tome.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(BMDataComponents.UPGRADE_TOME_DATA);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        UpgradeTome tome = stack.get(BMDataComponents.UPGRADE_TOME_DATA);
        if (tome == null) {
            return 0;
        }

        Holder<LivingUpgrade> upgrade = tome.upgrade();
        float exp = tome.exp();
        int currentLevel = LivingHelper.getLevelFromXp(upgrade, exp);
        int nextLevelExp = LivingHelper.nextLevelExp(upgrade, exp);

        // At max level - show full bar
        if (nextLevelExp == 0) {
            return 13;
        }

        // Get exp threshold for current level
        int currentLevelExp = LivingHelper.getExpForLevel(upgrade, currentLevel);
        if (currentLevelExp < 0) {
            currentLevelExp = 0; // Level 0 has no entry
        }

        // Calculate progress from current level to next level
        float progress = (exp - currentLevelExp) / (float) (nextLevelExp - currentLevelExp);
        return Math.round(progress * 13.0f);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Blood red color to match mod theme
        return 0xB00000;
    }
}
