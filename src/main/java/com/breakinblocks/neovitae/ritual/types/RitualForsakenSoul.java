package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that generates Spiritus from nearby mob deaths.
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

        int spiritusGenerated = 0;

        for (LivingEntity entity : entities) {
            double spiritusAmount = getSpiritusForEntity(entity);
            if (spiritusAmount > 0) {
                ItemStack spiritusStack = new ItemStack(NVItems.RAW_SPIRITUS.get());
                spiritusStack.set(NVDataComponents.SPIRITUS_AMOUNT, spiritusAmount);
                ItemEntity spiritusEntity = new ItemEntity(ctx.level(),
                        entity.getX(), entity.getY() + 0.5, entity.getZ(), spiritusStack);
                ctx.level().addFreshEntity(spiritusEntity);
                spiritusGenerated++;
                RitualHelper.chanceStream(ctx.level(), 10, () ->
                        StreamPresets.soulSiphon(entity, ctx.masterPos()).build()
                                .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128));
            }
        }

        ctx.syphon(getRefreshCost() * spiritusGenerated);
    }

    private double getSpiritusForEntity(LivingEntity entity) {
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
