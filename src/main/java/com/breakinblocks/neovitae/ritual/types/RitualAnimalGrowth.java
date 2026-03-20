package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.WillState;
import com.breakinblocks.neovitae.util.Utils;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual of Animal Growth - Accelerates baby animal growth with demon will enhancements.
 *
 * <p>Demon Will effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Reduces refresh time (20 ticks scaling down with will)</li>
 *   <li><b>Steadfast</b> - Breeding mode: extracts food from chest, triggers breeding in adults</li>
 *   <li><b>Destructive</b> - Applies Sacrificial Lamb effect to adult animals (1200 tick duration)</li>
 *   <li><b>Vengeful</b> - Reduces adult breeding cooldown timer</li>
 * </ul>
 */
public class RitualAnimalGrowth extends Ritual {

    public static final String GROWTH_RANGE = "growthRange";
    public static final String CHEST_RANGE = "chestRange";

    private static final double MIN_DEFAULT = 10.0;
    private static final double MIN_STEADFAST = 10.0;
    private static final double MIN_DESTRUCTIVE = 10.0;
    private static final double MIN_VENGEFUL = 10.0;

    private static final double WILL_PER_BREED = 1.0;
    private static final double WILL_PER_SACRIFICE = 0.5;
    private static final double WILL_PER_COOLDOWN = 0.3;

    private int refreshTime = 20;

    public RitualAnimalGrowth() {
        super("animal_growth", 0, 500, "ritual." + NeoVitae.MODID + ".animal_growth");
        addBlockRange(GROWTH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 0, -5), 11, 3, 11));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));

        setMaximumVolumeAndDistanceOfRange(GROWTH_RANGE, 1000, 10, 10);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        BlockPos masterPos = ctx.masterPos();

        WillState will = RitualHelper.queryWill(ctx.level(), masterPos, MIN_DEFAULT);

        boolean hasRaw = will.hasDefault();
        boolean doBreed = will.hasSteadfast();
        boolean doSacrifice = will.hasDestructive();
        boolean doVengeful = will.hasVengeful();

        refreshTime = hasRaw ? Math.max(5, 20 - (int) (will.getDefault() / 10)) : 20;

        double steadfastWillUsed = 0;
        double destructiveWillUsed = 0;
        double vengefulWillUsed = 0;

        int maxAnimals = ctx.maxOperations(getRefreshCost());
        int animalsProcessed = 0;

        BlockPos chestPos = RitualHelper.getRangePositions(ctx.master(), this, CHEST_RANGE, masterPos).getFirst();
        BlockEntity chestTile = ctx.level().getBlockEntity(chestPos);
        IItemHandler foodHandler = chestTile != null ? Utils.getInventory(chestTile, Direction.DOWN) : null;

        // --- Baby Growth ---
        List<Animal> babies = RitualHelper.getEntitiesInRange(ctx, this, GROWTH_RANGE,
                Animal.class, Animal::isBaby);

        for (Animal animal : babies) {
            if (animalsProcessed >= maxAnimals) break;

            int age = animal.getAge();
            if (age < 0) {
                animal.setAge(Math.min(age + 200, 0));
                animalsProcessed++;
            }
        }

        // --- STEADFAST: Breeding using food from chest ---
        if (doBreed && foodHandler != null) {
            List<Animal> adults = RitualHelper.getEntitiesInRange(ctx, this, GROWTH_RANGE,
                    Animal.class, animal -> !animal.isBaby() && animal.canFallInLove() && animal.getAge() == 0);

            for (Animal animal : adults) {
                if (animalsProcessed >= maxAnimals) break;
                if ((will.getSteadfast() - steadfastWillUsed) < WILL_PER_BREED) break;

                // Try to find food in the chest
                boolean fed = false;
                for (int slot = 0; slot < foodHandler.getSlots(); slot++) {
                    ItemStack foodStack = foodHandler.getStackInSlot(slot);
                    if (!foodStack.isEmpty() && animal.isFood(foodStack)) {
                        foodHandler.extractItem(slot, 1, false);
                        animal.setInLove(null);
                        steadfastWillUsed += WILL_PER_BREED;
                        animalsProcessed++;
                        fed = true;
                        break;
                    }
                }
                if (!fed) break; // No suitable food found
            }
        }

        // --- DESTRUCTIVE: Apply Sacrificial Lamb to adults ---
        if (doSacrifice) {
            List<Animal> adults = RitualHelper.getEntitiesInRange(ctx, this, GROWTH_RANGE,
                    Animal.class, animal -> !animal.isBaby() && animal.isAlive());

            for (Animal animal : adults) {
                if ((will.getDestructive() - destructiveWillUsed) < WILL_PER_SACRIFICE) break;

                if (!animal.hasEffect(NVMobEffects.SACRIFICIAL_LAMB)) {
                    animal.addEffect(new MobEffectInstance(NVMobEffects.SACRIFICIAL_LAMB, 1200, 0));
                    destructiveWillUsed += WILL_PER_SACRIFICE;
                }
            }
        }

        // --- VENGEFUL: Reduce breeding cooldown on adults ---
        if (doVengeful) {
            List<Animal> cooldownAnimals = RitualHelper.getEntitiesInRange(ctx, this, GROWTH_RANGE,
                    Animal.class, animal -> !animal.isBaby() && animal.getAge() > 0);

            for (Animal animal : cooldownAnimals) {
                if ((will.getVengeful() - vengefulWillUsed) < WILL_PER_COOLDOWN) break;

                int age = animal.getAge();
                int reduction = 10 + (int) (will.getVengeful() / 5);
                animal.setAge(Math.max(0, age - reduction));
                vengefulWillUsed += WILL_PER_COOLDOWN;
            }
        }

        will.use(EnumWillType.STEADFAST, steadfastWillUsed);
        will.use(EnumWillType.DESTRUCTIVE, destructiveWillUsed);
        will.use(EnumWillType.VENGEFUL, vengefulWillUsed);
        will.drain(ctx.level(), masterPos);

        ctx.syphon(getRefreshCost() * animalsProcessed);
    }

    @Override
    public int getRefreshTime() {
        return refreshTime;
    }

    @Override
    public int getRefreshCost() {
        return 30;
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".will.default"),
                Component.translatable(getTranslationKey() + ".will.steadfast"),
                Component.translatable(getTranslationKey() + ".will.destructive"),
                Component.translatable(getTranslationKey() + ".will.vengeful")
        };
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
