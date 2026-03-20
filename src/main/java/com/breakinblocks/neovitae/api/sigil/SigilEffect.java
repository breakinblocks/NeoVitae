package com.breakinblocks.neovitae.api.sigil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.function.Supplier;

/**
 * Extension of {@link ISigilEffect} used by NeoVitae's built-in sigil effects.
 *
 * <p>Addon developers creating custom sigil effects should implement this interface
 * (or {@link ISigilEffect} directly) and provide a {@link MapCodec} for serialization.
 * The codec is registered via NeoForge's {@code DeferredRegister} against
 * {@link com.breakinblocks.neovitae.api.registry.NeoVitaeRegistries#SIGIL_EFFECT_TYPE_KEY}.</p>
 *
 * <h3>Lifecycle</h3>
 * <p>Sigil effects are invoked by the sigil item at specific points:</p>
 * <ul>
 *   <li>{@link #useOnAir} -- right-click in air</li>
 *   <li>{@link #useOnBlock} -- right-click on a block</li>
 *   <li>{@link #useOnEntity} -- right-click on an entity</li>
 *   <li>{@link #activeTick} -- called every tick while a toggleable sigil is active</li>
 * </ul>
 * <p>Each use method returns {@code true} to signal that the effect was performed and LP
 * should be drained from the player's soul network. Returning {@code false} skips the LP cost.</p>
 *
 * <h3>Codec Registration</h3>
 * <p>Every concrete effect must expose a {@link MapCodec} via {@link #codec()}. The codec is
 * used by the dispatch codec system to serialize/deserialize effect instances from datapack JSON.
 * For singleton (stateless) effects, use {@link MapCodec#unit}. For effects with configurable
 * fields, build a {@link com.mojang.serialization.codecs.RecordCodecBuilder}.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * public record MyEffect(int radius) implements SigilEffect {
 *     public static final MapCodec<MyEffect> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
 *             Codec.INT.fieldOf("radius").forGetter(MyEffect::radius)
 *     ).apply(i, MyEffect::new));
 *
 *     @Override
 *     public MapCodec<? extends SigilEffect> codec() { return CODEC; }
 *
 *     @Override
 *     public boolean useOnAir(Level level, Player player, ItemStack stack) {
 *         // effect logic here
 *         return true;
 *     }
 * }
 * }</pre>
 *
 * @see ISigilEffect
 * @see SigilType
 */
public interface SigilEffect extends ISigilEffect {

    /**
     * Lazy-initialized dispatch codec that resolves registered effect types by their
     * {@code "type"} key in JSON.
     *
     * @deprecated Use {@link ISigilEffect#DISPATCH_CODEC} instead for better cross-mod compatibility.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    Supplier<Codec<SigilEffect>> CODEC = () -> (Codec<SigilEffect>) (Codec<?>) SigilEffectRegistry.SIGIL_EFFECT_CODEC.get();

    /**
     * Returns the {@link MapCodec} for this effect type. The returned codec is used by the
     * dispatch codec to serialize and deserialize this effect from datapack JSON.
     *
     * <p>Implementations must return a constant codec instance, typically declared as a
     * {@code public static final} field on the effect class.</p>
     *
     * @return the map codec for this effect type
     */
    @Override
    MapCodec<? extends SigilEffect> codec();
}
