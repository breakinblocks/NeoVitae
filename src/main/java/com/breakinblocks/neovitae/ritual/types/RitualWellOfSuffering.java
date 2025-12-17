package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.damagesource.BMDamageSources;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeHelper;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that damages nearby mobs to generate LP.
 */
public class RitualWellOfSuffering extends Ritual {

    public static final String DAMAGE_RANGE = "damageRange";

    public RitualWellOfSuffering() {
        super("well_of_suffering", 0, 50000, "ritual." + NeoVitae.MODID + ".well_of_suffering");
        addBlockRange(DAMAGE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));

        setMaximumVolumeAndDistanceOfRange(DAMAGE_RANGE, 0, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        int maxLPGenerated = 1000000; // Max LP capacity check would go here

        List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, DAMAGE_RANGE,
                LivingEntity.class, e -> !(e instanceof Player) && e.isAlive() && !e.isInvulnerable());

        int totalLP = 0;

        for (LivingEntity entity : entities) {
            float damage = 1.0F;
            float health = entity.getHealth();

            if (health > damage) {
                entity.hurt(ctx.level().damageSources().source(BMDamageSources.RITUAL), damage);

                if (entity.getHealth() < health) {
                    totalLP += EntitySacrificeHelper.calculateLP(entity, damage);
                }
            }
        }

        if (totalLP > 0) {
            ctx.syphon(getRefreshCost());
            ctx.network().add(ctx.master().ticket(totalLP), maxLPGenerated);
        }
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 2;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.DUSK);
        addOffsetRunes(components, 3, 1, 0, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualWellOfSuffering();
    }
}
