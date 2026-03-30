package com.breakinblocks.neovitae.api.sigil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

import java.util.Optional;

/**
 * A datapack-driven definition for a sigil, pairing EV cost configuration with
 * an {@link ISigilEffect} implementation that provides the actual behavior.
 *
 * <p>{@code SigilType} instances are loaded from JSON datapacks and registered into
 * NeoVitae's sigil type datapack registry. Each sigil item references a {@code SigilType}
 * to determine its costs and behavior.</p>
 *
 * <h3>Relationship to SigilEffect</h3>
 * <p>{@code SigilType} is the <em>data</em> side -- it holds EV costs, drain intervals,
 * and a reference to the {@link ISigilEffect} that implements the sigil's logic.
 * {@link ISigilEffect} / {@link SigilEffect} is the <em>behavior</em> side -- it defines
 * what happens when the sigil is used or ticked. A single {@code ISigilEffect} implementation
 * can be reused across multiple {@code SigilType} entries with different cost configurations.</p>
 *
 * <h3>Creating Sigil Types</h3>
 * <p>Use the provided factory methods for common patterns:</p>
 * <ul>
 *   <li>{@link #simple(int, ISigilEffect)} -- one-shot sigils with an air-use EV cost</li>
 *   <li>{@link #toggleable(int, int, ISigilEffect)} -- sigils that can be toggled on/off</li>
 *   <li>{@link #toggleableWithUse(int, int, int, ISigilEffect)} -- toggleable sigils that also
 *       have a block-use action with a separate EV cost</li>
 * </ul>
 *
 * @param lpCostAir      EV cost when used in air (right-click air)
 * @param lpCostBlock    EV cost when used on a block
 * @param lpCostEntity   EV cost when used on an entity
 * @param lpCostActive   EV cost per drain interval when the sigil is actively toggled on
 * @param drainInterval  ticks between LP drain for toggleable sigils (default: 100 = 5 seconds)
 * @param effect         the sigil effect implementation, or empty if this type has no behavior
 *
 * @see SigilEffect
 * @see ISigilEffect
 */
public record SigilType(
        int lpCostAir,
        int lpCostBlock,
        int lpCostEntity,
        int lpCostActive,
        int drainInterval,
        Optional<ISigilEffect> effect
) {
    /** Default drain interval in ticks (100 ticks = 5 seconds). */
    public static final int DEFAULT_DRAIN_INTERVAL = 100; // 5 seconds

    /** Codec for serializing/deserializing {@code SigilType} from datapack JSON. */
    public static final Codec<SigilType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("lp_cost_air", 0).forGetter(SigilType::lpCostAir),
            Codec.INT.optionalFieldOf("lp_cost_block", 0).forGetter(SigilType::lpCostBlock),
            Codec.INT.optionalFieldOf("lp_cost_entity", 0).forGetter(SigilType::lpCostEntity),
            Codec.INT.optionalFieldOf("lp_cost_active", 0).forGetter(SigilType::lpCostActive),
            Codec.INT.optionalFieldOf("drain_interval", DEFAULT_DRAIN_INTERVAL).forGetter(SigilType::drainInterval),
            ISigilEffect.DISPATCH_CODEC.get().optionalFieldOf("effect").forGetter(SigilType::effect)
    ).apply(instance, SigilType::new));

    /**
     * Client-side codec - syncs full effect data so client can execute client-side logic
     * (like Air Sigil launching player) and show correct toggle state in tooltips.
     */
    public static final Codec<SigilType> CLIENT_CODEC = CODEC;

    /** Codec that serializes a {@link Holder} reference to a registered {@code SigilType}. */
    public static final Codec<Holder<SigilType>> HOLDER_CODEC = RegistryFixedCodec.create(SigilTypeRegistry.SIGIL_TYPE_KEY);

    /** Network stream codec for syncing {@code SigilType} holder references to clients. */
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SigilType>> HOLDER_STREAM_CODEC =
            ByteBufCodecs.holderRegistry(SigilTypeRegistry.SIGIL_TYPE_KEY);

    /**
     * Returns the translation key for a sigil type, following the pattern
     * {@code "sigil.<namespace>.<path>"}.
     *
     * @param key the registry key of the sigil type
     * @return the translation key string
     */
    public static String descriptionId(ResourceKey<SigilType> key) {
        return Util.makeDescriptionId("sigil", key.location());
    }

    /**
     * Creates a simple one-shot sigil type that drains LP when right-clicked in air.
     *
     * @param lpCost the EV cost for air usage
     * @param effect the sigil effect implementation
     * @return a new non-toggleable sigil type
     */
    public static SigilType simple(int lpCost, ISigilEffect effect) {
        return new SigilType(lpCost, 0, 0, 0, DEFAULT_DRAIN_INTERVAL, Optional.of(effect));
    }

    /**
     * Creates a toggleable sigil type that periodically drains LP while active.
     *
     * @param lpCostActive  EV cost per drain tick while the sigil is toggled on
     * @param drainInterval ticks between each LP drain
     * @param effect        the sigil effect implementation (must return {@code true} from
     *                      {@link ISigilEffect#isToggleable()})
     * @return a new toggleable sigil type
     */
    public static SigilType toggleable(int lpCostActive, int drainInterval, ISigilEffect effect) {
        return new SigilType(0, 0, 0, lpCostActive, drainInterval, Optional.of(effect));
    }

    /**
     * Creates a toggleable sigil type that also has a block-use action with its own EV cost.
     *
     * @param lpCostBlock   EV cost when used on a block
     * @param lpCostActive  EV cost per drain tick while the sigil is toggled on
     * @param drainInterval ticks between each LP drain
     * @param effect        the sigil effect implementation
     * @return a new toggleable sigil type with block-use support
     */
    public static SigilType toggleableWithUse(int lpCostBlock, int lpCostActive, int drainInterval, ISigilEffect effect) {
        return new SigilType(0, lpCostBlock, 0, lpCostActive, drainInterval, Optional.of(effect));
    }

    /**
     * Returns whether this sigil type supports toggling. Delegates to the effect's
     * {@link ISigilEffect#isToggleable()} method.
     *
     * @return {@code true} if the sigil can be toggled on and off
     */
    public boolean isToggleable() {
        return effect.map(ISigilEffect::isToggleable).orElse(false);
    }

    /**
     * Returns the EV cost for the given usage context.
     *
     * @param context the context in which the sigil is being used
     * @return the EV cost, or 0 if no cost is defined for that context
     */
    public int getCostForContext(UseContext context) {
        return switch (context) {
            case AIR -> lpCostAir;
            case BLOCK -> lpCostBlock;
            case ENTITY -> lpCostEntity;
            case ACTIVE -> lpCostActive;
        };
    }

    /**
     * The context in which a sigil is being used, determining which EV cost applies.
     */
    public enum UseContext {
        AIR,
        BLOCK,
        ENTITY,
        ACTIVE
    }
}
