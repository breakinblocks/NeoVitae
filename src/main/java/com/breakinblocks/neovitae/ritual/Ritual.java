package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

import java.util.*;
import java.util.function.Consumer;

/**
 * Abstract base class for all rituals.
 * Rituals are multiblock structures built from ritual stones
 * that provide ongoing effects when activated.
 */
public abstract class Ritual {

    protected final Map<String, AreaDescriptor> modifiableRanges = new HashMap<>();
    protected final Map<String, Integer> volumeLimits = new HashMap<>();
    protected final Map<String, Integer> horizontalLimits = new HashMap<>();
    protected final Map<String, Integer> verticalLimits = new HashMap<>();

    private final String name;
    private final int crystalLevel;
    private final int activationCost;
    private final String translationKey;

    /**
     * Creates a new ritual.
     *
     * @param name           Unique identifier for this ritual
     * @param crystalLevel   Required activation crystal tier (1 = weak, 2 = awakened)
     * @param activationCost Base LP cost to activate
     * @param translationKey Translation key prefix for localization
     */
    public Ritual(String name, int crystalLevel, int activationCost, String translationKey) {
        this.name = name;
        this.crystalLevel = crystalLevel;
        this.activationCost = activationCost;
        this.translationKey = translationKey;
    }

    // ==================== Abstract Methods ====================

    /**
     * Performs the ritual's effect. Called every {@link #getRefreshTime()} ticks while active.
     *
     * @param masterRitualStone The master ritual stone running this ritual
     */
    public abstract void performRitual(IMasterRitualStone masterRitualStone);

    /**
     * Gets the LP cost per tick (refresh).
     *
     * @return LP drained each refresh
     */
    public abstract int getRefreshCost();

    /**
     * Gathers all rune components that make up this ritual's structure.
     *
     * @param components Consumer to add components to
     */
    public abstract void gatherComponents(Consumer<RitualComponent> components);

    /**
     * Creates a fresh copy of this ritual for a new master ritual stone.
     *
     * @return New ritual instance
     */
    public abstract Ritual getNewCopy();

    // ==================== Lifecycle Methods ====================

    /**
     * Called when a player attempts to activate this ritual.
     *
     * @param masterRitualStone The master ritual stone
     * @param player            The activating player
     * @param owner             UUID of the ritual owner
     * @return true if activation should proceed
     */
    public boolean activateRitual(IMasterRitualStone masterRitualStone, Player player, UUID owner) {
        return true;
    }

    /**
     * Called when the ritual is stopped.
     *
     * @param masterRitualStone The master ritual stone
     * @param breakType         Reason for stopping
     */
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        // Default: no cleanup needed
    }

    /**
     * Gets how often this ritual performs its effect.
     *
     * @return Ticks between each performRitual call
     */
    public int getRefreshTime() {
        return 20;
    }

    // ==================== Area Management ====================

    /**
     * Registers a modifiable area range for this ritual.
     *
     * @param key          Unique key for this range
     * @param defaultRange Default area descriptor
     */
    protected void addBlockRange(String key, AreaDescriptor defaultRange) {
        modifiableRanges.put(key, defaultRange);
    }

    /**
     * Sets the limits for a block range.
     */
    protected void setMaximumVolumeAndDistanceOfRange(String key, int maxVolume, int horizontalRadius, int verticalRadius) {
        volumeLimits.put(key, maxVolume);
        horizontalLimits.put(key, horizontalRadius);
        verticalLimits.put(key, verticalRadius);
    }

    /**
     * Gets the area descriptor for the given range key.
     */
    public AreaDescriptor getBlockRange(String key) {
        return modifiableRanges.get(key);
    }

    /**
     * Gets all modifiable range keys.
     */
    public List<String> getListOfRanges() {
        return new ArrayList<>(modifiableRanges.keySet());
    }

    /**
     * Gets the next range key in the list, cycling back to the first.
     */
    public String getNextBlockRange(String currentRange) {
        List<String> ranges = getListOfRanges();
        if (ranges.isEmpty()) {
            return "";
        }
        if (!ranges.contains(currentRange)) {
            return ranges.get(0);
        }

        int index = ranges.indexOf(currentRange);
        return ranges.get((index + 1) % ranges.size());
    }

    /**
     * Checks if a block range can be modified to the given bounds.
     */
    public EnumReaderBoundaries canBlockRangeBeModified(String key, AreaDescriptor descriptor,
                                                        IMasterRitualStone master, BlockPos offset1, BlockPos offset2) {
        int maxVolume = getMaxVolumeForRange(key);
        int maxVertical = getMaxVerticalRadiusForRange(key);
        int maxHorizontal = getMaxHorizontalRadiusForRange(key);

        if (maxVolume > 0 && !checkVolumeForOffsets(descriptor, offset1, offset2, maxVolume)) {
            return EnumReaderBoundaries.VOLUME_TOO_LARGE;
        }

        if (!descriptor.isWithinRange(offset1, offset2, maxVertical, maxHorizontal)) {
            return EnumReaderBoundaries.NOT_WITHIN_BOUNDARIES;
        }

        return EnumReaderBoundaries.SUCCESS;
    }

    private boolean checkVolumeForOffsets(AreaDescriptor descriptor, BlockPos offset1, BlockPos offset2, int maxVolume) {
        int dx = Math.abs(offset2.getX() - offset1.getX()) + 1;
        int dy = Math.abs(offset2.getY() - offset1.getY()) + 1;
        int dz = Math.abs(offset2.getZ() - offset1.getZ()) + 1;
        return dx * dy * dz <= maxVolume;
    }

    public int getMaxVolumeForRange(String key) {
        return volumeLimits.getOrDefault(key, Integer.MAX_VALUE);
    }

    public int getMaxVerticalRadiusForRange(String key) {
        return verticalLimits.getOrDefault(key, 256);
    }

    public int getMaxHorizontalRadiusForRange(String key) {
        return horizontalLimits.getOrDefault(key, 256);
    }

    // ==================== Serialization ====================

    /**
     * Reads ritual-specific data from NBT.
     */
    public void readFromNBT(CompoundTag tag) {
        ListTag areas = tag.getList("areas", Tag.TAG_COMPOUND);
        for (int i = 0; i < areas.size(); i++) {
            CompoundTag areaTag = areas.getCompound(i);
            String key = areaTag.getString("key");
            AreaDescriptor descriptor = modifiableRanges.get(key);
            if (descriptor != null) {
                descriptor.loadFromNBT(areaTag.getCompound("area"));
            }
        }
    }

    /**
     * Writes ritual-specific data to NBT.
     */
    public void writeToNBT(CompoundTag tag) {
        ListTag areas = new ListTag();
        for (Map.Entry<String, AreaDescriptor> entry : modifiableRanges.entrySet()) {
            CompoundTag areaTag = new CompoundTag();
            areaTag.putString("key", entry.getKey());
            CompoundTag descriptorTag = new CompoundTag();
            entry.getValue().saveToNBT(descriptorTag);
            areaTag.put("area", descriptorTag);
            areas.add(areaTag);
        }
        tag.put("areas", areas);
    }

    // ==================== Information Methods ====================

    /**
     * Provides information about this ritual to the player.
     */
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{Component.translatable(translationKey + ".info")};
    }

    /**
     * Provides information about a specific range to the player.
     */
    public Component provideInformationOfRangeToPlayer(Player player, String range) {
        if (getListOfRanges().contains(range)) {
            return Component.translatable(translationKey + "." + range + ".info");
        }
        return Component.translatable("ritual.neovitae.blockRange.noRange");
    }

    /**
     * Gets an error message for a failed block range modification.
     */
    public Component getErrorForBlockRangeOnFail(Player player, String key, IMasterRitualStone master,
                                                  BlockPos offset1, BlockPos offset2) {
        AreaDescriptor descriptor = getBlockRange(key);
        if (descriptor == null) {
            return Component.translatable("ritual.neovitae.blockRange.tooBig", "?");
        }

        int maxVolume = getMaxVolumeForRange(key);
        int maxVertical = getMaxVerticalRadiusForRange(key);
        int maxHorizontal = getMaxHorizontalRadiusForRange(key);

        if (maxVolume > 0 && !checkVolumeForOffsets(descriptor, offset1, offset2, maxVolume)) {
            return Component.translatable("ritual.neovitae.blockRange.tooBig", maxVolume);
        }
        return Component.translatable("ritual.neovitae.blockRange.tooFar", maxVertical, maxHorizontal);
    }

    // ==================== Helper Methods for Rune Placement ====================

    /**
     * Adds a single rune at the specified offset.
     */
    protected final void addRune(Consumer<RitualComponent> components, int x, int y, int z, EnumRuneType rune) {
        components.accept(new RitualComponent(x, y, z, rune));
    }

    /**
     * Adds runes in all 8 offset positions (for symmetrical patterns).
     */
    protected final void addOffsetRunes(Consumer<RitualComponent> components, int offset1, int offset2, int y, EnumRuneType rune) {
        addRune(components, offset1, y, offset2, rune);
        addRune(components, offset2, y, offset1, rune);
        addRune(components, offset1, y, -offset2, rune);
        addRune(components, -offset2, y, offset1, rune);
        addRune(components, -offset1, y, offset2, rune);
        addRune(components, offset2, y, -offset1, rune);
        addRune(components, -offset1, y, -offset2, rune);
        addRune(components, -offset2, y, -offset1, rune);
    }

    /**
     * Adds runes at all 4 corner positions.
     */
    protected final void addCornerRunes(Consumer<RitualComponent> components, int offset, int y, EnumRuneType rune) {
        addRune(components, offset, y, offset, rune);
        addRune(components, offset, y, -offset, rune);
        addRune(components, -offset, y, -offset, rune);
        addRune(components, -offset, y, offset, rune);
    }

    /**
     * Adds runes along the 4 cardinal directions.
     */
    protected final void addParallelRunes(Consumer<RitualComponent> components, int offset, int y, EnumRuneType rune) {
        addRune(components, offset, y, 0, rune);
        addRune(components, -offset, y, 0, rune);
        addRune(components, 0, y, -offset, rune);
        addRune(components, 0, y, offset, rune);
    }

    // ==================== Getters ====================

    public String getName() {
        return name;
    }

    public int getCrystalLevel() {
        return crystalLevel;
    }

    public int getActivationCost() {
        return activationCost;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Map<String, AreaDescriptor> getModifiableRanges() {
        return Collections.unmodifiableMap(modifiableRanges);
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ritual ritual)) return false;
        return crystalLevel == ritual.crystalLevel &&
               activationCost == ritual.activationCost &&
               Objects.equals(name, ritual.name) &&
               Objects.equals(translationKey, ritual.translationKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, crystalLevel, activationCost, translationKey);
    }

    @Override
    public String toString() {
        return "Ritual{name='%s', crystalLevel=%d, activationCost=%d}".formatted(name, crystalLevel, activationCost);
    }

    /**
     * Reasons a ritual can be stopped.
     */
    public enum BreakType {
        /** Ritual deactivated by player */
        DEACTIVATE,
        /** Master ritual stone was broken */
        BREAK_MRS,
        /** A ritual stone component was broken */
        BREAK_STONE,
        /** Another ritual was activated */
        ACTIVATE,
        /** Redstone signal stopped the ritual */
        REDSTONE,
        /** Ritual was destroyed by explosion */
        EXPLOSION
    }
}
