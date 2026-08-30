package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.world.AlternatorLinks;

public class AlternatorMenu extends AbstractContainerMenu {

    public static final int DATA_DELAY = 0;
    public static final int DATA_STOP_ON_REDSTONE = 1;
    public static final int DATA_RECEIVERS = 2;
    public static final int DATA_SIZE = 3;

    public final DungeonAlternatorBlockEntity tile;
    private final BlockPos pos;
    private final ContainerData data;

    public AlternatorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        super(NVMenus.ALTERNATOR.get(), containerId);
        this.pos = buf.readBlockPos();
        this.tile = playerInventory.player.level().getBlockEntity(pos) instanceof DungeonAlternatorBlockEntity alternator
                ? alternator : null;
        this.data = new SimpleContainerData(DATA_SIZE);
        addDataSlots(data);
    }

    public AlternatorMenu(int containerId, Inventory playerInventory, DungeonAlternatorBlockEntity tile) {
        super(NVMenus.ALTERNATOR.get(), containerId);
        this.tile = tile;
        this.pos = tile.getBlockPos();
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_DELAY -> tile.getDelay();
                    case DATA_STOP_ON_REDSTONE -> tile.stopsOnRedstone() ? 1 : 0;
                    case DATA_RECEIVERS -> tile.getReceivers().size();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return DATA_SIZE;
            }
        };
        addDataSlots(data);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getDelay() {
        return data.get(DATA_DELAY);
    }

    public boolean stopsOnRedstone() {
        return data.get(DATA_STOP_ON_REDSTONE) != 0;
    }

    public int getReceiverCount() {
        return data.get(DATA_RECEIVERS);
    }

    public int getMaxReceivers() {
        return AlternatorLinks.MAX_RECEIVERS;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (tile == null || tile.getLevel() == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(tile.getLevel(), pos),
                player, DungeonBlocks.ALTERNATOR.block().get());
    }
}
