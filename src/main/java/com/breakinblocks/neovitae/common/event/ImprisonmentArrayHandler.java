package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectImprisonment;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class ImprisonmentArrayHandler {

    private static final Set<GlobalPos> ARRAYS = ConcurrentHashMap.newKeySet();
    private static final int RADIUS = 5;

    private ImprisonmentArrayHandler() {}

    public static void register(Level level, BlockPos pos) {
        ARRAYS.add(GlobalPos.of(level.dimension(), pos.immutable()));
    }

    public static void unregister(Level level, BlockPos pos) {
        ARRAYS.remove(GlobalPos.of(level.dimension(), pos.immutable()));
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (ARRAYS.isEmpty()) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (mob.getType().getTags().anyMatch(t -> t.equals(NVTags.Entities.DENY_IMPRISONMENT))) return;

        Level level = mob.level();
        if (level.isClientSide()) return;

        ResourceKey<Level> dimension = level.dimension();
        BlockPos deathPos = mob.blockPosition();
        EntityType<?> type = mob.getType();

        Iterator<GlobalPos> it = ARRAYS.iterator();
        while (it.hasNext()) {
            GlobalPos gp = it.next();
            if (!gp.dimension().equals(dimension)) continue;

            BlockPos arrayPos = gp.pos();
            if (!(level.getBlockEntity(arrayPos) instanceof AlchemyArrayBlockEntity array)
                    || !(array.arrayEffect instanceof AlchemyArrayEffectImprisonment)) {
                it.remove();
                continue;
            }

            if (Math.abs(arrayPos.getX() - deathPos.getX()) > RADIUS
                    || Math.abs(arrayPos.getY() - deathPos.getY()) > RADIUS
                    || Math.abs(arrayPos.getZ() - deathPos.getZ()) > RADIUS) {
                continue;
            }

            if (!(level.getBlockEntity(arrayPos.below()) instanceof SpawnerBlockEntity spawner)) {
                continue;
            }

            spawner.setEntityId(type, level.getRandom());

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000),
                        arrayPos.getX() + 0.5, arrayPos.getY() + 0.3, arrayPos.getZ() + 0.5, 12, 0.3, 0.1, 0.3, 0.05);
                serverLevel.playSound(null, arrayPos, NVSounds.ALCHEMY_ARRAY_CRAFT.get(), SoundSource.BLOCKS, 0.8f, 0.6f);
            }

            array.doDropIngredients(false);
            level.setBlockAndUpdate(arrayPos, Blocks.AIR.defaultBlockState());
            it.remove();
        }
    }
}
