package com.breakinblocks.neovitae.datagen.content;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluids;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilType;
import com.breakinblocks.neovitae.api.sigil.effects.*;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

import java.util.Optional;

/**
 * Bootstrap content for sigil types datapack registry.
 */
public class SigilTypes {

    // Resource keys for sigil types
    public static final ResourceKey<SigilType> DIVINATION = SigilTypeRegistry.key("divination");
    public static final ResourceKey<SigilType> SEER = SigilTypeRegistry.key("seer");
    public static final ResourceKey<SigilType> WATER = SigilTypeRegistry.key("water");
    public static final ResourceKey<SigilType> LAVA = SigilTypeRegistry.key("lava");
    public static final ResourceKey<SigilType> VOID = SigilTypeRegistry.key("void");
    public static final ResourceKey<SigilType> GREEN_GROVE = SigilTypeRegistry.key("green_grove");
    public static final ResourceKey<SigilType> AIR = SigilTypeRegistry.key("air");
    public static final ResourceKey<SigilType> BLOOD_LIGHT = SigilTypeRegistry.key("blood_light");
    public static final ResourceKey<SigilType> FAST_MINER = SigilTypeRegistry.key("fast_miner");
    public static final ResourceKey<SigilType> MAGNETISM = SigilTypeRegistry.key("magnetism");
    public static final ResourceKey<SigilType> FROST = SigilTypeRegistry.key("frost");
    public static final ResourceKey<SigilType> SUPPRESSION = SigilTypeRegistry.key("suppression");
    public static final ResourceKey<SigilType> TELEPOSITION = SigilTypeRegistry.key("teleposition");
    public static final ResourceKey<SigilType> PHANTOM_BRIDGE = SigilTypeRegistry.key("phantom_bridge");
    public static final ResourceKey<SigilType> NECROMANCY = SigilTypeRegistry.key("necromancy");
    public static final ResourceKey<SigilType> BOUND_TREASURES = SigilTypeRegistry.key("bound_treasures");

    public static void bootstrap(BootstrapContext<SigilType> context) {
        // Divination sigils - information display, no EV cost
        context.register(DIVINATION, simple(0, new DivinationSigilEffect(false)));
        context.register(SEER, simple(0, new DivinationSigilEffect(true)));

        // Fluid placement sigils
        context.register(WATER, simple(100, new PlaceFluidSigilEffect(Fluids.WATER, 1000)));
        context.register(LAVA, simple(1000, new PlaceFluidSigilEffect(Fluids.LAVA, 1000)));

        // Void sigil - removes fluids
        context.register(VOID, simple(50, new VoidSigilEffect()));

        // Toggleable sigils
        context.register(GREEN_GROVE, toggleable(150, 100, new GreenGroveSigilEffect(15, 4)));
        context.register(FAST_MINER, toggleable(100, 100, new FastMinerSigilEffect(0)));
        context.register(MAGNETISM, toggleable(50, 100, new MagnetismSigilEffect(5, 0.05)));
        context.register(FROST, toggleable(100, 100, new FrostSigilEffect(5)));
        context.register(SUPPRESSION, toggleable(400, 100, new SuppressionSigilEffect(10, 10)));
        context.register(PHANTOM_BRIDGE, toggleable(100, 1, new PhantomBridgeSigilEffect(5)));

        // Single-use sigils
        context.register(AIR, simple(50, new AirSigilEffect()));
        context.register(BLOOD_LIGHT, simple(10, new BloodLightSigilEffect(15)));
        context.register(TELEPOSITION, simple(1000, new TelepositionSigilEffect()));
        context.register(NECROMANCY, simple(2000, new NecromancySigilEffect()));
        context.register(BOUND_TREASURES, simple(200, new BoundTreasuresSigilEffect()));
    }

    /**
     * Creates a simple sigil type with only air use cost.
     */
    private static SigilType simple(int lpCost, ISigilEffect effect) {
        return new SigilType(lpCost, lpCost, 0, 0, SigilType.DEFAULT_DRAIN_INTERVAL, Optional.of(effect));
    }

    /**
     * Creates a toggleable sigil type with active cost.
     */
    private static SigilType toggleable(int lpCostActive, int drainInterval, ISigilEffect effect) {
        return new SigilType(0, 0, 0, lpCostActive, drainInterval, Optional.of(effect));
    }
}
