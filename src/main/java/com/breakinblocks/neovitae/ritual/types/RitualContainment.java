package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that prevents mobs from leaving a defined area.
 * Entities that try to leave are pushed back to the center.
 */
public class RitualContainment extends Ritual {

    public static final String CONTAIN_RANGE = "containRange";

    public RitualContainment() {
        super("containment", 0, 2000, "ritual." + NeoVitae.MODID + ".containment");
        addBlockRange(CONTAIN_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 0, -5), 11, 5, 11));
        setMaximumVolumeAndDistanceOfRange(CONTAIN_RANGE, 2000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        AABB aabb = RitualHelper.getRangeAABB(ctx.master(), this, CONTAIN_RANGE, ctx.masterPos());
        if (aabb == null) return;

        AABB expandedAABB = aabb.inflate(2);

        List<LivingEntity> entities = ctx.level().getEntitiesOfClass(LivingEntity.class, expandedAABB,
                e -> !(e instanceof Player));

        int entitiesContained = 0;
        Vec3 center = Vec3.atCenterOf(ctx.masterPos());

        for (LivingEntity entity : entities) {
            Vec3 entityPos = entity.position();

            if (!aabb.contains(entityPos) && expandedAABB.contains(entityPos)) {
                Vec3 direction = center.subtract(entityPos).normalize().scale(0.5);
                entity.setDeltaMovement(direction);
                entitiesContained++;
                RitualHelper.chanceStream(ctx.level(), 20, () ->
                        StreamPresets.demonTether(ctx.masterPos(), entity.blockPosition()).build()
                                .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 64));
            }
        }

        if (entitiesContained > 0) {
            ctx.syphon(Math.min(getRefreshCost() * entitiesContained, ctx.currentEV()));
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        addRune(components, 3, 0, 0, EnumRuneType.EARTH);
        addRune(components, -3, 0, 0, EnumRuneType.EARTH);
        addRune(components, 0, 0, 3, EnumRuneType.EARTH);
        addRune(components, 0, 0, -3, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualContainment();
    }
}
