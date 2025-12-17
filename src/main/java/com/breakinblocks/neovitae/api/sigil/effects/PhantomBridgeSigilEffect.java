package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.blockentity.PhantomBridgeTile;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.function.Supplier;

/**
 * Sigil effect that creates a phantom platform of blocks below the player.
 * The phantom blocks disappear after a duration or when the player moves away.
 */
public record PhantomBridgeSigilEffect(int range) implements SigilEffect {

    public static final int DEFAULT_RANGE = 2;

    public static final MapCodec<PhantomBridgeSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("range", DEFAULT_RANGE).forGetter(PhantomBridgeSigilEffect::range)
            ).apply(instance, PhantomBridgeSigilEffect::new)
    );

    public static final Supplier<MapCodec<PhantomBridgeSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("phantom_bridge", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        if (level.isClientSide) {
            return;
        }

        BlockPos playerPos = player.blockPosition();
        int belowY = playerPos.getY() - 1;

        // Create a platform below the player
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                BlockPos checkPos = new BlockPos(playerPos.getX() + x, belowY, playerPos.getZ() + z);
                BlockState state = level.getBlockState(checkPos);

                // Only place phantom blocks in air or replaceable blocks
                if (state.isAir() || state.canBeReplaced()) {
                    BlockState phantomState = BMBlocks.PHANTOM_BRIDGE_BLOCK.get().defaultBlockState();
                    level.setBlockAndUpdate(checkPos, phantomState);
                    // Reset duration on the tile entity
                    BlockEntity be = level.getBlockEntity(checkPos);
                    if (be instanceof PhantomBridgeTile phantomTile) {
                        phantomTile.resetDuration();
                    }
                } else if (level.getBlockEntity(checkPos) instanceof PhantomBridgeTile existingPhantom) {
                    // Refresh existing phantom bridge blocks
                    existingPhantom.resetDuration();
                }
            }
        }
    }
}
