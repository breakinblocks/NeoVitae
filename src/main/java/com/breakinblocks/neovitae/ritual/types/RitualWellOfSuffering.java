package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeHelper;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that damages nearby mobs to generate LP.
 * Supports entity blacklist tag, baby entity modifier, and altar discovery.
 */
public class RitualWellOfSuffering extends Ritual {

    public static final String DAMAGE_RANGE = "damageRange";
    public static final String ALTAR_RANGE = "altarRange";

    /** Cached altar offset (relative to master pos). Persisted in NBT. */
    private BlockPos altarOffsetPos = null;

    public RitualWellOfSuffering() {
        super("well_of_suffering", 0, 50000, "ritual." + NeoVitae.MODID + ".well_of_suffering");
        addBlockRange(DAMAGE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        addBlockRange(ALTAR_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -10, -5), 11, 20, 11));

        setMaximumVolumeAndDistanceOfRange(DAMAGE_RANGE, 0, 10, 10);
        setMaximumVolumeAndDistanceOfRange(ALTAR_RANGE, 0, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        int maxEVGenerated = 1000000; // Max LP capacity check would go here

        List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, DAMAGE_RANGE,
                LivingEntity.class, e -> !(e instanceof Player) && e.isAlive() && !e.isInvulnerable()
                        && !e.getType().getTags().anyMatch(t -> t.equals(NVTags.Entities.WELL_OF_SUFFERING_BLACKLIST)));

        // Find the altar for direct LP feeding
        AraVitaeTile altar = findAltar(ctx);

        int totalEV = 0;
        BlockPos masterPos = ctx.masterPos();

        for (LivingEntity entity : entities) {
            float damage = 1.0F;
            float health = entity.getHealth();

            if (health > damage) {
                entity.hurt(ctx.level().damageSources().source(NVDamageSources.RITUAL), damage);

                if (entity.getHealth() < health) {
                    int lp = EntitySacrificeHelper.calculateLP(entity, damage);

                    if (entity.isBaby()) {
                        lp = (int) (lp * 0.5);
                    }

                    totalEV += lp;

                    RitualHelper.chanceStream(ctx.level(), 4, () ->
                            StreamPresets.bloodTendril(entity, masterPos).build()
                                    .sendToNearby(ctx.serverLevel(), masterPos, 64));
                }
            }
        }

        if (totalEV > 0) {
            ctx.syphon(getRefreshCost());
            ctx.network().add(ctx.master().ticket(totalEV), maxEVGenerated);

            // Also feed LP directly to altar if found
            if (altar != null) {
                altar.addSacrificeEV(totalEV, true);
            }
        }
    }

    private AraVitaeTile findAltar(RitualContext ctx) {
        RitualHelper.AltarSearchResult result = RitualHelper.findAltar(ctx, this, ALTAR_RANGE, altarOffsetPos);
        altarOffsetPos = result.offset();
        return result.altar();
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
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        altarOffsetPos = RitualHelper.readAltarOffset(tag);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        RitualHelper.writeAltarOffset(tag, altarOffsetPos);
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
