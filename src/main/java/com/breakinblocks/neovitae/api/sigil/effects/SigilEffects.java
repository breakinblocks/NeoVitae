package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;

import java.util.function.Supplier;

/**
 * Initializes all built-in sigil effect registrations.
 * This class must be loaded to trigger static initialization of effect registrations.
 */
public class SigilEffects {

    // Force load all effect classes to trigger their static registration
    public static final Supplier<MapCodec<AirSigilEffect>> AIR = AirSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<PlaceFluidSigilEffect>> PLACE_FLUID = PlaceFluidSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<VoidSigilEffect>> VOID = VoidSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<FastMinerSigilEffect>> FAST_MINER = FastMinerSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<GreenGroveSigilEffect>> GREEN_GROVE = GreenGroveSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<MagnetismSigilEffect>> MAGNETISM = MagnetismSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<FrostSigilEffect>> FROST = FrostSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<SuppressionSigilEffect>> SUPPRESSION = SuppressionSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<PhantomBridgeSigilEffect>> PHANTOM_BRIDGE = PhantomBridgeSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<DivinationSigilEffect>> DIVINATION = DivinationSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<BloodLightSigilEffect>> BLOOD_LIGHT = BloodLightSigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<TelepositionSigilEffect>> TELEPOSITION = TelepositionSigilEffect.REGISTRATION;

    /**
     * Call this method to ensure all effect types are registered.
     * This should be called during mod initialization.
     */
    public static void init() {
        // Accessing these fields forces their static initializers to run,
        // which triggers registration of the effect codecs.
        var air = AIR;
        var placeFluid = PLACE_FLUID;
        var void_ = VOID;
        var fastMiner = FAST_MINER;
        var greenGrove = GREEN_GROVE;
        var magnetism = MAGNETISM;
        var frost = FROST;
        var suppression = SUPPRESSION;
        var phantomBridge = PHANTOM_BRIDGE;
        var divination = DIVINATION;
        var bloodLight = BLOOD_LIGHT;
        var teleposition = TELEPOSITION;
    }
}
