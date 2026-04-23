package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.function.Supplier;

public record VoidSigilEffect() implements SigilEffect {
    public static final MapCodec<VoidSigilEffect> CODEC = MapCodec.unit(VoidSigilEffect::new);

    public static final Supplier<MapCodec<VoidSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("void", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide()) {
            return false;
        }

        HitResult rayTrace = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (rayTrace == null || rayTrace.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        BlockHitResult blockRayTrace = (BlockHitResult) rayTrace;
        BlockPos blockPos = blockRayTrace.getBlockPos();
        Direction sideHit = blockRayTrace.getDirection();

        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(blockPos, sideHit, stack)) {
            return false;
        }

        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.getBlock() instanceof BucketPickup bucketPickup) {
            bucketPickup.pickupBlock(player, level, blockPos, blockState);
            return true;
        }

        ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, blockPos, null);
        if (handler != null) {
            for (int tank = 0; tank < handler.size(); tank++) {
                FluidResource r = handler.getResource(tank);
                if (r.isEmpty()) continue;
                try (Transaction tx = Transaction.openRoot()) {
                    if (handler.extract(tank, r, 1000, tx) > 0) {
                        tx.commit();
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
