package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipeHelper;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that summons meteors based on item catalysts.
 * Searches for items within the ritual range and matches them to meteor recipes.
 * When a match is found, spawns a meteor entity that falls and generates blocks.
 */
public class RitualMeteor extends Ritual {

    public static final String CHECK_RANGE = "itemRange";

    public RitualMeteor() {
        super("meteor", 2, 250000, "ritual." + NeoVitae.MODID + ".meteor");
        addBlockRange(CHECK_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(CHECK_RANGE, 27, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, 0);
        if (ctx == null || !(ctx.level() instanceof ServerLevel)) return;

        AreaDescriptor itemRange = RitualHelper.getEffectiveRange(ctx.master(), this, CHECK_RANGE);
        List<ItemEntity> itemList = ctx.level().getEntitiesOfClass(ItemEntity.class,
                itemRange.getAABB(ctx.masterPos()));

        for (ItemEntity entityItem : itemList) {
            if (!entityItem.isAlive()) {
                continue;
            }

            ItemStack stack = entityItem.getItem();
            MeteorRecipe recipe = MeteorRecipeHelper.findRecipe(ctx.level(), stack);

            if (recipe != null) {
                int syphonAmount = recipe.getSyphon();

                if (ctx.currentEssence() < syphonAmount) {
                    return;
                }

                if (syphonAmount > 0) {
                    ctx.syphon(syphonAmount);
                }

                // Spawn meteor entity high above the ritual
                EntityMeteor meteor = new EntityMeteor(ctx.level(),
                        ctx.masterPos().getX() + 0.5,
                        ctx.level().getMaxBuildHeight() + 10,
                        ctx.masterPos().getZ() + 0.5);
                meteor.setDeltaMovement(0, -0.1, 0);
                meteor.setContainedStack(stack.split(1));
                ctx.level().addFreshEntity(meteor);

                if (stack.isEmpty()) {
                    entityItem.remove(RemovalReason.KILLED);
                }

                return;
            }
        }
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 0;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        // Large complex ritual pattern
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 4, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 4, 0, EnumRuneType.EARTH);

        // Elevated corners
        addCornerRunes(components, 4, 1, EnumRuneType.DUSK);
        addCornerRunes(components, 4, 2, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualMeteor();
    }
}
