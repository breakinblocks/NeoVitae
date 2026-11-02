package com.breakinblocks.neovitae.mixin;

import com.breakinblocks.neovitae.common.sentient.SentientEffectComponents;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private boolean neovitae$canWalkOnLava() {
        return (Object) this instanceof Player player
                && !player.isShiftKeyDown() && player.hasEffect(MobEffects.FIRE_RESISTANCE)
                && SentientHelper.hasFullSet(player)
                && SentientHelper.has(player, SentientEffectComponents.LAVA_STRIDER.get());
    }

    // This branch does not dispatch LivingBreatheEvent from the vanilla air-supply path.
    @Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void neovitae$gills(int air, CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof Player player && player.isUnderWater()
                && SentientHelper.hasFullSet(player)
                && SentientHelper.has(player, SentientEffectComponents.GILLS.get())) {
            cir.setReturnValue(Math.min(air + 4, player.getMaxAirSupply()));
        }
    }

    @Inject(method = "canStandOnFluid", at = @At("RETURN"), cancellable = true)
    private void neovitae$lavaStrider(FluidState fluid, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && fluid.is(FluidTags.LAVA) && neovitae$canWalkOnLava()) {
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "getLiquidCollisionShape", at = @At("RETURN"), cancellable = true)
    private void neovitae$lavaSurface(CallbackInfoReturnable<VoxelShape> cir) {
        if (cir.getReturnValue().isEmpty() && neovitae$canWalkOnLava()) {
            cir.setReturnValue(Shapes.box(0, 0, 0, 1, 0.5, 1));
        }
    }
}
