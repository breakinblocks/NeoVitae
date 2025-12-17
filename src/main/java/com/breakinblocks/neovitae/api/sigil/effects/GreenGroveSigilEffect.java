package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;

import java.util.function.Supplier;

/**
 * Green Grove Sigil effect - applies bonemeal effect when used on a block
 * and passively grows nearby crops when activated.
 */
public record GreenGroveSigilEffect(int range, int verticalRange) implements SigilEffect {
    public static final MapCodec<GreenGroveSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("range", 3).forGetter(GreenGroveSigilEffect::range),
            Codec.INT.optionalFieldOf("vertical_range", 2).forGetter(GreenGroveSigilEffect::verticalRange)
    ).apply(instance, GreenGroveSigilEffect::new));

    public static final Supplier<MapCodec<GreenGroveSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("green_grove", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        if (PlayerHelper.isFakePlayer(player)) {
            return false;
        }

        if (!level.isClientSide && applyBonemeal(stack, level, blockPos, player)) {
            level.levelEvent(2005, blockPos, 0);
            return true;
        }

        return false;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        if (PlayerHelper.isFakePlayer(player)) {
            return;
        }

        int posX = (int) Math.round(player.getX() - 0.5f);
        int posY = (int) player.getY();
        int posZ = (int) Math.round(player.getZ() - 0.5f);

        if (level instanceof ServerLevel serverWorld) {
            for (int ix = posX - range; ix <= posX + range; ix++) {
                for (int iz = posZ - range; iz <= posZ + range; iz++) {
                    for (int iy = posY - verticalRange; iy <= posY + verticalRange; iy++) {
                        BlockPos blockPos = new BlockPos(ix, iy, iz);
                        BlockState state = level.getBlockState(blockPos);
                        Block block = state.getBlock();

                        if (block instanceof BonemealableBlock growable && block != Blocks.GRASS_BLOCK) {
                            if (level.random.nextInt(50) == 0) {
                                BlockState preBlockState = level.getBlockState(blockPos);
                                if (growable.isValidBonemealTarget(serverWorld, blockPos, preBlockState)) {
                                    if (growable.isBonemealSuccess(level, level.random, blockPos, state)) {
                                        growable.performBonemeal(serverWorld, level.random, blockPos, state);

                                        BlockState newState = level.getBlockState(blockPos);
                                        if (!newState.equals(preBlockState)) {
                                            level.levelEvent(2005, blockPos, 0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean applyBonemeal(ItemStack stack, Level level, BlockPos pos, Player player) {
        BlockState blockstate = level.getBlockState(pos);

        if (blockstate.getBlock() instanceof BonemealableBlock growable) {
            if (growable.isValidBonemealTarget(level, pos, blockstate)) {
                if (level instanceof ServerLevel serverWorld) {
                    if (growable.isBonemealSuccess(level, level.random, pos, blockstate)) {
                        growable.performBonemeal(serverWorld, level.random, pos, blockstate);
                    }
                }
                return true;
            }
        }

        return false;
    }
}
