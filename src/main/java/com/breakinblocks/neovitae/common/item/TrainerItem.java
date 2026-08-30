package com.breakinblocks.neovitae.common.item;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.helper.ClientPlayerAccess;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.menu.GhostItemHandler;
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
        tooltip.accept(Component.translatable(limits.allowOthers()
                ? "tooltip.neovitae.trainer.allow_others"
                : "tooltip.neovitae.trainer.deny_others").withStyle(ChatFormatting.DARK_GRAY));
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

        GhostItemHandler handler = new GhostItemHandler(16) {
            @Override
            public boolean isValid(int index, ItemResource resource) {
                if (resource.isEmpty()) return true;
                ItemStack stack = resource.toStack(1);
                return stack.is(NVItems.UPGRADE_TOME) && stack.has(NVDataComponents.UPGRADE_TOME_DATA);
            }
        };

        UpgradeLimits limits = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        SimpleContainerData data = new TrainerData(handler, chest);
        List<Pair<Integer, Integer>> start = limits.fillData(handler);
        start.forEach(pair -> data.set(pair.first(), pair.second()));

        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (id, inv, playerIn) -> new TrainerMenu(id, inv, handler, data, displaySlot),
                        Component.translatable(NVItems.TRAINING_BRACELET.get().getDescriptionId())
                ),
                buf -> buf.writeInt(displaySlot)
        );
        return true;
    }

    public static class TrainerData extends SimpleContainerData {
        private final GhostItemHandler handler;
        private final ItemStack chest;

        public TrainerData(GhostItemHandler handler, ItemStack chest) {
            super(19);
            this.handler = handler;
            this.chest = chest;
        }

        @Override
        public void set(int index, int value) {
            if (index == 2 && value == 1) {
                Object2FloatOpenHashMap<Holder<SentientUpgrade>> map = new Object2FloatOpenHashMap<>();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack ghostStack = handler.getStackInSlot(i);
                    if (ghostStack.isEmpty()) {
                        continue;
                    }
                    UpgradeTome tome = ghostStack.get(NVDataComponents.UPGRADE_TOME_DATA);
                    if (tome == null) {
                        continue;
                    }
                    map.put(tome.upgrade(), SentientHelper.getExpForLevel(tome.upgrade(), this.get(3 + i)));
                }
                chest.set(NVDataComponents.LIMITS, new UpgradeLimits(this.get(1) == TrainerMenu.ALLOW, map));
                return;
            }
            super.set(index, value);
        }
    }
}
