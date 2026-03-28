package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.entity.projectile.EntityBloodLight;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;

import java.util.function.Supplier;

public record BloodLightSigilEffect(int defaultBrightness) implements SigilEffect {

    public static final MapCodec<BloodLightSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("default_brightness", BloodLightBlock.DEFAULT_BRIGHTNESS).forGetter(BloodLightSigilEffect::defaultBrightness)
            ).apply(instance, BloodLightSigilEffect::new)
    );

    public static final Supplier<MapCodec<BloodLightSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("blood_light", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    private boolean tryPlace(Level level, Player player, ItemStack stack, BlockPos placePos) {
        if (level.getBlockState(placePos).getBlock() instanceof BloodLightBlock) return false;
        if (!level.isEmptyBlock(placePos) && !level.getBlockState(placePos).canBeReplaced()) return false;

        int brightness = BloodLightHelper.getBrightness(stack, defaultBrightness);
        DyeColor color = BloodLightHelper.getColor(stack);

        if (!level.isClientSide) {
            BlockState lightState = BloodLightHelper.createBlockState(brightness);
            if (BlockProtectionHelper.tryPlaceBlock(level, placePos, lightState, player)) {
                BloodLightHelper.setBlockEntityColor(level, placePos, color);
                BloodLightHelper.playSound(level, placePos);
            }
        }
        return true;
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        return tryPlace(level, player, stack, blockPos.relative(side));
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        HitResult rayTrace = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (rayTrace != null && rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockRayTrace = (BlockHitResult) rayTrace;
            BlockPos hitPos = blockRayTrace.getBlockPos();
            return tryPlace(level, player, stack, hitPos.relative(blockRayTrace.getDirection()));
        }

        if (!level.isClientSide) {
            int brightness = BloodLightHelper.getBrightness(stack, defaultBrightness);
            DyeColor color = BloodLightHelper.getColor(stack);
            EntityBloodLight projectile = new EntityBloodLight(level, player, brightness, color);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.75F, 1.0F);
            level.addFreshEntity(projectile);
        }
        return true;
    }
}
