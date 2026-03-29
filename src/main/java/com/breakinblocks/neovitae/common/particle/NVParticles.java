package com.breakinblocks.neovitae.common.particle;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NVParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredParticleOptions>> BLOOD_FLAME =
            PARTICLES.register("blood_flame", NVParticles::coloredType);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredParticleOptions>> BLOOD_GLOW =
            PARTICLES.register("blood_glow", NVParticles::coloredType);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredParticleOptions>> BLOOD_DRIP =
            PARTICLES.register("blood_drip", NVParticles::coloredType);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredParticleOptions>> RUNE_GLOW =
            PARTICLES.register("rune_glow", NVParticles::coloredType);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColoredParticleOptions>> BLOOD_BUBBLE =
            PARTICLES.register("blood_bubble", NVParticles::coloredType);

    private static ParticleType<ColoredParticleOptions> coloredType() {
        return new ParticleType<>(false) {
            @Override
            public MapCodec<ColoredParticleOptions> codec() {
                return ColoredParticleOptions.codec(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, ColoredParticleOptions> streamCodec() {
                return ColoredParticleOptions.streamCodec(this);
            }
        };
    }

    public static void register(IEventBus modBus) {
        PARTICLES.register(modBus);
    }
}
