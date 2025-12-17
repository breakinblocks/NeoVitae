package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.entity.projectile.EntityBloodLight;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.function.Supplier;

/**
 * Sigil effect that creates blood light sources.
 * When looking at a block surface, places a blood light block.
 * When looking at air, throws a blood light projectile.
 */
public record BloodLightSigilEffect(int lifespan) implements SigilEffect {

    public static final int DEFAULT_LIFESPAN = BloodLightBlock.DEFAULT_LIFESPAN;

    public static final MapCodec<BloodLightSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("lifespan", DEFAULT_LIFESPAN).forGetter(BloodLightSigilEffect::lifespan)
            ).apply(instance, BloodLightSigilEffect::new)
    );

    public static final Supplier<MapCodec<BloodLightSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("blood_light", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        HitResult rayTrace = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (rayTrace != null && rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockRayTrace = (BlockHitResult) rayTrace;
            BlockPos blockPos = blockRayTrace.getBlockPos().relative(blockRayTrace.getDirection());

            if (level.isEmptyBlock(blockPos) || level.getBlockState(blockPos).canBeReplaced()) {
                // Place blood light block directly when looking at a surface
                BlockState lightState = BMBlocks.BLOOD_LIGHT.get().defaultBlockState()
                        .setValue(BloodLightBlock.LIFESPAN, lifespan);

                if (!BlockProtectionHelper.tryPlaceBlock(level, blockPos, lightState, player)) {
                    return false;
                }
                return true;
            }
        } else {
            // Throw blood light projectile when not looking at a block
            if (!level.isClientSide) {
                EntityBloodLight projectile = new EntityBloodLight(level, player);
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.75F, 1.0F);
                level.addFreshEntity(projectile);
            }
            return true;
        }

        return false;
    }
}
