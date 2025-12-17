package com.breakinblocks.neovitae.common.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.NeoVitae;

import javax.annotation.Nullable;

/**
 * Utility class for working with the Blood Magic dungeon dimension.
 */
public class DungeonDimensionHelper {

    public static final ResourceKey<Level> DUNGEON_DIMENSION = ResourceKey.create(
            Registries.DIMENSION, NeoVitae.rl("dungeon"));

    /**
     * Gets the dungeon dimension ServerLevel.
     * @param level Any world to get the server from
     * @return The dungeon ServerLevel, or null if not on server side
     */
    @Nullable
    public static ServerLevel getDungeonWorld(Level level) {
        if (level.getServer() == null) {
            return null;
        }
        return level.getServer().getLevel(DUNGEON_DIMENSION);
    }

    /**
     * Checks if the given level is the dungeon dimension.
     */
    public static boolean isDungeonDimension(Level level) {
        return level.dimension().equals(DUNGEON_DIMENSION);
    }

    /**
     * Teleports a player to the dungeon dimension at the specified position.
     * @param player The player to teleport
     * @param destination The destination position in the dungeon
     * @return true if teleportation was successful
     */
    public static boolean teleportToDungeon(Player player, BlockPos destination) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel dungeonLevel = getDungeonWorld(player.level());
        if (dungeonLevel == null) {
            return false;
        }

        serverPlayer.teleportTo(dungeonLevel,
                destination.getX() + 0.5,
                destination.getY(),
                destination.getZ() + 0.5,
                player.getYRot(),
                player.getXRot());
        return true;
    }

    /**
     * Teleports a player from the dungeon back to the overworld at the specified position.
     * @param player The player to teleport
     * @param destination The destination position in the overworld
     * @param targetDimension The dimension to teleport to
     * @return true if teleportation was successful
     */
    public static boolean teleportFromDungeon(Player player, BlockPos destination, ResourceKey<Level> targetDimension) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        ServerLevel targetLevel = player.level().getServer().getLevel(targetDimension);
        if (targetLevel == null) {
            return false;
        }

        serverPlayer.teleportTo(targetLevel,
                destination.getX() + 0.5,
                destination.getY(),
                destination.getZ() + 0.5,
                player.getYRot(),
                player.getXRot());
        return true;
    }

    /**
     * Teleports a player back to the overworld at the specified position.
     * Convenience method for teleporting to the overworld specifically.
     */
    public static boolean teleportToOverworld(Player player, BlockPos destination) {
        return teleportFromDungeon(player, destination, Level.OVERWORLD);
    }

    /**
     * Calculates the dungeon spawn position based on the grid system.
     * Each dungeon instance is placed in a grid to prevent overlap.
     * @param dungeonIndex The index of the dungeon instance
     * @param gridSpacing The spacing between dungeon grid positions
     * @return The calculated spawn position for the dungeon
     */
    public static BlockPos getDungeonSpawnPosition(int dungeonIndex, int gridSpacing) {
        // Simple spiral pattern for now - can be made more sophisticated
        int x = (dungeonIndex % 100) * gridSpacing;
        int z = (dungeonIndex / 100) * gridSpacing;
        int y = 64; // Default Y level for dungeon spawning
        return new BlockPos(x, y, z);
    }

    /**
     * Gets the resource location for the dungeon dimension.
     */
    public static ResourceLocation getDungeonDimensionId() {
        return NeoVitae.rl("dungeon");
    }
}
