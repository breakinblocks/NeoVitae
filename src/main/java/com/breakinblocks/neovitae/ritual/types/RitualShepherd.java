// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual of the Shepherd - tends animals in range: it accelerates the growth of the young
 * and coaxes ready adults into breeding, paying a small amount of Essentia Vitae per animal
 * to feed them rather than requiring food or a chest. Raw Spiritus quickens its pulse.
 */
public class RitualShepherd extends Ritual {

    public static final String GROWTH_RANGE = "growthRange";

    private static final double MIN_RAW = 10.0;

    private int refreshTime = 20;

    public RitualShepherd() {
        super("shepherd", 0, 500, "ritual." + NeoVitae.MODID + ".shepherd");
        addBlockRange(GROWTH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 0, -5), 11, 3, 11));
        setMaximumVolumeAndDistanceOfRange(GROWTH_RANGE, 1000, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        BlockPos masterPos = ctx.masterPos();
        SpiritusState will = RitualHelper.querySpiritus(ctx.level(), masterPos, MIN_RAW);

        RitualStats stats = getStats();
        int baseRefresh = stats != null ? stats.refreshTime() : 20;
        refreshTime = scaleByRawSpiritus(will, baseRefresh, Math.max(1, baseRefresh / 4), 10);

        int maxAnimals = ctx.maxOperations(getRefreshCost());
        int processed = 0;

        List<Animal> animals = RitualHelper.getEntitiesInRange(ctx, this, GROWTH_RANGE,
                Animal.class, Animal::isAlive);

        for (Animal animal : animals) {
            if (processed >= maxAnimals) break;
            if (!animal.isBaby()) continue;
            int age = animal.getAge();
            if (age < 0) {
                animal.setAge(Math.min(age + 200, 0));
                processed++;
                RitualHelper.chanceStream(ctx.level(), 10, () ->
                        StreamPresets.lifePulse(masterPos, animal.blockPosition()).color(0x44CC33).build()
                                .sendToNearby(ctx.serverLevel(), masterPos, 64));
            }
        }

        for (Animal animal : animals) {
            if (processed >= maxAnimals) break;
            if (animal.isBaby() || !animal.canFallInLove() || animal.getAge() != 0) continue;
            animal.setInLove(null);
            processed++;
            RitualHelper.chanceStream(ctx.level(), 10, () ->
                    StreamPresets.lifePulse(masterPos, animal.blockPosition()).color(0xCC66FF).build()
                            .sendToNearby(ctx.serverLevel(), masterPos, 64));
        }

        ctx.syphon(getRefreshCost() * processed);
    }

    @Override
    public int getRefreshTime() {
        return refreshTime;
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{ Component.translatable(getTranslationKey() + ".info") };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addRune(components, 0, 0, 3, EnumRuneType.WATER);
        addRune(components, 0, 0, -3, EnumRuneType.WATER);
        addRune(components, 3, 0, 0, EnumRuneType.WATER);
        addRune(components, -3, 0, 0, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualShepherd();
    }
}
