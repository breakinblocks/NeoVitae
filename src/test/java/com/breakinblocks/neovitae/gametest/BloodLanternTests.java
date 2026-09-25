package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BloodLanternSpawnHandler;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

public final class BloodLanternTests {
    private static final BlockPos LANTERN = new BlockPos(2, 2, 2);

    private BloodLanternTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("blood_lantern/chunk_load_does_not_read_sections", 100, helper -> {
            LevelChunk chunk = new LevelChunk(helper.getLevel(), new ChunkPos(1_000_000, 1_000_000)) {
                @Override
                public LevelChunkSection[] getSections() {
                    throw new AssertionError("Chunk-load callback scanned chunk sections");
                }
            };
            BloodLanternSpawnHandler.onChunkLoad(new ChunkEvent.Load(chunk, false));
            helper.succeed();
        });

        r.addIsolated("blood_lantern/first_spawn_finds_existing_blood_lantern", 100, helper -> {
            place(helper, NVBlocks.BLOOD_LANTERN.block().get());
            helper.assertTrue(spawn(helper, EntityType.COW, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                    "First spawn did not discover the existing lantern");
            helper.succeed();
        });

        r.addIsolated("blood_lantern/monsters_and_generation_are_unaffected", 100, helper -> {
            place(helper, NVBlocks.BLOOD_LANTERN.block().get());
            helper.assertTrue(!spawn(helper, EntityType.ZOMBIE, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                    "Lantern suppressed a monster");
            helper.assertTrue(!spawn(helper, EntityType.COW, EntitySpawnReason.CHUNK_GENERATION).isSpawnCancelled(),
                    "Lantern suppressed chunk generation");
            helper.succeed();
        });

        r.addIsolated("blood_lantern/removed_lantern_does_not_suppress", 100, helper -> {
            place(helper, NVBlocks.BLOOD_LANTERN.block().get());
            helper.assertTrue(spawn(helper, EntityType.COW, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                    "Lantern was not indexed");
            helper.setBlock(LANTERN, Blocks.AIR);
            helper.assertTrue(!spawn(helper, EntityType.COW, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                    "Stale cache entry suppressed a spawn after removal");
            helper.succeed();
        });

        r.addIsolated("blood_lantern/powered_demon_lantern_does_not_suppress", 100, helper -> {
            place(helper, NVBlocks.DEMON_LANTERN.block().get());
            helper.assertTrue(spawn(helper, EntityType.COW, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                    "Unpowered Demon Lantern did not suppress a spawn");
            helper.setBlock(LANTERN.east(), Blocks.REDSTONE_BLOCK);
            helper.assertTrue(!spawn(helper, EntityType.COW, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                    "Powered Demon Lantern suppressed a spawn");
            helper.succeed();
        });

        r.addIsolated("blood_lantern/config_disables_suppression", 100, helper -> {
            place(helper, NVBlocks.BLOOD_LANTERN.block().get());
            boolean previous = NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.get();
            try {
                NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.set(false);
                helper.assertTrue(!spawn(helper, EntityType.COW, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                        "Disabled lantern handler suppressed a spawn");
            } finally {
                NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.set(previous);
            }
            helper.assertTrue(spawn(helper, EntityType.COW, EntitySpawnReason.NATURAL).isSpawnCancelled(),
                    "Re-enabled handler failed to rediscover a lantern");
            helper.succeed();
        });

        r.add("blood_lantern/spawn_check_does_not_load_missing_chunks", 100, helper -> {
            ServerLevel level = helper.getLevel();
            BlockPos remote = new BlockPos(24_000_000, 80, 24_000_000);
            assertAreaUnloaded(helper, remote);
            FinalizeSpawnEvent event = event(level, EntityType.COW, EntitySpawnReason.NATURAL, remote);
            BloodLanternSpawnHandler.onFinalizeSpawn(event);
            assertAreaUnloaded(helper, remote);
            helper.assertTrue(!event.isSpawnCancelled(), "Empty unloaded area suppressed a spawn");
            helper.succeed();
        });

        r.add("blood_lantern/demon_lantern_does_not_load_redstone_neighbor_chunk", 100, helper -> {
            ServerLevel level = helper.getLevel();
            int chunkX = 1_400_000;
            int chunkZ = 1_400_000;
            LevelChunk chunk = level.getChunk(chunkX, chunkZ);
            BlockPos pos = new BlockPos((chunkX << 4) + 15, 200, (chunkZ << 4) + 8);
            helper.assertTrue(level.getChunkSource().getChunkNow(chunkX + 1, chunkZ) == null,
                    "Test requires an unloaded east neighbor");
            chunk.setBlockState(pos, NVBlocks.DEMON_LANTERN.block().get().defaultBlockState(), 0);
            BloodLanternSpawnHandler.onChunkLoad(new ChunkEvent.Load(chunk, false));
            try {
                FinalizeSpawnEvent event = event(level, EntityType.COW, EntitySpawnReason.NATURAL, pos.above());
                BloodLanternSpawnHandler.onFinalizeSpawn(event);
                helper.assertTrue(level.getChunkSource().getChunkNow(chunkX + 1, chunkZ) == null,
                        "Demon Lantern redstone check loaded its neighbor chunk");
                helper.assertTrue(!event.isSpawnCancelled(), "Suppression must wait for loaded redstone neighbors");
            } finally {
                chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), 0);
                BloodLanternSpawnHandler.onChunkUnload(new ChunkEvent.Unload(chunk));
            }
            helper.succeed();
        });
    }

    private static void assertAreaUnloaded(GameTestHelper helper, BlockPos pos) {
        for (int x = (pos.getX() >> 4) - 1; x <= (pos.getX() >> 4) + 1; x++) {
            for (int z = (pos.getZ() >> 4) - 1; z <= (pos.getZ() >> 4) + 1; z++) {
                helper.assertTrue(helper.getLevel().getChunkSource().getChunkNow(x, z) == null,
                        "Spawn check loaded a remote chunk");
            }
        }
    }

    private static void place(GameTestHelper helper, Block block) {
        helper.setBlock(LANTERN.below(), Blocks.STONE);
        helper.setBlock(LANTERN, block);
        // Simulate discovering a saved chunk, without relying on a player-placement event.
        LevelChunk chunk = helper.getLevel().getChunkAt(helper.absolutePos(LANTERN));
        BloodLanternSpawnHandler.onChunkLoad(new ChunkEvent.Load(chunk, false));
    }

    private static FinalizeSpawnEvent spawn(GameTestHelper helper, EntityType<? extends Mob> type, EntitySpawnReason reason) {
        FinalizeSpawnEvent event = event(helper.getLevel(), type, reason, helper.absolutePos(LANTERN.above()));
        BloodLanternSpawnHandler.onFinalizeSpawn(event);
        return event;
    }

    private static FinalizeSpawnEvent event(ServerLevel level, EntityType<? extends Mob> type, EntitySpawnReason reason, BlockPos pos) {
        Mob mob = type.create(level, reason);
        return new FinalizeSpawnEvent(mob, level, pos.getX(), pos.getY(), pos.getZ(),
                new DifficultyInstance(level.getDifficulty(), 0, 0, 0), reason, null, null);
    }
}
