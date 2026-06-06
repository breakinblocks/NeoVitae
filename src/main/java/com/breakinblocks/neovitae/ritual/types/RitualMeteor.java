// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipeHelper;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
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
        super("meteor", 1, 250000, "ritual." + NeoVitae.MODID + ".meteor");
        addBlockRange(CHECK_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(CHECK_RANGE, 27, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, 0);
        if (ctx == null) return;

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

                if (ctx.currentEV() < syphonAmount) {
                    return;
                }

                if (syphonAmount > 0) {
                    ctx.syphon(syphonAmount);
                }

                int maxRadius = 0;
                for (MeteorLayer layer : recipe.getLayerList()) {
                    if (layer.getLayerRadius() > maxRadius) maxRadius = layer.getLayerRadius();
                }
                int targetY = ctx.masterPos().getY() + 1 + maxRadius;

                EntityMeteor meteor = new EntityMeteor(ctx.level(),
                        ctx.masterPos().getX() + 0.5,
                        ctx.level().getMaxY() + 10,
                        ctx.masterPos().getZ() + 0.5);
                meteor.setDeltaMovement(0, -0.1, 0);
                meteor.setContainedStack(stack.split(1));
                meteor.setTargetY(targetY);
                ctx.level().addFreshEntity(meteor);

                StreamPresets
                        .demonTether(entityItem, ctx.masterPos()).build()
                        .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);

                if (stack.isEmpty()) {
                    entityItem.remove(RemovalReason.KILLED);
                }

                return;
            }
        }
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
