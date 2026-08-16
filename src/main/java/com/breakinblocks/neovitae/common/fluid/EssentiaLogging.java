package com.breakinblocks.neovitae.common.fluid;

import com.breakinblocks.neovitae.api.fluid.EssentiaLoggingAPI;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class EssentiaLogging {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final BooleanProperty ESSENTIA_LOGGED = new FalseFirstProperty("essentia_logged");

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

    public static void reportUnsupportedBlocks() {
        if (!EssentiaLoggingConfig.isEnabled()) {
            LOGGER.info("Essentia logging is disabled in neovitae-startup.toml; blocks will not hold Essentia Vitae");
            return;
        }
        List<ResourceLocation> skipped = new ArrayList<>();
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

    private static final class FalseFirstProperty extends BooleanProperty {
        private static final Collection<Boolean> VALUES = List.of(Boolean.FALSE, Boolean.TRUE);

        private FalseFirstProperty(String name) {
            super(name);
        }

        @Override
        public Collection<Boolean> getPossibleValues() {
            return VALUES;
        }
    }
}
