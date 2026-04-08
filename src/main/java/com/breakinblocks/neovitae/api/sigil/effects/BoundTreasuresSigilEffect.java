package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.function.Supplier;

public record BoundTreasuresSigilEffect() implements SigilEffect {

    public static final MapCodec<BoundTreasuresSigilEffect> CODEC = MapCodec.unit(BoundTreasuresSigilEffect::new);

    public static final Supplier<MapCodec<BoundTreasuresSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("bound_treasures", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        if (level.isClientSide) return false;
        if (!player.isShiftKeyDown()) return false;

        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof MenuProvider) {
            stack.set(NVDataComponents.TELEPOSER_POS.get(), blockPos);
            stack.set(NVDataComponents.TELEPOSER_DIMENSION.get(), level.dimension().location().toString());
            player.displayClientMessage(Component.translatable("tooltip.neovitae.bound_treasures.linked"), true);
            return false;
        }
        return false;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) return false;

        BlockPos chestPos = stack.get(NVDataComponents.TELEPOSER_POS.get());
        String dimStr = stack.get(NVDataComponents.TELEPOSER_DIMENSION.get());
        if (chestPos == null || dimStr == null) {
            player.displayClientMessage(Component.translatable("tooltip.neovitae.bound_treasures.not_linked"), true);
            return false;
        }

        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimStr));
        ServerLevel targetLevel = serverPlayer.server.getLevel(dimKey);
        if (targetLevel == null) return false;

        if (!targetLevel.isLoaded(chestPos)) {
            player.displayClientMessage(Component.translatable("tooltip.neovitae.bound_treasures.unloaded"), true);
            return false;
        }

        BlockEntity be = targetLevel.getBlockEntity(chestPos);
        if (be instanceof MenuProvider menuProvider) {
            serverPlayer.openMenu(menuProvider);
            return true;
        }

        player.displayClientMessage(Component.translatable("tooltip.neovitae.bound_treasures.missing"), true);
        return false;
    }
}
