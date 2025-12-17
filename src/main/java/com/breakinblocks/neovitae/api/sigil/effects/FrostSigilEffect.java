package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.function.Supplier;

/**
 * Sigil effect that freezes water blocks below the player into ice.
 */
public record FrostSigilEffect(int range) implements SigilEffect {

    public static final int DEFAULT_RANGE = 2;

    public static final MapCodec<FrostSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("range", DEFAULT_RANGE).forGetter(FrostSigilEffect::range)
            ).apply(instance, FrostSigilEffect::new)
    );

    public static final Supplier<MapCodec<FrostSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("frost", () -> CODEC);

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
        int playerX = playerPos.getX();
        int playerY = playerPos.getY();
        int playerZ = playerPos.getZ();

        // Check blocks below the player in a radius
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                BlockPos checkPos = new BlockPos(playerX + x, playerY - 1, playerZ + z);
                BlockState state = level.getBlockState(checkPos);

                // Check if it's a water source block
                if (state.getBlock() == Blocks.WATER && state.getValue(LiquidBlock.LEVEL) == 0) {
                    if (BlockProtectionHelper.canBreakBlock(level, checkPos, player)) {
                        level.setBlockAndUpdate(checkPos, Blocks.ICE.defaultBlockState());
                    }
                }
            }
        }
    }
}
