package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.ItemRitualReader;
import com.breakinblocks.neovitae.ritual.EnumRitualReaderState;

import java.util.ArrayList;
import java.util.List;

public class RitualConfiguratorMenu extends AbstractContainerMenu {

    public static final int ASPECT_BUTTON_BASE = 100;
    public static final int EDIT_IN_WORLD_BUTTON = 200;
    private static final double MAX_REACH_SQR = 64.0;

    public record RangeInfo(String key, int sizeX, int sizeY, int sizeZ,
                            int maxVolume, int maxHorizontal, int maxVertical) {}

    private final InteractionHand hand;
    private final BlockPos masterPos;
    private final String ritualKey;
    private final List<RangeInfo> ranges;
    private final int initialAspect;
    private final boolean ritualActive;

    public RitualConfiguratorMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        super(NVMenus.RITUAL_CONFIGURATOR.get(), containerId);
        this.hand = buf.readEnum(InteractionHand.class);
        this.masterPos = buf.readBlockPos();
        this.ritualKey = buf.readUtf();
        this.ranges = buf.readList(b -> new RangeInfo(b.readUtf(),
                b.readVarInt(), b.readVarInt(), b.readVarInt(),
                b.readVarInt(), b.readVarInt(), b.readVarInt()));
        this.initialAspect = buf.readVarInt();
        this.ritualActive = buf.readBoolean();
    }

    public RitualConfiguratorMenu(int containerId, Inventory playerInv, InteractionHand hand,
                                  BlockPos masterPos, String ritualKey, List<RangeInfo> ranges,
                                  int initialAspect, boolean ritualActive) {
        super(NVMenus.RITUAL_CONFIGURATOR.get(), containerId);
        this.hand = hand;
        this.masterPos = masterPos;
        this.ritualKey = ritualKey;
        this.ranges = new ArrayList<>(ranges);
        this.initialAspect = initialAspect;
        this.ritualActive = ritualActive;
    }

    public static void write(FriendlyByteBuf buf, InteractionHand hand, BlockPos masterPos,
                             String ritualKey, List<RangeInfo> ranges, int initialAspect, boolean ritualActive) {
        buf.writeEnum(hand);
        buf.writeBlockPos(masterPos);
        buf.writeUtf(ritualKey);
        buf.writeCollection(ranges, (b, r) -> {
            b.writeUtf(r.key());
            b.writeVarInt(r.sizeX());
            b.writeVarInt(r.sizeY());
            b.writeVarInt(r.sizeZ());
            b.writeVarInt(r.maxVolume());
            b.writeVarInt(r.maxHorizontal());
            b.writeVarInt(r.maxVertical());
        });
        buf.writeVarInt(initialAspect);
        buf.writeBoolean(ritualActive);
    }

    public boolean isRitualActive() {
        return ritualActive;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public String getRitualKey() {
        return ritualKey;
    }

    public List<RangeInfo> getRanges() {
        return ranges;
    }

    public int getInitialAspect() {
        return initialAspect;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof ItemRitualReader reader)) return false;
        if (player.distanceToSqr(masterPos.getX() + 0.5, masterPos.getY() + 0.5, masterPos.getZ() + 0.5) > MAX_REACH_SQR) {
            return false;
        }

        if (id >= 0 && id < ranges.size()) {
            reader.setRangeKey(stack, ranges.get(id).key());
            sync(player);
            return true;
        }

        if (id >= ASPECT_BUTTON_BASE && id < ASPECT_BUTTON_BASE + SpiritusType.values().length) {
            SpiritusType type = SpiritusType.values()[id - ASPECT_BUTTON_BASE];
            BlockEntity be = player.level().getBlockEntity(masterPos);
            if (be instanceof MasterRitualStoneBlockEntity mrs) {
                mrs.setActiveSpiritusAspect(type);
            }
            return true;
        }

        if (id == EDIT_IN_WORLD_BUTTON) {
            if (reader.getRangeKey(stack).isEmpty() && !ranges.isEmpty()) {
                reader.setRangeKey(stack, ranges.get(0).key());
            }
            reader.setState(stack, EnumRitualReaderState.SET_AREA_CORNER_1);
            if (player instanceof ServerPlayer sp) {
                sp.inventoryMenu.broadcastChanges();
                sp.closeContainer();
            }
            return true;
        }

        return false;
    }

    private void sync(Player player) {
        if (player instanceof ServerPlayer sp) {
            sp.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand).getItem() instanceof ItemRitualReader;
    }
}
