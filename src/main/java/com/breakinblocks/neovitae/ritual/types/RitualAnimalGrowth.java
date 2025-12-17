package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that accelerates the growth of baby animals.
 */
public class RitualAnimalGrowth extends Ritual {

    public static final String GROWTH_RANGE = "growthRange";

    public RitualAnimalGrowth() {
        super("animal_growth", 0, 500, "ritual." + NeoVitae.MODID + ".animal_growth");
        addBlockRange(GROWTH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 0, -5), 11, 3, 11));
        setMaximumVolumeAndDistanceOfRange(GROWTH_RANGE, 1000, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<AgeableMob> animals = RitualHelper.getEntitiesInRange(ctx, this, GROWTH_RANGE,
                AgeableMob.class, AgeableMob::isBaby);

        int maxAnimals = ctx.maxOperations(getRefreshCost());
        int animalsGrown = 0;

        for (AgeableMob animal : animals) {
            if (animalsGrown >= maxAnimals) break;

            // Speed up growth by reducing age
            int age = animal.getAge();
            if (age < 0) {
                // Baby animals have negative age
                animal.setAge(Math.min(age + 200, 0));
                animalsGrown++;
            }
        }

        ctx.syphon(getRefreshCost() * animalsGrown);
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 30;
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
        return new RitualAnimalGrowth();
    }
}
