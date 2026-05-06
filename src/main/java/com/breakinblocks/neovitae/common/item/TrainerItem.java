package com.breakinblocks.neovitae.common.item;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.menu.GhostItemHandler;
import com.breakinblocks.neovitae.common.menu.TrainerMenu;

import java.util.List;

public class TrainerItem extends Item {
    public TrainerItem(Item.Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ItemStack chest = SentientHelper.getChest(player);
            if (SentientHelper.isNeverValid(chest)) {
                return InteractionResult.FAIL;
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
            SimpleContainerData data = new SimpleContainerData(19) {
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
            };
            List<Pair<Integer, Integer>> start = limits.fillData(handler);
            start.forEach(pair -> data.set(pair.first(), pair.second()));

            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (id, inv, playerIn) -> new TrainerMenu(id, inv, handler, data, playerIn.getInventory().getSelectedSlot()),
                            Component.translatable(getDescriptionId())
                    ),
                    buf -> buf.writeInt(serverPlayer.getInventory().getSelectedSlot())
            );
        }

        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }
}
