package com.breakinblocks.neovitae.common.block.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.breakinblocks.neovitae.common.blockentity.BaseBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.world.AlternatorLinks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DungeonAlternatorBlockEntity extends BaseBlockEntity {

    public static final int DEFAULT_PULSE_RATE = 40;
    public static final int PULSE_LENGTH = 2;
    public static final int MAX_DELAY = 72000;

    private int tickCounter = 0;
    private int delay = 0;
    private boolean stopOnRedstone = true;
    private final List<BlockPos> receivers = new ArrayList<>();
    private Boolean lastEmit = null;

    public DungeonAlternatorBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.DUNGEON_ALTERNATOR_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DungeonAlternatorBlockEntity tile) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (tile.delay <= 0) {
            tile.refreshRunning();
            return;
        }
        tile.tickCounter++;
        if (tile.tickCounter >= tile.delay) {
            tile.tickCounter = 0;
        }
        tile.applyEmission(serverLevel, tile.delay <= PULSE_LENGTH || tile.tickCounter < PULSE_LENGTH);
    }

    private void applyEmission(ServerLevel level, boolean emit) {
        BlockState state = getBlockState();
        boolean stateMatches = state.getValue(BlockAlternator.ACTIVE) == emit;
        if (lastEmit != null && lastEmit == emit && stateMatches) {
            return;
        }
        lastEmit = emit;
        if (!stateMatches) {
            level.setBlock(worldPosition, state.setValue(BlockAlternator.ACTIVE, emit), Block.UPDATE_ALL);
        }
        for (BlockPos receiver : receivers) {
            AlternatorLinks.setPowered(level, receiver, emit);
        }
        for (BlockPos receiver : receivers) {
            notifyReceiver(level, receiver);
        }
    }

    private void notifyReceiver(ServerLevel level, BlockPos receiver) {
        if (!level.isLoaded(receiver)) {
            return;
        }
        level.neighborChanged(receiver, getBlockState().getBlock(), null);
        level.updateNeighborsAt(receiver, level.getBlockState(receiver).getBlock());
    }

    public void refreshRunning() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        boolean paused = stopOnRedstone && hasHardPower(serverLevel);
        boolean running = delay > 0 && !paused;
        if (!running) {
            applyEmission(serverLevel, false);
        }
        BlockState state = getBlockState();
        if (state.getValue(BlockAlternator.RUNNING) != running) {
            serverLevel.setBlock(worldPosition, state.setValue(BlockAlternator.RUNNING, running), Block.UPDATE_ALL);
        }
    }

    private boolean hasHardPower(ServerLevel level) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = worldPosition.relative(dir);
            if (receivers.contains(neighbor)) {
                continue;
            }
            if (level.getDirectSignal(neighbor, dir) > 0) {
                return true;
            }
        }
        return false;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int newDelay) {
        int clamped = Math.max(0, Math.min(MAX_DELAY, newDelay));
        if (clamped == delay) {
            return;
        }
        delay = clamped;
        tickCounter = 0;
        setChanged();
        refreshRunning();
    }

    public boolean stopsOnRedstone() {
        return stopOnRedstone;
    }

    public void setStopOnRedstone(boolean value) {
        if (value == stopOnRedstone) {
            return;
        }
        stopOnRedstone = value;
        setChanged();
        refreshRunning();
    }

    public List<BlockPos> getReceivers() {
        return Collections.unmodifiableList(receivers);
    }

    public boolean hasReceiver(BlockPos pos) {
        return receivers.contains(pos);
    }

    public boolean addReceiver(BlockPos pos) {
        if (receivers.size() >= AlternatorLinks.MAX_RECEIVERS || receivers.contains(pos)) {
            return false;
        }
        receivers.add(pos.immutable());
        if (level instanceof ServerLevel serverLevel) {
            AlternatorLinks.link(serverLevel, pos, worldPosition);
            if (Boolean.TRUE.equals(lastEmit)) {
                AlternatorLinks.setPowered(serverLevel, pos, true);
                notifyReceiver(serverLevel, pos);
            }
        }
        setChanged();
        return true;
    }

    public boolean removeReceiver(BlockPos pos) {
        if (!receivers.remove(pos)) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            AlternatorLinks.unlink(serverLevel, pos);
            notifyReceiver(serverLevel, pos);
        }
        setChanged();
        return true;
    }

    public void releaseAllReceivers() {
        if (!(level instanceof ServerLevel serverLevel)) {
            receivers.clear();
            return;
        }
        List<BlockPos> released = new ArrayList<>(receivers);
        receivers.clear();
        for (BlockPos receiver : released) {
            AlternatorLinks.unlink(serverLevel, receiver);
        }
        for (BlockPos receiver : released) {
            notifyReceiver(serverLevel, receiver);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        releaseAllReceivers();
        super.preRemoveSideEffects(pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            for (BlockPos receiver : receivers) {
                AlternatorLinks.link(serverLevel, receiver, worldPosition);
            }
            serverLevel.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level instanceof ServerLevel serverLevel) {
            for (BlockPos receiver : receivers) {
                if (AlternatorLinks.setPowered(serverLevel, receiver, false)) {
                    notifyReceiver(serverLevel, receiver);
                }
            }
            lastEmit = null;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("pulseRate", delay);
        tag.putBoolean("stopOnRedstone", stopOnRedstone);
        tag.store("receivers", BlockPos.CODEC.listOf(), List.copyOf(receivers));
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tickCounter = tag.getIntOr("tickCounter", 0);
        delay = tag.getIntOr("pulseRate", DEFAULT_PULSE_RATE);
        stopOnRedstone = tag.getBooleanOr("stopOnRedstone", true);
        receivers.clear();
        tag.read("receivers", BlockPos.CODEC.listOf()).ifPresent(receivers::addAll);
    }
}
