package com.breakinblocks.neovitae.compat.emi;

import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskEffectRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskEffectTransformRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.FlaskCombinationEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.ImperfectRitualEmiRecipe;
import com.breakinblocks.neovitae.compat.emi.recipe.RitualEmiRecipe;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class NVEmiRecipeSources {

    private NVEmiRecipeSources() {
    }

    public static List<RitualEmiRecipe> rituals() {
        List<RitualEmiRecipe> out = new ArrayList<>();
        Registry<Ritual> registry = RitualRegistry.getRitualRegistry();
        if (registry == null) return out;

        Level level = Minecraft.getInstance().level;
        for (Ritual ritual : registry) {
            ResourceLocation id = registry.getKey(ritual);
            if (id == null) continue;

            RitualStats stats = registry.wrapAsHolder(ritual).getData(NVDataMaps.RITUAL_STATS);
            if (stats != null && !stats.enabled()) continue;

            List<RitualComponent> components = RitualLayouts.get(level, ritual);
            out.add(new RitualEmiRecipe(id, ritual.getTranslationKey(), ritual.getActivationCost(),
                    ritual.getRefreshCost(), ritual.getCrystalLevel(), components));
        }
        return out;
    }

    public static List<ImperfectRitualEmiRecipe> imperfectRituals() {
        List<ImperfectRitualEmiRecipe> out = new ArrayList<>();
        Registry<ImperfectRitual> registry = RitualRegistry.getImperfectRitualRegistry();
        if (registry == null) return out;

        for (ImperfectRitual ritual : registry) {
            ResourceLocation id = registry.getKey(ritual);
            if (id == null) continue;

            Holder<ImperfectRitual> holder = registry.wrapAsHolder(ritual);
            ImperfectRitualStats stats = holder.getData(NVDataMaps.IMPERFECT_RITUAL_STATS);

            List<ItemStack> catalystBlocks = new ArrayList<>();
            int activationCost = ritual.getActivationCost();
            boolean consumesBlock = false;

            if (stats != null) {
                if (!stats.enabled()) continue;
                activationCost = stats.activationCost();
                consumesBlock = stats.consumeBlock();

                if (stats.block().isPresent()) {
                    catalystBlocks.add(displayItemFor(stats.block().get()));
                } else if (stats.blockTag().isPresent()) {
                    TagKey<Block> tag = stats.blockTag().get();
                    BuiltInRegistries.BLOCK.getTag(tag).ifPresent(holders -> {
                        for (Holder<Block> blockHolder : holders) {
                            catalystBlocks.add(displayItemFor(blockHolder.value()));
                        }
                    });
                }
            }
            if (catalystBlocks.isEmpty()) continue;

            out.add(new ImperfectRitualEmiRecipe(id, catalystBlocks, activationCost,
                    Component.translatable(ritual.getTranslationKey() + ".desc"), consumesBlock));
        }
        return out;
    }

    public static List<FlaskCombinationEmiRecipe> flaskCombinations(List<FlaskRecipe> allFlaskRecipes) {
        List<FlaskCombinationEmiRecipe> out = new ArrayList<>();

        Set<Holder<MobEffect>> allEffects = new LinkedHashSet<>();
        for (FlaskRecipe recipe : allFlaskRecipes) {
            if (recipe instanceof FlaskEffectRecipe er) {
                allEffects.add(er.getOutputEffect());
            } else if (recipe instanceof FlaskEffectTransformRecipe tr) {
                for (Pair<Holder<MobEffect>, Integer> pair : tr.getOutputEffects()) {
                    allEffects.add(pair.getFirst());
                }
            }
        }

        int index = 0;
        for (FlaskRecipe raw : allFlaskRecipes) {
            if (!(raw instanceof FlaskEffectRecipe recipe)) continue;
            for (Holder<MobEffect> baseEffect : allEffects) {
                if (baseEffect.equals(recipe.getOutputEffect())) continue;

                EffectHolder baseHolder = EffectHolder.create(baseEffect, 3600, 0);

                ItemStack inputFlask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
                ItemAlchemyFlask.setFlaskEffects(inputFlask, new FlaskEffects(List.of(baseHolder)));

                List<EffectHolder> combined = new ArrayList<>();
                combined.add(baseHolder);
                combined.add(EffectHolder.create(recipe.getOutputEffect(), recipe.getBaseDuration(), 0));
                ItemStack outputFlask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
                ItemAlchemyFlask.setFlaskEffects(outputFlask, new FlaskEffects(combined));

                out.add(new FlaskCombinationEmiRecipe(inputFlask, recipe.getInput(), outputFlask,
                        recipe.getSyphon(), recipe.getTicks(), recipe.getMinimumTier(),
                        NVEmiCategories.recipeId("flask_combination", index++)));
            }
        }
        return out;
    }

    public static ItemStack displayItemFor(Block block) {
        if (block == Blocks.WATER) return new ItemStack(Items.WATER_BUCKET);
        if (block == Blocks.LAVA) return new ItemStack(Items.LAVA_BUCKET);

        if (block instanceof LiquidBlock liquidBlock) {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof BucketItem bucketItem && bucketItem.content.isSame(liquidBlock.fluid)) {
                    return new ItemStack(bucketItem);
                }
            }
        }
        return new ItemStack(block);
    }
}
