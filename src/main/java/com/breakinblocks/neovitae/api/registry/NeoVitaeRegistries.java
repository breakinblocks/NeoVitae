package com.breakinblocks.neovitae.api.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitual;
import com.breakinblocks.neovitae.api.ritual.IRitual;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;

/**
 * Registry keys for Neo Vitae registries.
 * Use these keys with NeoForge's DeferredRegister to register custom content.
 *
 * <p>Example usage for custom rituals:</p>
 * <pre>{@code
 * // For regular rituals (extend Ritual class from main package)
 * public static final DeferredRegister<Ritual> MY_RITUALS =
 *     DeferredRegister.create(NeoVitaeRegistries.RITUAL_KEY, "yourmodid");
 *
 * // For imperfect rituals (extend ImperfectRitual class from main package)
 * public static final DeferredRegister<ImperfectRitual> MY_IMPERFECT_RITUALS =
 *     DeferredRegister.create(NeoVitaeRegistries.IMPERFECT_RITUAL_KEY, "yourmodid");
 * }</pre>
 *
 * <p>Example usage for custom sigil effects:</p>
 * <pre>{@code
 * public static final DeferredRegister<MapCodec<? extends ISigilEffect>> MY_SIGIL_EFFECTS =
 *     DeferredRegister.create(NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY, "yourmodid");
 *
 * public static final Supplier<MapCodec<MyCustomEffect>> MY_EFFECT =
 *     MY_SIGIL_EFFECTS.register("my_effect", () -> MyCustomEffect.CODEC);
 * }</pre>
 *
 * <p>Note: The actual implementation classes are in the main package.
 * This is because they contain complex logic that depends on internal systems.
 * The registry keys here use the interface types for reference.</p>
 */
public final class NeoVitaeRegistries {
    private NeoVitaeRegistries() {}

    private static final String NEOVITAE_NAMESPACE = "neovitae";

    /**
     * Registry key for regular rituals.
     * Rituals are multiblock structures that provide ongoing effects.
     */
    public static final ResourceKey<Registry<IRitual>> RITUAL_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(NEOVITAE_NAMESPACE, "ritual"));

    /**
     * Registry key for imperfect rituals.
     * Imperfect rituals are simpler one-time effects triggered by placing a block.
     */
    public static final ResourceKey<Registry<IImperfectRitual>> IMPERFECT_RITUAL_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(NEOVITAE_NAMESPACE, "imperfect_ritual"));

    /**
     * Registry key for sigil effect types.
     * Each sigil effect type has a MapCodec for serialization.
     *
     * <p>Implement {@link ISigilEffect} and register your effect's codec
     * to add custom sigil behaviors.</p>
     */
    public static final ResourceKey<Registry<MapCodec<? extends ISigilEffect>>> SIGIL_EFFECT_TYPE_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(NEOVITAE_NAMESPACE, "sigil_effect_type"));
}
