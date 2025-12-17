package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that pulls items toward the Master Ritual Stone.
 */
public class RitualMagnetism extends Ritual {

    public static final String MAGNET_RANGE = "magnetRange";

    public RitualMagnetism() {
        super("magnetism", 0, 500, "ritual." + NeoVitae.MODID + ".magnetism");
        addBlockRange(MAGNET_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -3, -10), 21, 7, 21));
        setMaximumVolumeAndDistanceOfRange(MAGNET_RANGE, 5000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<ItemEntity> items = RitualHelper.getEntitiesInRange(ctx, this, MAGNET_RANGE, ItemEntity.class,
                item -> !item.isRemoved());

        if (items.isEmpty()) return;

        Vec3 center = Vec3.atCenterOf(ctx.masterPos()).add(0, 1, 0);
        int itemsMoved = 0;

        for (ItemEntity item : items) {
            Vec3 itemPos = item.position();
            double distance = itemPos.distanceTo(center);

            if (distance > 1.0) {
                Vec3 direction = center.subtract(itemPos).normalize().scale(0.3);
                item.setDeltaMovement(item.getDeltaMovement().add(direction));
                itemsMoved++;
            }
        }

        ctx.syphon(getRefreshCost() * itemsMoved);
    }

    @Override
    public int getRefreshTime() {
        return 5;
    }

    @Override
    public int getRefreshCost() {
        return 2;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        addParallelRunes(components, 3, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualMagnetism();
    }
}
