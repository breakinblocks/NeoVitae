package com.breakinblocks.neovitae.ritual.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.PhantomBridgeBlockEntity;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.HashMap;
import java.util.Iterator;
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

    private static final Codec<BlockStateEntry> ENTRY_CODEC = RecordCodecBuilder.create(b -> b.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BlockStateEntry::pos),
            BlockState.CODEC.fieldOf("state").forGetter(BlockStateEntry::state)
    ).apply(b, BlockStateEntry::new));

    private static final Codec<List<BlockStateEntry>> ENTRY_LIST_CODEC = ENTRY_CODEC.listOf();

    private record BlockStateEntry(BlockPos pos, BlockState state) {}

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

        // Refresh duration on existing phantom blocks, dropping stale entries
        // whose bridge has already been broken/expired so the map stays bounded.
        Iterator<Map.Entry<BlockPos, BlockState>> it = phantomBlocks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, BlockState> entry = it.next();
            BlockPos pos = entry.getKey();
            BlockState state = ctx.level().getBlockState(pos);
            if (!state.is(NVBlocks.PHANTOM_BRIDGE_BLOCK.get())) {
                it.remove();
                continue;
            }
            BlockEntity be = ctx.level().getBlockEntity(pos);
            if (be instanceof PhantomBridgeBlockEntity phantomTile) {
                phantomTile.resetDuration();
            }
        }

        for (Player player : players) {
            if (blocksCreated >= maxBlocks) break;

            BlockPos playerPos = player.blockPosition();

            for (int x = -BRIDGE_WIDTH; x <= BRIDGE_WIDTH; x++) {
                for (int z = -BRIDGE_WIDTH; z <= BRIDGE_WIDTH; z++) {
                    for (int y = 1; y <= BRIDGE_DEPTH; y++) {
                        if (blocksCreated >= maxBlocks) break;

                        BlockPos targetPos = playerPos.offset(x, -y, z);

                        if (!range.isWithinArea(targetPos.subtract(ctx.masterPos()))) {
                            continue;
                        }

                        BlockState currentState = ctx.level().getBlockState(targetPos);

                        // If it's already a phantom bridge block, refresh it
                        if (currentState.is(NVBlocks.PHANTOM_BRIDGE_BLOCK.get())) {
                            BlockEntity be = ctx.level().getBlockEntity(targetPos);
                            if (be instanceof PhantomBridgeBlockEntity phantomTile) {
                                phantomTile.resetDuration();
                            }
                            continue;
                        }

                        // Only replace air or replaceable blocks
                        if (!currentState.isAir() && !currentState.canBeReplaced()) {
                            continue;
                        }

                        if (!BlockProtectionHelper.tryReplaceBlock(ctx.level(), targetPos,
                                NVBlocks.PHANTOM_BRIDGE_BLOCK.get().defaultBlockState(), owner)) {
                            continue;
                        }

                        // Track and configure the new phantom block
                        phantomBlocks.put(targetPos.immutable(), currentState);
                        BlockEntity be = ctx.level().getBlockEntity(targetPos);
                        if (be instanceof PhantomBridgeBlockEntity phantomTile) {
                            phantomTile.setContainedBlockState(currentState);
                            phantomTile.resetDuration();
                        }
                        blocksCreated++;
                        final BlockPos bridgePos = targetPos.immutable();
                        RitualHelper.chanceStream(ctx.level(), 6, () ->
                                StreamPresets.arcaneBolt(ctx.masterPos(), bridgePos).build()
                                        .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 64));
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

        for (Map.Entry<BlockPos, BlockState> entry : phantomBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = level.getBlockState(pos);
            if (state.is(NVBlocks.PHANTOM_BRIDGE_BLOCK.get())) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof PhantomBridgeBlockEntity phantomTile) {
                    phantomTile.removeBlock();
                }
            }
        }
        phantomBlocks.clear();
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
        List<BlockStateEntry> entries = phantomBlocks.entrySet().stream()
                .map(e -> new BlockStateEntry(e.getKey(), e.getValue()))
                .toList();
        ENTRY_LIST_CODEC.encodeStart(NbtOps.INSTANCE, entries).result()
                .ifPresent(t -> tag.put("phantomBlocks", t));
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        phantomBlocks.clear();
        Tag raw = tag.get("phantomBlocks");
        if (raw == null) return;
        ENTRY_LIST_CODEC.parse(NbtOps.INSTANCE, raw).result().ifPresent(list -> {
            for (BlockStateEntry entry : list) {
                phantomBlocks.put(entry.pos(), entry.state());
            }
        });
    }
}
