// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
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
            SentientStats stats = stack.get(NVDataComponents.UPGRADES.get());

            if (stats != null && !stats.upgrades().isEmpty()) {
                Object2FloatOpenHashMap<Holder<SentientUpgrade>> retained = new Object2FloatOpenHashMap<>();
                boolean extractedAny = false;

                for (Object2FloatMap.Entry<Holder<SentientUpgrade>> entry : stats.object2FloatEntrySet()) {
                    Holder<SentientUpgrade> upgradeHolder = entry.getKey();
                    float exp = entry.getFloatValue();
                    if (upgradeHolder.is(NVTags.Sentient.TRAINERS)) {
                        retained.put(upgradeHolder, exp);
                        continue;
                    }
                    ItemStack tome = new ItemStack(NVItems.UPGRADE_TOME.get());
                    tome.set(NVDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(upgradeHolder, exp));
                    BlockPos spawnPos = ctx.masterPos().above();
                    ItemEntity droppedTome = new ItemEntity(ctx.level(), spawnPos.getX() + 0.5,
                            spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, tome);
                    ctx.level().addFreshEntity(droppedTome);
                    extractedAny = true;
                }

                if (!extractedAny) {
                    continue;
                }

                stack.set(NVDataComponents.UPGRADES.get(), new SentientStats(retained));
                stack.set(NVDataComponents.CURRENT_UPGRADE_POINTS.get(), 0);
                StreamPresets
                        .voidTendril(itemEntity, ctx.masterPos()).build()
                        .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);
                ctx.syphon(getRefreshCost());
                break;
            }
        }
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
