package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that pushes mobs away from the ritual.
 * Opposite of containment - entities are pushed outward.
 */
public class RitualExpulsion extends Ritual {

    public static final String EXPEL_RANGE = "expelRange";

    public RitualExpulsion() {
        super("expulsion", 0, 2000, "ritual." + NeoVitae.MODID + ".expulsion");
        addBlockRange(EXPEL_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -5, -10), 21, 11, 21));
        setMaximumVolumeAndDistanceOfRange(EXPEL_RANGE, 5000, 25, 25);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, EXPEL_RANGE,
                LivingEntity.class, e -> !(e instanceof Player));

        if (entities.isEmpty()) return;

        Vec3 center = Vec3.atCenterOf(ctx.masterPos());
        int entitiesExpelled = 0;

        for (LivingEntity entity : entities) {
            Vec3 entityPos = entity.position();
            Vec3 direction = entityPos.subtract(center);

            // Normalize and scale for push force
            if (direction.lengthSqr() > 0.01) {
                direction = direction.normalize().scale(0.5);
                entity.setDeltaMovement(entity.getDeltaMovement().add(direction));
                entitiesExpelled++;
            }
        }

        if (entitiesExpelled > 0) {
            ctx.syphon(Math.min(getRefreshCost() * entitiesExpelled, ctx.currentEssence()));
        }
    }

    @Override
    public int getRefreshTime() {
        return 5;
    }

    @Override
    public int getRefreshCost() {
        return 5;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addRune(components, 3, 0, 0, EnumRuneType.AIR);
        addRune(components, -3, 0, 0, EnumRuneType.AIR);
        addRune(components, 0, 0, 3, EnumRuneType.AIR);
        addRune(components, 0, 0, -3, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualExpulsion();
    }
}
