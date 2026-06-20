package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class LexVitaeVacuumHandler {

    private LexVitaeVacuumHandler() {}

    private static int colorFor(SpiritusType type) {
        return switch (type) {
            case RAW -> 0x66E6E6;
            case RUINA -> 0x55EE55;
            case NIHILUM -> 0xFF8844;
            case VINDICTA -> 0xBB55EE;
            case INVICTUS -> 0xEEEEEE;
        };
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        Entity breaker = event.getBreaker();
        if (!(breaker instanceof ServerPlayer player)) return;
        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof LexVitaeItem)) return;
        if (!LexVitaeItem.isActive(mainHand)) return;
        if (!LexVitaeItem.isBeaming(player)) return;

        ServerLevel level = event.getLevel();
        BlockPos pos = event.getPos();
        Vec3 source = Vec3.atCenterOf(pos);
        Vec3 dest = player.position().add(0, player.getBbHeight() * 0.5, 0);

        List<ItemEntity> drops = event.getDrops();
        boolean anyVacuumed = false;
        Iterator<ItemEntity> it = drops.iterator();
        while (it.hasNext()) {
            ItemEntity ie = it.next();
            ItemStack stack = ie.getItem();
            if (stack.isEmpty()) { it.remove(); continue; }
            int before = stack.getCount();
            player.getInventory().add(stack);
            if (stack.isEmpty()) {
                it.remove();
                anyVacuumed = true;
            } else if (stack.getCount() < before) {
                ie.setPos(player.getX(), player.getY(), player.getZ());
                ie.setItem(stack);
                anyVacuumed = true;
            } else {
                ie.setPos(player.getX(), player.getY(), player.getZ());
            }
        }

        int xp = event.getDroppedExperience();
        if (xp > 0) {
            player.giveExperiencePoints(xp);
            event.setDroppedExperience(0);
        }

        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                0.4F, 1.4F + level.getRandom().nextFloat() * 0.3F);
        if (anyVacuumed) {
            if (!LexVitaeAoeHandler.isAoeBreaking()) {
                SpiritusType type = mainHand.getOrDefault(NVDataComponents.SPIRITUS_TYPE.get(), SpiritusType.RAW);
                spawnVacuumTrail(level, source, dest, colorFor(type));
            }
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                    0.18F, 1.6F + level.getRandom().nextFloat() * 0.2F);
        }
    }

    private static void spawnVacuumTrail(ServerLevel level, Vec3 from, Vec3 to, int color) {
        Vec3 path = to.subtract(from);
        double dist = path.length();
        if (dist < 0.5) return;
        Vec3 dir = path.scale(1.0 / dist);
        int steps = Math.max(3, (int) (dist * 1.5));
        var options = new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color, true);
        for (int i = 0; i <= steps; i++) {
            double f = (double) i / steps;
            Vec3 p = from.add(dir.scale(f * dist));
            level.sendParticles(options, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
    }
}
