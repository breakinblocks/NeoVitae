package com.breakinblocks.neovitae.api.fluid;

import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.BigDripleafStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.HeavyCoreBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.MangroveRootsBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls which blocks can hold Essentia Vitae the way vanilla blocks hold water.
 *
 * <p>Support is granted per block <em>class</em>, not per block instance, and is
 * checked with {@code isInstance}, so a mod that extends a vanilla block class
 * (the usual case for slabs, stairs, fences and walls) is supported already.</p>
 *
 * <p>NeoVitae adds an extra blockstate property to every supported block. A block
 * that builds its own {@code Map<BlockState, VoxelShape>} by listing property
 * combinations by hand, rather than using {@code Block#getShapeForEachState},
 * will not have an entry for the new states and will fail on world load. That is
 * why support is opt-in rather than granted to everything implementing
 * {@link SimpleWaterloggedBlock}.</p>
 *
 * <h2>Timing</h2>
 * <p>Blocks are constructed before any lifecycle event NeoVitae could fire, so
 * {@link #support(Class)} must be called from your mod's <strong>constructor</strong>,
 * before your blocks are registered. Calling it later has no effect on blocks that
 * already exist.</p>
 *
 * <pre>{@code
 * public MyMod(IEventBus modBus) {
 *     EssentiaLoggingAPI.support(MyFancySlabBlock.class);
 *     MY_BLOCKS.register(modBus);
 * }
 * }</pre>
 *
 * <p>The feature is experimental and off by default. Players and pack makers turn
 * it on with {@code essentiaLogging = true} in {@code config/neovitae-startup.toml};
 * while disabled, no block receives the property and this registry is ignored.</p>
 */
public final class EssentiaLoggingAPI {

    private static final List<Class<?>> SUPPORTED = new ArrayList<>(List.of(
            AmethystClusterBlock.class,
            BarrierBlock.class,
            BaseCoralPlantTypeBlock.class,
            BaseRailBlock.class,
            BigDripleafBlock.class,
            BigDripleafStemBlock.class,
            CampfireBlock.class,
            CandleBlock.class,
            ChainBlock.class,
            ChestBlock.class,
            ConduitBlock.class,
            CrossCollisionBlock.class,
            DecoratedPotBlock.class,
            EnderChestBlock.class,
            HangingRootsBlock.class,
            HeavyCoreBlock.class,
            LadderBlock.class,
            LanternBlock.class,
            LeavesBlock.class,
            LightBlock.class,
            LightningRodBlock.class,
            MangrovePropaguleBlock.class,
            MangroveRootsBlock.class,
            MultifaceBlock.class,
            PointedDripstoneBlock.class,
            ScaffoldingBlock.class,
            SculkSensorBlock.class,
            SculkShriekerBlock.class,
            SeaPickleBlock.class,
            SignBlock.class,
            SlabBlock.class,
            SmallDripleafBlock.class,
            StairBlock.class,
            TrapDoorBlock.class,
            WallBlock.class,
            WaterloggedTransparentBlock.class
    ));

    private static final List<Class<?>> EXCLUDED = new ArrayList<>();

    private EssentiaLoggingAPI() {
    }

    /**
     * Marks a block class as able to hold Essentia Vitae. Subclasses are covered too.
     *
     * @param type the block class to support
     */
    public static synchronized void support(Class<? extends Block> type) {
        if (!SUPPORTED.contains(type)) {
            SUPPORTED.add(type);
        }
    }

    /**
     * Withdraws support from a block class, overriding {@link #support(Class)}.
     * Use this when a supported class has a subclass that cannot cope with the
     * extra blockstate property.
     *
     * @param type the block class to leave alone
     */
    public static synchronized void exclude(Class<? extends Block> type) {
        if (!EXCLUDED.contains(type)) {
            EXCLUDED.add(type);
        }
    }

    /**
     * @param block the block being constructed
     * @return whether this block should carry the Essentia Vitae logging property
     */
    public static synchronized boolean isSupported(Block block) {
        if (!(block instanceof SimpleWaterloggedBlock)) {
            return false;
        }
        for (Class<?> type : EXCLUDED) {
            if (type.isInstance(block)) {
                return false;
            }
        }
        for (Class<?> type : SUPPORTED) {
            if (type.isInstance(block)) {
                return true;
            }
        }
        return false;
    }
}
