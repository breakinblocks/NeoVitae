package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpectralBlockTile;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.function.Supplier;

/**
 * Sigil effect that suppresses fluids in an area around the player,
 * replacing them with spectral blocks that restore the fluid when they expire.
 */
public record SuppressionSigilEffect(int range, int verticalRange) implements SigilEffect {

    public static final int DEFAULT_RANGE = 5;
    public static final int DEFAULT_VERTICAL_RANGE = 5;

    public static final MapCodec<SuppressionSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("range", DEFAULT_RANGE).forGetter(SuppressionSigilEffect::range),
                    Codec.INT.optionalFieldOf("vertical_range", DEFAULT_VERTICAL_RANGE).forGetter(SuppressionSigilEffect::verticalRange)
            ).apply(instance, SuppressionSigilEffect::new)
    );

    public static final Supplier<MapCodec<SuppressionSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("suppression", () -> CODEC);

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

        for (int x = -range; x <= range; x++) {
            for (int y = -verticalRange; y <= verticalRange; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    FluidState fluidState = state.getFluidState();

                    // Check if block contains fluid
                    if (!fluidState.isEmpty()) {
                        if (BlockProtectionHelper.canBreakBlock(level, checkPos, player)) {
                            // Replace with spectral block that will restore the fluid
                            BlockState originalState = state;
                            level.setBlockAndUpdate(checkPos, BMBlocks.SPECTRAL_BLOCK.get().defaultBlockState());
                            if (level.getBlockEntity(checkPos) instanceof SpectralBlockTile spectral) {
                                spectral.setContainedBlockState(originalState);
                                spectral.resetDuration();
                            }
                        }
                    }
                }
            }
        }
    }
}
