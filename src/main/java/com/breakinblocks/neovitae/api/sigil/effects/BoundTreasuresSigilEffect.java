package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import com.breakinblocks.neovitae.compat.ae2.AE2Compat;
import com.breakinblocks.neovitae.compat.occultism.OccultismCompat;
import com.breakinblocks.neovitae.compat.refinedstorage.RefinedStorageCompat;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.world.BoundTreasureLeases;
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
        if (level.isClientSide()) return false;
        if (!player.isShiftKeyDown()) return false;

        if (!BoundTreasureLeases.isContainer(level, blockPos)) {
            player.sendOverlayMessage(Component.translatable("tooltip.neovitae.bound_treasures.not_a_container"));
            return false;
        }

        stack.set(NVDataComponents.TELEPOSER_POS.get(), blockPos);
        stack.set(NVDataComponents.TELEPOSER_DIMENSION.get(), level.dimension().identifier().toString());

        Direction terminalSide = AE2Compat.findTerminalSide(level, blockPos, hitVec);
        if (terminalSide != null) {
            stack.set(NVDataComponents.BOUND_TREASURE_SIDE.get(), terminalSide);
        } else {
            stack.remove(NVDataComponents.BOUND_TREASURE_SIDE.get());
        }

        player.sendOverlayMessage(Component.translatable("tooltip.neovitae.bound_treasures.linked"));
        return false;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return false;

        BlockPos chestPos = stack.get(NVDataComponents.TELEPOSER_POS.get());
        String dimStr = stack.get(NVDataComponents.TELEPOSER_DIMENSION.get());
        if (chestPos == null || dimStr == null) {
            player.sendOverlayMessage(Component.translatable("tooltip.neovitae.bound_treasures.not_linked"));
            return false;
        }

        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, Identifier.parse(dimStr));
        ServerLevel targetLevel = serverPlayer.level().getServer().getLevel(dimKey);
        if (targetLevel == null) return false;

        if (!targetLevel.isLoaded(chestPos)) {
            player.sendOverlayMessage(Component.translatable("tooltip.neovitae.bound_treasures.unloaded"));
            return false;
        }

        if (OccultismCompat.openStorageAccess(serverPlayer, targetLevel, chestPos)) {
            BoundTreasureLeases.open(serverPlayer, targetLevel, chestPos);
            return true;
        }

        if (RefinedStorageCompat.openExtendedMenu(serverPlayer, targetLevel, chestPos)) {
            BoundTreasureLeases.open(serverPlayer, targetLevel, chestPos);
            return true;
        }

        MenuProvider menuProvider = BoundTreasureLeases.findMenuProvider(targetLevel, chestPos);
        if (menuProvider != null) {
            serverPlayer.openMenu(menuProvider, chestPos);
            BoundTreasureLeases.open(serverPlayer, targetLevel, chestPos);
            return true;
        }

        Direction boundSide = stack.get(NVDataComponents.BOUND_TREASURE_SIDE.get());
        if (AE2Compat.openTerminal(serverPlayer, targetLevel, chestPos, boundSide)) {
            BoundTreasureLeases.open(serverPlayer, targetLevel, chestPos);
            return true;
        }

        if (BoundTreasureLeases.isContainer(targetLevel, chestPos)) {
            BlockState state = targetLevel.getBlockState(chestPos);
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(chestPos), Direction.UP, chestPos, false);
            InteractionResult withItem = state.useItemOn(ItemStack.EMPTY, targetLevel, serverPlayer,
                    InteractionHand.MAIN_HAND, hit);
            if (withItem instanceof InteractionResult.TryEmptyHandInteraction) {
                state.useWithoutItem(targetLevel, serverPlayer, hit);
            }
            if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
                BoundTreasureLeases.open(serverPlayer, targetLevel, chestPos);
                return true;
            }
        }

        player.sendOverlayMessage(Component.translatable("tooltip.neovitae.bound_treasures.missing"));
        return false;
    }
}
