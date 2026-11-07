package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.client.helper.ClientPlayerAccess;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.menu.TrainerMenu;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class TrainerItem extends Item {
    public TrainerItem(Item.Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        Player player = ClientPlayerAccess.currentPlayer();
        UpgradeLimits limits = player == null ? null
                : SentientHelper.getChest(player).get(NVDataComponents.LIMITS);
        if (limits == null) {
            tooltip.accept(Component.translatable("tooltip.neovitae.trainer.unconfigured").withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.accept(Component.translatable("tooltip.neovitae.trainer.plan").withStyle(ChatFormatting.GOLD));
        limits.limits().object2FloatEntrySet().stream()
                .sorted(Comparator.comparing(entry -> SentientUpgrade.descriptionId(entry.getKey().getKey())))
                .forEach(entry -> {
                    int level = SentientHelper.getLevelFromXp(entry.getKey(), entry.getFloatValue());
                    MutableComponent name = Component.translatable(SentientUpgrade.descriptionId(entry.getKey().getKey()));
                    if (level <= 0) {
                        tooltip.accept(Component.translatable("tooltip.neovitae.trainer.blocked", name)
                                .withStyle(ChatFormatting.RED));
                    } else {
                        tooltip.accept(name.append(CommonComponents.SPACE)
                                .append(Component.literal(ChatUtil.toRoman(level)))
                                .withStyle(ChatFormatting.GRAY));
                    }
                });
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (!openTrainer(serverPlayer, serverPlayer.getInventory().getSelectedSlot())) {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }

    public static boolean openTrainer(ServerPlayer serverPlayer, int displaySlot) {
        ItemStack chest = SentientHelper.getChest(serverPlayer);
        if (SentientHelper.isNeverValid(chest)) {
            return false;
        }
        serverPlayer.openMenu(new SimpleMenuProvider(
                (id, inv, playerIn) -> new TrainerMenu(id, inv),
                Component.translatable(NVItems.TRAINING_BRACELET.get().getDescriptionId())));
        return true;
    }

    public static void setCap(Player player, Holder<SentientUpgrade> upgrade, int level) {
        ItemStack chest = SentientHelper.getChest(player);
        if (SentientHelper.isNeverValid(chest)) return;
        UpgradeLimits limits = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        chest.set(NVDataComponents.LIMITS, limits.withCap(upgrade, level, TrainerMenu.trainable(player.level().registryAccess())));
    }
}
