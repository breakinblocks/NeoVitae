// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.compat.ftbultimine.FTBUltimineCompat;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for safe block operations that respect protection mods like FTB Chunks.
 * <p>
 * Protection mods listen to BlockEvent.BreakEvent and BlockEvent.EntityPlaceEvent to
 * prevent unauthorized block modifications in claimed chunks. This helper fires these
 * events before performing block operations, allowing protection mods to cancel them.
 * <p>
 * Use these methods whenever NeoVitae needs to:
 * - Break or remove blocks (rituals, sigils, explosives)
 * - Place blocks (rituals, sigils, diviner)
 * - Modify blocks (changing block state, replacing fluids)
 */
public class BlockProtectionHelper {

    @Nullable
    private static Player automationPlayer(Level level, @Nullable Player player) {
        if (player == null || player instanceof FakePlayer || !(level instanceof ServerLevel serverLevel)) {
            return player;
        }
        FakePlayer fake = FakePlayerFactory.get(serverLevel, player.getGameProfile());
        fake.setPos(player.getX(), player.getY(), player.getZ());
        return fake;
    }

    public static boolean tryBreakBlock(Level level, BlockPos pos, @Nullable Player player) {
        if (level.isClientSide()) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return true; // Already air, nothing to break
        }

        Player breaker = automationPlayer(level, player);

        // Fire break event - protection mods can cancel this
        if (!fireBreakEvent(level, pos, state, breaker)) {
            return false;
        }

        // Perform the break
        return level.destroyBlock(pos, true, breaker);
    }

    public static boolean tryBreakBlockNoDrops(Level level, BlockPos pos, @Nullable Player player) {
        if (level.isClientSide()) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return true;
        }

        Player breaker = automationPlayer(level, player);

        if (!fireBreakEvent(level, pos, state, breaker)) {
            return false;
        }

        return level.destroyBlock(pos, false, breaker);
    }

    public static boolean tryRemoveBlock(Level level, BlockPos pos, @Nullable Player player) {
        if (level.isClientSide()) {
            return false;
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return true;
        }

        if (!fireBreakEvent(level, pos, state, player)) {
            return false;
        }

        return level.removeBlock(pos, false);
    }

    public static boolean tryPlaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable Player player) {
        return tryPlaceBlock(level, pos, newState, player, Block.UPDATE_ALL);
    }

    public static boolean tryPlaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable Player player, int updateFlags) {
        if (level.isClientSide()) {
            return false;
        }

        BlockState oldState = level.getBlockState(pos);

        if (!firePlaceEvent(level, pos, oldState, newState, player)) {
            return false;
        }

        return level.setBlock(pos, newState, updateFlags);
    }

    public static boolean tryReplaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable Player player) {
        if (level.isClientSide()) {
            return false;
        }

        BlockState oldState = level.getBlockState(pos);

        if (!oldState.isAir()) {
            if (!fireBreakEvent(level, pos, oldState, player)) {
                return false;
            }
        }

        if (!firePlaceEvent(level, pos, oldState, newState, player)) {
            return false;
        }

        return level.setBlock(pos, newState, Block.UPDATE_ALL);
    }

    public static boolean fireBreakEvent(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return true;
        }

        // If no player context, we can't fire the event properly
        // Some mods may still block based on chunk claims, but we can't simulate a player
        if (player == null) {
            // For non-player operations (rituals/machines), we still want protection
            // Use a fake player approach or just allow it
            // Most protection mods require a player to check permissions
            return true;
        }

        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(serverLevel, pos, state, automationPlayer(level, player));
        FTBUltimineCompat.beginSuppress();
        try {
            NeoForge.EVENT_BUS.post(event);
        } finally {
            FTBUltimineCompat.endSuppress();
        }
        return !event.isCanceled();
    }

    public static boolean firePlaceEvent(Level level, BlockPos pos, BlockState oldState, BlockState newState, @Nullable Player player) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return true;
        }

        if (player == null) {
            return true;
        }

        BlockSnapshot snapshot = BlockSnapshot.create(serverLevel.dimension(), serverLevel, pos);
        BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(snapshot, oldState, automationPlayer(level, player));
        NeoForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static boolean canBreakBlock(Level level, BlockPos pos, @Nullable Player player) {
        if (level.isClientSide()) {
            return true; // Client-side always returns true, server will validate
        }

        BlockState state = level.getBlockState(pos);
        return fireBreakEvent(level, pos, state, player);
    }

    public static boolean canPlaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable Player player) {
        if (level.isClientSide()) {
            return true;
        }

        BlockState oldState = level.getBlockState(pos);
        return firePlaceEvent(level, pos, oldState, newState, player);
    }

    public static List<ItemStack> getDropsIfBreakAllowed(ServerLevel level, BlockPos pos, @Nullable Player player, ItemStack tool) {
        BlockState state = level.getBlockState(pos);

        if (!fireBreakEvent(level, pos, state, player)) {
            return Collections.emptyList();
        }

        BlockEntity be = level.getBlockEntity(pos);
        return Block.getDrops(state, level, pos, be, player, tool);
    }

    @Nullable
    public static Player getPlayerFromUUID(Level level, @Nullable UUID ownerUUID) {
        if (ownerUUID == null || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getServer().getPlayerList().getPlayer(ownerUUID);
    }

    public static boolean tryBreakBlock(Level level, BlockPos pos, @Nullable UUID ownerUUID) {
        return tryBreakBlock(level, pos, getPlayerFromUUID(level, ownerUUID));
    }

    public static boolean tryBreakBlockNoDrops(Level level, BlockPos pos, @Nullable UUID ownerUUID) {
        return tryBreakBlockNoDrops(level, pos, getPlayerFromUUID(level, ownerUUID));
    }

    public static boolean tryRemoveBlock(Level level, BlockPos pos, @Nullable UUID ownerUUID) {
        return tryRemoveBlock(level, pos, getPlayerFromUUID(level, ownerUUID));
    }

    public static boolean tryPlaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable UUID ownerUUID) {
        return tryPlaceBlock(level, pos, newState, getPlayerFromUUID(level, ownerUUID));
    }

    public static boolean tryReplaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable UUID ownerUUID) {
        return tryReplaceBlock(level, pos, newState, getPlayerFromUUID(level, ownerUUID));
    }

    /**
     * If owner is offline, defaults to allowing the operation.
     * Use {@link #canBreakBlockStrict} to deny when owner is offline.
     */
    public static boolean canBreakBlock(Level level, BlockPos pos, @Nullable UUID ownerUUID) {
        return canBreakBlock(level, pos, getPlayerFromUUID(level, ownerUUID));
    }

    /**
     * Strict mode: returns false if the owner UUID is null or the owner is offline.
     */
    public static boolean canBreakBlockStrict(Level level, BlockPos pos, @Nullable UUID ownerUUID) {
        if (ownerUUID == null) {
            return false;
        }
        Player player = getPlayerFromUUID(level, ownerUUID);
        if (player == null) {
            return false;
        }
        return canBreakBlock(level, pos, player);
    }

    public static boolean canPlaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable UUID ownerUUID) {
        return canPlaceBlock(level, pos, newState, getPlayerFromUUID(level, ownerUUID));
    }

    @Nullable
    public static Player getPlayerFromEntity(@Nullable Entity entity) {
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    public static boolean tryPlaceBlock(Level level, BlockPos pos, BlockState newState, @Nullable Entity entity) {
        return tryPlaceBlock(level, pos, newState, getPlayerFromEntity(entity));
    }

    public static boolean tryRemoveBlock(Level level, BlockPos pos, @Nullable Entity entity) {
        return tryRemoveBlock(level, pos, getPlayerFromEntity(entity));
    }

    public static boolean canPlaceBlock(Level level, BlockPos pos, @Nullable Entity entity) {
        if (level.isClientSide()) {
            return true;
        }
        BlockState oldState = level.getBlockState(pos);
        return firePlaceEvent(level, pos, oldState, oldState, getPlayerFromEntity(entity));
    }

    public static boolean canPlaceBlock(Level level, BlockPos pos, @Nullable Player player) {
        if (level.isClientSide()) {
            return true;
        }
        BlockState oldState = level.getBlockState(pos);
        return firePlaceEvent(level, pos, oldState, oldState, player);
    }

    public static boolean canPlaceBlock(Level level, BlockPos pos, @Nullable UUID ownerUUID) {
        return canPlaceBlock(level, pos, getPlayerFromUUID(level, ownerUUID));
    }
}
