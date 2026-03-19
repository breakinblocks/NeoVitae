package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.common.blockentity.BloodAltarTile;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeHelper;
import com.breakinblocks.neovitae.util.AltarUtil;

/**
 * Dagger of Sacrifice - kills mobs near a Blood Altar to fill it with LP.
 * The amount of LP gained is based on the mob's max health and a configurable ratio.
 */
public class DaggerOfSacrificeItem extends Item {

    public DaggerOfSacrificeItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) {
            return false;
        }

        if (player instanceof FakePlayer) {
            return false;
        }

        if (player.level().isClientSide()) {
            return false;
        }

        // Cannot sacrifice players
        if (target instanceof Player) {
            return false;
        }

        // Target must be alive
        if (target.getHealth() < 0.5F) {
            return false;
        }

        // Calculate LP from the mob's current health
        int sacrificeValue = getSacrificeValue(target);
        if (sacrificeValue <= 0) {
            return false;
        }

        int lifeEssence = (int) (sacrificeValue * target.getHealth());

        // Baby mobs give half LP
        if (target.isBaby()) {
            lifeEssence = (int) (lifeEssence * 0.5F);
        }

        // Find a nearby altar (search radius matches self-sacrifice dagger)
        BlockPos altarPos = findAltar(target.level(), target.blockPosition());
        if (altarPos == null) {
            return false;
        }

        BlockEntity be = target.level().getBlockEntity(altarPos);
        if (!(be instanceof BloodAltarTile altar)) {
            return false;
        }

        // Fill the altar with LP (true = mob sacrifice, uses sacrifice rune modifier)
        altar.sacrificialDaggerCall(lifeEssence, true);

        // Kill the mob
        target.hurt(target.level().damageSources().source(NVDamageSources.SACRIFICE, player), Float.MAX_VALUE);

        // Effects
        Level level = target.level();
        double posX = target.getX();
        double posY = target.getY();
        double posZ = target.getZ();

        level.playSound(null, target.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
        for (int i = 0; i < 8; i++) {
            level.addParticle(DustParticleOptions.REDSTONE,
                    posX + level.random.nextDouble() - level.random.nextDouble(),
                    posY + level.random.nextDouble() - level.random.nextDouble(),
                    posZ + level.random.nextDouble() - level.random.nextDouble(),
                    0, 0, 0);
        }

        return true;
    }

    /**
     * Gets the LP-per-health-point value for an entity from the data map.
     */
    private int getSacrificeValue(LivingEntity entity) {
        return EntitySacrificeHelper.getLpPerDamage(entity);
    }

    /**
     * Finds a Blood Altar near the given position.
     * Searches a 5x5x4 area (±2 horizontal, -2 to +1 vertical) matching original behavior.
     */
    private BlockPos findAltar(Level level, BlockPos pos) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos testPos = pos.offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(testPos);
                    if (be instanceof BloodAltarTile) {
                        return testPos;
                    }
                }
            }
        }
        return null;
    }
}
