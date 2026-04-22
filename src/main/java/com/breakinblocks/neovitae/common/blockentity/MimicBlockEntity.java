package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.BlockMimic;
import com.breakinblocks.neovitae.util.ChatUtil;

import javax.annotation.Nullable;
import java.util.Objects;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.level.storage.TagValueInput;

public class MimicBlockEntity extends BaseBlockEntity {
    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();

    private BlockState mimic;
    public boolean dropItemsOnBreak = true;
    public CompoundTag tileTag = new CompoundTag();
    public BlockEntity mimicedTile = null;

    public int playerCheckRadius = 5;
    public int potionSpawnRadius = 5;
    public int potionSpawnInterval = 40;

    public ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    public MimicBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MimicBlockEntity(BlockPos pos, BlockState state) {
        this(NVTiles.MIMIC_TYPE.get(), pos, state);
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack heldItem, Direction side) {
        if (performSpecialAbility(player, side)) {
            return true;
        }

        if (player.isShiftKeyDown())
            return false;

        if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof BlockMimic)
                return false;
        }

        if (!inventory.getStackInSlot(0).isEmpty() && !player.getItemInHand(hand).isEmpty())
            return false;

        if (!dropItemsOnBreak && !player.isCreative())
            return false;

        ItemStack stack = player.getItemInHand(hand);
        if (mimic == null || mimic == Blocks.AIR.defaultBlockState()) {
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && !world.isClientSide()) {
                Block block = blockItem.getBlock();
                BlockState mimicState = block.defaultBlockState();
                if (!(mimicState.getBlock() instanceof BlockMimic)) {
                    this.setMimic(mimicState);
                }
            }
        }
        this.refreshTileEntity();

        if (player.isCreative()) {
            dropItemsOnBreak = inventory.getStackInSlot(0).isEmpty();
        }

        return true;
    }

    public boolean performSpecialAbility(Player player, Direction sideHit) {
        if (!player.isCreative()) {
            return false;
        }

        if (player.getUseItem().isEmpty() && !inventory.getStackInSlot(1).isEmpty()) {
            switch (sideHit) {
                case EAST, WEST -> {
                    if (player.isShiftKeyDown()) {
                        potionSpawnRadius = Math.max(potionSpawnRadius - 1, 0);
                        ChatUtil.sendNoSpam(player, Component.translatable("chat.neovitae.mimic.potionSpawnRadius.down", potionSpawnRadius));
                    } else {
                        potionSpawnRadius++;
                        ChatUtil.sendNoSpam(player, Component.translatable("chat.neovitae.mimic.potionSpawnRadius.up", potionSpawnRadius));
                    }
                }
                case NORTH, SOUTH -> {
                    if (player.isShiftKeyDown()) {
                        playerCheckRadius = Math.max(playerCheckRadius - 1, 0);
                        ChatUtil.sendNoSpam(player, Component.translatable("chat.neovitae.mimic.detectRadius.down", playerCheckRadius));
                    } else {
                        playerCheckRadius++;
                        ChatUtil.sendNoSpam(player, Component.translatable("chat.neovitae.mimic.detectRadius.up", playerCheckRadius));
                    }
                }
                case UP, DOWN -> {
                    if (player.isShiftKeyDown()) {
                        potionSpawnInterval = Math.max(potionSpawnInterval - 1, 1);
                        ChatUtil.sendNoSpam(player, Component.translatable("chat.neovitae.mimic.potionInterval.down", potionSpawnInterval));
                    } else {
                        potionSpawnInterval++;
                        ChatUtil.sendNoSpam(player, Component.translatable("chat.neovitae.mimic.potionInterval.up", potionSpawnInterval));
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void refreshTileEntity() {
        if (mimicedTile != null) {
            dropMimicedTileInventory();
        }
        mimicedTile = getTileFromStackWithTag(getLevel(), worldPosition, inventory.getStackInSlot(0), tileTag, mimic);
    }

    public void dropMimicedTileInventory() {
        if (!getLevel().isClientSide() && mimicedTile instanceof Container) {
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            inventory.setStackInSlot(1, ItemStack.EMPTY);
        }
    }

    @Nullable
    public static BlockEntity getTileFromStackWithTag(Level world, BlockPos pos, ItemStack stack, @Nullable CompoundTag tag, BlockState replacementState) {
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockState state = replacementState;
            if (block instanceof EntityBlock entityBlock) {
                BlockEntity tile = entityBlock.newBlockEntity(pos, state);

                if (tile == null)
                    return null;

                if (tag != null && !tag.isEmpty()) {
                    CompoundTag copyTag = tag.copy();
                    copyTag.putInt("x", pos.getX());
                    copyTag.putInt("y", pos.getY());
                    copyTag.putInt("z", pos.getZ());
                    try (ProblemReporter.ScopedCollector reporter =
                             new ProblemReporter.ScopedCollector(tile.problemPath(), org.slf4j.LoggerFactory.getLogger(MimicBlockEntity.class))) {
                        tile.loadWithComponents(TagValueInput.create(
                                reporter, world.registryAccess(), copyTag));
                    }
                }

                tile.setLevel(world);
                return tile;
            }
        }
        return null;
    }

    public void setMimic(BlockState mimic) {
        this.mimic = mimic;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            requestModelDataUpdate();
        }
    }

    public BlockState getMimic() {
        return mimic;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder().with(MIMIC, mimic).build();
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        dropItemsOnBreak = tag.getBooleanOr("dropItemsOnBreak", false);
        tileTag = tag.read("tileTag", CompoundTag.CODEC).orElseGet(CompoundTag::new);
        playerCheckRadius = tag.getIntOr("playerCheckRadius", 0);
        potionSpawnRadius = tag.getIntOr("potionSpawnRadius", 0);
        potionSpawnInterval = Math.max(1, tag.getIntOr("potionSpawnInterval", 0));

        tag.child("inventory").ifPresent(inventory::deserialize);

        tag.read("mimic", BlockState.CODEC).ifPresent(state -> mimic = state);

        mimicedTile = getTileFromStackWithTag(getLevel(), worldPosition, inventory.getStackInSlot(0), tileTag, mimic);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putBoolean("dropItemsOnBreak", dropItemsOnBreak);
        tag.store("tileTag", CompoundTag.CODEC, tileTag);
        tag.putInt("playerCheckRadius", playerCheckRadius);
        tag.putInt("potionSpawnRadius", potionSpawnRadius);
        tag.putInt("potionSpawnInterval", potionSpawnInterval);
        inventory.serialize(tag.child("inventory"));

        if (mimic != null) {
            tag.store("mimic", BlockState.CODEC, mimic);
        }
    }

    public void dropItems() {
        if (dropItemsOnBreak && level != null && !level.isClientSide()) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                }
            }
        }
        dropMimicedTileInventory();
    }

    public ItemStack getItem(int slot) {
        return inventory.getStackInSlot(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }
}
