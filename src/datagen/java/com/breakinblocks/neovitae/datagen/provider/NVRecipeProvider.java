package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import com.breakinblocks.neovitae.common.crafting.OrbTierIngredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import com.breakinblocks.neovitae.datagen.builder.recipe.BaseRecipeBuilder;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.datagen.builder.AlchemyArrayEffectRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.AlchemyArrayRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.TabulaVitaeRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.AthanorRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.AthanorPotionRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.AltarRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.ForgeSpiritusInfusionRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.ForgeTransformRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.ForgeUpgradeRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.HellfireForgeRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.TieredRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.MeteorRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.FlaskRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.SentientDowngradeRecipeBuilder;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStackTemplate;

public class NVRecipeProvider extends RecipeProvider {

    private final HolderGetter<Item> items;
    private final HolderGetter<Fluid> fluids;

    protected NVRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.items = registries.lookupOrThrow(Registries.ITEM);
        this.fluids = registries.lookupOrThrow(Registries.FLUID);
    }

    /** Wrap an Identifier as a recipe ResourceKey (the 26.1 save() contract). */
    protected static ResourceKey<Recipe<?>> rKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    /**
     * Creates an ingredient that matches a water bottle (potion with water contents)
     */
    private static Ingredient waterBottle() {
        var patch = DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER))
                .build();
        return DataComponentIngredient.of(false, new ItemStackTemplate(Items.POTION, patch));
    }

    @Override
    protected void buildRecipes() {
        RecipeOutput output = this.output;
        BaseRecipeBuilder.bindItemGetter(items);
        BaseRecipeBuilder.bindFluidGetter(fluids);
        addVanillaCraftingRecipes(output);
        addTieredRecipes(output);
        addAraVitaeRecipes(output);
        addHellfireForgeRecipes(output);
        addForgeUpgradeRecipes(output);
        addAlchemyArrayRecipes(output);
        addTabulaVitaeRecipes(output);
        addAthanorRecipes(output);
        addDungeonRecipes(output);
        addMeteorRecipes(output);
        addFlaskRecipes(output);
        addSentientDowngradeRecipes(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new NVRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "NeoVitae Recipes";
        }
    }

    private void addVanillaCraftingRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, NVItems.GUIDE_BOOK.get())
                .pattern(" r ")
                .pattern("rbr")
                .pattern(" r ")
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('b', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);

        // Sacrificial Dagger - diagonal dagger shape
        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, NVItems.SACRIFICIAL_DAGGER.get())
                .pattern("ggg")
                .pattern(" Gg")
                .pattern("i g")
                .define('g', Tags.Items.GLASS_BLOCKS)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                .save(output);

        // Ara Vitae - stone frame with furnace, gold ingots on bottom
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, NVBlocks.ARA_VITAE.block().get())
                .pattern("s s")
                .pattern("sfs")
                .pattern("ggg")
                .define('s', NVTags.Items.VITAE_STONE)
                .define('f', Items.FURNACE)
                .define('g', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(output);

        // Hellfire Forge (Soul Forge)
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, NVBlocks.HELLFIRE_FORGE.block().get())
                .pattern("i i")
                .pattern("sSs")
                .pattern("sos")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('s', NVTags.Items.VITAE_STONE)
                .define('S', NVItems.TABULA_RASA.get())
                .define('o', Tags.Items.STORAGE_BLOCKS_IRON)
                .unlockedBy("has_blank_slate", has(NVItems.TABULA_RASA.get()))
                .save(output);

        // Bloodstone
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.BLOODSTONE.block().get(), 8)
                .pattern("sss")
                .pattern("sbs")
                .pattern("sss")
                .define('s', Items.STONE)
                .define('b', NVFluids.ESSENTIA_VITAE_BUCKET.get())
                .unlockedBy("has_essentia_vitae", has(NVFluids.ESSENTIA_VITAE_BUCKET.get()))
                .save(output);

        // Bloodstone Brick (from stone + weak blood shard)
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.BLOODSTONE_BRICK.block().get(), 4)
                .pattern("ss")
                .pattern("sb")
                .define('s', NVTags.Items.VITAE_STONE)
                .define('b', NVItems.WEAK_BLOOD_SHARD.get())
                .unlockedBy("has_weak_blood_shard", has(NVItems.WEAK_BLOOD_SHARD.get()))
                .save(output);

        // Hellforged Block (storage block)
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.HELLFORGED_BLOCK.block().get())
                .pattern("iii")
                .pattern("iii")
                .pattern("iii")
                .define('i', NVTags.Items.INGOTS_HELLFORGED)
                .unlockedBy(getHasName(NVItems.HELLFORGED_INGOT.get()), has(NVTags.Items.INGOTS_HELLFORGED))
                .save(output, rKey(NeoVitae.rl("hellforged_block_from_ingots")));

        // Hellforged Ingot from Block
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, NVItems.HELLFORGED_INGOT.get(), 9)
                .requires(NVBlocks.HELLFORGED_BLOCK.block().get())
                .unlockedBy(getHasName(NVBlocks.HELLFORGED_BLOCK.block().get()), has(NVBlocks.HELLFORGED_BLOCK.block().get()))
                .save(output, rKey(NeoVitae.rl("hellforged_ingot_from_block")));

        // Raw Demonite Block from Raw Demonite (9 -> 1 block)
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, NVBlocks.RAW_DEMONITE_BLOCK.block().get())
                .pattern("ddd")
                .pattern("ddd")
                .pattern("ddd")
                .define('d', NVItems.DEMONITE_RAW.get())
                .unlockedBy("has_raw_demonite", has(NVItems.DEMONITE_RAW.get()))
                .save(output, rKey(NeoVitae.rl("raw_demonite_block")));

        // Raw Demonite from Block (1 block -> 9)
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, NVItems.DEMONITE_RAW.get(), 9)
                .requires(NVBlocks.RAW_DEMONITE_BLOCK.block().get())
                .unlockedBy("has_raw_demonite_block", has(NVBlocks.RAW_DEMONITE_BLOCK.block().get()))
                .save(output, rKey(NeoVitae.rl("raw_demonite_from_block")));

        // Corrupted Dust from 4 Tiny Corrupted Dust
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, NVItems.CORRUPTED_DUST.get())
                .requires(NVItems.CORRUPTED_DUST_TINY.get())
                .requires(NVItems.CORRUPTED_DUST_TINY.get())
                .requires(NVItems.CORRUPTED_DUST_TINY.get())
                .requires(NVItems.CORRUPTED_DUST_TINY.get())
                .unlockedBy("has_corrupted_tiny_dust", has(NVItems.CORRUPTED_DUST_TINY.get()))
                .save(output, rKey(NeoVitae.rl("corrupted_dust_from_tiny")));

        // Tiny Corrupted Dust from 1 Corrupted Dust
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, NVItems.CORRUPTED_DUST_TINY.get(), 4)
                .requires(NVItems.CORRUPTED_DUST.get())
                .unlockedBy("has_corrupted_dust", has(NVItems.CORRUPTED_DUST.get()))
                .save(output, rKey(NeoVitae.rl("corrupted_tiny_dust_from_corrupted")));

        // Raw Demonite smelting -> Hellforged Ingot
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(NVItems.DEMONITE_RAW.get()), RecipeCategory.MISC, CookingBookCategory.MISC, NVItems.HELLFORGED_INGOT.get(), 0, 200)
                .unlockedBy("has_raw_demonite", has(NVItems.DEMONITE_RAW.get()))
                .save(output, rKey(NeoVitae.rl("smelting/hellforged_ingot_from_raw")));

        // Raw Demonite blasting -> Hellforged Ingot
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(NVItems.DEMONITE_RAW.get()), RecipeCategory.MISC, CookingBookCategory.MISC, NVItems.HELLFORGED_INGOT.get(), 0, 100)
                .unlockedBy("has_raw_demonite", has(NVItems.DEMONITE_RAW.get()))
                .save(output, rKey(NeoVitae.rl("blasting/hellforged_ingot_from_raw")));

        // Blood Stained Glass from Sands of Vitae
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(NVBlocks.SANDS_OF_VITAE.block().get()), RecipeCategory.BUILDING_BLOCKS, CookingBookCategory.MISC, NVBlocks.BLOOD_STAINED_GLASS.block().get(), 0.1f, 200)
                .unlockedBy("has_sands", has(NVBlocks.SANDS_OF_VITAE.block().get()))
                .save(output, rKey(NeoVitae.rl("smelting/blood_stained_glass")));

        // Blood Stained Glass Pane from Blood Stained Glass
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.BLOOD_STAINED_GLASS_PANE.block().get(), 16)
                .pattern("GGG")
                .pattern("GGG")
                .define('G', NVBlocks.BLOOD_STAINED_GLASS.block().get())
                .unlockedBy("has_glass", has(NVBlocks.BLOOD_STAINED_GLASS.block().get()))
                .save(output, rKey(NeoVitae.rl("blood_stained_glass_pane")));

        // Synthetic Point - iron nuggets corners, meat edges, redstone center
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, NVItems.SYNTHETIC_POINT.get(), 2)
                .pattern("imi")
                .pattern("mrm")
                .pattern("imi")
                .define('i', Tags.Items.NUGGETS_IRON)
                .define('m', ItemTags.MEAT)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_meat", has(ItemTags.MEAT))
                .save(output);

        // Blank Rune - stone around, blank slate at top center, blood orb (tier 1) in center
        // Pattern: asa / aoa / aaa
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_BLANK.block().get())
                .pattern("asa")
                .pattern("aoa")
                .pattern("aaa")
                .define('a', NVTags.Items.VITAE_STONE)
                .define('s', NVItems.TABULA_RASA.get())
                .define('o', OrbTierIngredient.of(1))
                .unlockedBy("has_blank_slate", has(NVItems.TABULA_RASA.get()))
                .save(output);

        // Speed Rune - stone(a), blank_slate(b), sugar(c), blank_rune(d) - NO orb
        // Pattern: aba / cdc / aba
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_SPEED.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aba")
                .define('a', NVTags.Items.VITAE_STONE)
                .define('b', NVItems.TABULA_RASA.get())
                .define('c', Items.SUGAR)
                .define('d', NVBlocks.RUNE_BLANK.block().get())
                .unlockedBy("has_blank_rune", has(NVBlocks.RUNE_BLANK.block().get()))
                .save(output);

        // Sacrifice Rune - reinforced_slate(b), gold(c), blank_rune(d), orb_tier_2(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_SACRIFICE.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', NVTags.Items.VITAE_STONE)
                .define('b', NVItems.TABULA_ROBUR.get())
                .define('c', Tags.Items.INGOTS_GOLD)
                .define('d', NVBlocks.RUNE_BLANK.block().get())
                .define('e', OrbTierIngredient.of(2))
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output);

        // Self-Sacrifice Rune - reinforced_slate(b), glowstone_dust(c), blank_rune(d), orb_tier_2(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_SELF_SACRIFICE.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', NVTags.Items.VITAE_STONE)
                .define('b', NVItems.TABULA_ROBUR.get())
                .define('c', Items.GLOWSTONE_DUST)
                .define('d', NVBlocks.RUNE_BLANK.block().get())
                .define('e', OrbTierIngredient.of(2))
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output);

        // Capacity Rune - bucket(a), imbued_slate(d), blank_rune(c) - NO orb
        // Pattern: aba / bcb / ada
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_CAPACITY.block().get())
                .pattern("aba")
                .pattern("bcb")
                .pattern("ada")
                .define('a', NVTags.Items.VITAE_STONE)
                .define('b', Items.BUCKET)
                .define('c', NVBlocks.RUNE_BLANK.block().get())
                .define('d', NVItems.TABULA_ANIMATA.get())
                .unlockedBy("has_imbued_slate", has(NVItems.TABULA_ANIMATA.get()))
                .save(output);

        // Dislocation Rune - water_bucket(b), imbued_slate(d), blank_rune(c) - NO orb
        // Pattern: aba / bcb / ada
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_DISLOCATION.block().get())
                .pattern("aba")
                .pattern("bcb")
                .pattern("ada")
                .define('a', NVTags.Items.VITAE_STONE)
                .define('b', Items.WATER_BUCKET)
                .define('c', NVBlocks.RUNE_BLANK.block().get())
                .define('d', NVItems.TABULA_ANIMATA.get())
                .unlockedBy("has_imbued_slate", has(NVItems.TABULA_ANIMATA.get()))
                .save(output);

        // Charging Rune - special pattern with demonic_slate, orb_tier_4
        // Pattern: RsR / GrG / ReR (R=redstone, s=demonic_slate, G=glowstone, r=blank_rune, e=orb_tier_4)
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_CHARGING.block().get())
                .pattern("RsR")
                .pattern("GrG")
                .pattern("ReR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('s', NVItems.TABULA_SPIRITUS.get())
                .define('G', Tags.Items.DUSTS_GLOWSTONE)
                .define('r', NVBlocks.RUNE_BLANK.block().get())
                .define('e', OrbTierIngredient.of(4))
                .unlockedBy("has_demonic_slate", has(NVItems.TABULA_SPIRITUS.get()))
                .save(output);

        // Acceleration Rune - bucket(a), demonic_slate(b), gold(c), speed_rune(d), orb_tier_4(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_ACCELERATION.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', Items.BUCKET)
                .define('b', NVItems.TABULA_SPIRITUS.get())
                .define('c', Tags.Items.INGOTS_GOLD)
                .define('d', NVBlocks.RUNE_SPEED.block().get())
                .define('e', OrbTierIngredient.of(4))
                .unlockedBy("has_speed_rune", has(NVBlocks.RUNE_SPEED.block().get()))
                .save(output);

        // Augmented Capacity Rune - obsidian(a), demonic_slate(b), bucket(c), capacity_rune(d), orb_tier_4(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_CAPACITY_AUGMENTED.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', Tags.Items.OBSIDIANS)
                .define('b', NVItems.TABULA_SPIRITUS.get())
                .define('c', Items.BUCKET)
                .define('d', NVBlocks.RUNE_CAPACITY.block().get())
                .define('e', OrbTierIngredient.of(4))
                .unlockedBy("has_capacity_rune", has(NVBlocks.RUNE_CAPACITY.block().get()))
                .save(output);

        // Orb Rune - orb_tier_1(b), blank_rune(c), orb_tier_4(d) - uses two orb tiers
        // Pattern: aba / cdc / aba
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_ORB.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aba")
                .define('a', NVTags.Items.VITAE_STONE)
                .define('b', OrbTierIngredient.of(1))
                .define('c', NVBlocks.RUNE_BLANK.block().get())
                .define('d', OrbTierIngredient.of(4))
                .unlockedBy("has_blank_rune", has(NVBlocks.RUNE_BLANK.block().get()))
                .save(output);

        // Efficiency Rune - endgame recipe with hellforged parts
        // Pattern: RsR / hrh / ReR
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_EFFICIENCY.block().get())
                .pattern("RsR")
                .pattern("hrh")
                .pattern("ReR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('s', NVItems.TABULA_AETHEREA.get())
                .define('h', NVItems.HELLFORGED_PARTS.get())
                .define('r', NVBlocks.RUNE_BLANK.block().get())
                .define('e', OrbTierIngredient.of(5))
                .unlockedBy("has_hellforged_parts", has(NVItems.HELLFORGED_PARTS.get()))
                .save(output);

        // Tier 2 Runes (require bloodstone and netherite)
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_SPEED.block().get(), NVBlocks.RUNE_SPEED.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_SACRIFICE.block().get(), NVBlocks.RUNE_SACRIFICE.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_SELF_SACRIFICE.block().get(), NVBlocks.RUNE_SELF_SACRIFICE.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_CAPACITY.block().get(), NVBlocks.RUNE_CAPACITY.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_DISLOCATION.block().get(), NVBlocks.RUNE_DISLOCATION.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_CHARGING.block().get(), NVBlocks.RUNE_CHARGING.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_ACCELERATION.block().get(), NVBlocks.RUNE_ACCELERATION.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_CAPACITY_AUGMENTED.block().get(), NVBlocks.RUNE_CAPACITY_AUGMENTED.block().get());
        addTier2RuneRecipe(output, NVBlocks.RUNE_2_ORB.block().get(), NVBlocks.RUNE_ORB.block().get());
        // Reinforced Efficiency Rune - special recipe: 2 base efficiency runes + netherite scraps
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_2_EFFICIENCY.block().get())
                .pattern("nrn")
                .pattern("nsn")
                .pattern("nrn")
                .define('n', Items.NETHERITE_SCRAP)
                .define('r', NVBlocks.RUNE_EFFICIENCY.block().get())
                .define('s', ItemTags.STONE_CRAFTING_MATERIALS)
                .unlockedBy("has_efficiency_rune", has(NVBlocks.RUNE_EFFICIENCY.block().get()))
                .save(output);

        // Crystal Cluster (Hellfire Forge)
        HellfireForgeRecipeBuilder.build(NVBlocks.CRYSTAL_CLUSTER.block().get())
                .requires(Items.SCULK)
                .requires(NVItems.TABULA_AETHEREA.get())
                .requires(NVItems.WEAK_BLOOD_SHARD.get())
                .requires(Items.NETHER_STAR)
                .minSpiritus(2048)
                .drain(1024)
                .unlockedBy("has_ethereal_slate", has(NVItems.TABULA_AETHEREA.get()))
                .save(output, rKey(NeoVitae.rl("crystal_cluster")));

        // Crystal Cluster Brick (from crystal cluster)
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.CRYSTAL_CLUSTER_BRICK.block().get(), 4)
                .pattern("cc")
                .pattern("cc")
                .define('c', NVBlocks.CRYSTAL_CLUSTER.block().get())
                .unlockedBy("has_crystal_cluster", has(NVBlocks.CRYSTAL_CLUSTER.block().get()))
                .save(output);

        // Teleposer block
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, NVBlocks.TELEPOSER.block().get())
                .pattern("ggg")
                .pattern("ete")
                .pattern("ggg")
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('e', NVItems.BLOOD_PEARL.get())
                .define('t', NVItems.TELEPOSER_FOCUS.get())
                .unlockedBy("has_teleposer_focus", has(NVItems.TELEPOSER_FOCUS.get()))
                .save(output);

        // Reinforced Teleposer Focus
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, NVItems.TELEPOSER_FOCUS_REINFORCED.get())
                .requires(NVItems.TELEPOSER_FOCUS_ENHANCED.get())
                .requires(NVItems.WEAK_BLOOD_SHARD.get())
                .unlockedBy("has_enhanced_focus", has(NVItems.TELEPOSER_FOCUS_ENHANCED.get()))
                .save(output);

        // Incense Altar
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, NVBlocks.INCENSE_ALTAR.block().get())
                .pattern("s s")
                .pattern("shs")
                .pattern("lol")
                .define('s', NVTags.Items.VITAE_STONE)
                .define('h', ItemTags.COALS)
                .define('l', ItemTags.LOGS)
                .define('o', OrbTierIngredient.of(1))
                .unlockedBy("has_weak_orb", has(NVItems.ORB_WEAK.get()))
                .save(output);

        // Lava Crystal - tier 1+ orb center
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, NVItems.LAVA_CRYSTAL.get())
                .pattern("aba")
                .pattern("bcb")
                .pattern("ded")
                .define('a', Tags.Items.GLASS_BLOCKS)
                .define('b', Items.LAVA_BUCKET)
                .define('c', OrbTierIngredient.of(1))
                .define('d', Tags.Items.OBSIDIANS)
                .define('e', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_weak_orb", has(NVItems.ORB_WEAK.get()))
                .save(output);

        // Blank Ritual Stone - obsidian corners, reinforced slate edges, tier 2+ orb center
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.BLANK_RITUAL_STONE.block().get(), 4)
                .pattern("oso")
                .pattern("scs")
                .pattern("oso")
                .define('o', Tags.Items.OBSIDIANS)
                .define('s', NVItems.TABULA_ROBUR.get())
                .define('c', OrbTierIngredient.of(2))
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output, rKey(NeoVitae.rl("ritual_stone_blank")));

        // Master Ritual Stone - obsidian around, ritual stones corners, tier 3+ orb center
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.MASTER_RITUAL_STONE.block().get())
                .pattern("oso")
                .pattern("scs")
                .pattern("oso")
                .define('o', Tags.Items.OBSIDIANS)
                .define('s', NVBlocks.BLANK_RITUAL_STONE.block().get())
                .define('c', OrbTierIngredient.of(3))
                .unlockedBy("has_ritual_stone", has(NVBlocks.BLANK_RITUAL_STONE.block().get()))
                .save(output, rKey(NeoVitae.rl("ritual_stone_master")));

        // Imperfect Ritual Stone - obsidian corners, stone sides, weak blood orb center
        // Simple recipe for early-game weak rituals
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, NVBlocks.IMPERFECT_RITUAL_STONE.block().get())
                .pattern("oso")
                .pattern("scs")
                .pattern("oso")
                .define('o', Tags.Items.OBSIDIANS)
                .define('s', NVTags.Items.VITAE_STONE)
                .define('c', NVItems.ORB_WEAK.get())
                .unlockedBy("has_weak_orb", has(NVItems.ORB_WEAK.get()))
                .save(output, rKey(NeoVitae.rl("imperfect_ritual_stone")));

        // Ritual Reader - demonic slate, glass, gold ingots, tier 4+ orb
        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, NVItems.RITUAL_READER.get())
                .pattern("gog")
                .pattern("isi")
                .pattern(" s ")
                .define('s', NVItems.TABULA_SPIRITUS.get())
                .define('g', Tags.Items.GLASS_BLOCKS)
                .define('i', Tags.Items.INGOTS_GOLD)
                .define('o', OrbTierIngredient.of(4))
                .unlockedBy("has_demonic_slate", has(NVItems.TABULA_SPIRITUS.get()))
                .save(output);

        // Ritual Diviner (base) - diamonds, inscription tools, stick
        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, NVItems.RITUAL_DIVINER.get())
                .pattern("dfd")
                .pattern("ase")
                .pattern("dwd")
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('f', NVItems.INSCRIPTION_TOOL_FIRE.get())
                .define('a', NVItems.INSCRIPTION_TOOL_AIR.get())
                .define('s', Tags.Items.RODS_WOODEN)
                .define('e', NVItems.INSCRIPTION_TOOL_EARTH.get())
                .define('w', NVItems.INSCRIPTION_TOOL_WATER.get())
                .unlockedBy("has_inscription_tool", has(NVItems.INSCRIPTION_TOOL_AIR.get()))
                .save(output);

        // Ritual Diviner (dusk) - demonic slates, dusk inscription tool, base diviner
        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, NVItems.RITUAL_DIVINER_DUSK.get())
                .pattern(" S ")
                .pattern("tdt")
                .pattern(" S ")
                .define('S', NVItems.TABULA_SPIRITUS.get())
                .define('t', NVItems.INSCRIPTION_TOOL_DUSK.get())
                .define('d', NVItems.RITUAL_DIVINER.get())
                .unlockedBy("has_demonic_slate", has(NVItems.TABULA_SPIRITUS.get()))
                .save(output);

        // Incense Path Blocks
        // Wood Brick Path - 4x planks + tier 2+ orb
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.WOOD_BRICK_PATH.block().get(), 4)
                .requires(Ingredient.of(items.getOrThrow(ItemTags.PLANKS)))
                .requires(Ingredient.of(items.getOrThrow(ItemTags.PLANKS)))
                .requires(Ingredient.of(items.getOrThrow(ItemTags.PLANKS)))
                .requires(Ingredient.of(items.getOrThrow(ItemTags.PLANKS)))
                .requires(OrbTierIngredient.of(2))
                .unlockedBy("has_apprentice_orb", has(NVItems.ORB_APPRENTICE.get()))
                .save(output, rKey(NeoVitae.rl("path/path_wood_brick")));

        // Wood Tile Path - 4x wood brick path
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.WOOD_TILE_PATH.block().get(), 4)
                .requires(DungeonBlocks.WOOD_BRICK_PATH.block().get())
                .requires(DungeonBlocks.WOOD_BRICK_PATH.block().get())
                .requires(DungeonBlocks.WOOD_BRICK_PATH.block().get())
                .requires(DungeonBlocks.WOOD_BRICK_PATH.block().get())
                .unlockedBy("has_wood_brick_path", has(DungeonBlocks.WOOD_BRICK_PATH.block().get()))
                .save(output, rKey(NeoVitae.rl("path/path_wood_tile")));

        // Stone Brick Path - 4x stone + tier 3+ orb
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.STONE_BRICK_PATH.block().get(), 4)
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)))
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)))
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)))
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)))
                .requires(OrbTierIngredient.of(3))
                .unlockedBy("has_magician_orb", has(NVItems.ORB_MAGICIAN.get()))
                .save(output, rKey(NeoVitae.rl("path/path_stone_brick")));

        // Stone Tile Path - 4x stone brick path
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.STONE_TILE_PATH.block().get(), 4)
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .unlockedBy("has_stone_brick_path", has(DungeonBlocks.STONE_BRICK_PATH.block().get()))
                .save(output, rKey(NeoVitae.rl("path/path_stone_tile")));

        // Worn Stone Brick Path - 4x stone brick path + tier 4+ orb
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.WORN_STONE_BRICK_PATH.block().get(), 4)
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.STONE_BRICK_PATH.block().get())
                .requires(OrbTierIngredient.of(4))
                .unlockedBy("has_master_orb", has(NVItems.ORB_MASTER.get()))
                .save(output, rKey(NeoVitae.rl("path/path_worn_stone_brick")));

        // Worn Stone Tile Path - 4x worn stone brick path
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.WORN_STONE_TILE_PATH.block().get(), 4)
                .requires(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get())
                .requires(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get())
                .unlockedBy("has_worn_stone_brick_path", has(DungeonBlocks.WORN_STONE_BRICK_PATH.block().get()))
                .save(output, rKey(NeoVitae.rl("path/path_worn_stone_tile")));

        // Obsidian Brick Path - 4x obsidian + tier 5+ orb
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get(), 4)
                .requires(Blocks.OBSIDIAN)
                .requires(Blocks.OBSIDIAN)
                .requires(Blocks.OBSIDIAN)
                .requires(Blocks.OBSIDIAN)
                .requires(OrbTierIngredient.of(5))
                .unlockedBy("has_archmage_orb", has(NVItems.ORB_ARCHMAGE.get()))
                .save(output, rKey(NeoVitae.rl("path/path_obsidian_brick")));

        // Obsidian Tile Path - 4x obsidian brick path
        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, DungeonBlocks.OBSIDIAN_TILE_PATH.block().get(), 4)
                .requires(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get())
                .requires(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get())
                .requires(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get())
                .requires(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get())
                .unlockedBy("has_obsidian_brick_path", has(DungeonBlocks.OBSIDIAN_BRICK_PATH.block().get()))
                .save(output, rKey(NeoVitae.rl("path/path_obsidian_tile")));
    }

    private void addTieredRecipes(RecipeOutput output) {
        // Blood Tank Tier Upgrade - combines two tanks to upgrade tier
        TieredRecipeBuilder.fluid(RecipeCategory.MISC, NVBlocks.BLOOD_TANK.block().get())
                .pattern("g g")
                .pattern("tgt")
                .pattern("gsg")
                .define('g', Items.GLASS)
                .define('t', NVBlocks.BLOOD_TANK.block().get())
                .define('s', NVBlocks.BLOODSTONE.block().get())
                .primary(3) // slot index for primary tank
                .secondary(5) // slot index for secondary tank
                .unlockedBy("has_blood_tank", has(NVBlocks.BLOOD_TANK.block().get()))
                .save(output, rKey(NeoVitae.rl("blood_tank_upgrade")));
    }

    private void addAraVitaeRecipes(RecipeOutput output) {
        AltarRecipeBuilder.build(NVBlocks.BLOOD_LANTERN.block().get())
                .from(Items.SOUL_LANTERN)
                .minTier(0)
                .bloodNeeded(1500)
                .consumption(5)
                .drain(2)
                .unlockedBy("has_altar", has(NVBlocks.ARA_VITAE.block().get()))
                .save(output, rKey(NeoVitae.rl("blood_lantern")));

        // Blood Orb progression - each orb is made from different materials, NOT from previous orb
        AltarRecipeBuilder.build(NVItems.ORB_WEAK.get())
                .from(Tags.Items.GEMS_DIAMOND)
                .minTier(0)
                .bloodNeeded(2000)
                .consumption(5)
                .drain(1)
                .unlockedBy("has_altar", has(NVBlocks.ARA_VITAE.block().get()))
                .save(output, rKey(NeoVitae.rl("weak_blood_orb")));

        AltarRecipeBuilder.build(NVItems.ORB_APPRENTICE.get())
                .from(Tags.Items.STORAGE_BLOCKS_REDSTONE)  // Redstone Block
                .minTier(1)
                .bloodNeeded(5000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_weak_orb", has(NVItems.ORB_WEAK.get()))
                .save(output, rKey(NeoVitae.rl("apprentice_blood_orb")));

        AltarRecipeBuilder.build(NVItems.ORB_MAGICIAN.get())
                .from(Tags.Items.STORAGE_BLOCKS_GOLD)  // Gold Block
                .minTier(2)
                .bloodNeeded(25000)
                .consumption(20)
                .drain(20)
                .unlockedBy("has_apprentice_orb", has(NVItems.ORB_APPRENTICE.get()))
                .save(output, rKey(NeoVitae.rl("magician_blood_orb")));

        AltarRecipeBuilder.build(NVItems.ORB_MASTER.get())
                .from(NVItems.WEAK_BLOOD_SHARD.get())  // Weak Blood Shard
                .minTier(3)
                .bloodNeeded(40000)
                .consumption(30)
                .drain(50)
                .unlockedBy("has_magician_orb", has(NVItems.ORB_MAGICIAN.get()))
                .save(output, rKey(NeoVitae.rl("master_blood_orb")));

        AltarRecipeBuilder.build(NVItems.ORB_ARCHMAGE.get())
                .from(NVBlocks.HELLFORGED_BLOCK.block().get())  // Hellforged Block
                .minTier(4)
                .bloodNeeded(80000)
                .consumption(50)
                .drain(100)
                .unlockedBy("has_master_orb", has(NVItems.ORB_MASTER.get()))
                .save(output, rKey(NeoVitae.rl("archmage_blood_orb")));

        AltarRecipeBuilder.build(NVItems.ORB_TRANSCENDENT.get())
                .from(NVBlocks.CRYSTAL_CLUSTER.block().get())
                .minTier(5)
                .bloodNeeded(200000)
                .consumption(100)
                .drain(200)
                .unlockedBy("has_archmage_orb", has(NVItems.ORB_ARCHMAGE.get()))
                .save(output, rKey(NeoVitae.rl("transcendent_blood_orb")));

        // Activation Crystals
        AltarRecipeBuilder.build(NVItems.ACTIVATION_CRYSTAL_WEAK.get())
                .from(NVItems.LAVA_CRYSTAL.get())
                .minTier(2)
                .bloodNeeded(10000)
                .consumption(20)
                .drain(10)
                .unlockedBy("has_lava_crystal", has(NVItems.LAVA_CRYSTAL.get()))
                .save(output, rKey(NeoVitae.rl("weak_activation_crystal")));

        AltarRecipeBuilder.build(NVItems.ACTIVATION_CRYSTAL_AWAKENED.get())
                .from(NVItems.ACTIVATION_CRYSTAL_WEAK.get())
                .minTier(4)
                .bloodNeeded(40000)
                .consumption(30)
                .drain(50)
                .unlockedBy("has_weak_crystal", has(NVItems.ACTIVATION_CRYSTAL_WEAK.get()))
                .save(output, rKey(NeoVitae.rl("awakened_activation_crystal")));

        // Slates
        AltarRecipeBuilder.build(NVItems.TABULA_RASA.get())
                .from(NVTags.Items.VITAE_STONE)
                .minTier(0)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_altar", has(NVBlocks.ARA_VITAE.block().get()))
                .save(output, rKey(NeoVitae.rl("tabula_rasa")));

        AltarRecipeBuilder.build(NVItems.TABULA_ROBUR.get())
                .from(NVItems.TABULA_RASA.get())
                .minTier(1)
                .bloodNeeded(2000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_blank_slate", has(NVItems.TABULA_RASA.get()))
                .save(output, rKey(NeoVitae.rl("tabula_robur")));

        AltarRecipeBuilder.build(NVItems.TABULA_ANIMATA.get())
                .from(NVItems.TABULA_ROBUR.get())
                .minTier(2)
                .bloodNeeded(5000)
                .consumption(15)
                .drain(10)
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output, rKey(NeoVitae.rl("tabula_animata")));

        AltarRecipeBuilder.build(NVItems.TABULA_SPIRITUS.get())
                .from(NVItems.TABULA_ANIMATA.get())
                .minTier(3)
                .bloodNeeded(15000)
                .consumption(20)
                .drain(20)
                .unlockedBy("has_imbued_slate", has(NVItems.TABULA_ANIMATA.get()))
                .save(output, rKey(NeoVitae.rl("tabula_spiritus")));

        AltarRecipeBuilder.build(NVItems.TABULA_AETHEREA.get())
                .from(NVItems.TABULA_SPIRITUS.get())
                .minTier(4)
                .bloodNeeded(30000)
                .consumption(40)
                .drain(100)
                .unlockedBy("has_demonic_slate", has(NVItems.TABULA_SPIRITUS.get()))
                .save(output, rKey(NeoVitae.rl("tabula_aetherea")));

        AltarRecipeBuilder.build(NVFluids.ESSENTIA_VITAE_BUCKET.get())
                .from(Items.BUCKET)
                .minTier(0)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(0)
                .unlockedBy("has_altar", has(NVBlocks.ARA_VITAE.block().get()))
                .save(output, rKey(NeoVitae.rl("bucket_life")));

        // Teleposer Focus - ender pearl on tier 3 altar
        AltarRecipeBuilder.build(NVItems.TELEPOSER_FOCUS.get())
                .from(Tags.Items.ENDER_PEARLS)
                .minTier(3)
                .bloodNeeded(2000)
                .consumption(10)
                .drain(10)
                .unlockedBy("has_demonic_slate", has(NVItems.TABULA_SPIRITUS.get()))
                .save(output, rKey(NeoVitae.rl("teleposer_focus")));

        // Enhanced Teleposer Focus - from teleposer focus on tier 3 altar
        AltarRecipeBuilder.build(NVItems.TELEPOSER_FOCUS_ENHANCED.get())
                .from(NVItems.TELEPOSER_FOCUS.get())
                .minTier(3)
                .bloodNeeded(10000)
                .consumption(20)
                .drain(10)
                .unlockedBy("has_teleposer_focus", has(NVItems.TELEPOSER_FOCUS.get()))
                .save(output, rKey(NeoVitae.rl("enhanced_teleposer_focus")));

        // Inscription Tools
        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_AIR.get())
                .from(Items.GHAST_TEAR)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output, rKey(NeoVitae.rl("air_tool")));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_FIRE.get())
                .from(Items.MAGMA_CREAM)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output, rKey(NeoVitae.rl("fire_tool")));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_WATER.get())
                .from(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output, rKey(NeoVitae.rl("water_tool")));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_EARTH.get())
                .from(Tags.Items.OBSIDIANS)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output, rKey(NeoVitae.rl("earth_tool")));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_DUSK.get())
                .from(Tags.Items.STORAGE_BLOCKS_COAL)
                .minTier(3)
                .bloodNeeded(2000)
                .consumption(20)
                .drain(10)
                .unlockedBy("has_demonic_slate", has(NVItems.TABULA_SPIRITUS.get()))
                .save(output, rKey(NeoVitae.rl("dusk_tool")));

        // Alchemy Flask - glass bottle on tier 1 altar
        AltarRecipeBuilder.build(NVItems.ALCHEMY_FLASK.get())
                .from(Items.GLASS_BOTTLE)
                .minTier(1)
                .bloodNeeded(4000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_apprentice_orb", has(NVItems.ORB_APPRENTICE.get()))
                .save(output, rKey(NeoVitae.rl("alchemy_flask")));

        AltarRecipeBuilder.build(NVItems.BLOOD_SWEAT_AND_TEARS.get())
                .from(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .minTier(5)
                .bloodNeeded(2000)
                .consumption(5)
                .drain(1)
                .unlockedBy("has_archmage_orb", has(NVItems.ORB_ARCHMAGE.get()))
                .save(output, rKey(NeoVitae.rl("blood_sweat_and_tears_music")));

        // Sands of Vitae - soul sand on tier 0 altar
        AltarRecipeBuilder.build(NVBlocks.SANDS_OF_VITAE.block().get())
                .from(Blocks.SOUL_SAND)
                .minTier(0)
                .bloodNeeded(250)
                .consumption(5)
                .drain(1)
                .unlockedBy("has_altar", has(NVBlocks.ARA_VITAE.block().get()))
                .save(output, rKey(NeoVitae.rl("sands_of_vitae")));

        // Revenant Plate infusion - turns revenant plate into an empowered form via tier 4 altar
        AltarRecipeBuilder.build(NVItems.REVENANT_PLATE.get())
                .from(NVItems.GORE_CLOTTED_FANG.get())
                .minTier(3)
                .bloodNeeded(20000)
                .consumption(25)
                .drain(30)
                .unlockedBy("has_gore_fang", has(NVItems.GORE_CLOTTED_FANG.get()))
                .save(output, rKey(NeoVitae.rl("revenant_plate_infusion")));

        AltarRecipeBuilder.build(DungeonBlocks.SPIKES.asItem())
                .from(Items.IRON_BARS)
                .minTier(1)
                .bloodNeeded(500)
                .consumption(5)
                .drain(20)
                .unlockedBy("has_iron_bars", has(Items.IRON_BARS))
                .save(output, rKey(NeoVitae.rl("spikes")));

        AltarRecipeBuilder.build(NVItems.BLOOD_PEARL.get())
                .from(Items.ENDER_PEARL)
                .minTier(1)
                .bloodNeeded(500)
                .consumption(5)
                .drain(20)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(output, rKey(NeoVitae.rl("blood_pearl")));
    }

    private void addHellfireForgeRecipes(RecipeOutput output) {
        // Petty Soul Gem - redstone dust, gold ingot, glass, lapis gem
        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_GEM_PETTY.get())
                .requires(Tags.Items.DUSTS_REDSTONE)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(Tags.Items.GEMS_LAPIS)
                .minSpiritus(1)
                .drain(1)
                .unlockedBy("has_raw_will", has(NVItems.RAW_SPIRITUS.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_gem_petty")));

        // Lesser Soul Gem - petty gem, diamond, redstone block, lapis block
        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_GEM_LESSER.get())
                .requires(NVItems.SPIRITUS_GEM_PETTY.get())
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .requires(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minSpiritus(60)
                .drain(20)
                .unlockedBy("has_petty_gem", has(NVItems.SPIRITUS_GEM_PETTY.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_gem_lesser")));

        // Common Soul Gem - lesser gem, diamond, gold block, imbued slate
        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_GEM_COMMON.get())
                .requires(NVItems.SPIRITUS_GEM_LESSER.get())
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.STORAGE_BLOCKS_GOLD)
                .requires(NVItems.TABULA_ANIMATA.get())
                .minSpiritus(240)
                .drain(50)
                .unlockedBy("has_lesser_gem", has(NVItems.SPIRITUS_GEM_LESSER.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_gem_common")));

        // Greater Soul Gem - common gem, demonic slate, weak blood shard, demon crystal
        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_GEM_GREATER.get())
                .requires(NVItems.SPIRITUS_GEM_COMMON.get())
                .requires(NVItems.TABULA_SPIRITUS.get())
                .requires(NVItems.WEAK_BLOOD_SHARD.get())
                .requires(NVTags.Items.SPIRITUS_CRYSTALS)
                .minSpiritus(1000)
                .drain(100)
                .unlockedBy("has_common_gem", has(NVItems.SPIRITUS_GEM_COMMON.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_gem_greater")));

        // Note: Grand Soul Gem doesn't exist in 1.20.1 - removed

        // Athanor Block (shaped crafting recipe) - tier 3+ orb center
        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, NVBlocks.ATHANOR_BLOCK.block().get())
                .pattern("sss")
                .pattern("SoS")
                .pattern("IfI")
                .define('s', NVTags.Items.VITAE_STONE)
                .define('S', NVItems.TABULA_ANIMATA.get())
                .define('o', OrbTierIngredient.of(3))
                .define('I', Items.IRON_BLOCK)
                .define('f', Items.FURNACE)
                .unlockedBy("has_magician_orb", has(NVItems.ORB_MAGICIAN.get()))
                .save(output, rKey(NeoVitae.rl("athanor_block")));

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, NVItems.PRIMITIVE_HYDRATION_CELL.get())
                .pattern("csc")
                .pattern("cBc")
                .pattern("coc")
                .define('c', Tags.Items.COBBLESTONES)
                .define('s', NVItems.TABULA_RASA.get())
                .define('B', Items.WATER_BUCKET)
                .define('o', OrbTierIngredient.of(3))
                .unlockedBy("has_magician_orb", has(NVItems.ORB_MAGICIAN.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, NVItems.PRIMITIVE_FURNACE_CELL.get())
                .pattern("csc")
                .pattern("cfc")
                .pattern("coc")
                .define('c', Tags.Items.COBBLESTONES)
                .define('s', NVItems.TABULA_RASA.get())
                .define('f', Tags.Items.STORAGE_BLOCKS_COAL)
                .define('o', OrbTierIngredient.of(3))
                .unlockedBy("has_magician_orb", has(NVItems.ORB_MAGICIAN.get()))
                .save(output);

        // Blood Tank
        HellfireForgeRecipeBuilder.build(NVBlocks.BLOOD_TANK.block().get())
                .requires(Items.GLASS, 3)
                .requires(NVBlocks.BLOODSTONE.block().get())
                .minSpiritus(30)
                .drain(5)
                .unlockedBy("has_bloodstone", has(NVBlocks.BLOODSTONE.block().get()))
                .save(output, rKey(NeoVitae.rl("blood_tank")));

        // TODO: Sentient Station - block not yet implemented
        // HellfireForgeRecipeBuilder.build(NVBlocks.SENTIENT_STATION.block().get())
        //         .requires(NVBlocks.BLOODSTONE.block().get(), 2)
        //         .requires(Items.STONE, 2)
        //         .minSpiritus(50)
        //         .drain(10)
        //         .unlockedBy("has_bloodstone", has(NVBlocks.BLOODSTONE.block().get()))
        //         .save(output, rKey(NeoVitae.rl("sentient_station")));

        // Training Bracelet
        HellfireForgeRecipeBuilder.build(NVItems.TRAINING_BRACELET.get())
                .requires(Items.GOLD_INGOT, 2)
                .requires(Items.STRING)
                .requires(NVItems.RAW_SPIRITUS.get())
                .minSpiritus(10)
                .drain(5)
                .unlockedBy("has_raw_will", has(NVItems.RAW_SPIRITUS.get()))
                .save(output, rKey(NeoVitae.rl("training_bracelet")));

        ForgeTransformRecipeBuilder.build(NVItems.SENTIENT_SWORD.get())
                .transformInput(Items.IRON_SWORD)
                .catalyst(NVItems.SPIRITUS_GEM_PETTY.get())
                .minSpiritus(0).drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SPIRITUS_GEM_PETTY.get()))
                .save(output, rKey(NeoVitae.rl("sentient_sword")));

        ForgeTransformRecipeBuilder.build(NVItems.SENTIENT_AXE.get())
                .transformInput(Items.IRON_AXE)
                .catalyst(NVItems.SPIRITUS_GEM_PETTY.get())
                .minSpiritus(0).drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SPIRITUS_GEM_PETTY.get()))
                .save(output, rKey(NeoVitae.rl("sentient_axe")));

        ForgeTransformRecipeBuilder.build(NVItems.SENTIENT_PICKAXE.get())
                .transformInput(Items.IRON_PICKAXE)
                .catalyst(NVItems.SPIRITUS_GEM_PETTY.get())
                .minSpiritus(0).drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SPIRITUS_GEM_PETTY.get()))
                .save(output, rKey(NeoVitae.rl("sentient_pickaxe")));

        ForgeTransformRecipeBuilder.build(NVItems.SENTIENT_SHOVEL.get())
                .transformInput(Items.IRON_SHOVEL)
                .catalyst(NVItems.SPIRITUS_GEM_PETTY.get())
                .minSpiritus(0).drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SPIRITUS_GEM_PETTY.get()))
                .save(output, rKey(NeoVitae.rl("sentient_shovel")));

        ForgeTransformRecipeBuilder.build(NVItems.SENTIENT_SCYTHE.get())
                .transformInput(Items.IRON_HOE)
                .catalyst(NVItems.SPIRITUS_GEM_PETTY.get())
                .minSpiritus(0).drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SPIRITUS_GEM_PETTY.get()))
                .save(output, rKey(NeoVitae.rl("sentient_scythe")));

        HellfireForgeRecipeBuilder.build(NVItems.LEX_VITAE.get())
                .requires(NVItems.SENTIENT_SWORD.get())
                .requires(Items.NETHERITE_INGOT)
                .requires(NVItems.SPIRITUS_GEM_GREATER.get())
                .requires(NVItems.HELLFORGED_INGOT.get())
                .minSpiritus(2000)
                .drain(50)
                .unlockedBy("has_greater_gem", has(NVItems.SPIRITUS_GEM_GREATER.get()))
                .save(output, rKey(NeoVitae.rl("lex_vitae")));

        // Spiritus Blocks
        HellfireForgeRecipeBuilder.build(NVBlocks.VAS_MALEFICUM.block().get())
                .requires(Items.CAULDRON)
                .requires(NVTags.Items.VITAE_STONE)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.GEMS_DIAMOND)
                .minSpiritus(400)
                .drain(100)
                .unlockedBy("has_common_gem", has(NVItems.SPIRITUS_GEM_COMMON.get()))
                .save(output, rKey(NeoVitae.rl("vas_maleficum")));

        HellfireForgeRecipeBuilder.build(NVBlocks.CRYSTALLARIUM_MALEFICUM.block().get())
                .requires(NVBlocks.HELLFIRE_FORGE.block().get())
                .requires(NVTags.Items.VITAE_STONE)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.GLASS_BLOCKS)
                .minSpiritus(500)
                .drain(100)
                .unlockedBy("has_hellfire_forge", has(NVBlocks.HELLFIRE_FORGE.block().get()))
                .save(output, rKey(NeoVitae.rl("crystallarium_maleficum")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SPIRA_INFERNALIS.block().get())
                .requires(NVTags.Items.SPIRITUS_CRYSTALS)
                .requires(NVTags.Items.VITAE_STONE)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.STORAGE_BLOCKS_IRON)
                .minSpiritus(400)
                .drain(50)
                .unlockedBy("has_spiritus_crystal", has(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("spira_infernalis")));

        // Crystal Blocks
        HellfireForgeRecipeBuilder.build(NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get())
                .requires(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get(), 4)
                .minSpiritus(1200)
                .drain(100)
                .unlockedBy("has_raw_crystal", has(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("raw_spiritus_crystal")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SPIRITUS_RUINA_CRYSTAL.block().get())
                .requires(NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get(), 4)
                .minSpiritus(1200)
                .drain(100)
                .unlockedBy("has_corrosive_crystal", has(NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_ruina_crystal")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SPIRITUS_NIHILUM_CRYSTAL.block().get())
                .requires(NVItems.SPIRITUS_NIHILUM_CRYSTAL_ITEM.get(), 4)
                .minSpiritus(1200)
                .drain(100)
                .unlockedBy("has_destructive_crystal", has(NVItems.SPIRITUS_NIHILUM_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_nihilum_crystal")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SPIRITUS_VINDICTA_CRYSTAL.block().get())
                .requires(NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get(), 4)
                .minSpiritus(1200)
                .drain(100)
                .unlockedBy("has_vengeful_crystal", has(NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_vindicta_crystal")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SPIRITUS_INVICTUS_CRYSTAL.block().get())
                .requires(NVItems.SPIRITUS_INVICTUS_CRYSTAL_ITEM.get(), 4)
                .minSpiritus(1200)
                .drain(100)
                .unlockedBy("has_steadfast_crystal", has(NVItems.SPIRITUS_INVICTUS_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_invictus_crystal")));

        // Routing Nodes
        HellfireForgeRecipeBuilder.build(NVBlocks.ROUTING_CONDUIT.block().get())
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)), 2)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Tags.Items.GLASS_BLOCKS)
                .minSpiritus(100)
                .drain(5)
                .unlockedBy("has_lesser_gem", has(NVItems.SPIRITUS_GEM_LESSER.get()))
                .save(output, rKey(NeoVitae.rl("routing_conduit")));

        HellfireForgeRecipeBuilder.build(NVBlocks.INPUT_ROUTING_NODE.block().get())
                .requires(NVBlocks.ROUTING_CONDUIT.block().get())
                .requires(Items.HOPPER)
                .minSpiritus(200)
                .drain(10)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_CONDUIT.block().get()))
                .save(output, rKey(NeoVitae.rl("input_routing_node")));

        HellfireForgeRecipeBuilder.build(NVBlocks.OUTPUT_ROUTING_NODE.block().get())
                .requires(NVBlocks.ROUTING_CONDUIT.block().get())
                .requires(Items.DISPENSER)
                .minSpiritus(200)
                .drain(10)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_CONDUIT.block().get()))
                .save(output, rKey(NeoVitae.rl("output_routing_node")));

        HellfireForgeRecipeBuilder.build(NVBlocks.MASTER_ROUTING_NODE.block().get())
                .requires(NVBlocks.ROUTING_CONDUIT.block().get())
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minSpiritus(400)
                .drain(25)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_CONDUIT.block().get()))
                .save(output, rKey(NeoVitae.rl("master_routing_node")));

        // Node Upgrades
        HellfireForgeRecipeBuilder.build(NVItems.MASTER_NODE_UPGRADE.get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)), 2)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minSpiritus(400)
                .drain(50)
                .unlockedBy("has_master_routing_node", has(NVBlocks.MASTER_ROUTING_NODE.block().get()))
                .save(output, rKey(NeoVitae.rl("master_node_upgrade")));

        HellfireForgeRecipeBuilder.build(NVItems.MASTER_NODE_UPGRADE_SPEED.get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)), 2)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .minSpiritus(400)
                .drain(50)
                .unlockedBy("has_master_routing_node", has(NVBlocks.MASTER_ROUTING_NODE.block().get()))
                .save(output, rKey(NeoVitae.rl("master_node_upgrade_speed")));

        // Node Router
        HellfireForgeRecipeBuilder.build(NVItems.NODE_ROUTER.get())
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)), 2)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .minSpiritus(50)
                .drain(5)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_CONDUIT.block().get()))
                .save(output, rKey(NeoVitae.rl("node_router")));

        // Spiritus Gauge
        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_GAUGE.get())
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(NVTags.Items.SPIRITUS_CRYSTALS)
                .minSpiritus(400)
                .drain(50)
                .unlockedBy("has_spiritus_crystal", has(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_gauge")));

        // Sanguine Reverter (soul forge recipe)
        HellfireForgeRecipeBuilder.build(NVItems.SANGUINE_REVERTER.get())
                .requires(Items.SHEARS)
                .requires(NVTags.Items.VITAE_STONE)
                .requires(NVItems.TABULA_ANIMATA.get())
                .requires(Tags.Items.INGOTS_IRON)
                .minSpiritus(350)
                .drain(30)
                .unlockedBy("has_imbued_slate", has(NVItems.TABULA_ANIMATA.get()))
                .save(output, rKey(NeoVitae.rl("sanguine_reverter")));

        // Resonator (soul forge recipe)
        HellfireForgeRecipeBuilder.build(NVItems.RESONATOR.get())
                .requires(NVTags.Items.VITAE_STONE)
                .requires(Tags.Items.INGOTS_COPPER)
                .requires(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get())
                .minSpiritus(1200)
                .drain(100)
                .unlockedBy("has_raw_crystal", has(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get()))
                .save(output, rKey(NeoVitae.rl("resonator")));

        // Primitive Crystalline Resonator
        HellfireForgeRecipeBuilder.build(NVItems.PRIMITIVE_CRYSTALLINE_RESONATOR.get())
                .requires(Tags.Items.GEMS_AMETHYST)
                .requires(Tags.Items.INGOTS)
                .requires(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get())
                .requires(NVItems.TAU_OIL.get())
                .minSpiritus(1200)
                .drain(200)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, rKey(NeoVitae.rl("primitive_resonator")));

        // Hellforged Resonator
        HellfireForgeRecipeBuilder.build(NVItems.HELLFORGED_RESONATOR.get())
                .requires(Tags.Items.GEMS_AMETHYST)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get())
                .requires(NVItems.HELLFORGED_INGOT.get())
                .minSpiritus(1200)
                .drain(400)
                .unlockedBy("has_hellforged_ingot", has(NVItems.HELLFORGED_INGOT.get()))
                .save(output, rKey(NeoVitae.rl("hellforged_resonator")));

        // Throwing Daggers
        // Iron throwing dagger: 2 iron + 1 string, 500 EV, T1 orb, makes 16
        TabulaVitaeRecipeBuilder.build(NVItems.THROWING_DAGGER.get(), 16)
                .input(Tags.Items.INGOTS_IRON)
                .input(Tags.Items.INGOTS_IRON)
                .input(Tags.Items.STRINGS)
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "throwing_dagger");

        // Amethyst throwing dagger: 2 copper + 1 amethyst, 32 spiritus min, 2 drain, makes 16
        HellfireForgeRecipeBuilder.build(NVItems.THROWING_DAGGER_AMETHYST.get(), 16)
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_COPPER)), 2)
                .requires(Tags.Items.GEMS_AMETHYST)
                .minSpiritus(32)
                .drain(2)
                .unlockedBy("has_lesser_gem", has(NVItems.SPIRITUS_GEM_LESSER.get()))
                .save(output, rKey(NeoVitae.rl("throwing_dagger_amethyst")));

        // Syringe throwing dagger: 1 amethyst dagger + 1 bottle, 200 spiritus min, 10 drain, makes 1
        HellfireForgeRecipeBuilder.build(NVItems.THROWING_DAGGER_SYRINGE.get())
                .requires(NVItems.THROWING_DAGGER_AMETHYST.get())
                .requires(Items.GLASS_BOTTLE)
                .minSpiritus(200)
                .drain(10)
                .unlockedBy("has_amethyst_dagger", has(NVItems.THROWING_DAGGER_AMETHYST.get()))
                .save(output, rKey(NeoVitae.rl("throwing_dagger_syringe")));

        // Keys
        HellfireForgeRecipeBuilder.build(NVItems.SIMPLE_KEY.get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)), 2)
                .requires(NVItems.CORRUPTED_DUST_TINY.get())
                .minSpiritus(100)
                .drain(10)
                .unlockedBy("has_lesser_gem", has(NVItems.SPIRITUS_GEM_LESSER.get()))
                .save(output, rKey(NeoVitae.rl("simple_key")));

        HellfireForgeRecipeBuilder.build(NVItems.MINE_KEY.get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)), 2)
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(NVItems.CORRUPTED_DUST.get())
                .minSpiritus(200)
                .drain(25)
                .unlockedBy("has_common_gem", has(NVItems.SPIRITUS_GEM_COMMON.get()))
                .save(output, rKey(NeoVitae.rl("mine_key")));

        // Crystal Catalysts - nether_wart + tau_oil + sulfur + unique_seed
        // Raw catalyst uses potato
        HellfireForgeRecipeBuilder.build(NVItems.RAW_SPIRITUS_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.POTATO)
                .minSpiritus(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, rKey(NeoVitae.rl("raw_spiritus_catalyst")));

        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_RUINA_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.WHEAT_SEEDS)
                .minSpiritus(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_ruina_catalyst")));

        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_NIHILUM_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.BEETROOT)
                .minSpiritus(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_nihilum_catalyst")));

        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_VINDICTA_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.MELON_SEEDS)
                .minSpiritus(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_vindicta_catalyst")));

        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_INVICTUS_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.PUMPKIN_SEEDS)
                .minSpiritus(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_invictus_catalyst")));

        // Explosive Charges
        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDS)
                .requires(NVTags.Items.VITAE_STONE)
                .minSpiritus(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, rKey(NeoVitae.rl("shaped_charge")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.LOGS)
                .requires(ItemTags.PLANKS)
                .minSpiritus(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, rKey(NeoVitae.rl("deforester_charge")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDSTONE_BLOCKS)
                .requires(Tags.Items.SANDS)
                .minSpiritus(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, rKey(NeoVitae.rl("veinmine_charge")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.CRIMSON_STEMS)
                .requires(Tags.Items.MUSHROOMS)
                .minSpiritus(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, rKey(NeoVitae.rl("fungal_charge")));

        // Tier 2 charges
        HellfireForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get(), 6)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDS)
                .requires(Items.BRICK)
                .minSpiritus(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, rKey(NeoVitae.rl("aug_shaped_charge")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.LOGS)
                .requires(ItemTags.PLANKS)
                .minSpiritus(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, rKey(NeoVitae.rl("deforester_charge_2")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDSTONE_BLOCKS)
                .requires(Tags.Items.SANDS)
                .minSpiritus(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_2")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.CRIMSON_STEMS)
                .requires(Tags.Items.MUSHROOMS)
                .minSpiritus(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, rKey(NeoVitae.rl("fungal_charge_2")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDS)
                .requires(NVTags.Items.VITAE_STONE)
                .minSpiritus(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, rKey(NeoVitae.rl("shaped_charge_deep")));

        // Charge upgrade recipes - Tier 1 charges with basic anointments
        // Shaped Charge variants
        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_fortune_1")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_silk_touch")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_smelting")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_voiding")));

        // Deforester Charge variants
        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_fortune_1")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_silk_touch")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_smelting")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_voiding")));

        // Veinmine Charge variants
        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_fortune_1")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_silk_touch")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_smelting")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_voiding")));

        // Fungal Charge variants
        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .requires(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_fortune_1")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_silk_touch")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_smelting")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minSpiritus(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_voiding")));

        // Tier 2 charges - using anointment items directly
        // Aug Shaped Charge variants (uses _l anointments)
        HellfireForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("aug_shaped_charge_fortune_1_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("aug_shaped_charge_fortune_2_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("aug_shaped_charge_silk_touch_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("aug_shaped_charge_smelting_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, rKey(NeoVitae.rl("aug_shaped_charge_voiding")));

        // Shaped Charge Deep variants (uses _l anointments)
        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_deep_fortune_1_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_deep_fortune_2_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_deep_silk_touch_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_deep_smelting_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, rKey(NeoVitae.rl("shaped_charge_deep_voiding")));

        // Deforester Charge 2 variants
        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_2_fortune_1_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_2_fortune_2_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_2_silk_touch_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_2_smelting_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("deforester_charge_2_voiding")));

        // Veinmine Charge 2 variants
        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_2_fortune_1_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_2_fortune_2_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_2_silk_touch_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_2_smelting_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("veinmine_charge_2_voiding")));

        // Fungal Charge 2 variants
        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_2_fortune_1_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_2_fortune_2_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_2_silk_touch_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_2_smelting_l")));

        HellfireForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minSpiritus(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, rKey(NeoVitae.rl("fungal_charge_2_voiding")));

        // === Demon drop crafted items ===

        // Vitae Morsel - Tainted Flesh + Weak Blood Shard in forge
        HellfireForgeRecipeBuilder.build(NVItems.VITAE_MORSEL.get(), 4)
                .requires(NVItems.TAINTED_FLESH.get())
                .requires(NVItems.TAINTED_FLESH.get())
                .requires(NVItems.WEAK_BLOOD_SHARD.get())
                .requires(NVItems.GORE_CLOTTED_FANG.get())
                .minSpiritus(200)
                .drain(20)
                .unlockedBy("has_tainted_flesh", has(NVItems.TAINTED_FLESH.get()))
                .save(output, rKey(NeoVitae.rl("vitae_morsel")));

        // Bottled Spite - Ectoplasmic Residue + Glass Bottle + Venomgland Sac
        HellfireForgeRecipeBuilder.build(NVItems.BOTTLED_SPITE.get(), 2)
                .requires(NVItems.ECTOPLASMIC_RESIDUE.get())
                .requires(NVItems.VENOMGLAND_SAC.get())
                .requires(Items.GLASS_BOTTLE)
                .requires(NVItems.HOLLOW_GUT.get())
                .minSpiritus(300)
                .drain(30)
                .unlockedBy("has_ectoplasmic_residue", has(NVItems.ECTOPLASMIC_RESIDUE.get()))
                .save(output, rKey(NeoVitae.rl("bottled_spite")));

        // Demonite Trim Ingot - Demonite Fragment + Animus Mote
        HellfireForgeRecipeBuilder.build(NVItems.DEMONITE_TRIM_INGOT.get())
                .requires(NVItems.DEMONITE_FRAGMENT.get())
                .requires(NVItems.DEMONITE_FRAGMENT.get())
                .requires(NVItems.ANIMUS_MOTE.get())
                .requires(NVItems.WEAK_BLOOD_SHARD.get())
                .minSpiritus(500)
                .drain(50)
                .unlockedBy("has_animus_mote", has(NVItems.ANIMUS_MOTE.get()))
                .save(output, rKey(NeoVitae.rl("demonite_trim_ingot")));

        HellfireForgeRecipeBuilder.build(NVItems.BLIGHT_WHETSTONE.get())
                .requires(NVItems.BLIGHT_MARROW.get())
                .requires(NVItems.BLIGHT_MARROW.get())
                .requires(NVItems.SPIRITUS_RUINA_CATALYST.get())
                .requires(NVItems.TABULA_SPIRITUS.get())
                .minSpiritus(800)
                .drain(80)
                .requiredSpiritusType(SpiritusType.RUINA)
                .unlockedBy("has_blight_marrow", has(NVItems.BLIGHT_MARROW.get()))
                .save(output, rKey(NeoVitae.rl("blight_whetstone")));

        // Sigil of the Damned - Cinder Heart Fragment + Permafrost Core + Greater Spiritus Gem + Demonic Slate
        HellfireForgeRecipeBuilder.build(NVItems.SIGIL_DAMNED.get())
                .requires(NVItems.CINDER_HEART_FRAGMENT.get())
                .requires(NVItems.PERMAFROST_CORE.get())
                .requires(NVItems.SPIRITUS_GEM_GREATER.get())
                .requires(NVItems.TABULA_SPIRITUS.get())
                .minSpiritus(2000)
                .drain(200)
                .unlockedBy("has_cinder_heart", has(NVItems.CINDER_HEART_FRAGMENT.get()))
                .save(output, rKey(NeoVitae.rl("sigil_damned")));

        // Grand Soul Gem - now requires Animus Mote as a demon dungeon ingredient
        HellfireForgeRecipeBuilder.build(NVItems.SPIRITUS_GEM_GRAND.get())
                .requires(NVItems.SPIRITUS_GEM_GREATER.get())
                .requires(NVItems.ANIMUS_MOTE.get())
                .requires(NVItems.TABULA_AETHEREA.get())
                .requires(NVItems.PERMAFROST_CORE.get())
                .minSpiritus(4000)
                .drain(400)
                .unlockedBy("has_greater_gem", has(NVItems.SPIRITUS_GEM_GREATER.get()))
                .save(output, rKey(NeoVitae.rl("spiritus_gem_grand")));
    }

    // Helper methods

    private void addTier2RuneRecipe(RecipeOutput output, ItemLike result, ItemLike tier1Rune) {
        ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("nhn")
                .pattern("srs")
                .pattern("nsn")
                .define('n', Items.NETHERITE_SCRAP)
                .define('h', NVItems.HELLFORGED_PARTS.get())
                .define('r', tier1Rune)
                .define('s', ItemTags.STONE_CRAFTING_MATERIALS)
                .unlockedBy("has_tier1_rune", has(tier1Rune))
                .save(output);
    }

    private void addForgeUpgradeRecipes(RecipeOutput output) {
        ForgeUpgradeRecipeBuilder.build()
                .catalyst(NVItems.TABULA_ROBUR.get())
                .catalyst(Items.LAPIS_LAZULI)
                .catalyst(Items.NETHER_WART)
                .minSpiritus(200)
                .drain(400)
                .unlockedBy("has_reinforced_slate", has(NVItems.TABULA_ROBUR.get()))
                .save(output, rKey(NeoVitae.rl("blood_mending")));

        AthanorRecipeBuilder.build(NVTags.Items.RESONATOR)
                .input(NVItems.TABULA_ROBUR.get())
                .input(Ingredient.of(Items.ROTTEN_FLESH))
                .input(Ingredient.of(Items.BONE))
                .fluidInput(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 1000)
                .spiritusCost(SpiritusType.RAW, 20.0)
                .guaranteedOutput(NVItems.SIGIL_NECROMANCY.get())
                .save(output, rKey(NeoVitae.rl("sigil_necromancy")));

        ForgeSpiritusInfusionRecipeBuilder.build()
                .gemInput(NVTags.Items.SPIRITUS_GEM)
                .minSpiritus(1)
                .drain(0)
                .unlockedBy("has_spiritus_gem", has(NVTags.Items.SPIRITUS_GEM))
                .save(output, rKey(NeoVitae.rl("spiritus_infusion")));
    }

    private void addAlchemyArrayRecipes(RecipeOutput output) {
        // Divination Sigil - base: redstone, added: blank slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_DIVINATION.get())
                .base(Items.REDSTONE)
                .added(NVItems.TABULA_RASA.get())
                .texture("textures/models/alchemyarrays/divinationsigil.png")
                .save(output, "divination_sigil");

        // Seer Sigil - base: reagent_sight, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_SEER.get())
                .base(NVItems.REAGENT_SIGHT.get())
                .added(NVItems.TABULA_ROBUR.get())
                .texture("textures/models/alchemyarrays/sightsigil.png")
                .save(output, "seer_sigil");

        // Water Sigil - base: reagent_water, added: blank slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_WATER.get())
                .base(NVItems.REAGENT_WATER.get())
                .added(NVItems.TABULA_RASA.get())
                .texture("textures/models/alchemyarrays/watersigil.png")
                .save(output, "water_sigil");

        // Lava Sigil - base: reagent_lava, added: blank slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_LAVA.get())
                .base(NVItems.REAGENT_LAVA.get())
                .added(NVItems.TABULA_RASA.get())
                .texture("textures/models/alchemyarrays/lavasigil.png")
                .save(output, "lava_sigil");

        // Void Sigil - base: reagent_void, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_VOID.get())
                .base(NVItems.REAGENT_VOID.get())
                .added(NVItems.TABULA_ROBUR.get())
                .texture("textures/models/alchemyarrays/voidsigil.png")
                .save(output, "void_sigil");

        // Green Grove Sigil - base: reagent_growth, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_GREEN_GROVE.get())
                .base(NVItems.REAGENT_GROWTH.get())
                .added(NVItems.TABULA_ROBUR.get())
                .texture("textures/models/alchemyarrays/growthsigil.png")
                .save(output, "green_grove_sigil");

        // Fast Miner Sigil - base: reagent_fast_miner, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_FAST_MINER.get())
                .base(NVItems.REAGENT_FAST_MINER.get())
                .added(NVItems.TABULA_ROBUR.get())
                .texture("textures/models/alchemyarrays/fastminersigil.png")
                .save(output, "fast_miner_sigil");

        // Air Sigil - base: reagent_air, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_AIR.get())
                .base(NVItems.REAGENT_AIR.get())
                .added(NVItems.TABULA_ROBUR.get())
                .texture("textures/models/alchemyarrays/airsigil.png")
                .save(output, "air_sigil");

        // Blood Light Sigil - base: reagent_blood_light, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_BLOOD_LIGHT.get())
                .base(NVItems.REAGENT_BLOOD_LIGHT.get())
                .added(NVItems.TABULA_ANIMATA.get())
                .texture("textures/models/alchemyarrays/bloodlightsigil.png")
                .save(output, "blood_light_sigil");

        // Magnetism Sigil - base: reagent_magnetism, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_MAGNETISM.get())
                .base(NVItems.REAGENT_MAGNETISM.get())
                .added(NVItems.TABULA_ANIMATA.get())
                .texture("textures/models/alchemyarrays/magnetismsigil.png")
                .save(output, "magnetism_sigil");

        // Holding Sigil - base: reagent_holding, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_HOLDING.get())
                .base(NVItems.REAGENT_HOLDING.get())
                .added(NVItems.TABULA_ANIMATA.get())
                .texture("textures/models/alchemyarrays/holdingsigil.png")
                .save(output, "holding_sigil");

        // Suppression Sigil - base: reagent_suppression, added: demonic slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_SUPPRESSION.get())
                .base(NVItems.REAGENT_SUPPRESSION.get())
                .added(NVItems.TABULA_SPIRITUS.get())
                .texture("textures/models/alchemyarrays/suppressionsigil.png")
                .save(output, "suppression_sigil");

        // Teleposition Sigil - base: reagent_teleposition, added: demonic slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_TELEPOSITION.get())
                .base(NVItems.REAGENT_TELEPOSITION.get())
                .added(NVItems.TABULA_SPIRITUS.get())
                .texture("textures/models/alchemyarrays/telepositionsigil.png")
                .save(output, "teleposition_sigil");

        // Phantom Bridge Sigil - base: reagent_phantom_bridge, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_PHANTOM_BRIDGE.get())
                .base(NVItems.REAGENT_PHANTOM_BRIDGE.get())
                .added(NVItems.TABULA_ANIMATA.get())
                .texture("textures/models/alchemyarrays/phantombridgesigil.png")
                .save(output, "phantom_bridge_sigil");

        // Frost Sigil - base: reagent_frost, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_FROST.get())
                .base(NVItems.REAGENT_FROST.get())
                .added(NVItems.TABULA_ROBUR.get())
                .texture("textures/models/alchemyarrays/watersigil.png")
                .save(output, "frost_sigil");

        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_BOUND_TREASURES.get())
                .base(Items.ENDER_EYE)
                .added(NVItems.TABULA_ANIMATA.get())
                .texture("textures/models/alchemyarrays/defaultarray.png")
                .save(output, "bound_treasures_sigil");

        // Sentient Armor - reagent_binding + iron armor pieces
        AlchemyArrayRecipeBuilder.build(NVItems.SENTIENT_HELMET.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_HELMET)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "sentient_helmet");

        AlchemyArrayRecipeBuilder.build(NVItems.SENTIENT_PLATE.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_CHESTPLATE)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "sentient_plate");

        AlchemyArrayRecipeBuilder.build(NVItems.SENTIENT_LEGGINGS.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_LEGGINGS)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "sentient_leggings");

        AlchemyArrayRecipeBuilder.build(NVItems.SENTIENT_BOOTS.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_BOOTS)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "sentient_boots");

        // Training Bracelet
        AlchemyArrayRecipeBuilder.build(NVItems.TRAINING_BRACELET.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.DIAMOND)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "sentient_trainer");

        // Effect Arrays - create environmental effects, not items
        // Bounce Array - slimeball + redstone
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.BOUNCE)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.SLIME_BALLS)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .texture("textures/models/alchemyarrays/bouncearray.png")
                .save(output, "bounce");

        // Movement Array - feather + redstone
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.MOVEMENT)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.FEATHERS)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .texture("textures/models/alchemyarrays/movementarray.png")
                .save(output, "movement");

        // Spike Array - cobblestone + iron ingot
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.SPIKE)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.COBBLESTONES)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .texture("textures/models/alchemyarrays/spikearray.png")
                .save(output, "spike");

        // Updraft Array - feather + glowstone dust
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.UPDRAFT)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.FEATHERS)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .texture("textures/models/alchemyarrays/updraftarray.png")
                .save(output, "updraft");

        // Day Array (Sun) - coal + coal
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.DAY)
                .base(Items.COAL)
                .added(Items.COAL)
                .texture("textures/models/alchemyarrays/sunarray.png")
                .save(output, "day");

        // Night Array (Moon) - lapis + lapis
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.NIGHT)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .texture("textures/models/alchemyarrays/moonarray.png")
                .save(output, "night");

        // Teleposition Array - ender pearl + redstone
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.ELEVATOR)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.ENDER_PEARLS)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .texture("textures/models/alchemyarrays/elevatorarray.png")
                .save(output, "elevator");

        // Repulsion Array - iron ingot + lapis
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.REPULSION)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .texture("textures/models/alchemyarrays/repulsionarray.png")
                .save(output, "repulsion");

        // Collection Array - hopper + redstone
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.COLLECTION)
                .base(Items.HOPPER)
                .added(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .texture("textures/models/alchemyarrays/collectionarray.png")
                .save(output, "collection");

        // Light Array - glowstone dust + gold ingot
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.LIGHT)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)))
                .texture("textures/models/alchemyarrays/lightarray.png")
                .save(output, "light");

        // Furnace Array - coal + iron block
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.FURNACE)
                .base(Items.COAL)
                .added(Ingredient.of(items.getOrThrow(Tags.Items.STORAGE_BLOCKS_IRON)))
                .texture("textures/models/alchemyarrays/furnacearray.png")
                .save(output, "furnace");

        // Rain Array - water bucket + lapis
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.RAIN)
                .base(Items.WATER_BUCKET)
                .added(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .texture("textures/models/alchemyarrays/rainarray.png")
                .save(output, "rain");

        // Growth Array - bone meal + torchflower
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.GROWTH)
                .base(Items.BONE_MEAL)
                .added(Items.TORCHFLOWER)
                .texture("textures/models/alchemyarrays/growtharray.png")
                .save(output, "growth");

        // Freeze Array - snowball + lapis
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.FREEZE)
                .base(Items.SNOWBALL)
                .added(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .texture("textures/models/alchemyarrays/freezearray.png")
                .save(output, "freeze");

        // Signal Array - redstone + gold ingot
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.SIGNAL)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)))
                .texture("textures/models/alchemyarrays/signalarray.png")
                .save(output, "signal");

        // Trigger Array - redstone + iron ingot
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.TRIGGER)
                .base(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .added(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .texture("textures/models/alchemyarrays/triggerarray.png")
                .save(output, "trigger");

        // Spirit Siphon Array - imbued slate + corrupted dust
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.SPIRIT_SIPHON)
                .base(NVItems.TABULA_ANIMATA.get())
                .added(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_CORRUPTED)))
                .texture("textures/models/alchemyarrays/spiritsiphonarray.png")
                .save(output, "spirit_siphon");

        // Vortex Sigil Array - reinforced slate + blood pearl
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.VORTEX)
                .base(NVItems.TABULA_ROBUR.get())
                .added(Ingredient.of(NVItems.BLOOD_PEARL.get()))
                .texture("textures/models/alchemyarrays/vortexsigil.png")
                .save(output, "vortex");

        // Deflection Array - imbued slate + diamond
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.DEFLECTION)
                .base(NVItems.TABULA_ANIMATA.get())
                .added(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_DIAMOND)))
                .texture("textures/models/alchemyarrays/deflectionarray.png")
                .save(output, "deflection");

        // Endless Fountain Array - block of lapis + sea pickle
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.ENDLESS_FOUNTAIN)
                .base(Items.LAPIS_BLOCK)
                .added(Items.SEA_PICKLE)
                .texture("textures/models/alchemyarrays/fountainarray.png")
                .save(output, "endless_fountain");

        // Undertow Array - kelp + redstone
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.UNDERTOW)
                .base(Items.KELP)
                .added(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .texture("textures/models/alchemyarrays/undertowarray.png")
                .save(output, "undertow");

        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.LOYAL_FRIENDS)
                .base(Items.LEAD)
                .added(NVItems.TABULA_ROBUR.get())
                .save(output, "loyal_friends");
    }

    private void addTabulaVitaeRecipes(RecipeOutput output) {
        // Reagent Water - sugar, water bucket x2
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_WATER.get())
                .input(Items.SUGAR)
                .input(Items.WATER_BUCKET)
                .input(Items.WATER_BUCKET)
                .syphon(300)
                .ticks(200)
                .minimumTier(1)
                .save(output, "reagent_water");

        // Reagent Lava - lava bucket, redstone dust, cobblestone, coal block
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_LAVA.get())
                .input(Items.LAVA_BUCKET)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.COBBLESTONES)))
                .input(Items.COAL_BLOCK)
                .syphon(1000)
                .ticks(200)
                .minimumTier(1)
                .save(output, "reagent_lava");

        // Reagent Air - ghast tear, feather x2
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_AIR.get())
                .input(Items.GHAST_TEAR)
                .input(Items.FEATHER)
                .input(Items.FEATHER)
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_air");

        // Reagent Void - bucket, string x2, gunpowder
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_VOID.get())
                .input(Items.BUCKET)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .syphon(1000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_void");

        // Reagent Growth - saplings x2, sugar_cane, sugar
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_GROWTH.get())
                .input(Ingredient.of(items.getOrThrow(ItemTags.SAPLINGS)))
                .input(Ingredient.of(items.getOrThrow(ItemTags.SAPLINGS)))
                .input(Items.SUGAR_CANE)
                .input(Items.SUGAR)
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_growth");

        // Reagent Fast Miner - gold nugget, iron pickaxe, iron shovel
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_FAST_MINER.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .input(Items.IRON_PICKAXE)
                .input(Items.IRON_SHOVEL)
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_fast_miner");

        // Reagent Magnetism - string, gold_ingot x2, iron_block
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_MAGNETISM.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STORAGE_BLOCKS_IRON)))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_magnetism");

        // Reagent Blood Light - glowstone dust, torch, redstone x2
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_BLOOD_LIGHT.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Items.TORCH)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_blood_light");

        // Reagent Sight - glowstone_dust, glass x2, divination sigil
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_SIGHT.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(NVItems.SIGIL_DIVINATION.get())
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "reagent_sight");

        // Reagent Binding - glowstone_dust, redstone_dust, gunpowder, gold_nugget
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_BINDING.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_binding");

        // Reagent Holding - chest, leather, string x2
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_HOLDING.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CHESTS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.LEATHERS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_holding");

        // Reagent Suppression - teleposer, void sigil, gold ingot, bucket
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_SUPPRESSION.get())
                .input(NVBlocks.TELEPOSER.item().get())
                .input(NVItems.SIGIL_VOID.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)))
                .input(Items.BUCKET)
                .syphon(10000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "reagent_suppression");

        // Reagent Teleposition - teleposer, gold ingot, ender pearl, chorus fruit
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_TELEPOSITION.get())
                .input(NVBlocks.TELEPOSER.item().get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_GOLD)))
                .input(NVItems.BLOOD_PEARL.get())
                .input(Items.CHORUS_FRUIT)
                .syphon(10000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "reagent_teleposition");

        // Reagent Frost - ice, snowball x2, water bucket
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_FROST.get())
                .input(Blocks.ICE)
                .input(Items.SNOWBALL)
                .input(Items.SNOWBALL)
                .input(Items.WATER_BUCKET)
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_frost");

        // Reagent Phantom Bridge - feather, glass, soul sand, slime ball
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_PHANTOM_BRIDGE.get())
                .input(Items.FEATHER)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(Items.SOUL_SAND)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.SLIME_BALLS)))
                .syphon(5000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_phantom_bridge");

        // Arcane Scribe Tool - redstone, white dye (bone meal), gunpowder, coal
        TabulaVitaeRecipeBuilder.build(NVItems.ARCANE_SCRIBE_TOOL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DYES_WHITE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Ingredient.of(items.getOrThrow(ItemTags.COALS)))
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "arcane_scribe_tool");

        // Tau Oil - 3x weak tau + bone meal
        TabulaVitaeRecipeBuilder.build(NVItems.TAU_OIL.get())
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(Items.BONE_MEAL)
                .syphon(500)
                .ticks(200)
                .minimumTier(3)
                .save(output, "tau_oil");

        // Utility recipes
        // Leather from rotten flesh
        TabulaVitaeRecipeBuilder.build(Items.LEATHER, 4)
                .input(Items.ROTTEN_FLESH)
                .input(Items.ROTTEN_FLESH)
                .input(Items.ROTTEN_FLESH)
                .input(Items.ROTTEN_FLESH)
                .input(Items.FLINT)
                .input(Items.WATER_BUCKET)
                .syphon(100)
                .ticks(200)
                .minimumTier(1)
                .save(output, "leather_from_flesh");

        // String from wool
        TabulaVitaeRecipeBuilder.build(Items.STRING, 4)
                .input(Ingredient.of(items.getOrThrow(ItemTags.WOOL)))
                .input(Items.FLINT)
                .syphon(100)
                .ticks(100)
                .minimumTier(0)
                .save(output, "string");

        // Flint duplication
        TabulaVitaeRecipeBuilder.build(Items.FLINT, 2)
                .input(Items.GRAVEL)
                .input(Items.FLINT)
                .syphon(50)
                .ticks(20)
                .minimumTier(0)
                .save(output, "flint_from_gravel");

        // Bread from wheat
        TabulaVitaeRecipeBuilder.build(Items.BREAD)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_WHEAT)))
                .input(Items.SUGAR)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "bread");

        // Explosive Powder - gunpowder x2 + coal dust
        TabulaVitaeRecipeBuilder.build(NVItems.EXPLOSIVE_POWDER.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "explosive_powder");

        // Sulfur from lava bucket + cobblestone
        TabulaVitaeRecipeBuilder.build(NVItems.SULFUR.get(), 4)
                .input(Items.LAVA_BUCKET)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.COBBLESTONES)))
                .syphon(200)
                .ticks(100)
                .minimumTier(0)
                .save(output, "sulfur_from_lava");

        // Saltpeter from plant oil x2 + coal dust
        TabulaVitaeRecipeBuilder.build(NVItems.SALTPETER.get(), 3)
                .input(NVItems.PLANT_OIL.get())
                .input(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(200)
                .ticks(200)
                .minimumTier(1)
                .save(output, "saltpeter");

        // Gunpowder from sulfur + saltpeter + coal
        TabulaVitaeRecipeBuilder.build(Items.GUNPOWDER, 3)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_SULFUR)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_SALTPETER)))
                .input(Ingredient.of(items.getOrThrow(ItemTags.COALS)))
                .syphon(0)
                .ticks(100)
                .minimumTier(0)
                .save(output, "gunpowder");

        // Plant Oil recipes - from various crops
        TabulaVitaeRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_CARROT)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_CARROT)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_CARROT)))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_carrots");

        TabulaVitaeRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_POTATO)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_POTATO)))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_potatoes");

        TabulaVitaeRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_WHEAT)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_WHEAT)))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_wheat");

        TabulaVitaeRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_BEETROOT)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_BEETROOT)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_BEETROOT)))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_beets");

        // Basic Cutting Fluid - plant oil + redstone + gunpowder + sugar + coal dust + water bottle
        TabulaVitaeRecipeBuilder.build(NVItems.BASIC_CUTTING_FLUID.get())
                .input(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Items.GUNPOWDER)
                .input(Items.SUGAR)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .input(waterBottle())
                .syphon(1000)
                .ticks(200)
                .minimumTier(1)
                .save(output, "basic_cutting_fluid");

        // Slate Vial - blank slate + 5 glass
        TabulaVitaeRecipeBuilder.build(NVItems.TABULA_VIAL.get(), 8)
                .input(NVItems.TABULA_RASA.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "tabula_vial");

        // Anointment Recipes
        TabulaVitaeRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "fortune_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.SILK_TOUCH_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Items.COBWEB)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "silk_touch_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Items.BLAZE_POWDER)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "melee_damage_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Items.GLISTERING_MELON_SLICE)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "holy_water_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Items.GLASS_BOTTLE)
                .input(Items.ENCHANTED_BOOK)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "hidden_knowledge_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Items.SPECTRAL_ARROW)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "quick_draw_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.BONES)))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "looting_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .input(Items.BOW)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "bow_power_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.SMELTING_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Items.FURNACE)
                .input(Ingredient.of(items.getOrThrow(ItemTags.COALS)))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "smelting_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.VOIDING_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Items.NETHERRACK)
                .input(Items.COBBLED_DEEPSLATE)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "voiding_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .input(Items.BOW)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "bow_velocity_anointment");

        TabulaVitaeRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVItems.TABULA_VIAL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_COPPER)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_GOLD)))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "weapon_repair_anointment");

        // Frame Parts and Filter Recipes
        TabulaVitaeRecipeBuilder.build(NVItems.FRAME_PARTS.get(), 2)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)))
                .input(NVItems.TABULA_RASA.get())
                .syphon(1000)
                .ticks(100)
                .minimumTier(3)
                .save(output, "component_frame_parts");

        // Intermediate Cutting Fluid - tau oil + glowstone + gunpowder + sugar + sulfur + water bottle
        TabulaVitaeRecipeBuilder.build(NVItems.INTERMEDIATE_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Items.GUNPOWDER)
                .input(Items.SUGAR)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_SULFUR)))
                .input(waterBottle())
                .syphon(2000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "intermediate_cutting_fluid");

        // Advanced Cutting Fluid - tau oil + hellforged dust + glow berries + saltpeter + sulfur + water bottle
        TabulaVitaeRecipeBuilder.build(NVItems.ADVANCED_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_HELLFORGED)))
                .input(Items.GLOW_BERRIES)
                .input(NVItems.SALTPETER.get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_SULFUR)))
                .input(waterBottle())
                .syphon(4000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "advanced_cutting_fluid");

        // Anointment _L variants (extended duration - use tau oil)
        TabulaVitaeRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_L.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "fortune_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .input(NVItems.SILK_TOUCH_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.COBWEB)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "silk_touch_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_L.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.BLAZE_POWDER)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "melee_damage_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_L.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.GLISTERING_MELON_SLICE)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "holy_water_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_L.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.GLASS_BOTTLE)
                .input(Items.ENCHANTED_BOOK)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "hidden_knowledge_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_L.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Items.SPECTRAL_ARROW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "quick_draw_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_L.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.BONES)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "looting_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_L.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_power_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.SMELTING_ANOINTMENT_L.get())
                .input(NVItems.SMELTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.FURNACE)
                .input(Ingredient.of(items.getOrThrow(ItemTags.COALS)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "smelting_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.VOIDING_ANOINTMENT_L.get())
                .input(NVItems.VOIDING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.NETHERRACK)
                .input(Items.COBBLED_DEEPSLATE)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "voiding_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_L.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_velocity_anointment_l");

        TabulaVitaeRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_L.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_COPPER)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_GOLD)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "weapon_repair_anointment_l");

        // Anointment _2 variants (level 2 - use strong tau)
        TabulaVitaeRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_2.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "fortune_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_2.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Items.BLAZE_POWDER)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "melee_damage_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_2.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Items.GLISTERING_MELON_SLICE)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "holy_water_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_2.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Items.GLASS_BOTTLE)
                .input(Items.ENCHANTED_BOOK)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "hidden_knowledge_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_2.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Items.SPECTRAL_ARROW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "quick_draw_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_2.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.BONES)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "looting_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_2.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_power_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_2.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_velocity_anointment_2");

        TabulaVitaeRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_2.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_COPPER)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_GOLD)))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "weapon_repair_anointment_2");

        // Anointment _XL variants (extra long - use tau oil + hellforged sand + amethyst)
        TabulaVitaeRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_XL.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "fortune_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.SILK_TOUCH_ANOINTMENT_XL.get())
                .input(NVItems.SILK_TOUCH_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.COBWEB)
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "silk_touch_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_XL.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "melee_damage_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_XL.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.GLISTERING_MELON_SLICE)
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "holy_water_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_XL.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.ENCHANTED_BOOK)
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "hidden_knowledge_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_XL.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.SPECTRAL_ARROW)
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "quick_draw_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_XL.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "looting_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_XL.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_power_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.SMELTING_ANOINTMENT_XL.get())
                .input(NVItems.SMELTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(ItemTags.COALS)))
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "smelting_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.VOIDING_ANOINTMENT_XL.get())
                .input(NVItems.VOIDING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.COBBLED_DEEPSLATE)
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "voiding_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_XL.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_velocity_anointment_xl");

        TabulaVitaeRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_XL.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_COPPER)))
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "weapon_repair_anointment_xl");

        // Anointment _3 variants (level 3 - use strong tau + hellforged sand + glow berries)
        TabulaVitaeRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_3.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "fortune_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_3.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "melee_damage_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_3.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Items.GLISTERING_MELON_SLICE)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "holy_water_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_3.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Items.ENCHANTED_BOOK)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "hidden_knowledge_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_3.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Items.SPECTRAL_ARROW)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "quick_draw_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_3.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "looting_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_3.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_IRON)))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_power_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_3.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NUGGETS_GOLD)))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_velocity_anointment_3");

        TabulaVitaeRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_3.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_DUST.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.INGOTS_COPPER)))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "weapon_repair_anointment_3");


        // === CATALYST RECIPES ===
        // Simple Catalyst - sugar, redstone, glowstone, gunpowder, nether wart
        TabulaVitaeRecipeBuilder.build(NVItems.SIMPLE_CATALYST.get(), 2)
                .input(Items.SUGAR)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.CROPS_NETHER_WART)))
                .syphon(200)
                .ticks(100)
                .minimumTier(2)
                .save(output, "simple_catalyst");

        // Strengthened Catalyst - simple catalyst, copper dust, glow berries, cobbled deepslate
        TabulaVitaeRecipeBuilder.build(NVItems.STRENGTHENED_CATALYST.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COPPER)))
                .input(Items.GLOW_BERRIES)
                .input(Items.COBBLED_DEEPSLATE)
                .syphon(1000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "strengthened_catalyst");

        // Cycling Catalyst - simple catalyst, lapis x2, green dye, sand
        TabulaVitaeRecipeBuilder.build(NVItems.CYCLING_CATALYST.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_LAPIS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DYES_GREEN)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.SANDS)))
                .syphon(1000)
                .ticks(100)
                .minimumTier(2)
                .save(output, "cycling_catalyst");

        // Combinational Catalyst - simple catalyst, brown mushroom, red mushroom, slime ball, coal dust
        TabulaVitaeRecipeBuilder.build(NVItems.COMBINATIONAL_CATALYST.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Items.BROWN_MUSHROOM)
                .input(Items.RED_MUSHROOM)
                .input(Items.SLIME_BALL)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(2000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "combinational");

        // Mundane Lengthening Catalyst - weak tau, simple catalyst, redstone x2
        TabulaVitaeRecipeBuilder.build(NVItems.MUNDANE_LENGTHENING_CATALYST.get())
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .syphon(1000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "mundane_lengthening");

        // Mundane Power Catalyst - strong tau, simple catalyst, glowstone x2
        TabulaVitaeRecipeBuilder.build(NVItems.MUNDANE_POWER_CATALYST.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .syphon(1000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "mundane_power");

        // Average Lengthening Catalyst - weak tau, strengthened catalyst, redstone, hellforged dust
        TabulaVitaeRecipeBuilder.build(NVItems.AVERAGE_LENGTHENING_CATALYST.get())
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(NVItems.STRENGTHENED_CATALYST.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_HELLFORGED)))
                .syphon(2000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "average_lengthening");

        // Average Power Catalyst - strong tau, strengthened catalyst, glowstone, hellforged dust
        TabulaVitaeRecipeBuilder.build(NVItems.AVERAGE_POWER_CATALYST.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.STRENGTHENED_CATALYST.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_HELLFORGED)))
                .syphon(2000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "average_power");

        // === SIGIL-POWERED RECIPES ===
        // Water Bucket from Water Sigil
        TabulaVitaeRecipeBuilder.build(Items.WATER_BUCKET)
                .input(NVItems.SIGIL_WATER.get())
                .input(Items.BUCKET)
                .syphon(300)
                .ticks(60)
                .minimumTier(1)
                .save(output, "sigil_water_bucket");

        // Lava Bucket from Lava Sigil
        TabulaVitaeRecipeBuilder.build(Items.LAVA_BUCKET)
                .input(NVItems.SIGIL_LAVA.get())
                .input(Items.BUCKET)
                .syphon(1000)
                .ticks(100)
                .minimumTier(1)
                .save(output, "sigil_lava_bucket");

        // Clay from Sand with Water Sigil
        TabulaVitaeRecipeBuilder.build(Items.CLAY_BALL, 2)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.SANDS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.SANDS)))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(350)
                .ticks(100)
                .minimumTier(2)
                .save(output, "clay_from_dust_sigil");

        // Leather from Rotten Flesh with Water Sigil
        TabulaVitaeRecipeBuilder.build(Items.LEATHER, 4)
                .input(Items.ROTTEN_FLESH)
                .input(Items.ROTTEN_FLESH)
                .input(Items.ROTTEN_FLESH)
                .input(Items.ROTTEN_FLESH)
                .input(Items.FLINT)
                .input(NVItems.SIGIL_WATER.get())
                .syphon(400)
                .ticks(200)
                .minimumTier(1)
                .save(output, "leather_from_flesh_sigil");

        // Sulfur from Lava Sigil
        TabulaVitaeRecipeBuilder.build(NVItems.SULFUR.get(), 4)
                .input(NVItems.SIGIL_LAVA.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.COBBLESTONES)))
                .syphon(1200)
                .ticks(100)
                .minimumTier(0)
                .save(output, "sulfur_from_sigil");

        // Basic Cutting Fluid with Water Sigil
        TabulaVitaeRecipeBuilder.build(NVItems.BASIC_CUTTING_FLUID.get())
                .input(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Items.SUGAR)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(1100)
                .ticks(200)
                .minimumTier(1)
                .save(output, "basic_cutting_fluid_sigil");

        // Intermediate Cutting Fluid with Water Sigil
        TabulaVitaeRecipeBuilder.build(NVItems.INTERMEDIATE_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Items.SUGAR)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_SULFUR)))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(2100)
                .ticks(200)
                .minimumTier(3)
                .save(output, "intermediate_cutting_fluid_sigil");

        // Advanced Cutting Fluid with Water Sigil
        TabulaVitaeRecipeBuilder.build(NVItems.ADVANCED_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_HELLFORGED)))
                .input(Items.GLOW_BERRIES)
                .input(NVItems.SALTPETER.get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_SULFUR)))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(4100)
                .ticks(200)
                .minimumTier(4)
                .save(output, "advance_cutting_fluid_sigil");

        // === OTHER UTILITY RECIPES ===
        // Cobweb from String
        TabulaVitaeRecipeBuilder.build(Items.COBWEB)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.STRINGS)))
                .syphon(50)
                .ticks(50)
                .minimumTier(1)
                .save(output, "cobweb");

        // Explosive Cell (primitive)
        TabulaVitaeRecipeBuilder.build(NVItems.PRIMITIVE_EXPLOSIVE_CELL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "explosive_cell");

        // Hellforged Explosive Cell
        TabulaVitaeRecipeBuilder.build(NVItems.HELLFORGED_EXPLOSIVE_CELL.get())
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_SULFUR)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_REDSTONE)))
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_QUARTZ)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_HELLFORGED)))
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.DUSTS_COAL)))
                .syphon(4000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "hellforged_explosive_cell");

        // Gold Nuggets from Gilded Blackstone
        TabulaVitaeRecipeBuilder.build(Items.GOLD_NUGGET, 9)
                .input(Items.GILDED_BLACKSTONE)
                .syphon(200)
                .ticks(100)
                .minimumTier(2)
                .save(output, "gold_ore_from_gilded");

        // Grass Block from Dirt
        TabulaVitaeRecipeBuilder.build(Items.GRASS_BLOCK)
                .input(Items.DIRT)
                .input(Items.BONE_MEAL)
                .input(Items.WHEAT_SEEDS)
                .syphon(200)
                .ticks(200)
                .minimumTier(1)
                .save(output, "grass_block");

        // Nether Wart from Nether Wart Block
        TabulaVitaeRecipeBuilder.build(Items.NETHER_WART)
                .input(Items.NETHER_WART_BLOCK)
                .syphon(50)
                .ticks(40)
                .minimumTier(1)
                .save(output, "nether_wart_from_block");

        // Plant Oil from Potatoes
        TabulaVitaeRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Items.POTATO)
                .input(Items.POTATO)
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_taters");

        // Reagent Fast Miner (alt recipe with tools)
        TabulaVitaeRecipeBuilder.build(NVItems.REAGENT_FAST_MINER.get())
                .input(Items.IRON_PICKAXE)
                .input(Items.IRON_AXE)
                .input(Items.IRON_SHOVEL)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GUNPOWDERS)))
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_fastminer");

        // Weak Filling Agent
        TabulaVitaeRecipeBuilder.build(NVItems.WEAK_FILLING_AGENT.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Items.SUGAR_CANE)
                .input(Items.CRIMSON_FUNGUS)
                .input(Items.WARPED_FUNGUS)
                .syphon(2000)
                .ticks(100)
                .minimumTier(2)
                .save(output, "weak_filling");
        // Tabula Vitae recipe (crafting recipe for the table itself)
        ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, NVBlocks.TABULA_VITAE.block().get())
                .pattern("sss")
                .pattern("wbw")
                .pattern("gog")
                .define('s', NVTags.Items.VITAE_STONE)
                .define('w', ItemTags.PLANKS)
                .define('b', Tags.Items.INGOTS_IRON)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('o', NVItems.TABULA_RASA.get())
                .unlockedBy("has_blank_slate", has(NVItems.TABULA_RASA.get()))
                .save(output, rKey(NeoVitae.rl("tabula_vitae")));
    }

    private void addAthanorRecipes(RecipeOutput output) {
        // Utility recipes - hydration (all require 200mB water)
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.SANDS)))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.CLAY_BALL, 1)
                .chancedOutput(Items.CLAY_BALL, 1, 0.5)
                .save(output, rKey(NeoVitae.rl("clay_from_dust")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.TERRACOTTA))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.CLAY, 1)
                .save(output, rKey(NeoVitae.rl("clay_from_terracotta")));

        // Netherrack to sulfur
        AthanorRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Items.NETHERRACK))
                .guaranteedOutput(NVItems.SULFUR.get(), 2)
                .save(output, rKey(NeoVitae.rl("netherrack_to_sulfur")));

        // Weak blood shard from tau + life essence
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(NVBlocks.STRONG_TAU.item().get())
                .fluidInput(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 3200)
                .guaranteedOutput(NVItems.WEAK_BLOOD_SHARD.get())
                .save(output, rKey(NeoVitae.rl("weakbloodshard_tau")));

        // Dirt to Mud
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.DIRT))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MUD, 1)
                .save(output, rKey(NeoVitae.rl("mud_from_dirt")));

        // === DYE WASHING RECIPES ===
        // Wool washing (any colored wool → white wool)
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(items.getOrThrow(ItemTags.WOOL)))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WHITE_WOOL, 1)
                .save(output, rKey(NeoVitae.rl("wash_wool")));

        // Carpet washing (any colored carpet → white carpet)
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(items.getOrThrow(ItemTags.WOOL_CARPETS)))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WHITE_CARPET, 1)
                .save(output, rKey(NeoVitae.rl("wash_carpet")));

        // Bed washing (any colored bed → white bed)
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(items.getOrThrow(ItemTags.BEDS)))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WHITE_BED, 1)
                .save(output, rKey(NeoVitae.rl("wash_bed")));

        // Stained glass washing → clear glass
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_BLOCKS)))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.GLASS, 1)
                .save(output, rKey(NeoVitae.rl("wash_glass")));

        // Stained glass pane washing → clear glass pane
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.GLASS_PANES)))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.GLASS_PANE, 1)
                .save(output, rKey(NeoVitae.rl("wash_glass_pane")));

        // === CONCRETE SOLIDIFICATION RECIPES ===
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WHITE_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WHITE_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_white_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.ORANGE_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.ORANGE_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_orange_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.MAGENTA_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MAGENTA_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_magenta_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.LIGHT_BLUE_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_light_blue_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.YELLOW_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.YELLOW_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_yellow_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.LIME_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.LIME_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_lime_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.PINK_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.PINK_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_pink_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.GRAY_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.GRAY_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_gray_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.LIGHT_GRAY_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_light_gray_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CYAN_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.CYAN_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_cyan_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.PURPLE_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.PURPLE_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_purple_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.BLUE_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.BLUE_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_blue_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.BROWN_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.BROWN_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_brown_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.GREEN_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.GREEN_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_green_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.RED_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.RED_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_red_concrete")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.BLACK_CONCRETE_POWDER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.BLACK_CONCRETE, 1)
                .save(output, rKey(NeoVitae.rl("solidify_black_concrete")));

        // === MOSS SPREADING RECIPES ===
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_COBBLESTONE, 1)
                .save(output, rKey(NeoVitae.rl("mossify_cobblestone")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE_STAIRS))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_COBBLESTONE_STAIRS, 1)
                .save(output, rKey(NeoVitae.rl("mossify_cobblestone_stairs")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE_SLAB))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_COBBLESTONE_SLAB, 1)
                .save(output, rKey(NeoVitae.rl("mossify_cobblestone_slab")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE_WALL))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_COBBLESTONE_WALL, 1)
                .save(output, rKey(NeoVitae.rl("mossify_cobblestone_wall")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICKS))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_STONE_BRICKS, 1)
                .save(output, rKey(NeoVitae.rl("mossify_stone_bricks")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICK_STAIRS))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_STONE_BRICK_STAIRS, 1)
                .save(output, rKey(NeoVitae.rl("mossify_stone_brick_stairs")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICK_SLAB))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_STONE_BRICK_SLAB, 1)
                .save(output, rKey(NeoVitae.rl("mossify_stone_brick_slab")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICK_WALL))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.MOSSY_STONE_BRICK_WALL, 1)
                .save(output, rKey(NeoVitae.rl("mossify_stone_brick_wall")));

        // === COPPER OXIDATION RECIPES ===
        // Copper Block oxidation chain
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COPPER_BLOCK))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.EXPOSED_COPPER, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/copper_block_to_exposed_copper")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_COPPER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WEATHERED_COPPER, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/exposed_copper_to_weathered_copper")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_COPPER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.OXIDIZED_COPPER, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/weathered_copper_to_oxidized_copper")));

        // Cut Copper oxidation chain
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CUT_COPPER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.EXPOSED_CUT_COPPER, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/cut_copper_to_exposed_cut_copper")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_CUT_COPPER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WEATHERED_CUT_COPPER, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/exposed_cut_copper_to_weathered_cut_copper")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_CUT_COPPER))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.OXIDIZED_CUT_COPPER, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/weathered_cut_copper_to_oxidized_cut_copper")));

        // Cut Copper Stairs oxidation chain
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CUT_COPPER_STAIRS))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.EXPOSED_CUT_COPPER_STAIRS, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/cut_copper_stairs_to_exposed_cut_copper_stairs")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_CUT_COPPER_STAIRS))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WEATHERED_CUT_COPPER_STAIRS, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/exposed_cut_copper_stairs_to_weathered_cut_copper_stairs")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_CUT_COPPER_STAIRS))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.OXIDIZED_CUT_COPPER_STAIRS, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/weathered_cut_copper_stairs_to_oxidized_cut_copper_stairs")));

        // Cut Copper Slab oxidation chain
        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CUT_COPPER_SLAB))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.EXPOSED_CUT_COPPER_SLAB, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/cut_copper_slab_to_exposed_cut_copper_slab")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_CUT_COPPER_SLAB))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.WEATHERED_CUT_COPPER_SLAB, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/exposed_cut_copper_slab_to_weathered_cut_copper_slab")));

        AthanorRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_CUT_COPPER_SLAB))
                .fluidInput(Fluids.WATER, 200)
                .guaranteedOutput(Items.OXIDIZED_CUT_COPPER_SLAB, 1)
                .save(output, rKey(NeoVitae.rl("oxidize/weathered_cut_copper_slab_to_oxidized_cut_copper_slab")));

        // === RUNE REVERSION RECIPES ===
        // Speed Rune 2 -> Speed Rune + hellforged parts + netherite scrap
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_SPEED.item().get())
                .guaranteedOutput(NVBlocks.RUNE_SPEED.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/speed")));

        // Acceleration Rune 2 -> Acceleration Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_ACCELERATION.item().get())
                .guaranteedOutput(NVBlocks.RUNE_ACCELERATION.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/acceleration")));

        // Capacity Rune 2 -> Capacity Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_CAPACITY.item().get())
                .guaranteedOutput(NVBlocks.RUNE_CAPACITY.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/capacity")));

        // Augmented Capacity Rune 2 -> Augmented Capacity Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_CAPACITY_AUGMENTED.item().get())
                .guaranteedOutput(NVBlocks.RUNE_CAPACITY_AUGMENTED.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/aug_capacity")));

        // Charging Rune 2 -> Charging Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_CHARGING.item().get())
                .guaranteedOutput(NVBlocks.RUNE_CHARGING.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/charging")));

        // Dislocation Rune 2 -> Dislocation Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_DISLOCATION.item().get())
                .guaranteedOutput(NVBlocks.RUNE_DISLOCATION.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/displacement")));

        // Orb Rune 2 -> Orb Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_ORB.item().get())
                .guaranteedOutput(NVBlocks.RUNE_ORB.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/orb_rune")));

        // Sacrifice Rune 2 -> Sacrifice Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_SACRIFICE.item().get())
                .guaranteedOutput(NVBlocks.RUNE_SACRIFICE.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/sac")));

        // Self-Sacrifice Rune 2 -> Self-Sacrifice Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_SELF_SACRIFICE.item().get())
                .guaranteedOutput(NVBlocks.RUNE_SELF_SACRIFICE.item().get())
                .chancedOutput(NVItems.HELLFORGED_PARTS.get(), 1.0)
                .chancedOutput(Items.NETHERITE_SCRAP, 4, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/self_sac")));

        // Efficiency Rune 2 -> Efficiency Rune
        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_EFFICIENCY.item().get())
                .guaranteedOutput(NVBlocks.RUNE_EFFICIENCY.item().get())
                .chancedOutput(Items.NETHERITE_SCRAP, 6, 1.0)
                .save(output, rKey(NeoVitae.rl("reversion/efficiency")));

        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_WEAK.get())
                .guaranteedOutput(Items.DIAMOND)
                .save(output, rKey(NeoVitae.rl("reversion/weak_blood_orb")));

        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_APPRENTICE.get())
                .guaranteedOutput(Items.REDSTONE_BLOCK)
                .save(output, rKey(NeoVitae.rl("reversion/apprentice_blood_orb")));

        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_MAGICIAN.get())
                .guaranteedOutput(Items.GOLD_BLOCK)
                .save(output, rKey(NeoVitae.rl("reversion/magician_blood_orb")));

        AthanorRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_MASTER.get())
                .guaranteedOutput(NVItems.WEAK_BLOOD_SHARD.get())
                .save(output, rKey(NeoVitae.rl("reversion/master_blood_orb")));

        // === HELLFORGED PROCESSING ===
        // Demonite Fragments from raw hellforged (cutting fluid, spiritus-boosted)
        AthanorRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(items.getOrThrow(NVTags.Items.RAW_MATERIALS_HELLFORGED)))
                .guaranteedOutput(NVItems.DEMONITE_FRAGMENT.get(), 3)
                .spiritusBoost()
                .save(output, rKey(NeoVitae.rl("fragmentshellforged")));

        // Raw hellforged processing removed - no raw hellforged item exists

        // === OTHER UTILITY ===
        // Netherrack to Sulfur + Lava
        AthanorRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(items.getOrThrow(Tags.Items.NETHERRACKS)))
                .guaranteedOutput(NVItems.SULFUR.get())
                .fluidOutput(Fluids.LAVA, 50)
                .save(output, rKey(NeoVitae.rl("netherrack_to_sulfur")));

        // === POTION TRANSFER RECIPES ===
        // Tipped Amethyst Throwing Dagger - 8 amethyst daggers + lingering flask = 8 tipped daggers
        // The lingering flask's potion effects are transferred to the output daggers
        AthanorPotionRecipeBuilder.build(NVTags.Items.LINGERING_FLASK)
                .input(Ingredient.of(NVItems.THROWING_DAGGER_AMETHYST.get()))
                .guaranteedOutput(NVItems.THROWING_DAGGER_TIPPED.get(), 8)
                .save(output, rKey(NeoVitae.rl("tipped_throwing_dagger")));

        // === SPIRITUS INFUSION RECIPES ===
        AthanorRecipeBuilder.build(NVTags.Items.RESONATOR)
                .input(Ingredient.of(Items.DIAMOND))
                .input(NVItems.HELLFORGED_INGOT.get())
                .guaranteedOutput(Items.NETHER_STAR)
                .fluidInput(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 4000)
                .spiritusCost(SpiritusType.RAW, 50.0)
                .save(output, rKey(NeoVitae.rl("nether_star_from_diamond")));
    }

    private void addDungeonRecipes(RecipeOutput output) {
        for (DungeonVariant variant : DungeonVariant.values()) {
            String suffix = variant.getSuffix();
            ItemLike crystal = switch (variant) {
                case RAW -> NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get();
                case RUINA -> NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get();
                case NIHILUM -> NVItems.SPIRITUS_NIHILUM_CRYSTAL_ITEM.get();
                case INVICTUS -> NVItems.SPIRITUS_INVICTUS_CRYSTAL_ITEM.get();
                case VINDICTA -> NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get();
            };
            SpiritusType aspectType = switch (variant) {
                case RAW -> SpiritusType.RAW;
                case RUINA -> SpiritusType.RUINA;
                case NIHILUM -> SpiritusType.NIHILUM;
                case INVICTUS -> SpiritusType.INVICTUS;
                case VINDICTA -> SpiritusType.VINDICTA;
            };

            ItemLike stone = DungeonBlocks.DUNGEON_STONE.get(variant);
            ItemLike brick1 = DungeonBlocks.DUNGEON_BRICK_1.get(variant);
            ItemLike brick2 = DungeonBlocks.DUNGEON_BRICK_2.get(variant);
            ItemLike brick3 = DungeonBlocks.DUNGEON_BRICK_3.get(variant);
            ItemLike polished = DungeonBlocks.DUNGEON_POLISHED.get(variant);
            ItemLike tile = DungeonBlocks.DUNGEON_TILE.get(variant);
            ItemLike smallbrick = DungeonBlocks.DUNGEON_SMALLBRICK.get(variant);
            ItemLike tilespecial = DungeonBlocks.DUNGEON_TILESPECIAL.get(variant);

            ItemLike stoneSlab = DungeonBlocks.DUNGEON_STONE_SLAB.get(variant);
            ItemLike brickSlab = DungeonBlocks.DUNGEON_BRICK_SLAB.get(variant);
            ItemLike polishedSlab = DungeonBlocks.DUNGEON_POLISHED_SLAB.get(variant);
            ItemLike tileSlab = DungeonBlocks.DUNGEON_TILE_SLAB.get(variant);

            ItemLike stoneStairs = DungeonBlocks.DUNGEON_STONE_STAIRS.get(variant);
            ItemLike brickStairs = DungeonBlocks.DUNGEON_BRICK_STAIRS.get(variant);
            ItemLike polishedStairs = DungeonBlocks.DUNGEON_POLISHED_STAIRS.get(variant);

            ItemLike stoneWall = DungeonBlocks.DUNGEON_STONE_WALL.get(variant);
            ItemLike brickWall = DungeonBlocks.DUNGEON_BRICK_WALL.get(variant);
            ItemLike polishedWall = DungeonBlocks.DUNGEON_POLISHED_WALL.get(variant);
            ItemLike tileWall = DungeonBlocks.DUNGEON_TILE_WALL.get(variant);

            // === ATHANOR: smooth stone + matching Spiritus Crystal → 16 dungeon stone (bulk) ===
            AthanorRecipeBuilder.build(NVTags.Items.RESONATOR)
                    .input(Items.SMOOTH_STONE)
                    .input(crystal)
                    .guaranteedOutput(stone.asItem(), 16)
                    .save(output, rKey(NeoVitae.rl("dungeon/dungeon_stone" + suffix)));

            // === HELLFIRE FORGE: 3 smooth + 1 diamond → 4 dungeon stone, 128 min / 16 drain ===
            HellfireForgeRecipeBuilder.build(stone.asItem(), 4)
                    .requires(Items.SMOOTH_STONE, 3)
                    .requires(Items.DIAMOND)
                    .minSpiritus(128)
                    .drain(16)
                    .requiredSpiritusType(aspectType)
                    .unlockedBy("has_smooth_stone", has(Items.SMOOTH_STONE))
                    .save(output, rKey(NeoVitae.rl("dungeon/forge_dungeon_stone" + suffix)));

            // === CRAFTING: stone → brick (2x2 = 4, vanilla stone → stone bricks) ===
            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, brick1, 4)
                    .pattern("ss")
                    .pattern("ss")
                    .define('s', stone)
                    .unlockedBy("has_dungeon_stone", has(stone))
                    .save(output, rKey(NeoVitae.rl("dungeon/dungeon_brick1" + suffix)));

            // === SLAB CRAFTING: 3 blocks → 6 slabs ===
            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, stoneSlab, 6)
                    .pattern("sss")
                    .define('s', stone)
                    .unlockedBy("has_dungeon_stone", has(stone))
                    .save(output, rKey(NeoVitae.rl("dungeon/dungeon_stone_slab" + suffix)));

            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, brickSlab, 6)
                    .pattern("sss")
                    .define('s', brick1)
                    .unlockedBy("has_dungeon_brick", has(brick1))
                    .save(output, rKey(NeoVitae.rl("dungeon/dungeon_brick_slab" + suffix)));

            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, polishedSlab, 6)
                    .pattern("sss")
                    .define('s', polished)
                    .unlockedBy("has_dungeon_polished", has(polished))
                    .save(output, rKey(NeoVitae.rl("dungeon/dungeon_polished_slab" + suffix)));

            ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, tileSlab, 6)
                    .pattern("sss")
                    .define('s', tile)
                    .unlockedBy("has_dungeon_tile", has(tile))
                    .save(output, rKey(NeoVitae.rl("dungeon/dungeon_tile_slab" + suffix)));

            // === STONECUTTER: stone → block variants ===
            dungeonStonecutting(output, stone, brick1, "sc_brick1_from_stone" + suffix);
            dungeonStonecutting(output, stone, brick2, "sc_brick2_from_stone" + suffix);
            dungeonStonecutting(output, stone, brick3, "sc_brick3_from_stone" + suffix);
            dungeonStonecutting(output, stone, polished, "sc_polished_from_stone" + suffix);
            dungeonStonecutting(output, stone, tile, "sc_tile_from_stone" + suffix);
            dungeonStonecutting(output, stone, smallbrick, "sc_smallbrick_from_stone" + suffix);
            dungeonStonecutting(output, stone, tilespecial, "sc_tilespecial_from_stone" + suffix);

            // stone → shape derivatives
            dungeonStonecutting(output, stone, stoneSlab, 2, "sc_stone_slab_from_stone" + suffix);
            dungeonStonecutting(output, stone, stoneStairs, "sc_stone_stairs_from_stone" + suffix);
            dungeonStonecutting(output, stone, stoneWall, "sc_stone_wall_from_stone" + suffix);
            dungeonStonecutting(output, stone, brickSlab, 2, "sc_brick_slab_from_stone" + suffix);
            dungeonStonecutting(output, stone, brickStairs, "sc_brick_stairs_from_stone" + suffix);
            dungeonStonecutting(output, stone, brickWall, "sc_brick_wall_from_stone" + suffix);
            dungeonStonecutting(output, stone, polishedSlab, 2, "sc_polished_slab_from_stone" + suffix);
            dungeonStonecutting(output, stone, polishedStairs, "sc_polished_stairs_from_stone" + suffix);
            dungeonStonecutting(output, stone, polishedWall, "sc_polished_wall_from_stone" + suffix);
            dungeonStonecutting(output, stone, tileSlab, 2, "sc_tile_slab_from_stone" + suffix);
            dungeonStonecutting(output, stone, tileWall, "sc_tile_wall_from_stone" + suffix);

            // brick1 → shape derivatives
            dungeonStonecutting(output, brick1, brickSlab, 2, "sc_brick_slab_from_brick" + suffix);
            dungeonStonecutting(output, brick1, brickStairs, "sc_brick_stairs_from_brick" + suffix);
            dungeonStonecutting(output, brick1, brickWall, "sc_brick_wall_from_brick" + suffix);

            // polished → shape derivatives
            dungeonStonecutting(output, polished, polishedSlab, 2, "sc_polished_slab_from_polished" + suffix);
            dungeonStonecutting(output, polished, polishedStairs, "sc_polished_stairs_from_polished" + suffix);
            dungeonStonecutting(output, polished, polishedWall, "sc_polished_wall_from_polished" + suffix);

            // tile → shape derivatives
            dungeonStonecutting(output, tile, tileSlab, 2, "sc_tile_slab_from_tile" + suffix);
            dungeonStonecutting(output, tile, tileWall, "sc_tile_wall_from_tile" + suffix);
        }

        // === SPIKE TRAP: dispenser in middle, spikes above, dungeon stone everywhere else ===
        ItemLike rawDungeonStone = DungeonBlocks.DUNGEON_STONE.get(DungeonVariant.RAW);
        ShapedRecipeBuilder.shaped(items, RecipeCategory.REDSTONE, DungeonBlocks.SPIKE_TRAP.asItem())
                .pattern("SkS")
                .pattern("SdS")
                .pattern("SSS")
                .define('S', rawDungeonStone)
                .define('k', DungeonBlocks.SPIKES.asItem())
                .define('d', Items.DISPENSER)
                .unlockedBy("has_spikes", has(DungeonBlocks.SPIKES.asItem()))
                .save(output, rKey(NeoVitae.rl("dungeon/spike_trap")));
    }

    private void dungeonStonecutting(RecipeOutput output, ItemLike input, ItemLike result, String id) {
        dungeonStonecutting(output, input, result, 1, id);
    }

    private void dungeonStonecutting(RecipeOutput output, ItemLike input, ItemLike result, int count, String id) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(input), RecipeCategory.BUILDING_BLOCKS, result, count)
                .unlockedBy("has_input", has(input))
                .save(output, rKey(NeoVitae.rl("dungeon/" + id)));
    }

    /**
     * Meteor recipes for the Meteor Ritual.
     * Each recipe defines a catalyst item, EV cost, explosion radius, and layers of blocks.
     */
    private void addMeteorRecipes(RecipeOutput output) {
        String basePath = "meteor/";

        // Iron Block Meteor - basic ores with cobblestone shell
        MeteorRecipeBuilder.meteor(Ingredient.of(items.getOrThrow(Tags.Items.STORAGE_BLOCKS_IRON)), 1000000, 14)
                .addLayer(new MeteorLayer(8, 0, Blocks.IRON_ORE)
                        .addShellBlock(Blocks.COBBLESTONE)
                        .addWeightedBlock(Blocks.GOLD_ORE, 30)
                        .addWeightedBlock(Blocks.COPPER_ORE, 200)
                        .addWeightedBlock(Blocks.LAPIS_ORE, 60)
                        .addWeightedBlock(Blocks.REDSTONE_ORE, 100))
                .addLayer(new MeteorLayer(14, 100, Blocks.STONE)
                        .setMinWeight(1000)
                        .addWeightedBlock(Blocks.IRON_ORE, 400)
                        .addWeightedBlock(Blocks.GOLD_ORE, 30)
                        .addWeightedBlock(Blocks.COPPER_ORE, 200)
                        .addWeightedBlock(Blocks.LAPIS_ORE, 60)
                        .addWeightedBlock(Blocks.REDSTONE_ORE, 100))
                .save(output, rKey(NeoVitae.rl(basePath + "iron")));

        // Stone Meteor - large but mostly stone/coal
        MeteorRecipeBuilder.meteor(Ingredient.of(items.getOrThrow(NVTags.Items.VITAE_STONE)), 1000000, 30)
                .addLayer(new MeteorLayer(32, 0, Blocks.STONE)
                        .setMinWeight(400)
                        .addShellBlock(Blocks.COBBLESTONE)
                        .addWeightedBlock(Blocks.COAL_ORE, 150)
                        .addWeightedBlock(Blocks.IRON_ORE, 50))
                .save(output, rKey(NeoVitae.rl(basePath + "stone")));

        // Diamond Meteor - small but diamond-rich
        MeteorRecipeBuilder.meteor(Ingredient.of(items.getOrThrow(Tags.Items.GEMS_DIAMOND)), 1000000, 8)
                .addLayer(new MeteorLayer(4, 0, Blocks.DIAMOND_ORE))
                .addLayer(new MeteorLayer(10, 0, Blocks.COBBLESTONE)
                        .setMinWeight(1000)
                        .addWeightedBlock(Blocks.DIAMOND_ORE, 100)
                        .addWeightedBlock(Blocks.EMERALD_ORE, 75))
                .save(output, rKey(NeoVitae.rl(basePath + "diamond")));

        // Nether Meteor - nether materials including ancient debris
        MeteorRecipeBuilder.meteor(Ingredient.of(items.getOrThrow(Tags.Items.DUSTS_GLOWSTONE)), 1000000, 12)
                .addLayer(new MeteorLayer(16, 0, Blocks.NETHERRACK)
                        .setMinWeight(500)
                        .addWeightedBlock(Blocks.GLOWSTONE, 100)
                        .addWeightedBlock(Blocks.NETHER_QUARTZ_ORE, 150)
                        .addWeightedBlock(Blocks.NETHER_GOLD_ORE, 60))
                .addLayer(new MeteorLayer(10, 0, Blocks.BLACKSTONE)
                        .addShellBlock(Blocks.GLOWSTONE)
                        .addWeightedBlock(Blocks.ANCIENT_DEBRIS, 60)
                        .setMinWeight(1000)
                        .addWeightedBlock(Blocks.CHISELED_POLISHED_BLACKSTONE, 300)
                        .addWeightedBlock(Blocks.GILDED_BLACKSTONE, 200)
                        .addWeightedBlock(Blocks.POLISHED_BLACKSTONE, 400))
                .save(output, rKey(NeoVitae.rl(basePath + "nether")));
    }

    // ==================== Flask Recipes ====================

    private void addFlaskRecipes(RecipeOutput output) {
        // ==================== Effect Recipes ====================
        // Basic effects - add a new effect to a flask

        // Movement effects
        FlaskRecipeBuilder.effect(MobEffects.SPEED, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.SUGAR)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("speed_boost")));

        FlaskRecipeBuilder.effect(MobEffects.JUMP_BOOST, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.RABBIT_FOOT)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("jump_boost")));

        FlaskRecipeBuilder.effect(MobEffects.SLOW_FALLING, 1800)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.PHANTOM_MEMBRANE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("slow_fall")));

        // Resistance effects
        FlaskRecipeBuilder.effect(MobEffects.FIRE_RESISTANCE, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.MAGMA_CREAM)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("fire_resist")));

        FlaskRecipeBuilder.effect(MobEffects.WATER_BREATHING, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.PUFFERFISH)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("water_breathing")));

        // Combat effects
        FlaskRecipeBuilder.effect(MobEffects.STRENGTH, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.BLAZE_POWDER)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("strength")));

        FlaskRecipeBuilder.effect(MobEffects.REGENERATION, 900)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GHAST_TEAR)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("regen")));

        FlaskRecipeBuilder.effect(MobEffects.INSTANT_HEALTH, 0)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GLISTERING_MELON_SLICE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("health")));

        // Debuff effects
        FlaskRecipeBuilder.effect(MobEffects.WEAKNESS, 1800)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("weakness")));

        FlaskRecipeBuilder.effect(MobEffects.POISON, 900)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("poison")));

        // Vision effects
        FlaskRecipeBuilder.effect(MobEffects.NIGHT_VISION, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GOLDEN_CARROT)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("night_vision")));

        // NeoVitae custom effects
        FlaskRecipeBuilder.effect(NVMobEffects.PASSIVITY, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.HONEYCOMB)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("passivity")));

        FlaskRecipeBuilder.effect(NVMobEffects.BOUNCE, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.SLIME_BALL)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("bounce")));

        FlaskRecipeBuilder.effect(NVMobEffects.HARD_CLOAK, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.OBSIDIAN)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("hard_cloak")));

        // ==================== Effect Transform Recipes ====================
        // Transform one effect into another using fermented spider eye or other items

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.SPEED)
                .outputEffect(MobEffects.SLOWNESS, 1800)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("speed_to_slow")));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.JUMP_BOOST)
                .outputEffect(MobEffects.SLOWNESS, 1800)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("jump_to_slow")));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.INSTANT_HEALTH)
                .outputEffect(MobEffects.INSTANT_DAMAGE, 0)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("health_to_harm")));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.POISON)
                .outputEffect(MobEffects.INSTANT_DAMAGE, 0)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("poison_to_harm")));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.NIGHT_VISION)
                .outputEffect(MobEffects.INVISIBILITY, 3600)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("night_to_invis")));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.SLOW_FALLING)
                .outputEffect(MobEffects.LEVITATION, 3600)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("fall_to_levitation")));

        // Night vision to spectral sight
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.NIGHT_VISION)
                .outputEffect(NVMobEffects.SPECTRAL_SIGHT, 3600)
                .addIngredient(Items.GLOWSTONE_DUST)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("night_to_spectral")));

        // Jump boost to grounded
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.JUMP_BOOST)
                .outputEffect(NVMobEffects.GROUNDED, 1800)
                .addIngredient(Items.COBWEB)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("jump_to_grounded")));

        // Grounded + slow falling = gravity (requires combinational catalyst)
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.GROUNDED)
                .inputEffect(MobEffects.SLOW_FALLING)
                .outputEffect(NVMobEffects.GRAVITY, 1800)
                .addIngredient(NVItems.COMBINATIONAL_CATALYST.get())
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("gravity")));

        // Gravity to suspended
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.GRAVITY)
                .outputEffect(NVMobEffects.SUSPENDED, 1800)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("gravity_to_suspended")));

        // Suspended + levitation = flight (requires combinational catalyst)
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.SUSPENDED)
                .inputEffect(MobEffects.LEVITATION)
                .outputEffect(NVMobEffects.FLIGHT, 3600)
                .addIngredient(NVItems.COMBINATIONAL_CATALYST.get())
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("suspended_to_flight")));

        // Gravity + heal = heavy heart (requires combinational catalyst)
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.GRAVITY)
                .inputEffect(MobEffects.INSTANT_HEALTH)
                .outputEffect(NVMobEffects.HEAVY_HEART, 1800)
                .addIngredient(NVItems.COMBINATIONAL_CATALYST.get())
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("gravity_to_heart")));

        // Hard cloak to obsidian cloak
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.HARD_CLOAK)
                .outputEffect(NVMobEffects.OBSIDIAN_CLOAK, 3600)
                .addIngredient(Tags.Items.GEMS_DIAMOND)
                .addIngredient(Items.CRYING_OBSIDIAN)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("hard_to_obsidian")));

        // ==================== Potency Recipes ====================
        // Increase the amplifier (potency) of effects

        addPotencyRecipes(output, MobEffects.SPEED, "speed_boost");
        addPotencyRecipes(output, MobEffects.JUMP_BOOST, "jump_boost");
        addPotencyRecipes(output, MobEffects.STRENGTH, "strength");
        addPotencyRecipes(output, MobEffects.WEAKNESS, "weakness");
        addPotencyRecipes(output, MobEffects.POISON, "poison");
        addPotencyRecipes(output, MobEffects.REGENERATION, "regen");
        addPotencyRecipes(output, MobEffects.LEVITATION, "levitation");
        addPotencyRecipes(output, MobEffects.SLOWNESS, "slowness");
        addPotencyRecipes(output, NVMobEffects.HARD_CLOAK, "hard_cloak");
        addPotencyRecipes(output, NVMobEffects.HEAVY_HEART, "heavy_heart");
        addPotencyRecipes(output, NVMobEffects.OBSIDIAN_CLOAK, "obsidian_cloak");
        addPotencyRecipes(output, NVMobEffects.GRAVITY, "gravity");
        addPotencyRecipes(output, NVMobEffects.FLIGHT, "flight");
        addPotencyRecipes(output, NVMobEffects.SPECTRAL_SIGHT, "spectral_sight");

        // Instant effects (health/harm) have different modifiers
        FlaskRecipeBuilder.potency(MobEffects.INSTANT_HEALTH, 1, 0.5)
                .addIngredient(NVItems.MUNDANE_POWER_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("potency_health")));

        FlaskRecipeBuilder.potency(MobEffects.INSTANT_DAMAGE, 1, 0.5)
                .addIngredient(NVItems.MUNDANE_POWER_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("potency_harm")));

        FlaskRecipeBuilder.potency(MobEffects.INSTANT_HEALTH, 2, 0.25)
                .addIngredient(NVItems.AVERAGE_POWER_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(3)
                .save(output, rKey(NeoVitae.rl("potency_average_health")));

        FlaskRecipeBuilder.potency(MobEffects.INSTANT_DAMAGE, 2, 0.25)
                .addIngredient(NVItems.AVERAGE_POWER_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(3)
                .save(output, rKey(NeoVitae.rl("potency_average_harm")));

        // ==================== Length Recipes ====================
        // Increase the duration modifier of effects (for effects that don't have potency tiers)

        addLengthRecipes(output, MobEffects.SPEED, "speed_boost");
        addLengthRecipes(output, MobEffects.JUMP_BOOST, "jump_boost");
        addLengthRecipes(output, MobEffects.STRENGTH, "strength");
        addLengthRecipes(output, MobEffects.WEAKNESS, "weakness");
        addLengthRecipes(output, MobEffects.POISON, "poison");
        addLengthRecipes(output, MobEffects.REGENERATION, "regen");
        addLengthRecipes(output, MobEffects.LEVITATION, "levitation");
        addLengthRecipes(output, MobEffects.SLOWNESS, "slowness");
        addLengthRecipes(output, NVMobEffects.HARD_CLOAK, "hard_cloak");
        addLengthRecipes(output, NVMobEffects.HEAVY_HEART, "heavy_heart");
        addLengthRecipes(output, NVMobEffects.OBSIDIAN_CLOAK, "obsidian_cloak");
        addLengthRecipes(output, NVMobEffects.GRAVITY, "gravity");
        addLengthRecipes(output, NVMobEffects.FLIGHT, "flight");
        addLengthRecipes(output, NVMobEffects.SPECTRAL_SIGHT, "spectral_sight");

        // Effects that only have length modifiers (no potency)
        addLengthOnlyRecipes(output, MobEffects.FIRE_RESISTANCE, "fire_resist");
        addLengthOnlyRecipes(output, MobEffects.WATER_BREATHING, "water_breathing");
        addLengthOnlyRecipes(output, MobEffects.NIGHT_VISION, "night_vision");
        addLengthOnlyRecipes(output, MobEffects.INVISIBILITY, "invisibility");
        addLengthOnlyRecipes(output, MobEffects.SLOW_FALLING, "slow_fall");
        addLengthOnlyRecipes(output, NVMobEffects.PASSIVITY, "passivity");
        addLengthOnlyRecipes(output, NVMobEffects.BOUNCE, "bounce");
        addLengthOnlyRecipes(output, NVMobEffects.GROUNDED, "grounded");
        addLengthOnlyRecipes(output, NVMobEffects.SUSPENDED, "suspended");

        // ==================== Fill Recipes ====================
        // Refill depleted flasks

        FlaskRecipeBuilder.fill(1)
                .addIngredient(NVItems.WEAK_FILLING_AGENT.get())
                .syphon(1000).ticks(200).minimumTier(0)
                .save(output, rKey(NeoVitae.rl("fill_weak")));

        FlaskRecipeBuilder.fill(3)
                .addIngredient(NVItems.STANDARD_FILLING_AGENT.get())
                .syphon(3000).ticks(200).minimumTier(0)
                .save(output, rKey(NeoVitae.rl("fill_standard")));

        // ==================== Flask Transform Recipes ====================
        // Transform flask type (regular -> splash -> lingering)

        FlaskRecipeBuilder.itemTransform(NVItems.ALCHEMY_FLASK_THROWABLE.get())
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GUNPOWDER)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("flask_splash")));

        FlaskRecipeBuilder.itemTransform(NVItems.ALCHEMY_FLASK_LINGERING.get())
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.DRAGON_BREATH)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("flask_lingering")));

        // ==================== Cycle Recipe ====================
        // Cycle the order of effects in the flask

        FlaskRecipeBuilder.cycle(1)
                .addIngredient(NVItems.CYCLING_CATALYST.get())
                .syphon(500).ticks(50).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("cycle_basic")));
    }

    /**
     * Helper method to add potency recipes for effects that can be amplified
     */
    private void addPotencyRecipes(RecipeOutput output, Holder<MobEffect> effect, String name) {
        // Mundane potency (amplifier +1, duration modifier 0.5)
        FlaskRecipeBuilder.potency(effect, 1, 0.5)
                .addIngredient(NVItems.MUNDANE_POWER_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("potency_" + name)));

        // Average potency (amplifier +2, duration modifier 0.25)
        FlaskRecipeBuilder.potency(effect, 2, 0.25)
                .addIngredient(NVItems.AVERAGE_POWER_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(4)
                .save(output, rKey(NeoVitae.rl("potency_average_" + name)));
    }

    /**
     * Helper method to add length recipes for effects that can have their duration extended
     */
    private void addLengthRecipes(RecipeOutput output, Holder<MobEffect> effect, String name) {
        // Mundane length (duration modifier 2.6667x)
        FlaskRecipeBuilder.length(effect, 2.6667)
                .addIngredient(NVItems.MUNDANE_LENGTHENING_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("length_" + name)));

        // Average length (duration modifier 7.1112x)
        FlaskRecipeBuilder.length(effect, 7.1112)
                .addIngredient(NVItems.AVERAGE_LENGTHENING_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(4)
                .save(output, rKey(NeoVitae.rl("length_average_" + name)));
    }

    /**
     * Helper method to add length-only recipes for effects that only have duration modifiers
     */
    private void addLengthOnlyRecipes(RecipeOutput output, Holder<MobEffect> effect, String name) {
        // Mundane length (duration modifier 2.6667x)
        FlaskRecipeBuilder.length(effect, 2.6667)
                .addIngredient(NVItems.MUNDANE_LENGTHENING_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, rKey(NeoVitae.rl("length_" + name)));

        // Average length (duration modifier 7.1112x)
        FlaskRecipeBuilder.length(effect, 7.1112)
                .addIngredient(NVItems.AVERAGE_LENGTHENING_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(4)
                .save(output, rKey(NeoVitae.rl("length_average_" + name)));
    }

    // ==================== Sentient Downgrade Recipes ====================

    private void addSentientDowngradeRecipes(RecipeOutput output) {
        String basePath = "downgrade/";

        // Battle Hungry - increases hunger drain
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.ROTTEN_FLESH),
                NeoVitae.rl("battle_hungry"))
                .save(output, rKey(NeoVitae.rl(basePath + "battle_hungry")));

        // Melee Decrease - reduces melee damage
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.STONE_SWORD),
                NeoVitae.rl("melee_decrease"))
                .save(output, rKey(NeoVitae.rl(basePath + "melee_decrease")));

        // Quenched - reduces saturation
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.GLASS_BOTTLE),
                NeoVitae.rl("quenched"))
                .save(output, rKey(NeoVitae.rl(basePath + "quenched")));

        // Storm Trooper - reduces arrow accuracy
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.ARROW),
                NeoVitae.rl("storm_trooper"))
                .save(output, rKey(NeoVitae.rl(basePath + "storm_trooper")));

        // Dig Slowdown - reduces mining speed
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.STONE_PICKAXE),
                NeoVitae.rl("dig_slowdown"))
                .save(output, rKey(NeoVitae.rl(basePath + "dig_slowdown")));

        // Slow Heal - reduces healing effectiveness
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.GHAST_TEAR),
                NeoVitae.rl("slow_heal"))
                .save(output, rKey(NeoVitae.rl(basePath + "slow_heal")));

        // Swim Decrease - reduces swim speed
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.WATER_BUCKET),
                NeoVitae.rl("swim_decrease"))
                .save(output, rKey(NeoVitae.rl(basePath + "swim_decrease")));

        // Speed Decrease - reduces movement speed
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.SOUL_SAND),
                NeoVitae.rl("speed_decrease"))
                .save(output, rKey(NeoVitae.rl(basePath + "speed_decrease")));

        // Crippled Arm - reduces blocking effectiveness
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.SHIELD),
                NeoVitae.rl("crippled_arm"))
                .save(output, rKey(NeoVitae.rl(basePath + "crippled_arm")));

        // Demon-drop based downgrades
        // Poisoned Blood - periodic poison ticks (from venomgland sac)
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(NVItems.VENOMGLAND_SAC.get()),
                NeoVitae.rl("poisoned_blood"))
                .save(output, rKey(NeoVitae.rl(basePath + "poisoned_blood")));

        // Hollow Hunger - increased hunger drain (from hollow gut, thematic upgrade over rotten flesh)
        SentientDowngradeRecipeBuilder.downgrade(
                Ingredient.of(NVItems.HOLLOW_GUT.get()),
                NeoVitae.rl("hollow_hunger"))
                .save(output, rKey(NeoVitae.rl(basePath + "hollow_hunger")));
    }

}
