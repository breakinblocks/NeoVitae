package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.common.entity.mob.*;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;

import java.util.List;
import java.util.function.Supplier;

public record NecromancySigilEffect() implements SigilEffect {

    public static final MapCodec<NecromancySigilEffect> CODEC = MapCodec.unit(NecromancySigilEffect::new);

    public static final Supplier<MapCodec<NecromancySigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("necromancy", () -> CODEC);

    private static final double RANGE = 32.0;
    private static final double SUMMON_SEARCH_RADIUS = 64.0;

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return false;

        int limit = NeoVitae.SERVER_CONFIG.NECROMANCY_MAX_SUMMONS.get();
        if (limit > 0 && countOwnedSummons(serverLevel, player) >= limit) {
            player.displayClientMessage(
                    Component.translatable("message.neovitae.sigil.necromancy.at_limit", limit), true);
            return false;
        }

        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(RANGE));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        Vec3 spawnPos = hit.getType() == HitResult.Type.BLOCK
                ? Vec3.atBottomCenterOf(hit.getBlockPos().relative(hit.getDirection()))
                : end;

        Mob summon = createRandomSummon(serverLevel, player);
        summon.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), 0);
        summon.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(summon.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        serverLevel.addFreshEntity(summon);

        return true;
    }

    @Override
    public boolean useOnEntity(Level level, Player player, ItemStack stack, Entity target) {
        if (level.isClientSide) return false;
        if (!player.isShiftKeyDown()) return false;

        if (target instanceof INecromancySummon summon && player.getUUID().equals(summon.getOwnerUUID())) {
            target.discard();
        }
        return false;
    }

    private static int countOwnedSummons(ServerLevel level, Player player) {
        AABB search = player.getBoundingBox().inflate(SUMMON_SEARCH_RADIUS);
        return level.getEntitiesOfClass(Mob.class, search,
                mob -> mob instanceof INecromancySummon summon
                        && mob.isAlive()
                        && player.getUUID().equals(summon.getOwnerUUID())).size();
    }

    private static Mob createRandomSummon(ServerLevel level, Player owner) {
        List<EntityType<? extends Mob>> types = List.of(
                NVEntities.NECROMANCY_SUMMON.get(),
                NVEntities.NECROMANCY_SUMMON_HUSK.get(),
                NVEntities.NECROMANCY_SUMMON_SKELETON.get(),
                NVEntities.NECROMANCY_SUMMON_STRAY.get()
        );

        EntityType<? extends Mob> chosen = types.get(level.random.nextInt(types.size()));
        Mob mob = chosen.create(level);

        if (mob instanceof INecromancySummon summon) {
            summon.setOwner(owner);
        }

        return mob;
    }
}
