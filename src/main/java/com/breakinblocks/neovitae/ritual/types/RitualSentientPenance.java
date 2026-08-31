// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeInput;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RitualSentientPenance extends Ritual {

    public static final String PENANCE_RANGE = "penanceRange";

    public RitualSentientPenance() {
        super("penance", 1, 20000, "ritual." + NeoVitae.MODID + ".penance");
        addBlockRange(PENANCE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-2, 1, -2), 5, 2, 5));
        setMaximumVolumeAndDistanceOfRange(PENANCE_RANGE, 50, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        AABB checkArea = new AABB(ctx.masterPos()).inflate(1, 2, 1);
        List<Player> players = ctx.level().getEntitiesOfClass(Player.class, checkArea);

        Player wearer = null;
        for (Player player : players) {
            ItemStack chestpiece = SentientHelper.getChest(player);
            if (!chestpiece.isEmpty() && chestpiece.is(NVTags.Items.SENTIENT_SET)) {
                wearer = player;
                break;
            }
        }
        if (wearer == null) return;

        List<ItemEntity> items = RitualHelper.getEntitiesInRange(ctx, this, PENANCE_RANGE, ItemEntity.class);
        if (items.isEmpty()) return;

        Registry<SentientUpgrade> registry = ctx.level().registryAccess().registryOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES);

        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            Optional<RecipeHolder<SentientDowngradeRecipe>> recipe = ctx.level().getRecipeManager()
                    .getRecipeFor(NVRecipes.SENTIENT_DOWNGRADE_TYPE.get(), new SentientDowngradeInput(stack), ctx.level());
            if (recipe.isEmpty()) continue;

            ResourceKey<SentientUpgrade> upgradeKey = ResourceKey.create(NVRegistries.Keys.SENTIENT_UPGRADES,
                    recipe.get().value().getSentientUpgradeId());
            Optional<Holder.Reference<SentientUpgrade>> holder = registry.getHolder(upgradeKey);
            if (holder.isEmpty()) continue;

            ItemStack chest = SentientHelper.getChest(wearer);
            float currentExp = chest.getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY)
                    .upgrades().getOrDefault(holder.get(), 0f);
            int nextLevelExp = SentientHelper.nextLevelExp(holder.get(), currentExp);
            if (nextLevelExp == 0) continue;

            float consumed = SentientHelper.applyExp(wearer, holder.get(), nextLevelExp - currentExp, true);
            if (consumed <= 0) continue;

            ItemStack reduced = stack.copy();
            reduced.shrink(1);
            if (reduced.isEmpty()) {
                itemEntity.remove(RemovalReason.KILLED);
            } else {
                itemEntity.setItem(reduced);
            }

            StreamPresets.voidTendril(wearer, ctx.masterPos()).build()
                    .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);

            ctx.syphon(getRefreshCost());
            return;
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 3, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 3, 0, EnumRuneType.TENEBRAE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSentientPenance();
    }
}
