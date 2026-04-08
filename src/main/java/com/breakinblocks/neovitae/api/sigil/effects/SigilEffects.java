package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;

import java.util.function.Supplier;

/**
 * Registry of all built-in sigil effect types provided by NeoVitae.
 *
 * <p>Each constant holds a {@link Supplier} for the {@link MapCodec} of a built-in
 * {@link SigilEffect} implementation. These are registered into NeoVitae's sigil effect
 * type registry during mod initialization.</p>
 *
 * <p>Addon developers do not need to interact with this class. To register custom
 * sigil effects, create your own {@link net.neoforged.neoforge.registries.DeferredRegister}
 * against {@link com.breakinblocks.neovitae.api.registry.NeoVitaeRegistries#SIGIL_EFFECT_TYPE_KEY}
 * and register your effect's {@link MapCodec} there.</p>
 *
 * @see SigilEffect
 * @see com.breakinblocks.neovitae.api.sigil.ISigilEffect
 */
public class SigilEffects {

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
    public static final Supplier<MapCodec<NecromancySigilEffect>> NECROMANCY = NecromancySigilEffect.REGISTRATION;
    public static final Supplier<MapCodec<BoundTreasuresSigilEffect>> BOUND_TREASURES = BoundTreasuresSigilEffect.REGISTRATION;

    /**
     * Forces class loading to trigger static initialization of all built-in effect registrations.
     * Called internally during mod setup; addon developers should not call this.
     */
    public static void init() {
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
        var necromancy = NECROMANCY;
        var boundTreasures = BOUND_TREASURES;
    }
}
