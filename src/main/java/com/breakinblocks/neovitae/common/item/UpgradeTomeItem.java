package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.living.LivingHelper;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class UpgradeTomeItem extends Item {
    public UpgradeTomeItem(Item.Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack tomeStack = player.getItemInHand(usedHand);
        UpgradeTome tome = tomeStack.get(NVDataComponents.UPGRADE_TOME_DATA);
        if (tome == null) {
            return InteractionResult.PASS;
        }

        XpFunc expAdder = LivingHelper::applyExp;
        if (player.isShiftKeyDown()) {
            expAdder = LivingHelper::applyExpToCap;
        }

        float consumed = expAdder.apply(player, tome.upgrade(), tome.exp(), true);
        if (player.hasInfiniteMaterials()) { // creative, no consume item/exp, only add >:
            return InteractionResult.SUCCESS.heldItemTransformedTo(tomeStack);
        }

        if (consumed >= tome.exp()) {
            return InteractionResult.SUCCESS.heldItemTransformedTo(ItemStack.EMPTY);
        }

        tomeStack.set(NVDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(tome.upgrade(), tome.exp() - consumed));
        return InteractionResult.SUCCESS.heldItemTransformedTo(tomeStack);
    }

    @FunctionalInterface
    public interface XpFunc {
        Float apply(Player player, Holder<LivingUpgrade> upgrade, Float exp, boolean fromTome);
    }

    // @Override (removed: not an override in 26.1)
    public String getDescriptionId(ItemStack stack) {
        UpgradeTome tome = stack.get(NVDataComponents.UPGRADE_TOME_DATA);
        return tome == null ? getDescriptionId() : getDescriptionId() + "." + tome.upgrade().getKey().identifier().getPath();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        UpgradeTome tome = stack.get(NVDataComponents.UPGRADE_TOME_DATA);
        if (tome != null) {
            tome.addToTooltip(context, tooltipComponents, tooltipFlag, stack);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(NVDataComponents.UPGRADE_TOME_DATA);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        UpgradeTome tome = stack.get(NVDataComponents.UPGRADE_TOME_DATA);
        if (tome == null) {
            return 0;
        }

        Holder<LivingUpgrade> upgrade = tome.upgrade();
        float exp = tome.exp();
        int currentLevel = LivingHelper.getLevelFromXp(upgrade, exp);
        int nextLevelExp = LivingHelper.nextLevelExp(upgrade, exp);

        if (nextLevelExp == 0) {
            return 13;
        }

        int currentLevelExp = LivingHelper.getExpForLevel(upgrade, currentLevel);
        if (currentLevelExp < 0) {
            currentLevelExp = 0;
        }

        float progress = (exp - currentLevelExp) / (float) (nextLevelExp - currentLevelExp);
        return Math.round(progress * 13.0f);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0xB00000;
    }
}
