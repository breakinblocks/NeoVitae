package com.breakinblocks.neovitae.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.registry.NeoVitaeRegistries;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.api.sigil.effects.SigilEffects;

import java.util.function.Supplier;

/**
 * Registry for sigil effect types.
 * This is a builtin registry that maps effect type names to their codecs.
 *
 * <p>External mods should use {@link NeoVitaeRegistries#SIGIL_EFFECT_TYPE_KEY}
 * to register custom sigil effects.</p>
 */
public class SigilEffectRegistry {
    /**
     * @deprecated Use {@link NeoVitaeRegistries#SIGIL_EFFECT_TYPE_KEY} instead
     */
    @Deprecated
    public static final ResourceKey<Registry<MapCodec<? extends ISigilEffect>>> SIGIL_EFFECT_TYPE_KEY =
            NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY;

    @SuppressWarnings("unchecked")
    public static final DeferredRegister<MapCodec<? extends ISigilEffect>> SIGIL_EFFECT_TYPES =
            DeferredRegister.create(NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY, NeoVitae.MODID);

    /**
     * Lazily initialized dispatch codec for sigil effects.
     * Dispatches to the appropriate codec based on the "type" field.
     * Returns ISigilEffect for compatibility with external mods.
     */
    @SuppressWarnings("unchecked")
    public static final Supplier<Codec<ISigilEffect>> SIGIL_EFFECT_CODEC = () -> Codec.lazyInitialized(() ->
            SIGIL_EFFECT_TYPES.getRegistry().get()
                    .byNameCodec()
                    .dispatch("type", ISigilEffect::codec, codec -> (MapCodec<ISigilEffect>) codec)
    );

    public static void register(IEventBus modBus) {
        SIGIL_EFFECT_TYPES.makeRegistry(builder -> {});
        SIGIL_EFFECT_TYPES.register(modBus);

        // Initialize the dispatch codec holder in the API
        ISigilEffect.DISPATCH_CODEC.setDelegate(SIGIL_EFFECT_CODEC);

        // Initialize all built-in effect types
        SigilEffects.init();
    }
}
