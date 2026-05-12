package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
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
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.UUIDUtil;

public class MasterRitualStoneBlockEntity extends BaseBlockEntity implements IMasterRitualStone {

    private UUID owner;
    private Ritual currentRitual;
    private Identifier currentRitualId;  // Store the ritual ID separately since currentRitual is a copy
    private boolean active = false;
    private Direction direction = Direction.NORTH;
    private boolean inverted = false;
    private int cooldown = 0;
    private long runningTime = 0;
    private SpiritusType activeSpiritusAspect = SpiritusType.RAW;

    private Map<String, AreaDescriptor> blockRanges = new HashMap<>();

    public MasterRitualStoneBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.MASTER_RITUAL_STONE_TYPE.get(), pos, state);
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MasterRitualStoneBlockEntity tile) {
        if (level.isClientSide()) {
            if (tile.active && tile.currentRitual != null) {
                LoopSoundManager.tryStartLoop(
                        NVSounds.RITUAL_AMBIENT.get(), 0.2f, level, pos,
                        be -> be instanceof MasterRitualStoneBlockEntity mrs && mrs.active && mrs.currentRitual != null
                );
            }
            return;
        }

        if (tile.cooldown > 0) {
            tile.cooldown--;
            return;
        }

        if (tile.active && tile.currentRitual != null) {
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

    @Override
    public boolean activateRitual(Ritual ritual, Player player, int crystalLevel) {
        if (level == null || level.isClientSide()) return false;

        var ritualHolder = RitualRegistry.getRitualRegistry().wrapAsHolder(ritual);
        RitualStats stats = ritualHolder.getData(NVDataMaps.RITUAL_STATS);
        if (stats != null && !stats.enabled()) {
            if (player != null) {
                player.sendSystemMessage(
                        Component.translatable("chat.neovitae.ritual.disabled"));
            }
            return false;
        }

        if (ritual.getCrystalLevel() > crystalLevel) {
            if (player != null) {
                player.sendOverlayMessage(
                        Component.translatable("ritual.neovitae.crystalLevel.insufficient"));
            }
            return false;
        }

        if (!checkStructure(ritual)) {
            if (player != null) {
                player.sendOverlayMessage(
                        Component.translatable("ritual.neovitae.structure.invalid"));
            }
            return false;
        }

        Anima network = AnimaHelper.getAnima(player.getUUID());
        if (network == null || network.getCurrentEV() < ritual.getActivationCost()) {
            if (player != null) {
                player.sendOverlayMessage(
                        Component.translatable("ritual.neovitae.activation.insufficient"));
            }
            return false;
        }

        RitualEvent.Activate activateEvent = new RitualEvent.Activate(this, ritual, player, crystalLevel);
        if (NeoForge.EVENT_BUS.post(activateEvent).isCanceled()) {
            return false;
        }

        if (!ritual.activateRitual(this, player, player.getUUID())) {
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

        blockRanges.clear();
        for (Map.Entry<String, AreaDescriptor> entry : ritual.getModifiableRanges().entrySet()) {
            blockRanges.put(entry.getKey(), entry.getValue().copy());
        }

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
    public void forceActivateRitual(Ritual ritual, @javax.annotation.Nullable Player player) {
        if (level == null || level.isClientSide()) return;

        if (active && currentRitual != null) {
            stopRitual(Ritual.BreakType.ACTIVATE);
        }

        this.currentRitual = ritual.getNewCopy();
        this.currentRitualId = RitualRegistry.getId(ritual);  // Store ID from original, not the copy
        this.owner = player != null ? player.getUUID() : null;
        this.active = true;
        this.runningTime = 0;

        blockRanges.clear();
        for (Map.Entry<String, AreaDescriptor> entry : ritual.getModifiableRanges().entrySet()) {
            blockRanges.put(entry.getKey(), entry.getValue().copy());
        }

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
        return com.breakinblocks.neovitae.ritual.RitualLayouts.get(getLevel(), ritual);
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
    public void provideInformationOfRitualToPlayer(Player player) {
        if (currentRitual != null) {
            Component[] info = currentRitual.provideInformationOfRitualToPlayer(player);
            for (Component component : info) {
                player.sendSystemMessage(component);
            }
        }
    }

    @Override
    public void provideInformationOfRangeToPlayer(Player player, String key) {
        if (currentRitual != null) {
            player.sendSystemMessage(currentRitual.provideInformationOfRangeToPlayer(player, key));
        }
    }

    @Override
    public void provideInformationOfOffsetToPlayer(Player player, AreaDescriptor.Rectangle descriptor) {
        BlockPos min = descriptor.getMinimumOffset();
        BlockPos max = descriptor.getMaximumOffset();
        player.sendSystemMessage(
                Component.translatable("ritual.neovitae.offset.info",
                        min.getX(), min.getY(), min.getZ(),
                        max.getX(), max.getY(), max.getZ()));
    }

    @Override
    public void notifyOwner(Component message) {
        if (owner == null || level == null || level.isClientSide()) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(owner);
            if (player != null) {
                player.sendSystemMessage(message);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);

        if (owner != null) {
            tag.store("owner", UUIDUtil.CODEC, owner);
        }
        tag.putBoolean("active", active);
        tag.putBoolean("inverted", inverted);
        tag.putInt("cooldown", cooldown);
        tag.putLong("runningTime", runningTime);
        tag.putString("direction", direction.getName());
        tag.putString("activeAspect", activeSpiritusAspect.getSerializedName());

        if (currentRitual != null && currentRitualId != null) {
            tag.putString("ritual", currentRitualId.toString());

            CompoundTag ritualData = new CompoundTag();
            currentRitual.writeToNBT(ritualData);
            tag.store("ritualData", CompoundTag.CODEC, ritualData);

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
            tag.store("blockRanges", CompoundTag.CODEC, rangesTag);
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);

        if (tag.read("owner", UUIDUtil.CODEC).isPresent()) {
            owner = tag.read("owner", UUIDUtil.CODEC).orElse(null);
        }
        active = tag.getBooleanOr("active", false);
        inverted = tag.getBooleanOr("inverted", false);
        cooldown = tag.getIntOr("cooldown", 0);
        runningTime = tag.getLongOr("runningTime", 0L);

        tag.getString("direction").ifPresent(d -> {
            direction = Direction.byName(d);
            if (direction == null) direction = Direction.NORTH;
        });

        tag.getString("activeAspect").ifPresent(w -> {
            try {
                activeSpiritusAspect = SpiritusType.valueOf(w.toUpperCase());
            } catch (IllegalArgumentException e) {
                activeSpiritusAspect = SpiritusType.RAW;
            }
        });

        String ritualStr = tag.getStringOr("ritual", "");
        if (!ritualStr.isEmpty()) {
            Identifier ritualId = Identifier.parse(ritualStr);
            Ritual ritual = RitualRegistry.getRitual(ritualId);
            if (ritual != null) {
                currentRitualId = ritualId;  // Restore the ID
                currentRitual = ritual.getNewCopy();

                tag.read("ritualData", CompoundTag.CODEC).ifPresent(currentRitual::readFromNBT);

                blockRanges.clear();
                var rangesOpt = tag.read("blockRanges", CompoundTag.CODEC);
                if (rangesOpt.isPresent()) {
                    CompoundTag rangesTag = rangesOpt.get();
                    for (String key : rangesTag.keySet()) {
                        CompoundTag rangeTag = rangesTag.getCompoundOrEmpty(key);
                        String type = rangeTag.getStringOr("type", "");
                        AreaDescriptor desc = createAreaDescriptor(type);
                        if (desc != null) {
                            desc.loadFromNBT(rangeTag);
                            blockRanges.put(key, desc);
                        }
                    }
                } else {
                    for (Map.Entry<String, AreaDescriptor> entry : currentRitual.getModifiableRanges().entrySet()) {
                        blockRanges.put(entry.getKey(), entry.getValue().copy());
                    }
                }
            } else {
                active = false;
                currentRitualId = null;
            }
        } else if (active) {
            active = false;
        }
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
