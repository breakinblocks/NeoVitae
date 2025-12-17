package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.LivingStats;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that removes upgrades from living armor, returning upgrade tomes.
 */
public class RitualDowngrade extends Ritual {

    public static final String DOWNGRADE_RANGE = "downgradeRange";

    public RitualDowngrade() {
        super("downgrade", 1, 20000, "ritual." + NeoVitae.MODID + ".downgrade");
        addBlockRange(DOWNGRADE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-2, 1, -2), 5, 2, 5));
        setMaximumVolumeAndDistanceOfRange(DOWNGRADE_RANGE, 50, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<ItemEntity> items = RitualHelper.getEntitiesInRange(ctx, this, DOWNGRADE_RANGE, ItemEntity.class);

        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            LivingStats stats = stack.get(BMDataComponents.UPGRADES.get());

            if (stats != null && !stats.upgrades().isEmpty()) {
                // Remove all upgrades and spawn upgrade tomes
                stats.upgrades().forEach((upgradeHolder, exp) -> {
                    ItemStack tome = new ItemStack(BMItems.UPGRADE_TOME.get());
                    tome.set(BMDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(upgradeHolder, exp));
                    BlockPos spawnPos = ctx.masterPos().above();
                    ItemEntity droppedTome = new ItemEntity(ctx.level(), spawnPos.getX() + 0.5,
                            spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, tome);
                    ctx.level().addFreshEntity(droppedTome);
                });

                // Clear upgrades from the item
                stack.remove(BMDataComponents.UPGRADES.get());
                ctx.syphon(getRefreshCost());
                break; // Only process one item per ritual tick
            }
        }
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 10000;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 2, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 4, 0, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDowngrade();
    }
}
