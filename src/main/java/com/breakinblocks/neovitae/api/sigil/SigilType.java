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
 * Defines a sigil type loaded from datapacks.
 * Contains LP costs and effect reference for the sigil.
 *
 * @param lpCostAir      LP cost when used in air (right-click air)
 * @param lpCostBlock    LP cost when used on a block
 * @param lpCostEntity   LP cost when used on an entity
 * @param lpCostActive   LP cost per drain interval when active (toggleable sigils)
 * @param drainInterval  Ticks between LP drain for toggleable sigils
 * @param effect         The sigil effect implementation
 */
public record SigilType(
        int lpCostAir,
        int lpCostBlock,
        int lpCostEntity,
        int lpCostActive,
        int drainInterval,
        Optional<ISigilEffect> effect
) {
    public static final int DEFAULT_DRAIN_INTERVAL = 100; // 5 seconds

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

    public static final Codec<Holder<SigilType>> HOLDER_CODEC = RegistryFixedCodec.create(SigilTypeRegistry.SIGIL_TYPE_KEY);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<SigilType>> HOLDER_STREAM_CODEC =
            ByteBufCodecs.holderRegistry(SigilTypeRegistry.SIGIL_TYPE_KEY);

    /**
     * Generates a translation key for the given sigil type resource key.
     */
    public static String descriptionId(ResourceKey<SigilType> key) {
        return Util.makeDescriptionId("sigil", key.location());
    }

    /**
     * Creates a simple sigil type with only air use cost.
     */
    public static SigilType simple(int lpCost, ISigilEffect effect) {
        return new SigilType(lpCost, 0, 0, 0, DEFAULT_DRAIN_INTERVAL, Optional.of(effect));
    }

    /**
     * Creates a toggleable sigil type with active cost.
     */
    public static SigilType toggleable(int lpCostActive, int drainInterval, ISigilEffect effect) {
        return new SigilType(0, 0, 0, lpCostActive, drainInterval, Optional.of(effect));
    }

    /**
     * Creates a toggleable sigil type that also has block/entity interaction.
     */
    public static SigilType toggleableWithUse(int lpCostBlock, int lpCostActive, int drainInterval, ISigilEffect effect) {
        return new SigilType(0, lpCostBlock, 0, lpCostActive, drainInterval, Optional.of(effect));
    }

    /**
     * Whether this sigil is toggleable (has an active tick component).
     */
    public boolean isToggleable() {
        return effect.map(ISigilEffect::isToggleable).orElse(false);
    }

    /**
     * Gets the appropriate LP cost based on the usage context.
     */
    public int getCostForContext(UseContext context) {
        return switch (context) {
            case AIR -> lpCostAir;
            case BLOCK -> lpCostBlock;
            case ENTITY -> lpCostEntity;
            case ACTIVE -> lpCostActive;
        };
    }

    public enum UseContext {
        AIR,
        BLOCK,
        ENTITY,
        ACTIVE
    }
}
