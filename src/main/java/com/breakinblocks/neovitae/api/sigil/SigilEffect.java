package com.breakinblocks.neovitae.api.sigil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.function.Supplier;

/**
 * Internal interface extending the API's ISigilEffect with codec support.
 * Use this interface when implementing sigil effects within Blood Magic.
 *
 * <p>External mods should implement {@link ISigilEffect} from the API package.</p>
 */
public interface SigilEffect extends ISigilEffect {

    /**
     * Lazy-initialized codec that dispatches to registered effect types.
     * @deprecated Use {@link ISigilEffect#DISPATCH_CODEC} instead for better compatibility.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    Supplier<Codec<SigilEffect>> CODEC = () -> (Codec<SigilEffect>) (Codec<?>) SigilEffectRegistry.SIGIL_EFFECT_CODEC.get();

    /**
     * Returns the codec for this specific effect type.
     */
    @Override
    MapCodec<? extends SigilEffect> codec();
}
