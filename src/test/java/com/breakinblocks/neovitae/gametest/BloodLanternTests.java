package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BloodLanternSpawnHandler;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class BloodLanternTests {
    private static final BlockPos LANTERN = new BlockPos(2, 2, 2);

    @GameTest(template = "empty_5x5x7")
    public void chunkLoadDoesNotReadSections(GameTestHelper helper) {
        LevelChunk chunk = new LevelChunk(helper.getLevel(), new ChunkPos(1_000_000, 1_000_000)) {
            @Override
            public LevelChunkSection[] getSections() {
                throw new AssertionError("Chunk-load callback scanned chunk sections");
            }
        };
        BloodLanternSpawnHandler.onChunkLoad(new ChunkEvent.Load(chunk, false));
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7")
    public void firstSpawnFindsExistingBloodLantern(GameTestHelper helper) {
        place(helper, NVBlocks.BLOOD_LANTERN.block().get());
        helper.assertTrue(spawn(helper, EntityType.COW, MobSpawnType.NATURAL).isSpawnCancelled(),
                "First spawn did not discover the existing lantern");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7")
    public void monstersAndGenerationAreUnaffected(GameTestHelper helper) {
        place(helper, NVBlocks.BLOOD_LANTERN.block().get());
        helper.assertTrue(!spawn(helper, EntityType.ZOMBIE, MobSpawnType.NATURAL).isSpawnCancelled(),
                "Lantern suppressed a monster");
        helper.assertTrue(!spawn(helper, EntityType.COW, MobSpawnType.CHUNK_GENERATION).isSpawnCancelled(),
                "Lantern suppressed chunk generation");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7")
    public void removedLanternDoesNotSuppress(GameTestHelper helper) {
        place(helper, NVBlocks.BLOOD_LANTERN.block().get());
        helper.assertTrue(spawn(helper, EntityType.COW, MobSpawnType.NATURAL).isSpawnCancelled(),
                "Lantern was not indexed");
        helper.setBlock(LANTERN, Blocks.AIR);
        helper.assertTrue(!spawn(helper, EntityType.COW, MobSpawnType.NATURAL).isSpawnCancelled(),
                "Stale cache entry suppressed a spawn after removal");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7")
    public void poweredDemonLanternDoesNotSuppress(GameTestHelper helper) {
        place(helper, NVBlocks.DEMON_LANTERN.block().get());
        helper.assertTrue(spawn(helper, EntityType.COW, MobSpawnType.NATURAL).isSpawnCancelled(),
                "Unpowered Demon Lantern did not suppress a spawn");
        helper.setBlock(LANTERN.east(), Blocks.REDSTONE_BLOCK);
        helper.assertTrue(!spawn(helper, EntityType.COW, MobSpawnType.NATURAL).isSpawnCancelled(),
                "Powered Demon Lantern suppressed a spawn");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7")
    public void configDisablesSuppression(GameTestHelper helper) {
        place(helper, NVBlocks.BLOOD_LANTERN.block().get());
        boolean previous = NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.get();
        try {
            NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.set(false);
            helper.assertTrue(!spawn(helper, EntityType.COW, MobSpawnType.NATURAL).isSpawnCancelled(),
                    "Disabled lantern handler suppressed a spawn");
        } finally {
            NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.set(previous);
        }
        helper.assertTrue(spawn(helper, EntityType.COW, MobSpawnType.NATURAL).isSpawnCancelled(),
                "Re-enabled handler failed to rediscover a lantern");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7")
    public void spawnCheckDoesNotLoadMissingChunks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos remote = new BlockPos(24_000_000, 80, 24_000_000);
        assertAreaUnloaded(helper, remote);
        FinalizeSpawnEvent event = event(level, EntityType.COW, MobSpawnType.NATURAL, remote);
        BloodLanternSpawnHandler.onFinalizeSpawn(event);
        assertAreaUnloaded(helper, remote);
        helper.assertTrue(!event.isSpawnCancelled(), "Empty unloaded area suppressed a spawn");
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void demonLanternDoesNotLoadRedstoneNeighborChunk(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        int chunkX = 1_400_000;
        int chunkZ = 1_400_000;
        LevelChunk chunk = level.getChunk(chunkX, chunkZ);
        BlockPos pos = new BlockPos((chunkX << 4) + 15, 200, (chunkZ << 4) + 8);
        helper.assertTrue(level.getChunkSource().getChunkNow(chunkX + 1, chunkZ) == null,
                "Test requires an unloaded east neighbor");
        chunk.setBlockState(pos, NVBlocks.DEMON_LANTERN.block().get().defaultBlockState(), false);
        BloodLanternSpawnHandler.onChunkLoad(new ChunkEvent.Load(chunk, false));
        try {
            FinalizeSpawnEvent event = event(level, EntityType.COW, MobSpawnType.NATURAL, pos.above());
            BloodLanternSpawnHandler.onFinalizeSpawn(event);
            helper.assertTrue(level.getChunkSource().getChunkNow(chunkX + 1, chunkZ) == null,
                    "Demon Lantern redstone check loaded its neighbor chunk");
            helper.assertTrue(!event.isSpawnCancelled(), "Suppression must wait for loaded redstone neighbors");
        } finally {
            chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
            BloodLanternSpawnHandler.onChunkUnload(new ChunkEvent.Unload(chunk));
        }
        helper.succeed();
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

    private static FinalizeSpawnEvent spawn(GameTestHelper helper, EntityType<? extends Mob> type, MobSpawnType reason) {
        FinalizeSpawnEvent event = event(helper.getLevel(), type, reason, helper.absolutePos(LANTERN.above()));
        BloodLanternSpawnHandler.onFinalizeSpawn(event);
        return event;
    }

    private static FinalizeSpawnEvent event(ServerLevel level, EntityType<? extends Mob> type, MobSpawnType reason, BlockPos pos) {
        Mob mob = type.create(level);
        return new FinalizeSpawnEvent(mob, level, pos.getX(), pos.getY(), pos.getZ(),
                new DifficultyInstance(level.getDifficulty(), 0, 0, 0), reason, null, null);
    }
}
