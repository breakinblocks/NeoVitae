package com.breakinblocks.neovitae.ritual;

import net.neoforged.neoforge.registries.DeferredHolder;
import com.breakinblocks.neovitae.ritual.types.*;

import java.util.function.Supplier;

/**
 * Registration of all Blood Magic rituals.
 * Uses NeoForge's DeferredRegister system.
 */
public final class BMRituals {
    private BMRituals() {}

    public static final DeferredHolder<Ritual, RitualWater> WATER =
            registerRitual("water", RitualWater::new);

    public static final DeferredHolder<Ritual, RitualLava> LAVA =
            registerRitual("lava", RitualLava::new);

    public static final DeferredHolder<Ritual, RitualGreenGrove> GREEN_GROVE =
            registerRitual("green_grove", RitualGreenGrove::new);

    public static final DeferredHolder<Ritual, RitualWellOfSuffering> WELL_OF_SUFFERING =
            registerRitual("well_of_suffering", RitualWellOfSuffering::new);

    public static final DeferredHolder<Ritual, RitualFeatheredKnife> FEATHERED_KNIFE =
            registerRitual("feathered_knife", RitualFeatheredKnife::new);

    public static final DeferredHolder<Ritual, RitualHarvest> HARVEST =
            registerRitual("harvest", RitualHarvest::new);

    public static final DeferredHolder<Ritual, RitualRegeneration> REGENERATION =
            registerRitual("regeneration", RitualRegeneration::new);

    public static final DeferredHolder<Ritual, RitualSpeed> SPEED =
            registerRitual("speed", RitualSpeed::new);

    public static final DeferredHolder<Ritual, RitualJumping> JUMPING =
            registerRitual("jumping", RitualJumping::new);

    public static final DeferredHolder<Ritual, RitualMagnetism> MAGNETISM =
            registerRitual("magnetism", RitualMagnetism::new);

    public static final DeferredHolder<Ritual, RitualAnimalGrowth> ANIMAL_GROWTH =
            registerRitual("animal_growth", RitualAnimalGrowth::new);

    public static final DeferredHolder<Ritual, RitualCrushing> CRUSHING =
            registerRitual("crushing", RitualCrushing::new);

    public static final DeferredHolder<Ritual, RitualFelling> FELLING =
            registerRitual("felling", RitualFelling::new);

    public static final DeferredHolder<Ritual, RitualSuppression> SUPPRESSION =
            registerRitual("suppression", RitualSuppression::new);

    public static final DeferredHolder<Ritual, RitualContainment> CONTAINMENT =
            registerRitual("containment", RitualContainment::new);

    public static final DeferredHolder<Ritual, RitualExpulsion> EXPULSION =
            registerRitual("expulsion", RitualExpulsion::new);

    public static final DeferredHolder<Ritual, RitualZephyr> ZEPHYR =
            registerRitual("zephyr", RitualZephyr::new);

    public static final DeferredHolder<Ritual, RitualPump> PUMP =
            registerRitual("pump", RitualPump::new);

    public static final DeferredHolder<Ritual, RitualCrystalHarvest> CRYSTAL_HARVEST =
            registerRitual("crystal_harvest", RitualCrystalHarvest::new);

    public static final DeferredHolder<Ritual, RitualDowngrade> DOWNGRADE =
            registerRitual("downgrade", RitualDowngrade::new);

    public static final DeferredHolder<Ritual, RitualMeteor> METEOR =
            registerRitual("meteor", RitualMeteor::new);

    public static final DeferredHolder<Ritual, RitualForsakenSoul> FORSAKEN_SOUL =
            registerRitual("forsaken_soul", RitualForsakenSoul::new);

    public static final DeferredHolder<Ritual, RitualFullStomach> FULL_STOMACH =
            registerRitual("full_stomach", RitualFullStomach::new);

    public static final DeferredHolder<Ritual, RitualCondor> CONDOR =
            registerRitual("condor", RitualCondor::new);

    public static final DeferredHolder<Ritual, RitualEllipse> ELLIPSE =
            registerRitual("ellipse", RitualEllipse::new);

    public static final DeferredHolder<Ritual, RitualSphere> SPHERE =
            registerRitual("sphere", RitualSphere::new);

    public static final DeferredHolder<Ritual, RitualArmourEvolve> ARMOUR_EVOLVE =
            registerRitual("armour_evolve", RitualArmourEvolve::new);

    public static final DeferredHolder<Ritual, RitualUpgradeRemove> UPGRADE_REMOVE =
            registerRitual("upgrade_remove", RitualUpgradeRemove::new);

    public static final DeferredHolder<Ritual, RitualCrystalSplit> CRYSTAL_SPLIT =
            registerRitual("crystal_split", RitualCrystalSplit::new);

    public static final DeferredHolder<Ritual, RitualCrafting> CRAFTING =
            registerRitual("crafting", RitualCrafting::new);

    public static final DeferredHolder<Ritual, RitualYawningVoid> YAWNING_VOID =
            registerRitual("yawning_void", RitualYawningVoid::new);

    public static final DeferredHolder<Ritual, RitualPlacer> PLACER =
            registerRitual("placer", RitualPlacer::new);

    public static final DeferredHolder<Ritual, RitualGrounding> GROUNDING =
            registerRitual("grounding", RitualGrounding::new);

    public static final DeferredHolder<Ritual, RitualGeode> GEODE =
            registerRitual("geode", RitualGeode::new);

    public static final DeferredHolder<Ritual, RitualSimpleDungeon> SIMPLE_DUNGEON =
            registerRitual("simple_dungeon", RitualSimpleDungeon::new);

    public static final DeferredHolder<Ritual, RitualStandardDungeon> STANDARD_DUNGEON =
            registerRitual("standard_dungeon", RitualStandardDungeon::new);

    public static final DeferredHolder<Ritual, RitualPhantomBridge> PHANTOM_BRIDGE =
            registerRitual("phantom_bridge", RitualPhantomBridge::new);

    public static final DeferredHolder<ImperfectRitual, com.breakinblocks.neovitae.ritual.imperfect.ImperfectRitualRain> IMPERFECT_RAIN =
            registerImperfectRitual("rain", com.breakinblocks.neovitae.ritual.imperfect.ImperfectRitualRain::new);

    // Strong Zombie - Coal Block, 5000 LP
    public static final DeferredHolder<ImperfectRitual, com.breakinblocks.neovitae.ritual.imperfect.ImperfectRitualZombie> IMPERFECT_ZOMBIE =
            registerImperfectRitual("zombie", com.breakinblocks.neovitae.ritual.imperfect.ImperfectRitualZombie::new);

    // Fire Resistance II - Bedrock above (Nether), 5000 LP
    public static final DeferredHolder<ImperfectRitual, com.breakinblocks.neovitae.ritual.imperfect.ImperfectRitualResistance> IMPERFECT_RESISTANCE =
            registerImperfectRitual("resistance", com.breakinblocks.neovitae.ritual.imperfect.ImperfectRitualResistance::new);

    private static <T extends Ritual> DeferredHolder<Ritual, T> registerRitual(String name, Supplier<T> supplier) {
        return RitualRegistry.RITUALS.register(name, supplier);
    }

    private static <T extends ImperfectRitual> DeferredHolder<ImperfectRitual, T> registerImperfectRitual(
            String name, Supplier<T> supplier) {
        return RitualRegistry.IMPERFECT_RITUALS.register(name, supplier);
    }

    /**
     * Force class loading to trigger static field initialization.
     * Called during mod construction to ensure rituals are registered.
     */
    public static void init() {

    }
}
