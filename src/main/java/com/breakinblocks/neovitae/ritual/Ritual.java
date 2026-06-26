// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datamap.RitualStats;

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
     * @param activationCost Base EV cost to activate
     * @param translationKey Translation key prefix for localization
     */
    public Ritual(String name, int crystalLevel, int activationCost, String translationKey) {
        this.name = name;
        this.crystalLevel = crystalLevel;
        this.activationCost = activationCost;
        this.translationKey = translationKey;
    }

    /**
     * Performs the ritual's effect. Called every {@link #getRefreshTime()} ticks while active.
     */
    public abstract void performRitual(IMasterRitualStone masterRitualStone);

    public boolean usesKeepCount() {
        return false;
    }

    protected RitualStats getStats() {
        return RitualRegistry.getStats(this);
    }

    public int getRefreshCost() {
        RitualStats stats = getStats();
        return stats != null ? stats.refreshCost() : 1;
    }

    public abstract void gatherComponents(Consumer<RitualComponent> components);

    public abstract Ritual getNewCopy();
    public boolean canActivate(IMasterRitualStone masterRitualStone, Player player) {
        return true;
    }

    public boolean activateRitual(IMasterRitualStone masterRitualStone, Player player, UUID owner) {
        return true;
    }

    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
    }

    public int getRefreshTime() {
        RitualStats stats = getStats();
        return stats != null ? stats.refreshTime() : 20;
    }

    public SoundEvent getAmbientSound() {
        return NVSounds.RITUAL_AMBIENT.get();
    }

    protected void addBlockRange(String key, AreaDescriptor defaultRange) {
        modifiableRanges.put(key, defaultRange);
    }

    protected void setMaximumVolumeAndDistanceOfRange(String key, int maxVolume, int horizontalRadius, int verticalRadius) {
        volumeLimits.put(key, maxVolume);
        horizontalLimits.put(key, horizontalRadius);
        verticalLimits.put(key, verticalRadius);
    }

    public AreaDescriptor getBlockRange(String key) {
        return modifiableRanges.get(key);
    }

    public List<String> getListOfRanges() {
        return new ArrayList<>(modifiableRanges.keySet());
    }

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

    private RitualStats.RangeLimit getRangeLimit(String key) {
        RitualStats stats = getStats();
        return stats != null ? stats.rangeLimits().get(key) : null;
    }

    public int getMaxVolumeForRange(String key) {
        RitualStats.RangeLimit limit = getRangeLimit(key);
        if (limit != null) return limit.maxVolume();
        return volumeLimits.getOrDefault(key, Integer.MAX_VALUE);
    }

    public int getMaxVerticalRadiusForRange(String key) {
        RitualStats.RangeLimit limit = getRangeLimit(key);
        if (limit != null) return limit.maxVerticalRadius();
        return verticalLimits.getOrDefault(key, 256);
    }

    public int getMaxHorizontalRadiusForRange(String key) {
        RitualStats.RangeLimit limit = getRangeLimit(key);
        if (limit != null) return limit.maxHorizontalRadius();
        return horizontalLimits.getOrDefault(key, 256);
    }

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

    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{Component.translatable(translationKey + ".info")};
    }

    public Component provideInformationOfRangeToPlayer(Player player, String range) {
        if (getListOfRanges().contains(range)) {
            return Component.translatable(translationKey + "." + range + ".info");
        }
        return Component.translatable("ritual.neovitae.blockRange.noRange");
    }

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

    protected final void addRune(Consumer<RitualComponent> components, int x, int y, int z, EnumRuneType rune) {
        components.accept(new RitualComponent(x, y, z, rune));
    }

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

    protected final void addCornerRunes(Consumer<RitualComponent> components, int offset, int y, EnumRuneType rune) {
        addRune(components, offset, y, offset, rune);
        addRune(components, offset, y, -offset, rune);
        addRune(components, -offset, y, -offset, rune);
        addRune(components, -offset, y, offset, rune);
    }

    /**
     * Calculates a scaled refresh time based on the amount of spiritus present.
     * Higher will amounts result in faster (lower) refresh times, clamped to a minimum.
     *
     * @param spiritusAmount  The amount of spiritus influencing the refresh time
     * @param baseTime    The base refresh time in ticks (used when no will is present)
     * @param minTime     The minimum refresh time in ticks (floor value)
     * @param spiritusDivisor The divisor applied to the will amount to determine tick reduction
     * @return The scaled refresh time, no lower than {@code minTime}
     */
    protected static int scaleRefreshTime(double spiritusAmount, int baseTime, int minTime, double spiritusDivisor) {
        return Math.max(minTime, baseTime - (int) (spiritusAmount / spiritusDivisor));
    }

    /**
     * Applies {@link #scaleRefreshTime} when raw spiritus is present, otherwise
     * returns {@code baseTime}. Collapses the common
     * {@code hasRaw ? scaleRefreshTime(...) : baseTime} ternary used by the
     * raw-spiritus-accelerated rituals (animal growth, crushing, green grove).
     */
    protected static int scaleByRawSpiritus(com.breakinblocks.neovitae.api.spiritus.SpiritusState will,
                                        int baseTime, int minTime, double spiritusDivisor) {
        return will.hasRaw() ? scaleRefreshTime(will.getRaw(), baseTime, minTime, spiritusDivisor) : baseTime;
    }

    protected final void addParallelRunes(Consumer<RitualComponent> components, int offset, int y, EnumRuneType rune) {
        addRune(components, offset, y, 0, rune);
        addRune(components, -offset, y, 0, rune);
        addRune(components, 0, y, -offset, rune);
        addRune(components, 0, y, offset, rune);
    }

    public String getName() {
        return name;
    }

    public int getCrystalLevel() {
        RitualStats stats = getStats();
        return stats != null ? stats.crystalLevel() : crystalLevel;
    }

    public int getActivationCost() {
        RitualStats stats = getStats();
        return stats != null ? stats.activationCost() : activationCost;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Map<String, AreaDescriptor> getModifiableRanges() {
        return Collections.unmodifiableMap(modifiableRanges);
    }

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

    public enum BreakType {
        DEACTIVATE,
        BREAK_MRS,
        BREAK_STONE,
        ACTIVATE,
        REDSTONE,
        EXPLOSION
    }
}
