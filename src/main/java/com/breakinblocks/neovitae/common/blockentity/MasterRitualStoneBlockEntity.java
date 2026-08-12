package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.advancement.NVCriteriaTriggers;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.event.RitualEvent;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MasterRitualStoneBlockEntity extends BaseBlockEntity implements IMasterRitualStone {

    private UUID owner;
    private Ritual currentRitual;
    private ResourceLocation currentRitualId;  // Store the ritual ID separately since currentRitual is a copy
    private boolean active = false;
    private Direction direction = Direction.NORTH;
    private boolean inverted = false;
    private int cooldown = 0;
    private long runningTime = 0;
    private long lastStructureCheckMillis = 0L;
    private static final long STRUCTURE_CHECK_INTERVAL_MILLIS = 5000L;
    private SpiritusType activeSpiritusAspect = SpiritusType.RAW;
    private int keepCount = 2;

    private Map<String, AreaDescriptor> blockRanges = new HashMap<>();
    private ResourceLocation rangesConfiguredFor;

    public MasterRitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.MASTER_RITUAL_STONE_TYPE.get(), pos, state);
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MasterRitualStoneBlockEntity tile) {
        if (level.isClientSide()) {
            if (tile.active && tile.currentRitual != null && !isRedstoneSuspended(level, pos, tile)) {
                LoopSoundManager.tryStartLoop(
                        resolveAmbientSound(tile.currentRitual, tile.currentRitualId), 0.2f, level, pos,
                        be -> be instanceof MasterRitualStoneBlockEntity mrs && mrs.active && mrs.currentRitual != null
                );
            }
            return;
        }

        if (tile.cooldown > 0) {
            tile.cooldown--;
            return;
        }

        if (tile.active && tile.currentRitual != null && !isRedstoneSuspended(level, pos, tile)) {
            long now = System.currentTimeMillis();
            if (now - tile.lastStructureCheckMillis >= STRUCTURE_CHECK_INTERVAL_MILLIS) {
                tile.lastStructureCheckMillis = now;
                if (!tile.checkStructureWithDirection(tile.currentRitual, tile.direction)) {
                    tile.stopRitual(Ritual.BreakType.BREAK_STONE);
                    return;
                }
            }

            tile.runningTime++;

            // Rune glow at the master stone while ritual is active
            if (tile.runningTime % 20 == 0) {
                ((ServerLevel) level).sendParticles(
                        new ColoredParticleOptions(NVParticles.RUNE_GLOW.get(), 0xAA0000),
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        1, 0.0, 0.0, 0.0, 0);
            }

            if (tile.runningTime % tile.currentRitual.getRefreshTime() == 0) {
                tile.performRitual();
            }
        }
    }

    private static boolean isRedstoneSuspended(Level level, BlockPos pos, MasterRitualStoneBlockEntity tile) {
        boolean powered = level.hasNeighborSignal(pos);
        return tile.inverted ? !powered : powered;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public BlockPos getBlockPos() {
        return worldPosition;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    @Override
    public Ritual getCurrentRitual() {
        return currentRitual;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
        setChanged();
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
        setChanged();
    }

    @Override
    public long getRunningTime() {
        return runningTime;
    }

    private static SoundEvent resolveAmbientSound(Ritual ritual, ResourceLocation ritualId) {
        if (ritualId != null) {
            Ritual registered = RitualRegistry.getRitual(ritualId);
            if (registered != null) {
                var holder = RitualRegistry.getRitualRegistry().wrapAsHolder(registered);
                RitualStats stats = holder.getData(NVDataMaps.RITUAL_STATS);
                if (stats != null && stats.ambientSound().isPresent()) {
                    SoundEvent fromData = BuiltInRegistries.SOUND_EVENT.get(stats.ambientSound().get());
                    if (fromData != null) return fromData;
                }
            }
        }
        return ritual.getAmbientSound();
    }

    @Override
    public boolean activateRitual(Ritual ritual, Player player, int crystalLevel) {
        if (level == null || level.isClientSide()) return false;

        var ritualHolder = RitualRegistry.getRitualRegistry().wrapAsHolder(ritual);
        RitualStats stats = ritualHolder.getData(NVDataMaps.RITUAL_STATS);
        if (stats != null && !stats.enabled()) {
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.ritual.disabled"), true);
            }
            return false;
        }

        if (ritual.getCrystalLevel() > crystalLevel) {
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable("ritual.neovitae.crystalLevel.insufficient"), true);
            }
            return false;
        }

        if (!checkStructure(ritual)) {
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable("ritual.neovitae.structure.invalid"), true);
            }
            return false;
        }

        if (player != null && !ritual.canActivate(this, player)) {
            player.displayClientMessage(
                    Component.translatable("ritual.neovitae.activation.blocked"), true);
            return false;
        }

        Anima network = AnimaHelper.getAnima(player.getUUID());
        if (network == null || network.getCurrentEV() < ritual.getActivationCost()) {
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable("ritual.neovitae.activation.insufficient"), true);
            }
            return false;
        }

        RitualEvent.Activate activateEvent = new RitualEvent.Activate(this, ritual, player, crystalLevel);
        if (NeoForge.EVENT_BUS.post(activateEvent).isCanceled()) {
            return false;
        }

        if (!ritual.activateRitual(this, player, player.getUUID())) {
            player.displayClientMessage(
                    Component.translatable("ritual.neovitae.activation.blocked"), true);
            return false;
        }

        if (active && currentRitual != null) {
            stopRitual(Ritual.BreakType.ACTIVATE);
        }

        this.currentRitual = ritual.getNewCopy();
        this.currentRitualId = RitualRegistry.getId(ritual);  // Store ID from original, not the copy
        this.owner = player.getUUID();
        this.active = true;
        this.runningTime = 0;

        initializeBlockRanges(ritual);

        network.syphon(ticket(ritual.getActivationCost()));

        level.playSound(null, worldPosition, NVSounds.RITUAL_ACTIVATE.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
        ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 10, 0.4, 0.2, 0.4, 0);

        NeoForge.EVENT_BUS.post(new RitualEvent.Activated(this, currentRitual, player));

        if (player instanceof ServerPlayer serverPlayer && currentRitualId != null) {
            NVCriteriaTriggers.RITUAL_ACTIVATED.get().trigger(serverPlayer, currentRitualId.toString());
        }

        setChanged();
        return true;
    }

    /**
     * Force activates a ritual without cost, structure check, or owner requirement.
     * Used for admin commands and testing.
     */
    public void forceActivateRitual(Ritual ritual, @Nullable Player player) {
        if (level == null || level.isClientSide()) return;

        if (active && currentRitual != null) {
            stopRitual(Ritual.BreakType.ACTIVATE);
        }

        this.currentRitual = ritual.getNewCopy();
        this.currentRitualId = RitualRegistry.getId(ritual);  // Store ID from original, not the copy
        this.owner = player != null ? player.getUUID() : null;
        this.active = true;
        this.runningTime = 0;

        initializeBlockRanges(ritual);

        level.playSound(null, worldPosition, NVSounds.RITUAL_ACTIVATE.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
        ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 10, 0.4, 0.2, 0.4, 0);

        if (player != null) {
            NeoForge.EVENT_BUS.post(new RitualEvent.Activated(this, currentRitual, player));

            if (player instanceof ServerPlayer serverPlayer && currentRitualId != null) {
                NVCriteriaTriggers.RITUAL_ACTIVATED.get().trigger(serverPlayer, currentRitualId.toString());
            }
        }

        setChanged();
    }

    @Override
    public void performRitual() {
        if (level == null || level.isClientSide() || !active || currentRitual == null) return;

        if (owner == null) {
            stopRitual(Ritual.BreakType.DEACTIVATE);
            return;
        }

        Anima network = getOwnerNetwork();
        if (network == null) {
            return;
        }

        if (network.getCurrentEV() < currentRitual.getRefreshCost()) {
            return;
        }

        RitualEvent.Perform performEvent = new RitualEvent.Perform(this, currentRitual);
        if (NeoForge.EVENT_BUS.post(performEvent).isCanceled()) {
            return;
        }

        currentRitual.performRitual(this);
    }

    @Override
    public void stopRitual(Ritual.BreakType breakType) {
        if (currentRitual != null) {
            NeoForge.EVENT_BUS.post(new RitualEvent.Stop(this, currentRitual, breakType));
            currentRitual.stopRitual(this, breakType);
            if (level != null && !level.isClientSide()) {
                level.playSound(null, worldPosition, NVSounds.RITUAL_COMPLETE.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
                StreamPresets.voidMark(worldPosition).build().sendToNearby((ServerLevel) level, worldPosition, 64);
            }
        }
        currentRitual = null;
        currentRitualId = null;
        active = false;
        runningTime = 0;
        blockRanges.clear();
        setChanged();
    }

    @Override
    public boolean checkStructure(Ritual ritual) {
        if (level == null) return false;

        for (Direction dir : new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}) {
            if (checkStructureWithDirection(ritual, dir)) {
                this.direction = dir;
                setChanged();
                return true;
            }
        }
        return false;
    }

    private boolean checkStructureWithDirection(Ritual ritual, Direction dir) {
        for (RitualComponent component : getRitualComponents(ritual)) {
            BlockPos rotatedOffset = rotateOffset(component.offset(), dir);
            BlockPos componentPos = getBlockPos().offset(rotatedOffset);
            BlockState state = level.getBlockState(componentPos);

            if (state.getBlock() instanceof IRitualStone ritualStone) {
                if (!ritualStone.isRuneType(level, componentPos, component.runeType())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private BlockPos rotateOffset(BlockPos offset, Direction dir) {
        return switch (dir) {
            case NORTH -> offset;
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default -> offset;
        };
    }

    private List<RitualComponent> getRitualComponents(Ritual ritual) {
        return RitualLayouts.get(getLevel(), ritual);
    }

    @Override
    public AreaDescriptor getBlockRange(String key) {
        return blockRanges.get(key);
    }

    @Override
    public Map<String, AreaDescriptor> getBlockRanges() {
        return blockRanges;
    }

    @Override
    public void setBlockRange(String key, AreaDescriptor descriptor) {
        blockRanges.put(key, descriptor);
        setChanged();
    }

    public ResourceLocation getCurrentRitualId() {
        return currentRitualId;
    }

    public void markRangesConfiguredFor(ResourceLocation ritualId) {
        this.rangesConfiguredFor = ritualId;
        setChanged();
    }

    private void initializeBlockRanges(Ritual ritual) {
        boolean keepPresets = rangesConfiguredFor != null && rangesConfiguredFor.equals(currentRitualId);
        Map<String, AreaDescriptor> preconfigured = keepPresets ? new HashMap<>(blockRanges) : Map.of();
        blockRanges.clear();
        for (Map.Entry<String, AreaDescriptor> entry : ritual.getModifiableRanges().entrySet()) {
            AreaDescriptor preset = preconfigured.get(entry.getKey());
            blockRanges.put(entry.getKey(), preset != null ? preset : entry.getValue().copy());
        }
        rangesConfiguredFor = currentRitualId;
    }

    @Override
    public void setBlockRanges(Map<String, AreaDescriptor> ranges) {
        this.blockRanges = new HashMap<>(ranges);
        setChanged();
    }

    @Override
    public SpiritusType getActiveSpiritusAspect() {
        return activeSpiritusAspect;
    }

    @Override
    public void setActiveSpiritusAspect(SpiritusType type) {
        this.activeSpiritusAspect = type;
        setChanged();
    }

    @Override
    public int getKeepCount() {
        return keepCount;
    }

    public void setKeepCount(int count) {
        this.keepCount = Math.max(0, Math.min(count, 64));
        setChanged();
    }

    @Override
    public void provideInformationOfRitualToPlayer(Player player) {
        if (currentRitual != null) {
            Component[] info = currentRitual.provideInformationOfRitualToPlayer(player);
            for (Component component : info) {
                player.displayClientMessage(component, false);
            }
        }
    }

    @Override
    public void provideInformationOfRangeToPlayer(Player player, String key) {
        if (currentRitual != null) {
            player.displayClientMessage(currentRitual.provideInformationOfRangeToPlayer(player, key), false);
        }
    }

    @Override
    public void provideInformationOfOffsetToPlayer(Player player, AreaDescriptor.Rectangle descriptor) {
        BlockPos min = descriptor.getMinimumOffset();
        BlockPos max = descriptor.getMaximumOffset();
        player.displayClientMessage(
                Component.translatable("ritual.neovitae.offset.info",
                        min.getX(), min.getY(), min.getZ(),
                        max.getX(), max.getY(), max.getZ()), false);
    }

    @Override
    public void notifyOwner(Component message) {
        if (owner == null || level == null || level.isClientSide()) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(owner);
            if (player != null) {
                player.displayClientMessage(message, false);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (owner != null) {
            tag.putUUID("owner", owner);
        }
        tag.putBoolean("active", active);
        tag.putBoolean("inverted", inverted);
        tag.putInt("cooldown", cooldown);
        tag.putLong("runningTime", runningTime);
        tag.putString("direction", direction.getName());
        tag.putString("activeAspect", activeSpiritusAspect.getSerializedName());
        tag.putInt("keepCount", keepCount);

        if (currentRitual != null && currentRitualId != null) {
            tag.putString("ritual", currentRitualId.toString());

            CompoundTag ritualData = new CompoundTag();
            currentRitual.writeToNBT(ritualData);
            tag.put("ritualData", ritualData);
        }

        CompoundTag rangesTag = new CompoundTag();
        for (Map.Entry<String, AreaDescriptor> entry : blockRanges.entrySet()) {
            CompoundTag rangeTag = new CompoundTag();
            AreaDescriptor desc = entry.getValue();
            if (desc instanceof AreaDescriptor.Rectangle) {
                rangeTag.putString("type", "rectangle");
            } else if (desc instanceof AreaDescriptor.HemiSphere) {
                rangeTag.putString("type", "hemisphere");
            } else if (desc instanceof AreaDescriptor.Cross) {
                rangeTag.putString("type", "cross");
            }
            desc.saveToNBT(rangeTag);
            rangesTag.put(entry.getKey(), rangeTag);
        }
        if (!rangesTag.isEmpty()) {
            tag.put("blockRanges", rangesTag);
        }
        if (rangesConfiguredFor != null) {
            tag.putString("rangesConfiguredFor", rangesConfiguredFor.toString());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.hasUUID("owner")) {
            owner = tag.getUUID("owner");
        }
        active = tag.getBoolean("active");
        inverted = tag.getBoolean("inverted");
        cooldown = tag.getInt("cooldown");
        runningTime = tag.getLong("runningTime");
        keepCount = tag.contains("keepCount") ? tag.getInt("keepCount") : 2;

        if (tag.contains("direction")) {
            direction = Direction.byName(tag.getString("direction"));
            if (direction == null) direction = Direction.NORTH;
        }

        if (tag.contains("activeAspect")) {
            try {
                activeSpiritusAspect = SpiritusType.valueOf(tag.getString("activeAspect").toUpperCase());
            } catch (IllegalArgumentException e) {
                activeSpiritusAspect = SpiritusType.RAW;
            }
        }

        if (tag.contains("ritual")) {
            ResourceLocation ritualId = ResourceLocation.parse(tag.getString("ritual"));
            Ritual ritual = RitualRegistry.getRitual(ritualId);
            if (ritual != null) {
                currentRitualId = ritualId;  // Restore the ID
                currentRitual = ritual.getNewCopy();

                if (tag.contains("ritualData")) {
                    currentRitual.readFromNBT(tag.getCompound("ritualData"));
                }
            } else {
                active = false;
                currentRitualId = null;
            }
        } else if (active) {
            active = false;
        }

        rangesConfiguredFor = tag.contains("rangesConfiguredFor")
                ? ResourceLocation.parse(tag.getString("rangesConfiguredFor"))
                : null;

        blockRanges.clear();
        if (tag.contains("blockRanges")) {
            CompoundTag rangesTag = tag.getCompound("blockRanges");
            for (String key : rangesTag.getAllKeys()) {
                CompoundTag rangeTag = rangesTag.getCompound(key);
                String type = rangeTag.getString("type");
                AreaDescriptor desc = createAreaDescriptor(type);
                if (desc != null) {
                    desc.loadFromNBT(rangeTag);
                    blockRanges.put(key, desc);
                }
            }
        } else if (currentRitual != null) {
            for (Map.Entry<String, AreaDescriptor> entry : currentRitual.getModifiableRanges().entrySet()) {
                blockRanges.put(entry.getKey(), entry.getValue().copy());
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide() && active && currentRitual != null) {
            currentRitual.onLoad(this);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide() && currentRitual != null) {
            currentRitual.onUnload(this);
        }
        super.setRemoved();
    }

    private AreaDescriptor createAreaDescriptor(String type) {
        return switch (type) {
            case "rectangle" -> new AreaDescriptor.Rectangle(BlockPos.ZERO, BlockPos.ZERO);
            case "hemisphere" -> new AreaDescriptor.HemiSphere(BlockPos.ZERO, 1);
            case "cross" -> new AreaDescriptor.Cross(BlockPos.ZERO, 1, 1);
            default -> {
                NeoVitae.LOGGER.warn("Unknown AreaDescriptor type: {}", type);
                yield null;
            }
        };
    }
}
