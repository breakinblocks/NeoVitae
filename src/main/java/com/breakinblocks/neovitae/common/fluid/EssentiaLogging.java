package com.breakinblocks.neovitae.common.fluid;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.fluid.EssentiaLoggingAPI;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EssentiaLogging {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Property<Boolean> ESSENTIA_LOGGED = new FalseFirstProperty("essentia_logged");

    private static FluidState loggedFluid;

    private EssentiaLogging() {
    }

    public static boolean isLoggable(BlockState state) {
        return state.hasProperty(ESSENTIA_LOGGED);
    }

    public static boolean isLogged(BlockState state) {
        return state.hasProperty(ESSENTIA_LOGGED) && state.getValue(ESSENTIA_LOGGED);
    }

    public static boolean canLog(BlockState state) {
        return isLoggable(state)
                && !state.getValue(ESSENTIA_LOGGED)
                && !(state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED));
    }

    public static boolean isEssentiaVitae(Fluid fluid) {
        return fluid == NVFluids.ESSENTIA_VITAE_SOURCE.get() || fluid == NVFluids.ESSENTIA_VITAE_FLOWING.get();
    }

    public static FluidState loggedFluidState() {
        FluidState cached = loggedFluid;
        if (cached != null) {
            return cached;
        }
        if (!NVFluids.ESSENTIA_VITAE_SOURCE.isBound()) {
            return Fluids.EMPTY.defaultFluidState();
        }
        cached = NVFluids.ESSENTIA_VITAE_SOURCE.get().getSource(false);
        loggedFluid = cached;
        return cached;
    }

    /**
     * Vanilla caches each blockstate's fluid during bootstrap, long before our fluids exist, so the
     * logged states resolve to nothing on that first pass. Re-running the cache once registration
     * has finished settles them. 26.1 dropped {@code Blocks.rebuildCache}, hence the manual sweep.
     */
    public static void rebuildFluidCache() {
        Block.BLOCK_STATE_REGISTRY.forEach(BlockState::initCache);
    }

    public static void reportUnsupportedBlocks() {
        List<Identifier> skipped = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof SimpleWaterloggedBlock && !EssentiaLoggingAPI.isSupported(block)) {
                skipped.add(BuiltInRegistries.BLOCK.getKey(block));
            }
        }
        if (skipped.isEmpty()) {
            return;
        }
        LOGGER.info("{} block(s) hold water but not Essentia Vitae; ask for support if you want them covered", skipped.size());
        LOGGER.debug("Blocks without Essentia Vitae logging support: {}", skipped);
    }

    /**
     * A boolean property that lists {@code false} first. {@link net.minecraft.world.level.block.state.properties.BooleanProperty}
     * is final in 26.1 and orders {@code true} first, which would make every waterloggable block
     * default to logged, since {@code StateDefinition.any()} takes each property's first value.
     */
    private static final class FalseFirstProperty extends Property<Boolean> {
        private static final List<Boolean> VALUES = List.of(Boolean.FALSE, Boolean.TRUE);

        private FalseFirstProperty(String name) {
            super(name, Boolean.class);
        }

        @Override
        public List<Boolean> getPossibleValues() {
            return VALUES;
        }

        @Override
        public String getName(Boolean value) {
            return value.toString();
        }

        @Override
        public Optional<Boolean> getValue(String name) {
            if ("true".equals(name)) return Optional.of(Boolean.TRUE);
            if ("false".equals(name)) return Optional.of(Boolean.FALSE);
            return Optional.empty();
        }

        @Override
        public int getInternalIndex(Boolean value) {
            return value ? 1 : 0;
        }
    }
}
