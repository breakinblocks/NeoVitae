package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that generates Demon Will from nearby mob deaths.
 * The more mobs die in the area, the more will is generated.
 */
public class RitualForsakenSoul extends Ritual {

    public static final String FORSAKEN_RANGE = "forsakenRange";

    public RitualForsakenSoul() {
        super("forsaken_soul", 1, 40000, "ritual." + NeoVitae.MODID + ".forsaken_soul");
        addBlockRange(FORSAKEN_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -10, -10), 21, 21, 21));
        setMaximumVolumeAndDistanceOfRange(FORSAKEN_RANGE, 10000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, FORSAKEN_RANGE,
                LivingEntity.class, e -> !(e instanceof Player) && e.isDeadOrDying());

        if (entities.isEmpty()) return;

        int willGenerated = 0;

        for (LivingEntity entity : entities) {
            // Calculate will based on entity type
            double willAmount = getWillForEntity(entity);
            if (willAmount > 0) {
                // Spawn will item with appropriate will amount
                ItemStack willStack = new ItemStack(BMItems.RAW_WILL.get());
                willStack.set(BMDataComponents.DEMON_WILL_AMOUNT, willAmount);
                ItemEntity willEntity = new ItemEntity(ctx.level(),
                        entity.getX(), entity.getY() + 0.5, entity.getZ(), willStack);
                ctx.level().addFreshEntity(willEntity);
                willGenerated++;
            }
        }

        ctx.syphon(getRefreshCost() * willGenerated);
    }

    private double getWillForEntity(LivingEntity entity) {
        EntityType<?> type = entity.getType();
        if (type == EntityType.WITHER) return 100.0;
        if (type == EntityType.ENDER_DRAGON) return 500.0;
        if (type == EntityType.ELDER_GUARDIAN) return 50.0;
        if (type == EntityType.WARDEN) return 200.0;
        if (type == EntityType.ZOMBIE || type == EntityType.SKELETON) return 5.0;
        if (type == EntityType.CREEPER) return 10.0;
        if (type == EntityType.ENDERMAN) return 15.0;
        return 3.0;
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 100;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 4, 0, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualForsakenSoul();
    }
}
