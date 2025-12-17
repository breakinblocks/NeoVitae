package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.blockentity.PhantomBridgeTile;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual of the Phantom Bridge - Creates temporary walkable bridges
 * beneath players within the ritual's area of effect.
 * Uses PhantomBridgeBlock for solid temporary platforms.
 */
public class RitualPhantomBridge extends Ritual {

    public static final String BRIDGE_RANGE = "bridgeRange";
    private static final int BRIDGE_DEPTH = 2; // How far below players to create bridges
    private static final int BRIDGE_WIDTH = 1; // Radius around player's position

    // Track phantom bridge blocks created by this ritual
    private final Map<BlockPos, BlockState> phantomBlocks = new HashMap<>();

    public RitualPhantomBridge() {
        super("phantom_bridge", 0, 2000, "ritual." + NeoVitae.MODID + ".phantom_bridge");
        addBlockRange(BRIDGE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-15, -15, -15), 31, 31, 31));
        setMaximumVolumeAndDistanceOfRange(BRIDGE_RANGE, 0, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        AreaDescriptor range = RitualHelper.getEffectiveRange(ctx.master(), this, BRIDGE_RANGE);
        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, BRIDGE_RANGE, Player.class);

        int blocksCreated = 0;
        int maxBlocks = ctx.maxOperations(getRefreshCost());
        UUID owner = ctx.master().getOwner();

        // First, refresh duration on existing phantom blocks
        for (BlockPos pos : phantomBlocks.keySet()) {
            BlockState state = ctx.level().getBlockState(pos);
            if (state.is(BMBlocks.PHANTOM_BRIDGE_BLOCK.get())) {
                BlockEntity be = ctx.level().getBlockEntity(pos);
                if (be instanceof PhantomBridgeTile phantomTile) {
                    phantomTile.resetDuration();
                }
            }
        }

        // Create bridges under each player
        for (Player player : players) {
            if (blocksCreated >= maxBlocks) break;

            BlockPos playerPos = player.blockPosition();

            // Create a small platform below the player
            for (int x = -BRIDGE_WIDTH; x <= BRIDGE_WIDTH; x++) {
                for (int z = -BRIDGE_WIDTH; z <= BRIDGE_WIDTH; z++) {
                    for (int y = 1; y <= BRIDGE_DEPTH; y++) {
                        if (blocksCreated >= maxBlocks) break;

                        BlockPos targetPos = playerPos.offset(x, -y, z);

                        // Check if position is within ritual range
                        if (!range.isWithinArea(targetPos.subtract(ctx.masterPos()))) {
                            continue;
                        }

                        BlockState currentState = ctx.level().getBlockState(targetPos);

                        // If it's already a phantom bridge block, refresh it
                        if (currentState.is(BMBlocks.PHANTOM_BRIDGE_BLOCK.get())) {
                            BlockEntity be = ctx.level().getBlockEntity(targetPos);
                            if (be instanceof PhantomBridgeTile phantomTile) {
                                phantomTile.resetDuration();
                            }
                            continue;
                        }

                        // Only replace air or replaceable blocks
                        if (!currentState.isAir() && !currentState.canBeReplaced()) {
                            continue;
                        }

                        // Check block protection
                        if (!BlockProtectionHelper.tryReplaceBlock(ctx.level(), targetPos,
                                BMBlocks.PHANTOM_BRIDGE_BLOCK.get().defaultBlockState(), owner)) {
                            continue;
                        }

                        // Track and configure the new phantom block
                        phantomBlocks.put(targetPos.immutable(), currentState);
                        BlockEntity be = ctx.level().getBlockEntity(targetPos);
                        if (be instanceof PhantomBridgeTile phantomTile) {
                            phantomTile.setContainedBlockState(currentState);
                            phantomTile.resetDuration();
                        }
                        blocksCreated++;
                    }
                }
            }
        }

        ctx.syphon(getRefreshCost() * blocksCreated);
    }

    @Override
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        Level level = masterRitualStone.getLevel();
        if (level == null || level.isClientSide()) return;

        // Remove all phantom bridge blocks immediately
        for (Map.Entry<BlockPos, BlockState> entry : phantomBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = level.getBlockState(pos);
            if (state.is(BMBlocks.PHANTOM_BRIDGE_BLOCK.get())) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof PhantomBridgeTile phantomTile) {
                    phantomTile.removeBlock();
                }
            }
        }
        phantomBlocks.clear();
    }

    @Override
    public int getRefreshTime() {
        return 1; // Run every tick for smooth bridge creation
    }

    @Override
    public int getRefreshCost() {
        return 1;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualPhantomBridge();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        ListTag blockList = new ListTag();
        for (Map.Entry<BlockPos, BlockState> entry : phantomBlocks.entrySet()) {
            CompoundTag blockTag = new CompoundTag();
            blockTag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            blockTag.put("state", NbtUtils.writeBlockState(entry.getValue()));
            blockList.add(blockTag);
        }
        tag.put("phantomBlocks", blockList);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        phantomBlocks.clear();
        if (tag.contains("phantomBlocks")) {
            ListTag blockList = tag.getList("phantomBlocks", Tag.TAG_COMPOUND);
            for (int i = 0; i < blockList.size(); i++) {
                CompoundTag blockTag = blockList.getCompound(i);
                NbtUtils.readBlockPos(blockTag, "pos").ifPresent(pos -> {
                    BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), blockTag.getCompound("state"));
                    phantomBlocks.put(pos, state);
                });
            }
        }
    }
}
