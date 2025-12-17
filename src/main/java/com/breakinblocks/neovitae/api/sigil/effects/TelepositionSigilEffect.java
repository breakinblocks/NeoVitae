package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.blockentity.TeleposerTile;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.Set;
import java.util.function.Supplier;

/**
 * Sigil effect that teleports the player to a bound teleposer location.
 * The teleposer location must be set by right-clicking on a teleposer block.
 */
public record TelepositionSigilEffect() implements SigilEffect {

    public static final MapCodec<TelepositionSigilEffect> CODEC = MapCodec.unit(TelepositionSigilEffect::new);

    public static final Supplier<MapCodec<TelepositionSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("teleposition", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return false;
        }

        BlockPos boundPos = stack.get(BMDataComponents.TELEPOSER_POS.get());
        String dimensionId = stack.get(BMDataComponents.TELEPOSER_DIMENSION.get());

        if (boundPos == null || dimensionId == null) {
            player.sendSystemMessage(Component.translatable("tooltip.neovitae.sigil.teleposition.unbound"));
            return false;
        }

        MinecraftServer server = level.getServer();
        if (server == null) {
            return false;
        }

        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimensionId));
        ServerLevel targetLevel = server.getLevel(dimensionKey);
        if (targetLevel == null) {
            player.sendSystemMessage(Component.translatable("tooltip.neovitae.sigil.teleposition.invalid_dimension"));
            return false;
        }

        BlockEntity tile = targetLevel.getBlockEntity(boundPos);

        if (!(tile instanceof TeleposerTile)) {
            player.sendSystemMessage(Component.translatable("tooltip.neovitae.sigil.teleposition.no_teleposer"));
            return false;
        }

        // Teleport the player to the teleposer location
        BlockPos teleportPos = boundPos.above();
        double x = teleportPos.getX() + 0.5;
        double y = teleportPos.getY();
        double z = teleportPos.getZ() + 0.5;

        if (level.dimension() == dimensionKey) {
            player.teleportTo(x, y, z);
        } else {
            player.teleportTo(targetLevel, x, y, z, Set.of(), player.getYRot(), player.getXRot());
        }

        return true;
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        if (level.isClientSide) {
            return false;
        }

        BlockEntity tile = level.getBlockEntity(blockPos);
        if (tile instanceof TeleposerTile) {
            // Bind to this teleposer
            stack.set(BMDataComponents.TELEPOSER_POS.get(), blockPos);
            stack.set(BMDataComponents.TELEPOSER_DIMENSION.get(), level.dimension().location().toString());
            player.sendSystemMessage(Component.translatable("tooltip.neovitae.sigil.teleposition.bound",
                    blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            return true;
        }

        return false;
    }
}
