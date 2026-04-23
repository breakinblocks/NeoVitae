package com.breakinblocks.neovitae.common.menu;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.DungeonSealBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class DungeonSealMenu extends AbstractContainerMenu {

    public static final List<Supplier<ItemDungeonKey>> KEY_TYPES = List.of(
            () -> NVItems.SIMPLE_KEY.get(),
            () -> NVItems.STANDARD_KEY.get(),
            () -> NVItems.MINE_KEY.get(),
            () -> NVItems.MINE_ENTRANCE_KEY.get(),
            () -> NVItems.BOSS_KEY.get()
    );

    private final BlockPos sealPos;
    private final DungeonSealBlockEntity seal;
    private final Inventory playerInventory;
    private final int validKeyMask;
    private final ContainerData keyCounts;

    // Client constructor
    public DungeonSealMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        super(NVMenus.DUNGEON_SEAL.get(), containerId);
        this.playerInventory = playerInv;
        this.sealPos = buf.readBlockPos();
        this.validKeyMask = buf.readInt();

        if (playerInv.player.level().getBlockEntity(sealPos) instanceof DungeonSealBlockEntity sealBE) {
            this.seal = sealBE;
        } else {
            this.seal = null;
        }

        this.keyCounts = new SimpleContainerData(KEY_TYPES.size());
        this.addDataSlots(keyCounts);
    }

    // Server constructor
    public DungeonSealMenu(int containerId, Inventory playerInv, DungeonSealBlockEntity seal, int validKeyMask) {
        super(NVMenus.DUNGEON_SEAL.get(), containerId);
        this.playerInventory = playerInv;
        this.sealPos = seal.getBlockPos();
        this.seal = seal;
        this.validKeyMask = validKeyMask;

        this.keyCounts = new ContainerData() {
            @Override
            public int get(int index) {
                if (index < 0 || index >= KEY_TYPES.size()) return 0;
                Item keyItem = KEY_TYPES.get(index).get();
                int count = 0;
                for (int i = 0; i < playerInv.getContainerSize(); i++) {
                    ItemStack stack = playerInv.getItem(i);
                    if (stack.getItem() == keyItem) {
                        count += stack.getCount();
                    }
                }
                return count;
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() { return KEY_TYPES.size(); }
        };
        this.addDataSlots(keyCounts);
    }

    public boolean isKeyValid(int index) {
        return index >= 0 && index < KEY_TYPES.size() && (validKeyMask & (1 << index)) != 0;
    }

    public int getKeyCount(int index) {
        return keyCounts.get(index);
    }

    public int getValidKeyMask() {
        return validKeyMask;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= KEY_TYPES.size()) return false;
        if (!isKeyValid(id)) return false;

        if (seal == null || seal.isRemoved()) return false;

        ItemDungeonKey keyItem = KEY_TYPES.get(id).get();

        // Find the key in inventory
        int keySlot = -1;
        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            if (playerInventory.getItem(i).getItem() == keyItem) {
                keySlot = i;
                break;
            }
        }

        if (keySlot == -1 && !player.getAbilities().instabuild) return false;

        boolean success = seal.requestRoomFromControllerWithKey(player, keyItem);

        if (success) {
            if (!player.getAbilities().instabuild && keySlot >= 0) {
                playerInventory.getItem(keySlot).shrink(1);
            }
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.dungeon.seal.opened")
                            .withStyle(ChatFormatting.GREEN));
        } else {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.dungeon.seal.failed")
                            .withStyle(ChatFormatting.RED));
        }

        if (player instanceof ServerPlayer sp) {
            sp.closeContainer();
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (seal == null || seal.getLevel() == null) return false;
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(seal.getLevel(), sealPos),
                player, NVBlocks.DUNGEON_SEAL.block().get());
    }

    public static int computeValidKeyMask(DungeonSealBlockEntity seal) {
        int mask = 0;
        for (int i = 0; i < KEY_TYPES.size(); i++) {
            if (KEY_TYPES.get(i).get().canOpenDoor(seal.getPotentialRoomTypes())) {
                mask |= (1 << i);
            }
        }
        return mask;
    }
}
