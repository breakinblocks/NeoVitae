package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.datagen.builder.AlchemyArrayEffectRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.AlchemyArrayRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.AlchemyTableRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.ARCRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.ARCPotionRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.AltarRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.SoulForgeRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.TieredRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.MeteorRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.FlaskRecipeBuilder;
import com.breakinblocks.neovitae.datagen.builder.recipe.LivingDowngradeRecipeBuilder;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class NVRecipeProvider extends RecipeProvider {

    public NVRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    /**
     * Creates an ingredient that matches a water bottle (potion with water contents)
     */
    private static Ingredient waterBottle() {
        ItemStack waterBottle = new ItemStack(Items.POTION);
        waterBottle.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
        return DataComponentIngredient.of(false, waterBottle);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        addVanillaCraftingRecipes(output);
        addTieredRecipes(output);
        addBloodAltarRecipes(output);
        addSoulForgeRecipes(output);
        addAlchemyArrayRecipes(output);
        addAlchemyTableRecipes(output);
        addARCRecipes(output);
        addMeteorRecipes(output);
        addFlaskRecipes(output);
        addLivingDowngradeRecipes(output);
    }

    private void addVanillaCraftingRecipes(RecipeOutput output) {
        // Sacrificial Dagger - diagonal dagger shape
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, NVItems.SACRIFICIAL_DAGGER.get())
                .pattern("ggg")
                .pattern(" Gg")
                .pattern("i g")
                .define('g', Tags.Items.GLASS_BLOCKS)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                .save(output);

        // Blood Altar - stone frame with furnace, gold ingots on bottom
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, NVBlocks.BLOOD_ALTAR.block().get())
                .pattern("s s")
                .pattern("sfs")
                .pattern("ggg")
                .define('s', Tags.Items.STONES)
                .define('f', Items.FURNACE)
                .define('g', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(output);

        // Hellfire Forge (Soul Forge)
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, NVBlocks.HELLFIRE_FORGE.block().get())
                .pattern("i i")
                .pattern("sSs")
                .pattern("sos")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('s', Tags.Items.STONES)
                .define('S', NVItems.SLATE_BLANK.get())
                .define('o', Tags.Items.STORAGE_BLOCKS_IRON)
                .unlockedBy("has_blank_slate", has(NVItems.SLATE_BLANK.get()))
                .save(output);

        // Bloodstone
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.BLOODSTONE.block().get(), 8)
                .pattern("sss")
                .pattern("sbs")
                .pattern("sss")
                .define('s', Items.STONE)
                .define('b', NVFluids.LIFE_ESSENCE_BUCKET.get())
                .unlockedBy("has_life_essence", has(NVFluids.LIFE_ESSENCE_BUCKET.get()))
                .save(output);

        // Bloodstone Brick (from stone + weak blood shard)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.BLOODSTONE_BRICK.block().get(), 4)
                .pattern("ss")
                .pattern("sb")
                .define('s', Tags.Items.STONES)
                .define('b', NVItems.WEAK_BLOOD_SHARD.get())
                .unlockedBy("has_weak_blood_shard", has(NVItems.WEAK_BLOOD_SHARD.get()))
                .save(output);

        // Hellforged Block (storage block)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.HELLFORGED_BLOCK.block().get())
                .pattern("iii")
                .pattern("iii")
                .pattern("iii")
                .define('i', NVTags.Items.INGOTS_HELLFORGED)
                .unlockedBy(getHasName(NVItems.HELLFORGED_INGOT.get()), has(NVTags.Items.INGOTS_HELLFORGED))
                .save(output, NeoVitae.rl("hellforged_block_from_ingots"));

        // Hellforged Ingot from Block
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NVItems.HELLFORGED_INGOT.get(), 9)
                .requires(NVBlocks.HELLFORGED_BLOCK.block().get())
                .unlockedBy(getHasName(NVBlocks.HELLFORGED_BLOCK.block().get()), has(NVBlocks.HELLFORGED_BLOCK.block().get()))
                .save(output, NeoVitae.rl("hellforged_ingot_from_block"));

        // Synthetic Point - iron nuggets corners, meat edges, redstone center
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NVItems.SYNTHETIC_POINT.get(), 2)
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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_BLANK.block().get())
                .pattern("asa")
                .pattern("aoa")
                .pattern("aaa")
                .define('a', Tags.Items.STONES)
                .define('s', NVItems.SLATE_BLANK.get())
                .define('o', OrbTierIngredient.of(1))
                .unlockedBy("has_blank_slate", has(NVItems.SLATE_BLANK.get()))
                .save(output);

        // Speed Rune - stone(a), blank_slate(b), sugar(c), blank_rune(d) - NO orb
        // Pattern: aba / cdc / aba
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_SPEED.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aba")
                .define('a', Tags.Items.STONES)
                .define('b', NVItems.SLATE_BLANK.get())
                .define('c', Items.SUGAR)
                .define('d', NVBlocks.RUNE_BLANK.block().get())
                .unlockedBy("has_blank_rune", has(NVBlocks.RUNE_BLANK.block().get()))
                .save(output);

        // Sacrifice Rune - reinforced_slate(b), gold(c), blank_rune(d), orb_tier_2(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_SACRIFICE.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', Tags.Items.STONES)
                .define('b', NVItems.SLATE_REINFORCED.get())
                .define('c', Tags.Items.INGOTS_GOLD)
                .define('d', NVBlocks.RUNE_BLANK.block().get())
                .define('e', OrbTierIngredient.of(2))
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output);

        // Self-Sacrifice Rune - reinforced_slate(b), glowstone_dust(c), blank_rune(d), orb_tier_2(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_SELF_SACRIFICE.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', Tags.Items.STONES)
                .define('b', NVItems.SLATE_REINFORCED.get())
                .define('c', Items.GLOWSTONE_DUST)
                .define('d', NVBlocks.RUNE_BLANK.block().get())
                .define('e', OrbTierIngredient.of(2))
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output);

        // Capacity Rune - bucket(a), imbued_slate(d), blank_rune(c) - NO orb
        // Pattern: aba / bcb / ada
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_CAPACITY.block().get())
                .pattern("aba")
                .pattern("bcb")
                .pattern("ada")
                .define('a', Tags.Items.STONES)
                .define('b', Items.BUCKET)
                .define('c', NVBlocks.RUNE_BLANK.block().get())
                .define('d', NVItems.SLATE_IMBUED.get())
                .unlockedBy("has_imbued_slate", has(NVItems.SLATE_IMBUED.get()))
                .save(output);

        // Dislocation Rune - water_bucket(b), imbued_slate(d), blank_rune(c) - NO orb
        // Pattern: aba / bcb / ada
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_DISLOCATION.block().get())
                .pattern("aba")
                .pattern("bcb")
                .pattern("ada")
                .define('a', Tags.Items.STONES)
                .define('b', Items.WATER_BUCKET)
                .define('c', NVBlocks.RUNE_BLANK.block().get())
                .define('d', NVItems.SLATE_IMBUED.get())
                .unlockedBy("has_imbued_slate", has(NVItems.SLATE_IMBUED.get()))
                .save(output);

        // Charging Rune - special pattern with demonic_slate, orb_tier_4
        // Pattern: RsR / GrG / ReR (R=redstone, s=demonic_slate, G=glowstone, r=blank_rune, e=orb_tier_4)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_CHARGING.block().get())
                .pattern("RsR")
                .pattern("GrG")
                .pattern("ReR")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('s', NVItems.SLATE_DEMONIC.get())
                .define('G', Tags.Items.DUSTS_GLOWSTONE)
                .define('r', NVBlocks.RUNE_BLANK.block().get())
                .define('e', OrbTierIngredient.of(4))
                .unlockedBy("has_demonic_slate", has(NVItems.SLATE_DEMONIC.get()))
                .save(output);

        // Acceleration Rune - bucket(a), demonic_slate(b), gold(c), speed_rune(d), orb_tier_4(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_ACCELERATION.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', Items.BUCKET)
                .define('b', NVItems.SLATE_DEMONIC.get())
                .define('c', Tags.Items.INGOTS_GOLD)
                .define('d', NVBlocks.RUNE_SPEED.block().get())
                .define('e', OrbTierIngredient.of(4))
                .unlockedBy("has_speed_rune", has(NVBlocks.RUNE_SPEED.block().get()))
                .save(output);

        // Augmented Capacity Rune - obsidian(a), demonic_slate(b), bucket(c), capacity_rune(d), orb_tier_4(e)
        // Pattern: aba / cdc / aea
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_CAPACITY_AUGMENTED.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aea")
                .define('a', Tags.Items.OBSIDIANS)
                .define('b', NVItems.SLATE_DEMONIC.get())
                .define('c', Items.BUCKET)
                .define('d', NVBlocks.RUNE_CAPACITY.block().get())
                .define('e', OrbTierIngredient.of(4))
                .unlockedBy("has_capacity_rune", has(NVBlocks.RUNE_CAPACITY.block().get()))
                .save(output);

        // Orb Rune - orb_tier_1(b), blank_rune(c), orb_tier_4(d) - uses two orb tiers
        // Pattern: aba / cdc / aba
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.RUNE_ORB.block().get())
                .pattern("aba")
                .pattern("cdc")
                .pattern("aba")
                .define('a', Tags.Items.STONES)
                .define('b', OrbTierIngredient.of(1))
                .define('c', NVBlocks.RUNE_BLANK.block().get())
                .define('d', OrbTierIngredient.of(4))
                .unlockedBy("has_blank_rune", has(NVBlocks.RUNE_BLANK.block().get()))
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

        // Crystal Cluster - NO CRAFTING RECIPE (obtained from rituals/other means)

        // Crystal Cluster Brick (from crystal cluster)
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.CRYSTAL_CLUSTER_BRICK.block().get(), 4)
                .pattern("cc")
                .pattern("cc")
                .define('c', NVBlocks.CRYSTAL_CLUSTER.block().get())
                .unlockedBy("has_crystal_cluster", has(NVBlocks.CRYSTAL_CLUSTER.block().get()))
                .save(output);

        // Teleposer block
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, NVBlocks.TELEPOSER.block().get())
                .pattern("ggg")
                .pattern("ete")
                .pattern("ggg")
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('e', Tags.Items.ENDER_PEARLS)
                .define('t', NVItems.TELEPOSER_FOCUS.get())
                .unlockedBy("has_teleposer_focus", has(NVItems.TELEPOSER_FOCUS.get()))
                .save(output);

        // Reinforced Teleposer Focus
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NVItems.TELEPOSER_FOCUS_REINFORCED.get())
                .requires(NVItems.TELEPOSER_FOCUS_ENHANCED.get())
                .requires(NVItems.WEAK_BLOOD_SHARD.get())
                .unlockedBy("has_enhanced_focus", has(NVItems.TELEPOSER_FOCUS_ENHANCED.get()))
                .save(output);

        // Lava Crystal - tier 1+ orb center
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NVItems.LAVA_CRYSTAL.get())
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
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.BLANK_RITUAL_STONE.block().get(), 4)
                .pattern("oso")
                .pattern("scs")
                .pattern("oso")
                .define('o', Tags.Items.OBSIDIANS)
                .define('s', NVItems.SLATE_REINFORCED.get())
                .define('c', OrbTierIngredient.of(2))
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output, NeoVitae.rl("ritual_stone_blank"));

        // Master Ritual Stone - obsidian around, ritual stones corners, tier 3+ orb center
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.MASTER_RITUAL_STONE.block().get())
                .pattern("oso")
                .pattern("scs")
                .pattern("oso")
                .define('o', Tags.Items.OBSIDIANS)
                .define('s', NVBlocks.BLANK_RITUAL_STONE.block().get())
                .define('c', OrbTierIngredient.of(3))
                .unlockedBy("has_ritual_stone", has(NVBlocks.BLANK_RITUAL_STONE.block().get()))
                .save(output, NeoVitae.rl("ritual_stone_master"));

        // Imperfect Ritual Stone - obsidian corners, stone sides, weak blood orb center
        // Simple recipe for early-game weak rituals
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, NVBlocks.IMPERFECT_RITUAL_STONE.block().get())
                .pattern("oso")
                .pattern("scs")
                .pattern("oso")
                .define('o', Tags.Items.OBSIDIANS)
                .define('s', Tags.Items.STONES)
                .define('c', NVItems.ORB_WEAK.get())
                .unlockedBy("has_weak_orb", has(NVItems.ORB_WEAK.get()))
                .save(output, NeoVitae.rl("imperfect_ritual_stone"));
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
                .save(output, NeoVitae.rl("blood_tank_upgrade"));
    }

    private void addBloodAltarRecipes(RecipeOutput output) {
        // Blood Orb progression - each orb is made from different materials, NOT from previous orb
        AltarRecipeBuilder.build(NVItems.ORB_WEAK.get())
                .from(Tags.Items.GEMS_DIAMOND)
                .minTier(0)
                .bloodNeeded(2000)
                .consumption(5)
                .drain(1)
                .unlockedBy("has_altar", has(NVBlocks.BLOOD_ALTAR.block().get()))
                .save(output, NeoVitae.rl("weak_blood_orb"));

        AltarRecipeBuilder.build(NVItems.ORB_APPRENTICE.get())
                .from(Tags.Items.STORAGE_BLOCKS_REDSTONE)  // Redstone Block
                .minTier(1)
                .bloodNeeded(5000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_weak_orb", has(NVItems.ORB_WEAK.get()))
                .save(output, NeoVitae.rl("apprentice_blood_orb"));

        AltarRecipeBuilder.build(NVItems.ORB_MAGICIAN.get())
                .from(Tags.Items.STORAGE_BLOCKS_GOLD)  // Gold Block
                .minTier(2)
                .bloodNeeded(25000)
                .consumption(20)
                .drain(20)
                .unlockedBy("has_apprentice_orb", has(NVItems.ORB_APPRENTICE.get()))
                .save(output, NeoVitae.rl("magician_blood_orb"));

        AltarRecipeBuilder.build(NVItems.ORB_MASTER.get())
                .from(NVItems.WEAK_BLOOD_SHARD.get())  // Weak Blood Shard
                .minTier(3)
                .bloodNeeded(40000)
                .consumption(30)
                .drain(50)
                .unlockedBy("has_magician_orb", has(NVItems.ORB_MAGICIAN.get()))
                .save(output, NeoVitae.rl("master_blood_orb"));

        AltarRecipeBuilder.build(NVItems.ORB_ARCHMAGE.get())
                .from(NVBlocks.HELLFORGED_BLOCK.block().get())  // Hellforged Block
                .minTier(4)
                .bloodNeeded(80000)
                .consumption(50)
                .drain(100)
                .unlockedBy("has_master_orb", has(NVItems.ORB_MASTER.get()))
                .save(output, NeoVitae.rl("archmage_blood_orb"));

        // Note: Transcendent orb doesn't exist in 1.20.1 - removed

        // Slates
        AltarRecipeBuilder.build(NVItems.SLATE_BLANK.get())
                .from(Tags.Items.STONES)
                .minTier(0)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_altar", has(NVBlocks.BLOOD_ALTAR.block().get()))
                .save(output, NeoVitae.rl("blank_slate"));

        AltarRecipeBuilder.build(NVItems.SLATE_REINFORCED.get())
                .from(NVItems.SLATE_BLANK.get())
                .minTier(1)
                .bloodNeeded(2000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_blank_slate", has(NVItems.SLATE_BLANK.get()))
                .save(output, NeoVitae.rl("reinforced_slate"));

        AltarRecipeBuilder.build(NVItems.SLATE_IMBUED.get())
                .from(NVItems.SLATE_REINFORCED.get())
                .minTier(2)
                .bloodNeeded(5000)
                .consumption(15)
                .drain(10)
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output, NeoVitae.rl("imbued_slate"));

        AltarRecipeBuilder.build(NVItems.SLATE_DEMONIC.get())
                .from(NVItems.SLATE_IMBUED.get())
                .minTier(3)
                .bloodNeeded(15000)
                .consumption(20)
                .drain(20)
                .unlockedBy("has_imbued_slate", has(NVItems.SLATE_IMBUED.get()))
                .save(output, NeoVitae.rl("demonic_slate"));

        AltarRecipeBuilder.build(NVItems.SLATE_ETHEREAL.get())
                .from(NVItems.SLATE_DEMONIC.get())
                .minTier(4)
                .bloodNeeded(30000)
                .consumption(40)
                .drain(100)
                .unlockedBy("has_demonic_slate", has(NVItems.SLATE_DEMONIC.get()))
                .save(output, NeoVitae.rl("ethereal_slate"));

        // Additional Blood Altar recipes
        AltarRecipeBuilder.build(NVItems.SOUL_SNARE.get())
                .from(Tags.Items.STRINGS)
                .minTier(0)
                .bloodNeeded(500)
                .consumption(5)
                .drain(1)
                .unlockedBy("has_altar", has(NVBlocks.BLOOD_ALTAR.block().get()))
                .save(output, NeoVitae.rl("soul_snare"));

        AltarRecipeBuilder.build(NVItems.DAGGER_OF_SACRIFICE.get())
                .from(Items.IRON_SWORD)
                .minTier(1)
                .bloodNeeded(3000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_altar", has(NVBlocks.BLOOD_ALTAR.block().get()))
                .save(output, NeoVitae.rl("dagger_of_sacrifice"));

        AltarRecipeBuilder.build(NVFluids.LIFE_ESSENCE_BUCKET.get())
                .from(Items.BUCKET)
                .minTier(0)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(0)
                .unlockedBy("has_altar", has(NVBlocks.BLOOD_ALTAR.block().get()))
                .save(output, NeoVitae.rl("bucket_life"));

        // Teleposer Focus - ender pearl on tier 3 altar
        AltarRecipeBuilder.build(NVItems.TELEPOSER_FOCUS.get())
                .from(Tags.Items.ENDER_PEARLS)
                .minTier(3)
                .bloodNeeded(2000)
                .consumption(10)
                .drain(10)
                .unlockedBy("has_demonic_slate", has(NVItems.SLATE_DEMONIC.get()))
                .save(output, NeoVitae.rl("teleposer_focus"));

        // Enhanced Teleposer Focus - from teleposer focus on tier 3 altar
        AltarRecipeBuilder.build(NVItems.TELEPOSER_FOCUS_ENHANCED.get())
                .from(NVItems.TELEPOSER_FOCUS.get())
                .minTier(3)
                .bloodNeeded(10000)
                .consumption(20)
                .drain(10)
                .unlockedBy("has_teleposer_focus", has(NVItems.TELEPOSER_FOCUS.get()))
                .save(output, NeoVitae.rl("enhanced_teleposer_focus"));

        // Inscription Tools
        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_AIR.get())
                .from(Items.GHAST_TEAR)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output, NeoVitae.rl("air_tool"));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_FIRE.get())
                .from(Items.MAGMA_CREAM)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output, NeoVitae.rl("fire_tool"));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_WATER.get())
                .from(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output, NeoVitae.rl("water_tool"));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_EARTH.get())
                .from(Tags.Items.OBSIDIANS)
                .minTier(2)
                .bloodNeeded(1000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_reinforced_slate", has(NVItems.SLATE_REINFORCED.get()))
                .save(output, NeoVitae.rl("earth_tool"));

        AltarRecipeBuilder.build(NVItems.INSCRIPTION_TOOL_DUSK.get())
                .from(Tags.Items.STORAGE_BLOCKS_COAL)
                .minTier(3)
                .bloodNeeded(2000)
                .consumption(20)
                .drain(10)
                .unlockedBy("has_demonic_slate", has(NVItems.SLATE_DEMONIC.get()))
                .save(output, NeoVitae.rl("dusk_tool"));

        // Alchemy Flask - glass bottle on tier 1 altar
        AltarRecipeBuilder.build(NVItems.ALCHEMY_FLASK.get())
                .from(Items.GLASS_BOTTLE)
                .minTier(1)
                .bloodNeeded(4000)
                .consumption(5)
                .drain(5)
                .unlockedBy("has_apprentice_orb", has(NVItems.ORB_APPRENTICE.get()))
                .save(output, NeoVitae.rl("alchemy_flask"));

        // Bleeding Edge Music Disc - raw demonite block on tier 3 altar
        AltarRecipeBuilder.build(NVItems.BLEEDING_EDGE.get())
                .from(NVBlocks.RAW_DEMONITE_BLOCK.item().get())
                .minTier(3)
                .bloodNeeded(10000)
                .consumption(20)
                .drain(10)
                .unlockedBy("has_master_orb", has(NVItems.ORB_MASTER.get()))
                .save(output, NeoVitae.rl("bleeding_edge_music"));
    }

    private void addSoulForgeRecipes(RecipeOutput output) {
        // Petty Soul Gem - redstone dust, gold ingot, glass, lapis gem
        SoulForgeRecipeBuilder.build(NVItems.SOUL_GEM_PETTY.get())
                .requires(Tags.Items.DUSTS_REDSTONE)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(Tags.Items.GEMS_LAPIS)
                .minWill(1)
                .drain(1)
                .unlockedBy("has_raw_will", has(NVItems.RAW_WILL.get()))
                .save(output, NeoVitae.rl("soul_gem_petty"));

        // Lesser Soul Gem - petty gem, diamond, redstone block, lapis block
        SoulForgeRecipeBuilder.build(NVItems.SOUL_GEM_LESSER.get())
                .requires(NVItems.SOUL_GEM_PETTY.get())
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .requires(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minWill(60)
                .drain(20)
                .unlockedBy("has_petty_gem", has(NVItems.SOUL_GEM_PETTY.get()))
                .save(output, NeoVitae.rl("soul_gem_lesser"));

        // Common Soul Gem - lesser gem, diamond, gold block, imbued slate
        SoulForgeRecipeBuilder.build(NVItems.SOUL_GEM_COMMON.get())
                .requires(NVItems.SOUL_GEM_LESSER.get())
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.STORAGE_BLOCKS_GOLD)
                .requires(NVItems.SLATE_IMBUED.get())
                .minWill(240)
                .drain(50)
                .unlockedBy("has_lesser_gem", has(NVItems.SOUL_GEM_LESSER.get()))
                .save(output, NeoVitae.rl("soul_gem_common"));

        // Greater Soul Gem - common gem, demonic slate, weak blood shard, demon crystal
        SoulForgeRecipeBuilder.build(NVItems.SOUL_GEM_GREATER.get())
                .requires(NVItems.SOUL_GEM_COMMON.get())
                .requires(NVItems.SLATE_DEMONIC.get())
                .requires(NVItems.WEAK_BLOOD_SHARD.get())
                .requires(NVTags.Items.DEMON_CRYSTALS)
                .minWill(1000)
                .drain(100)
                .unlockedBy("has_common_gem", has(NVItems.SOUL_GEM_COMMON.get()))
                .save(output, NeoVitae.rl("soul_gem_greater"));

        // Note: Grand Soul Gem doesn't exist in 1.20.1 - removed

        // ARC Block (shaped crafting recipe) - tier 3+ orb center
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NVBlocks.ARC_BLOCK.block().get())
                .pattern("sss")
                .pattern("SoS")
                .pattern("IfI")
                .define('s', Tags.Items.STONES)
                .define('S', NVItems.SLATE_IMBUED.get())
                .define('o', OrbTierIngredient.of(3))
                .define('I', Items.IRON_BLOCK)
                .define('f', Items.FURNACE)
                .unlockedBy("has_magician_orb", has(NVItems.ORB_MAGICIAN.get()))
                .save(output, NeoVitae.rl("arc_block"));

        // Blood Tank
        SoulForgeRecipeBuilder.build(NVBlocks.BLOOD_TANK.block().get())
                .requires(Items.GLASS, 3)
                .requires(NVBlocks.BLOODSTONE.block().get())
                .minWill(30)
                .drain(5)
                .unlockedBy("has_bloodstone", has(NVBlocks.BLOODSTONE.block().get()))
                .save(output, NeoVitae.rl("blood_tank"));

        // TODO: Living Station - block not yet implemented
        // SoulForgeRecipeBuilder.build(NVBlocks.LIVING_STATION.block().get())
        //         .requires(NVBlocks.BLOODSTONE.block().get(), 2)
        //         .requires(Items.STONE, 2)
        //         .minWill(50)
        //         .drain(10)
        //         .unlockedBy("has_bloodstone", has(NVBlocks.BLOODSTONE.block().get()))
        //         .save(output, NeoVitae.rl("living_station"));

        // Training Bracelet
        SoulForgeRecipeBuilder.build(NVItems.TRAINING_BRACELET.get())
                .requires(Items.GOLD_INGOT, 2)
                .requires(Items.STRING)
                .requires(NVItems.RAW_WILL.get())
                .minWill(10)
                .drain(5)
                .unlockedBy("has_raw_will", has(NVItems.RAW_WILL.get()))
                .save(output, NeoVitae.rl("training_bracelet"));

        // Sentient Tools
        SoulForgeRecipeBuilder.build(NVItems.SENTIENT_SWORD.get())
                .requires(NVItems.SOUL_GEM_PETTY.get())
                .requires(Items.IRON_SWORD)
                .minWill(0)
                .drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SOUL_GEM_PETTY.get()))
                .save(output, NeoVitae.rl("sentient_sword"));

        SoulForgeRecipeBuilder.build(NVItems.SENTIENT_AXE.get())
                .requires(NVItems.SOUL_GEM_PETTY.get())
                .requires(Items.IRON_AXE)
                .minWill(0)
                .drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SOUL_GEM_PETTY.get()))
                .save(output, NeoVitae.rl("sentient_axe"));

        SoulForgeRecipeBuilder.build(NVItems.SENTIENT_PICKAXE.get())
                .requires(NVItems.SOUL_GEM_PETTY.get())
                .requires(Items.IRON_PICKAXE)
                .minWill(0)
                .drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SOUL_GEM_PETTY.get()))
                .save(output, NeoVitae.rl("sentient_pickaxe"));

        SoulForgeRecipeBuilder.build(NVItems.SENTIENT_SHOVEL.get())
                .requires(NVItems.SOUL_GEM_PETTY.get())
                .requires(Items.IRON_SHOVEL)
                .minWill(0)
                .drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SOUL_GEM_PETTY.get()))
                .save(output, NeoVitae.rl("sentient_shovel"));

        SoulForgeRecipeBuilder.build(NVItems.SENTIENT_SCYTHE.get())
                .requires(NVItems.SOUL_GEM_PETTY.get())
                .requires(Items.IRON_HOE)
                .minWill(0)
                .drain(0)
                .unlockedBy("has_petty_gem", has(NVItems.SOUL_GEM_PETTY.get()))
                .save(output, NeoVitae.rl("sentient_scythe"));

        // Demon Will Blocks
        SoulForgeRecipeBuilder.build(NVBlocks.DEMON_CRUCIBLE.block().get())
                .requires(Items.CAULDRON)
                .requires(Tags.Items.STONES)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.GEMS_DIAMOND)
                .minWill(400)
                .drain(100)
                .unlockedBy("has_common_gem", has(NVItems.SOUL_GEM_COMMON.get()))
                .save(output, NeoVitae.rl("demon_crucible"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEMON_CRYSTALLIZER.block().get())
                .requires(NVBlocks.HELLFIRE_FORGE.block().get())
                .requires(Tags.Items.STONES)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.GLASS_BLOCKS)
                .minWill(500)
                .drain(100)
                .unlockedBy("has_hellfire_forge", has(NVBlocks.HELLFIRE_FORGE.block().get()))
                .save(output, NeoVitae.rl("demon_crystallizer"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEMON_PYLON.block().get())
                .requires(NVTags.Items.DEMON_CRYSTALS)
                .requires(Tags.Items.STONES)
                .requires(Tags.Items.GEMS_LAPIS)
                .requires(Tags.Items.STORAGE_BLOCKS_IRON)
                .minWill(400)
                .drain(50)
                .unlockedBy("has_demon_crystal", has(NVItems.RAW_CRYSTAL.get()))
                .save(output, NeoVitae.rl("demon_pylon"));

        // Crystal Blocks
        SoulForgeRecipeBuilder.build(NVBlocks.RAW_DEMON_CRYSTAL.block().get())
                .requires(NVItems.RAW_CRYSTAL.get(), 4)
                .minWill(1200)
                .drain(100)
                .unlockedBy("has_raw_crystal", has(NVItems.RAW_CRYSTAL.get()))
                .save(output, NeoVitae.rl("raw_demon_crystal"));

        SoulForgeRecipeBuilder.build(NVBlocks.CORROSIVE_DEMON_CRYSTAL.block().get())
                .requires(NVItems.CORROSIVE_CRYSTAL.get(), 4)
                .minWill(1200)
                .drain(100)
                .unlockedBy("has_corrosive_crystal", has(NVItems.CORROSIVE_CRYSTAL.get()))
                .save(output, NeoVitae.rl("corrosive_demon_crystal"));

        SoulForgeRecipeBuilder.build(NVBlocks.DESTRUCTIVE_DEMON_CRYSTAL.block().get())
                .requires(NVItems.DESTRUCTIVE_CRYSTAL.get(), 4)
                .minWill(1200)
                .drain(100)
                .unlockedBy("has_destructive_crystal", has(NVItems.DESTRUCTIVE_CRYSTAL.get()))
                .save(output, NeoVitae.rl("destructive_demon_crystal"));

        SoulForgeRecipeBuilder.build(NVBlocks.VENGEFUL_DEMON_CRYSTAL.block().get())
                .requires(NVItems.VENGEFUL_CRYSTAL.get(), 4)
                .minWill(1200)
                .drain(100)
                .unlockedBy("has_vengeful_crystal", has(NVItems.VENGEFUL_CRYSTAL.get()))
                .save(output, NeoVitae.rl("vengeful_demon_crystal"));

        SoulForgeRecipeBuilder.build(NVBlocks.STEADFAST_DEMON_CRYSTAL.block().get())
                .requires(NVItems.STEADFAST_CRYSTAL.get(), 4)
                .minWill(1200)
                .drain(100)
                .unlockedBy("has_steadfast_crystal", has(NVItems.STEADFAST_CRYSTAL.get()))
                .save(output, NeoVitae.rl("steadfast_demon_crystal"));

        // Routing Nodes
        SoulForgeRecipeBuilder.build(NVBlocks.ROUTING_NODE.block().get())
                .requires(Ingredient.of(Tags.Items.STONES), 2)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Tags.Items.GLASS_BLOCKS)
                .minWill(100)
                .drain(5)
                .unlockedBy("has_lesser_gem", has(NVItems.SOUL_GEM_LESSER.get()))
                .save(output, NeoVitae.rl("routing_node"));

        SoulForgeRecipeBuilder.build(NVBlocks.INPUT_ROUTING_NODE.block().get())
                .requires(NVBlocks.ROUTING_NODE.block().get())
                .requires(Items.HOPPER)
                .minWill(200)
                .drain(10)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_NODE.block().get()))
                .save(output, NeoVitae.rl("input_routing_node"));

        SoulForgeRecipeBuilder.build(NVBlocks.OUTPUT_ROUTING_NODE.block().get())
                .requires(NVBlocks.ROUTING_NODE.block().get())
                .requires(Items.DISPENSER)
                .minWill(200)
                .drain(10)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_NODE.block().get()))
                .save(output, NeoVitae.rl("output_routing_node"));

        SoulForgeRecipeBuilder.build(NVBlocks.MASTER_ROUTING_NODE.block().get())
                .requires(NVBlocks.ROUTING_NODE.block().get())
                .requires(Tags.Items.GEMS_DIAMOND)
                .requires(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minWill(400)
                .drain(25)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_NODE.block().get()))
                .save(output, NeoVitae.rl("master_routing_node"));

        // Node Upgrades
        SoulForgeRecipeBuilder.build(NVItems.MASTER_NODE_UPGRADE.get())
                .requires(Ingredient.of(Tags.Items.INGOTS_IRON), 2)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(Tags.Items.STORAGE_BLOCKS_LAPIS)
                .minWill(400)
                .drain(50)
                .unlockedBy("has_master_routing_node", has(NVBlocks.MASTER_ROUTING_NODE.block().get()))
                .save(output, NeoVitae.rl("master_node_upgrade"));

        SoulForgeRecipeBuilder.build(NVItems.MASTER_NODE_UPGRADE_SPEED.get())
                .requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 2)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .minWill(400)
                .drain(50)
                .unlockedBy("has_master_routing_node", has(NVBlocks.MASTER_ROUTING_NODE.block().get()))
                .save(output, NeoVitae.rl("master_node_upgrade_speed"));

        // Node Router
        SoulForgeRecipeBuilder.build(NVItems.NODE_ROUTER.get())
                .requires(Ingredient.of(Tags.Items.STONES), 2)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .minWill(50)
                .drain(5)
                .unlockedBy("has_routing_node", has(NVBlocks.ROUTING_NODE.block().get()))
                .save(output, NeoVitae.rl("node_router"));

        // Demon Will Gauge
        SoulForgeRecipeBuilder.build(NVItems.DEMON_WILL_GAUGE.get())
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .requires(Tags.Items.GLASS_BLOCKS)
                .requires(NVTags.Items.DEMON_CRYSTALS)
                .minWill(400)
                .drain(50)
                .unlockedBy("has_demon_crystal", has(NVItems.RAW_CRYSTAL.get()))
                .save(output, NeoVitae.rl("demon_will_gauge"));

        // Sanguine Reverter (soul forge recipe)
        SoulForgeRecipeBuilder.build(NVItems.SANGUINE_REVERTER.get())
                .requires(Items.SHEARS)
                .requires(Tags.Items.STONES)
                .requires(NVItems.SLATE_IMBUED.get())
                .requires(Tags.Items.INGOTS_IRON)
                .minWill(350)
                .drain(30)
                .unlockedBy("has_imbued_slate", has(NVItems.SLATE_IMBUED.get()))
                .save(output, NeoVitae.rl("sanguine_reverter"));

        // Resonator (soul forge recipe)
        SoulForgeRecipeBuilder.build(NVItems.RESONATOR.get())
                .requires(Tags.Items.STONES)
                .requires(Tags.Items.INGOTS_COPPER)
                .requires(NVItems.RAW_CRYSTAL.get())
                .minWill(1200)
                .drain(100)
                .unlockedBy("has_raw_crystal", has(NVItems.RAW_CRYSTAL.get()))
                .save(output, NeoVitae.rl("resonator"));

        // Primitive Crystalline Resonator
        SoulForgeRecipeBuilder.build(NVItems.PRIMITIVE_CRYSTALLINE_RESONATOR.get())
                .requires(Tags.Items.GEMS_AMETHYST)
                .requires(Tags.Items.INGOTS)
                .requires(NVItems.RAW_CRYSTAL.get())
                .requires(NVItems.TAU_OIL.get())
                .minWill(1200)
                .drain(200)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, NeoVitae.rl("primitive_resonator"));

        // Hellforged Resonator
        SoulForgeRecipeBuilder.build(NVItems.HELLFORGED_RESONATOR.get())
                .requires(Tags.Items.GEMS_AMETHYST)
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(NVItems.RAW_CRYSTAL.get())
                .requires(NVItems.HELLFORGED_INGOT.get())
                .minWill(1200)
                .drain(400)
                .unlockedBy("has_hellforged_ingot", has(NVItems.HELLFORGED_INGOT.get()))
                .save(output, NeoVitae.rl("hellforged_resonator"));

        // Throwing Daggers
        // Iron throwing dagger: 2 iron + 1 string, 32 will min, 5 drain, makes 16
        SoulForgeRecipeBuilder.build(NVItems.THROWING_DAGGER.get(), 16)
                .requires(Ingredient.of(Tags.Items.INGOTS_IRON), 2)
                .requires(Tags.Items.STRINGS)
                .minWill(32)
                .drain(5)
                .unlockedBy("has_lesser_gem", has(NVItems.SOUL_GEM_LESSER.get()))
                .save(output, NeoVitae.rl("throwing_dagger"));

        // Amethyst throwing dagger: 2 copper + 1 amethyst, 32 will min, 2 drain, makes 16
        SoulForgeRecipeBuilder.build(NVItems.THROWING_DAGGER_AMETHYST.get(), 16)
                .requires(Ingredient.of(Tags.Items.INGOTS_COPPER), 2)
                .requires(Tags.Items.GEMS_AMETHYST)
                .minWill(32)
                .drain(2)
                .unlockedBy("has_lesser_gem", has(NVItems.SOUL_GEM_LESSER.get()))
                .save(output, NeoVitae.rl("throwing_dagger_amethyst"));

        // Syringe throwing dagger: 1 amethyst dagger + 1 bottle, 200 will min, 10 drain, makes 1
        SoulForgeRecipeBuilder.build(NVItems.THROWING_DAGGER_SYRINGE.get())
                .requires(NVItems.THROWING_DAGGER_AMETHYST.get())
                .requires(Items.GLASS_BOTTLE)
                .minWill(200)
                .drain(10)
                .unlockedBy("has_amethyst_dagger", has(NVItems.THROWING_DAGGER_AMETHYST.get()))
                .save(output, NeoVitae.rl("throwing_dagger_syringe"));

        // Keys
        SoulForgeRecipeBuilder.build(NVItems.SIMPLE_KEY.get())
                .requires(Ingredient.of(Tags.Items.INGOTS_IRON), 2)
                .requires(Tags.Items.NUGGETS_GOLD)
                .minWill(100)
                .drain(10)
                .unlockedBy("has_lesser_gem", has(NVItems.SOUL_GEM_LESSER.get()))
                .save(output, NeoVitae.rl("simple_key"));

        SoulForgeRecipeBuilder.build(NVItems.MINE_KEY.get())
                .requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 2)
                .requires(Tags.Items.GEMS_DIAMOND)
                .minWill(200)
                .drain(25)
                .unlockedBy("has_common_gem", has(NVItems.SOUL_GEM_COMMON.get()))
                .save(output, NeoVitae.rl("mine_key"));

        // Crystal Catalysts - nether_wart + tau_oil + sulfur + unique_seed
        // Raw catalyst uses potato
        SoulForgeRecipeBuilder.build(NVItems.RAW_CRYSTAL_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.POTATO)
                .minWill(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, NeoVitae.rl("raw_catalyst"));

        // Corrosive catalyst uses wheat_seeds
        SoulForgeRecipeBuilder.build(NVItems.CORROSIVE_CRYSTAL_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.WHEAT_SEEDS)
                .minWill(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, NeoVitae.rl("corrosive_catalyst"));

        // Destructive catalyst uses beetroot
        SoulForgeRecipeBuilder.build(NVItems.DESTRUCTIVE_CRYSTAL_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.BEETROOT)
                .minWill(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, NeoVitae.rl("destructive_catalyst"));

        // Vengeful catalyst uses melon_seeds
        SoulForgeRecipeBuilder.build(NVItems.VENGEFUL_CRYSTAL_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.MELON_SEEDS)
                .minWill(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, NeoVitae.rl("vengeful_catalyst"));

        // Steadfast catalyst uses pumpkin_seeds
        SoulForgeRecipeBuilder.build(NVItems.STEADFAST_CRYSTAL_CATALYST.get())
                .requires(Tags.Items.CROPS_NETHER_WART)
                .requires(NVItems.TAU_OIL.get())
                .requires(NVTags.Items.DUSTS_SULFUR)
                .requires(Items.PUMPKIN_SEEDS)
                .minWill(400)
                .drain(20)
                .unlockedBy("has_tau_oil", has(NVItems.TAU_OIL.get()))
                .save(output, NeoVitae.rl("steadfast_catalyst"));

        // Explosive Charges
        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDS)
                .requires(Tags.Items.STONES)
                .minWill(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, NeoVitae.rl("shaped_charge"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.LOGS)
                .requires(ItemTags.PLANKS)
                .minWill(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, NeoVitae.rl("deforester_charge"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDSTONE_BLOCKS)
                .requires(Tags.Items.SANDS)
                .minWill(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, NeoVitae.rl("veinmine_charge"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get(), 8)
                .requires(Tags.Items.COBBLESTONES)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.CRIMSON_STEMS)
                .requires(Tags.Items.MUSHROOMS)
                .minWill(10)
                .drain(0.5)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(output, NeoVitae.rl("fungal_charge"));

        // Tier 2 charges
        SoulForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get(), 6)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDS)
                .requires(Items.BRICK)
                .minWill(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, NeoVitae.rl("aug_shaped_charge"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.LOGS)
                .requires(ItemTags.PLANKS)
                .minWill(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, NeoVitae.rl("deforester_charge_2"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDSTONE_BLOCKS)
                .requires(Tags.Items.SANDS)
                .minWill(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, NeoVitae.rl("veinmine_charge_2"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(ItemTags.CRIMSON_STEMS)
                .requires(Tags.Items.MUSHROOMS)
                .minWill(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, NeoVitae.rl("fungal_charge_2"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get(), 4)
                .requires(Tags.Items.STORAGE_BLOCKS_COPPER)
                .requires(Items.CHARCOAL)
                .requires(Tags.Items.SANDS)
                .requires(Tags.Items.STONES)
                .minWill(80)
                .drain(2.5)
                .unlockedBy("has_copper_block", has(Tags.Items.STORAGE_BLOCKS_COPPER))
                .save(output, NeoVitae.rl("shaped_charge_deep"));

        // Charge upgrade recipes - Tier 1 charges with basic anointments
        // Shaped Charge variants
        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .requires(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_fortune_1"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_silk_touch"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_smelting"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(NVBlocks.SHAPED_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_shaped_charge", has(NVBlocks.SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_voiding"));

        // Deforester Charge variants
        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .requires(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_fortune_1"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_silk_touch"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_smelting"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_deforester_charge", has(NVBlocks.DEFORESTER_CHARGE.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_voiding"));

        // Veinmine Charge variants
        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .requires(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_fortune_1"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_silk_touch"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_smelting"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_veinmine_charge", has(NVBlocks.VEINMINE_CHARGE.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_voiding"));

        // Fungal Charge variants
        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .requires(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_fortune_1"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.COBWEB)
                .requires(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_silk_touch"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.FURNACE)
                .requires(Items.CHARCOAL)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_smelting"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE.item().get())
                .requires(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .requires(Items.NETHERRACK)
                .requires(Items.COBBLED_DEEPSLATE)
                .minWill(60).drain(1.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_fungal_charge", has(NVBlocks.FUNGAL_CHARGE.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_voiding"));

        // Tier 2 charges - using anointment items directly
        // Aug Shaped Charge variants (uses _l anointments)
        SoulForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("aug_shaped_charge_fortune_1_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("aug_shaped_charge_fortune_2_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("aug_shaped_charge_silk_touch_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("aug_shaped_charge_smelting_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_aug_shaped_charge", has(NVBlocks.AUG_SHAPED_CHARGE.item().get()))
                .save(output, NeoVitae.rl("aug_shaped_charge_voiding"));

        // Shaped Charge Deep variants (uses _l anointments)
        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_deep_fortune_1_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_deep_fortune_2_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_deep_silk_touch_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_deep_smelting_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_shaped_charge_deep", has(NVBlocks.SHAPED_CHARGE_DEEP.item().get()))
                .save(output, NeoVitae.rl("shaped_charge_deep_voiding"));

        // Deforester Charge 2 variants
        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_2_fortune_1_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_2_fortune_2_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_2_silk_touch_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_2_smelting_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_deforester_charge_2", has(NVBlocks.DEFORESTER_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("deforester_charge_2_voiding"));

        // Veinmine Charge 2 variants
        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_2_fortune_1_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_2_fortune_2_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_2_silk_touch_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_2_smelting_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_veinmine_charge_2", has(NVBlocks.VEINMINE_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("veinmine_charge_2_voiding"));

        // Fungal Charge 2 variants
        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_2_fortune_1_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.FORTUNE_ANOINTMENT_2.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:fortune", 2, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_2_fortune_2_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:silk_touch", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_2_silk_touch_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.SMELTING_ANOINTMENT_L.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:smelting", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_2_smelting_l"));

        SoulForgeRecipeBuilder.build(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVBlocks.FUNGAL_CHARGE_2.item().get())
                .requires(NVItems.VOIDING_ANOINTMENT.get())
                .minWill(300).drain(4.0)
                .withAnointment("neovitae:voiding", 1, 1)
                .unlockedBy("has_fungal_charge_2", has(NVBlocks.FUNGAL_CHARGE_2.item().get()))
                .save(output, NeoVitae.rl("fungal_charge_2_voiding"));
    }

    // Helper methods

    private void addTier2RuneRecipe(RecipeOutput output, ItemLike result, ItemLike tier1Rune) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .pattern("nbn")
                .pattern("brb")
                .pattern("nbn")
                .define('n', Items.NETHERITE_SCRAP)
                .define('b', NVBlocks.BLOODSTONE.block().get())
                .define('r', tier1Rune)
                .unlockedBy("has_tier1_rune", has(tier1Rune))
                .save(output);
    }

    private void addAlchemyArrayRecipes(RecipeOutput output) {
        // Divination Sigil - base: redstone, added: blank slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_DIVINATION.get())
                .base(Items.REDSTONE)
                .added(NVItems.SLATE_BLANK.get())
                .texture("textures/models/alchemyarrays/divinationsigil.png")
                .save(output, "divination_sigil");

        // Seer Sigil - base: reagent_sight, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_SEER.get())
                .base(NVItems.REAGENT_SIGHT.get())
                .added(NVItems.SLATE_REINFORCED.get())
                .texture("textures/models/alchemyarrays/sightsigil.png")
                .save(output, "seer_sigil");

        // Water Sigil - base: reagent_water, added: blank slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_WATER.get())
                .base(NVItems.REAGENT_WATER.get())
                .added(NVItems.SLATE_BLANK.get())
                .texture("textures/models/alchemyarrays/watersigil.png")
                .save(output, "water_sigil");

        // Lava Sigil - base: reagent_lava, added: blank slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_LAVA.get())
                .base(NVItems.REAGENT_LAVA.get())
                .added(NVItems.SLATE_BLANK.get())
                .texture("textures/models/alchemyarrays/lavasigil.png")
                .save(output, "lava_sigil");

        // Void Sigil - base: reagent_void, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_VOID.get())
                .base(NVItems.REAGENT_VOID.get())
                .added(NVItems.SLATE_REINFORCED.get())
                .texture("textures/models/alchemyarrays/voidsigil.png")
                .save(output, "void_sigil");

        // Green Grove Sigil - base: reagent_growth, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_GREEN_GROVE.get())
                .base(NVItems.REAGENT_GROWTH.get())
                .added(NVItems.SLATE_REINFORCED.get())
                .texture("textures/models/alchemyarrays/growthsigil.png")
                .save(output, "green_grove_sigil");

        // Fast Miner Sigil - base: reagent_fast_miner, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_FAST_MINER.get())
                .base(NVItems.REAGENT_FAST_MINER.get())
                .added(NVItems.SLATE_REINFORCED.get())
                .texture("textures/models/alchemyarrays/fastminersigil.png")
                .save(output, "fast_miner_sigil");

        // Air Sigil - base: reagent_air, added: reinforced slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_AIR.get())
                .base(NVItems.REAGENT_AIR.get())
                .added(NVItems.SLATE_REINFORCED.get())
                .texture("textures/models/alchemyarrays/airsigil.png")
                .save(output, "air_sigil");

        // Blood Light Sigil - base: reagent_blood_light, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_BLOOD_LIGHT.get())
                .base(NVItems.REAGENT_BLOOD_LIGHT.get())
                .added(NVItems.SLATE_IMBUED.get())
                .texture("textures/models/alchemyarrays/bloodlightsigil.png")
                .save(output, "blood_light_sigil");

        // Magnetism Sigil - base: reagent_magnetism, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_MAGNETISM.get())
                .base(NVItems.REAGENT_MAGNETISM.get())
                .added(NVItems.SLATE_IMBUED.get())
                .texture("textures/models/alchemyarrays/magnetismsigil.png")
                .save(output, "magnetism_sigil");

        // Holding Sigil - base: reagent_holding, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_HOLDING.get())
                .base(NVItems.REAGENT_HOLDING.get())
                .added(NVItems.SLATE_IMBUED.get())
                .texture("textures/models/alchemyarrays/holdingsigil.png")
                .save(output, "holding_sigil");

        // Suppression Sigil - base: reagent_suppression, added: demonic slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_SUPPRESSION.get())
                .base(NVItems.REAGENT_SUPPRESSION.get())
                .added(NVItems.SLATE_DEMONIC.get())
                .texture("textures/models/alchemyarrays/suppressionsigil.png")
                .save(output, "suppression_sigil");

        // Teleposition Sigil - base: reagent_teleposition, added: demonic slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_TELEPOSITION.get())
                .base(NVItems.REAGENT_TELEPOSITION.get())
                .added(NVItems.SLATE_DEMONIC.get())
                .texture("textures/models/alchemyarrays/telepositionsigil.png")
                .save(output, "teleposition_sigil");

        // Phantom Bridge Sigil - base: reagent_phantom_bridge, added: imbued slate
        AlchemyArrayRecipeBuilder.build(NVItems.SIGIL_PHANTOM_BRIDGE.get())
                .base(NVItems.REAGENT_PHANTOM_BRIDGE.get())
                .added(NVItems.SLATE_IMBUED.get())
                .texture("textures/models/alchemyarrays/phantombridgesigil.png")
                .save(output, "phantom_bridge_sigil");

        // Living Armor - reagent_binding + iron armor pieces
        AlchemyArrayRecipeBuilder.build(NVItems.LIVING_HELMET.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_HELMET)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "living_helmet");

        AlchemyArrayRecipeBuilder.build(NVItems.LIVING_PLATE.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_CHESTPLATE)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "living_plate");

        AlchemyArrayRecipeBuilder.build(NVItems.LIVING_LEGGINGS.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_LEGGINGS)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "living_leggings");

        AlchemyArrayRecipeBuilder.build(NVItems.LIVING_BOOTS.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.IRON_BOOTS)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "living_boots");

        // Training Bracelet
        AlchemyArrayRecipeBuilder.build(NVItems.TRAINING_BRACELET.get())
                .base(NVItems.REAGENT_BINDING.get())
                .added(Items.DIAMOND)
                .texture("textures/models/alchemyarrays/bindingarray.png")
                .save(output, "living_trainer");

        // Effect Arrays - create environmental effects, not items
        // Bounce Array - slimeball + redstone
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.BOUNCE)
                .base(Ingredient.of(Tags.Items.SLIME_BALLS))
                .added(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .texture("textures/models/alchemyarrays/bouncearray.png")
                .save(output, "bounce");

        // Movement Array - feather + redstone
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.MOVEMENT)
                .base(Ingredient.of(Tags.Items.FEATHERS))
                .added(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .texture("textures/models/alchemyarrays/movementarray.png")
                .save(output, "movement");

        // Spike Array - cobblestone + iron ingot
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.SPIKE)
                .base(Ingredient.of(Tags.Items.COBBLESTONES))
                .added(Ingredient.of(Tags.Items.INGOTS_IRON))
                .texture("textures/models/alchemyarrays/spikearray.png")
                .save(output, "spike");

        // Updraft Array - feather + glowstone dust
        AlchemyArrayEffectRecipeBuilder.effect(AlchemyArrayEffectType.UPDRAFT)
                .base(Ingredient.of(Tags.Items.FEATHERS))
                .added(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
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
                .base(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .added(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .texture("textures/models/alchemyarrays/moonarray.png")
                .save(output, "night");
    }

    private void addAlchemyTableRecipes(RecipeOutput output) {
        // Reagent Water - sugar, water bucket x2
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_WATER.get())
                .input(Items.SUGAR)
                .input(Items.WATER_BUCKET)
                .input(Items.WATER_BUCKET)
                .syphon(300)
                .ticks(200)
                .minimumTier(1)
                .save(output, "reagent_water");

        // Reagent Lava - lava bucket, redstone dust, cobblestone, coal block
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_LAVA.get())
                .input(Items.LAVA_BUCKET)
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.COBBLESTONES))
                .input(Items.COAL_BLOCK)
                .syphon(1000)
                .ticks(200)
                .minimumTier(1)
                .save(output, "reagent_lava");

        // Reagent Air - ghast tear, feather x2
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_AIR.get())
                .input(Items.GHAST_TEAR)
                .input(Items.FEATHER)
                .input(Items.FEATHER)
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_air");

        // Reagent Void - bucket, string x2, gunpowder
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_VOID.get())
                .input(Items.BUCKET)
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .syphon(1000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_void");

        // Reagent Growth - saplings x2, sugar_cane, sugar
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_GROWTH.get())
                .input(Ingredient.of(ItemTags.SAPLINGS))
                .input(Ingredient.of(ItemTags.SAPLINGS))
                .input(Items.SUGAR_CANE)
                .input(Items.SUGAR)
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_growth");

        // Reagent Fast Miner - gold nugget, iron pickaxe, iron shovel
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_FAST_MINER.get())
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .input(Items.IRON_PICKAXE)
                .input(Items.IRON_SHOVEL)
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_fast_miner");

        // Reagent Magnetism - string, gold_ingot x2, iron_block
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_MAGNETISM.get())
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .input(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .input(Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_magnetism");

        // Reagent Blood Light - glowstone dust, torch, redstone x2
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_BLOOD_LIGHT.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Items.TORCH)
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_blood_light");

        // Reagent Sight - glowstone_dust, glass x2, divination sigil
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_SIGHT.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(NVItems.SIGIL_DIVINATION.get())
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "reagent_sight");

        // Reagent Binding - glowstone_dust, redstone_dust, gunpowder, gold_nugget
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_BINDING.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_binding");

        // Reagent Holding - chest, leather, string x2
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_HOLDING.get())
                .input(Ingredient.of(Tags.Items.CHESTS))
                .input(Ingredient.of(Tags.Items.LEATHERS))
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Ingredient.of(Tags.Items.STRINGS))
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_holding");

        // Reagent Suppression - teleposer, void sigil, gold ingot, bucket
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_SUPPRESSION.get())
                .input(NVBlocks.TELEPOSER.item().get())
                .input(NVItems.SIGIL_VOID.get())
                .input(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .input(Items.BUCKET)
                .syphon(10000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "reagent_suppression");

        // Reagent Teleposition - teleposer, gold ingot, ender pearl, chorus fruit
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_TELEPOSITION.get())
                .input(NVBlocks.TELEPOSER.item().get())
                .input(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .input(Items.ENDER_PEARL)
                .input(Items.CHORUS_FRUIT)
                .syphon(10000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "reagent_teleposition");

        // Reagent Phantom Bridge - feather, glass, soul sand, slime ball
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_PHANTOM_BRIDGE.get())
                .input(Items.FEATHER)
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(Items.SOUL_SAND)
                .input(Ingredient.of(Tags.Items.SLIME_BALLS))
                .syphon(5000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "reagent_phantom_bridge");

        // Arcane Ash - redstone, white dye (bone meal), gunpowder, coal
        AlchemyTableRecipeBuilder.build(NVItems.ARCANE_ASHES.get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.DYES_WHITE))
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Ingredient.of(ItemTags.COALS))
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "arcane_ash");

        // Tau Oil - 3x weak tau + bone meal
        AlchemyTableRecipeBuilder.build(NVItems.TAU_OIL.get())
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
        AlchemyTableRecipeBuilder.build(new ItemStack(Items.LEATHER, 4))
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
        AlchemyTableRecipeBuilder.build(new ItemStack(Items.STRING, 4))
                .input(Ingredient.of(ItemTags.WOOL))
                .input(Items.FLINT)
                .syphon(100)
                .ticks(100)
                .minimumTier(0)
                .save(output, "string");

        // Flint duplication
        AlchemyTableRecipeBuilder.build(new ItemStack(Items.FLINT, 2))
                .input(Items.GRAVEL)
                .input(Items.FLINT)
                .syphon(50)
                .ticks(20)
                .minimumTier(0)
                .save(output, "flint_from_gravel");

        // Bread from wheat
        AlchemyTableRecipeBuilder.build(Items.BREAD)
                .input(Ingredient.of(Tags.Items.CROPS_WHEAT))
                .input(Items.SUGAR)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "bread");

        // Explosive Powder - gunpowder x2 + coal dust
        AlchemyTableRecipeBuilder.build(NVItems.EXPLOSIVE_POWDER.get())
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "explosive_powder");

        // Sulfur from lava bucket + cobblestone
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.SULFUR.get(), 4))
                .input(Items.LAVA_BUCKET)
                .input(Ingredient.of(Tags.Items.COBBLESTONES))
                .syphon(200)
                .ticks(100)
                .minimumTier(0)
                .save(output, "sulfur_from_lava");

        // Saltpeter from plant oil x2 + coal dust
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.SALTPETER.get(), 3))
                .input(NVItems.PLANT_OIL.get())
                .input(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(200)
                .ticks(200)
                .minimumTier(1)
                .save(output, "saltpeter");

        // Gunpowder from sulfur + saltpeter + coal
        AlchemyTableRecipeBuilder.build(new ItemStack(Items.GUNPOWDER, 3))
                .input(Ingredient.of(NVTags.Items.DUSTS_SULFUR))
                .input(Ingredient.of(NVTags.Items.DUSTS_SALTPETER))
                .input(Ingredient.of(ItemTags.COALS))
                .syphon(0)
                .ticks(100)
                .minimumTier(0)
                .save(output, "gunpowder");

        // Plant Oil recipes - from various crops
        AlchemyTableRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(Tags.Items.CROPS_CARROT))
                .input(Ingredient.of(Tags.Items.CROPS_CARROT))
                .input(Ingredient.of(Tags.Items.CROPS_CARROT))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_carrots");

        AlchemyTableRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(Tags.Items.CROPS_POTATO))
                .input(Ingredient.of(Tags.Items.CROPS_POTATO))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_potatoes");

        AlchemyTableRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(Tags.Items.CROPS_WHEAT))
                .input(Ingredient.of(Tags.Items.CROPS_WHEAT))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_wheat");

        AlchemyTableRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(Tags.Items.CROPS_BEETROOT))
                .input(Ingredient.of(Tags.Items.CROPS_BEETROOT))
                .input(Ingredient.of(Tags.Items.CROPS_BEETROOT))
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_beets");

        // Basic Cutting Fluid - plant oil + redstone + gunpowder + sugar + coal dust + water bottle
        AlchemyTableRecipeBuilder.build(NVItems.BASIC_CUTTING_FLUID.get())
                .input(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Items.GUNPOWDER)
                .input(Items.SUGAR)
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .input(waterBottle())
                .syphon(1000)
                .ticks(200)
                .minimumTier(1)
                .save(output, "basic_cutting_fluid");

        // Slate Vial - blank slate + 5 glass
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.SLATE_VIAL.get(), 8))
                .input(NVItems.SLATE_BLANK.get())
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .syphon(500)
                .ticks(200)
                .minimumTier(1)
                .save(output, "slate_vial");

        // Anointment Recipes
        AlchemyTableRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "fortune_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.SILK_TOUCH_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Items.COBWEB)
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "silk_touch_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Items.BLAZE_POWDER)
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "melee_damage_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Items.GLISTERING_MELON_SLICE)
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "holy_water_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Items.GLASS_BOTTLE)
                .input(Items.ENCHANTED_BOOK)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "hidden_knowledge_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Items.SPECTRAL_ARROW)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "quick_draw_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .input(Ingredient.of(Tags.Items.BONES))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "looting_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Ingredient.of(Tags.Items.INGOTS_IRON))
                .input(Items.BOW)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "bow_power_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.SMELTING_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Items.FURNACE)
                .input(Ingredient.of(ItemTags.COALS))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "smelting_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.VOIDING_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Items.NETHERRACK)
                .input(Items.COBBLED_DEEPSLATE)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "voiding_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .input(Items.BOW)
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "bow_velocity_anointment");

        AlchemyTableRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVItems.SLATE_VIAL.get())
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .input(Ingredient.of(Tags.Items.INGOTS_COPPER))
                .input(Ingredient.of(NVTags.Items.DUSTS_GOLD))
                .syphon(500)
                .ticks(100)
                .minimumTier(1)
                .save(output, "weapon_repair_anointment");

        // Frame Parts and Filter Recipes
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.FRAME_PARTS.get(), 2))
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .input(Ingredient.of(Tags.Items.STONES))
                .input(NVItems.SLATE_BLANK.get())
                .syphon(1000)
                .ticks(100)
                .minimumTier(3)
                .save(output, "component_frame_parts");

        AlchemyTableRecipeBuilder.build(NVItems.ITEM_ROUTER_FILTER.get())
                .input(NVItems.FRAME_PARTS.get())
                .input(Ingredient.of(Tags.Items.LEATHERS))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.DYES_RED))
                .syphon(500)
                .ticks(100)
                .minimumTier(3)
                .save(output, "router_filter");

        AlchemyTableRecipeBuilder.build(NVItems.ITEM_TAG_FILTER.get())
                .input(NVItems.FRAME_PARTS.get())
                .input(Ingredient.of(Tags.Items.INGOTS))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Items.CLAY_BALL)
                .syphon(500)
                .ticks(100)
                .minimumTier(3)
                .save(output, "tag_router_filter");

        AlchemyTableRecipeBuilder.build(NVItems.ITEM_MOD_FILTER.get())
                .input(NVItems.FRAME_PARTS.get())
                .input(NVItems.SLATE_REINFORCED.get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.DYES_YELLOW))
                .syphon(500)
                .ticks(100)
                .minimumTier(3)
                .save(output, "mod_router_filter");

        AlchemyTableRecipeBuilder.build(NVItems.ITEM_ENCHANT_FILTER.get())
                .input(NVItems.FRAME_PARTS.get())
                .input(Items.ENCHANTED_BOOK)
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Items.PAPER)
                .syphon(500)
                .ticks(100)
                .minimumTier(3)
                .save(output, "enchant_router_filter");

        AlchemyTableRecipeBuilder.build(NVItems.ITEM_COMPOSITE_FILTER.get())
                .input(NVItems.FRAME_PARTS.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(NVItems.SLATE_IMBUED.get())
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "composite_router_filter");

        // Intermediate Cutting Fluid - tau oil + glowstone + gunpowder + sugar + sulfur + water bottle
        AlchemyTableRecipeBuilder.build(NVItems.INTERMEDIATE_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Items.GUNPOWDER)
                .input(Items.SUGAR)
                .input(Ingredient.of(NVTags.Items.DUSTS_SULFUR))
                .input(waterBottle())
                .syphon(2000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "intermediate_cutting_fluid");

        // Advanced Cutting Fluid - tau oil + hellforged dust + glow berries + saltpeter + sulfur + water bottle
        AlchemyTableRecipeBuilder.build(NVItems.ADVANCED_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(NVTags.Items.DUSTS_HELLFORGED))
                .input(Items.GLOW_BERRIES)
                .input(NVItems.SALTPETER.get())
                .input(Ingredient.of(NVTags.Items.DUSTS_SULFUR))
                .input(waterBottle())
                .syphon(4000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "advanced_cutting_fluid");

        // Anointment _L variants (extended duration - use tau oil)
        AlchemyTableRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_L.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "fortune_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.SILK_TOUCH_ANOINTMENT_L.get())
                .input(NVItems.SILK_TOUCH_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.COBWEB)
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "silk_touch_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_L.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.BLAZE_POWDER)
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "melee_damage_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_L.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.GLISTERING_MELON_SLICE)
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "holy_water_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_L.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.GLASS_BOTTLE)
                .input(Items.ENCHANTED_BOOK)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "hidden_knowledge_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_L.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Items.SPECTRAL_ARROW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "quick_draw_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_L.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .input(Ingredient.of(Tags.Items.BONES))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "looting_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_L.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.INGOTS_IRON))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_power_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.SMELTING_ANOINTMENT_L.get())
                .input(NVItems.SMELTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.FURNACE)
                .input(Ingredient.of(ItemTags.COALS))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "smelting_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.VOIDING_ANOINTMENT_L.get())
                .input(NVItems.VOIDING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.NETHERRACK)
                .input(Items.COBBLED_DEEPSLATE)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "voiding_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_L.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_velocity_anointment_l");

        AlchemyTableRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_L.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.INGOTS_COPPER))
                .input(Ingredient.of(NVTags.Items.DUSTS_GOLD))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "weapon_repair_anointment_l");

        // Anointment _2 variants (level 2 - use strong tau)
        AlchemyTableRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_2.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "fortune_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_2.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Items.BLAZE_POWDER)
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "melee_damage_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_2.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Items.GLISTERING_MELON_SLICE)
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "holy_water_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_2.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Items.GLASS_BOTTLE)
                .input(Items.ENCHANTED_BOOK)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "hidden_knowledge_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_2.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Items.SPECTRAL_ARROW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "quick_draw_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_2.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .input(Ingredient.of(Tags.Items.BONES))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "looting_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_2.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(Tags.Items.INGOTS_IRON))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_power_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_2.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .input(Items.BOW)
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "bow_velocity_anointment_2");

        AlchemyTableRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_2.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(Ingredient.of(Tags.Items.INGOTS_COPPER))
                .input(Ingredient.of(NVTags.Items.DUSTS_GOLD))
                .syphon(1000).ticks(100).minimumTier(3)
                .save(output, "weapon_repair_anointment_2");

        // Anointment _XL variants (extra long - use tau oil + hellforged sand + amethyst)
        AlchemyTableRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_XL.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "fortune_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.SILK_TOUCH_ANOINTMENT_XL.get())
                .input(NVItems.SILK_TOUCH_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.COBWEB)
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "silk_touch_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_XL.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "melee_damage_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_XL.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.GLISTERING_MELON_SLICE)
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "holy_water_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_XL.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.ENCHANTED_BOOK)
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "hidden_knowledge_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_XL.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.SPECTRAL_ARROW)
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "quick_draw_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_XL.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "looting_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_XL.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.INGOTS_IRON))
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_power_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.SMELTING_ANOINTMENT_XL.get())
                .input(NVItems.SMELTING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(ItemTags.COALS))
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "smelting_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.VOIDING_ANOINTMENT_XL.get())
                .input(NVItems.VOIDING_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Items.COBBLED_DEEPSLATE)
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "voiding_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_XL.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_velocity_anointment_xl");

        AlchemyTableRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_XL.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.INGOTS_COPPER))
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.AMETHYST_SHARD)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "weapon_repair_anointment_xl");

        // Anointment _3 variants (level 3 - use strong tau + hellforged sand + glow berries)
        AlchemyTableRecipeBuilder.build(NVItems.FORTUNE_ANOINTMENT_3.get())
                .input(NVItems.FORTUNE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "fortune_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.MELEE_DAMAGE_ANOINTMENT_3.get())
                .input(NVItems.MELEE_DAMAGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "melee_damage_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.HOLY_WATER_ANOINTMENT_3.get())
                .input(NVItems.HOLY_WATER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Items.GLISTERING_MELON_SLICE)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "holy_water_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT_3.get())
                .input(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Items.ENCHANTED_BOOK)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "hidden_knowledge_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.QUICK_DRAW_ANOINTMENT_3.get())
                .input(NVItems.QUICK_DRAW_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Items.SPECTRAL_ARROW)
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "quick_draw_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.LOOTING_ANOINTMENT_3.get())
                .input(NVItems.LOOTING_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "looting_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_POWER_ANOINTMENT_3.get())
                .input(NVItems.BOW_POWER_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(Tags.Items.INGOTS_IRON))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_power_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.BOW_VELOCITY_ANOINTMENT_3.get())
                .input(NVItems.BOW_VELOCITY_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(Tags.Items.NUGGETS_GOLD))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "bow_velocity_anointment_3");

        AlchemyTableRecipeBuilder.build(NVItems.WEAPON_REPAIR_ANOINTMENT_3.get())
                .input(NVItems.WEAPON_REPAIR_ANOINTMENT.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.HELLFORGED_SAND.get())
                .input(Items.GLOW_BERRIES)
                .input(Ingredient.of(Tags.Items.INGOTS_COPPER))
                .syphon(2000).ticks(100).minimumTier(4)
                .save(output, "weapon_repair_anointment_3");


        // === CATALYST RECIPES ===
        // Simple Catalyst - sugar, redstone, glowstone, gunpowder, nether wart
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.SIMPLE_CATALYST.get(), 2))
                .input(Items.SUGAR)
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Ingredient.of(Tags.Items.CROPS_NETHER_WART))
                .syphon(200)
                .ticks(100)
                .minimumTier(2)
                .save(output, "simple_catalyst");

        // Strengthened Catalyst - simple catalyst, copper dust, glow berries, cobbled deepslate
        AlchemyTableRecipeBuilder.build(NVItems.STRENGTHENED_CATALYST.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(NVTags.Items.DUSTS_COPPER))
                .input(Items.GLOW_BERRIES)
                .input(Items.COBBLED_DEEPSLATE)
                .syphon(1000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "strengthened_catalyst");

        // Cycling Catalyst - simple catalyst, lapis x2, green dye, sand
        AlchemyTableRecipeBuilder.build(NVItems.CYCLING_CATALYST.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .input(Ingredient.of(Tags.Items.GEMS_LAPIS))
                .input(Ingredient.of(Tags.Items.DYES_GREEN))
                .input(Ingredient.of(Tags.Items.SANDS))
                .syphon(1000)
                .ticks(100)
                .minimumTier(2)
                .save(output, "cycling_catalyst");

        // Combinational Catalyst - simple catalyst, brown mushroom, red mushroom, slime ball, coal dust
        AlchemyTableRecipeBuilder.build(NVItems.COMBINATIONAL_CATALYST.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Items.BROWN_MUSHROOM)
                .input(Items.RED_MUSHROOM)
                .input(Items.SLIME_BALL)
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(2000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "combinational");

        // Mundane Lengthening Catalyst - weak tau, simple catalyst, redstone x2
        AlchemyTableRecipeBuilder.build(NVItems.MUNDANE_LENGTHENING_CATALYST.get())
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .syphon(1000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "mundane_lengthening");

        // Mundane Power Catalyst - strong tau, simple catalyst, glowstone x2
        AlchemyTableRecipeBuilder.build(NVItems.MUNDANE_POWER_CATALYST.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .syphon(1000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "mundane_power");

        // Average Lengthening Catalyst - weak tau, strengthened catalyst, redstone, hellforged dust
        AlchemyTableRecipeBuilder.build(NVItems.AVERAGE_LENGTHENING_CATALYST.get())
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(NVItems.STRENGTHENED_CATALYST.get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(NVTags.Items.DUSTS_HELLFORGED))
                .syphon(2000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "average_lengthening");

        // Average Power Catalyst - strong tau, strengthened catalyst, glowstone, hellforged dust
        AlchemyTableRecipeBuilder.build(NVItems.AVERAGE_POWER_CATALYST.get())
                .input(NVBlocks.STRONG_TAU.item().get())
                .input(NVItems.STRENGTHENED_CATALYST.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Ingredient.of(NVTags.Items.DUSTS_HELLFORGED))
                .syphon(2000)
                .ticks(100)
                .minimumTier(4)
                .save(output, "average_power");

        // === SIGIL-POWERED RECIPES ===
        // Water Bucket from Water Sigil
        AlchemyTableRecipeBuilder.build(Items.WATER_BUCKET)
                .input(NVItems.SIGIL_WATER.get())
                .input(Items.BUCKET)
                .syphon(300)
                .ticks(60)
                .minimumTier(1)
                .save(output, "sigil_water_bucket");

        // Lava Bucket from Lava Sigil
        AlchemyTableRecipeBuilder.build(Items.LAVA_BUCKET)
                .input(NVItems.SIGIL_LAVA.get())
                .input(Items.BUCKET)
                .syphon(1000)
                .ticks(100)
                .minimumTier(1)
                .save(output, "sigil_lava_bucket");

        // Clay from Sand with Water Sigil
        AlchemyTableRecipeBuilder.build(new ItemStack(Items.CLAY_BALL, 2))
                .input(Ingredient.of(Tags.Items.SANDS))
                .input(Ingredient.of(Tags.Items.SANDS))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(350)
                .ticks(100)
                .minimumTier(2)
                .save(output, "clay_from_sand_sigil");

        // Leather from Rotten Flesh with Water Sigil
        AlchemyTableRecipeBuilder.build(new ItemStack(Items.LEATHER, 4))
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
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.SULFUR.get(), 4))
                .input(NVItems.SIGIL_LAVA.get())
                .input(Ingredient.of(Tags.Items.COBBLESTONES))
                .syphon(1200)
                .ticks(100)
                .minimumTier(0)
                .save(output, "sulfur_from_sigil");

        // Basic Cutting Fluid with Water Sigil
        AlchemyTableRecipeBuilder.build(NVItems.BASIC_CUTTING_FLUID.get())
                .input(NVItems.PLANT_OIL.get())
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Items.SUGAR)
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(1100)
                .ticks(200)
                .minimumTier(1)
                .save(output, "basic_cutting_fluid_sigil");

        // Intermediate Cutting Fluid with Water Sigil
        AlchemyTableRecipeBuilder.build(NVItems.INTERMEDIATE_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Items.SUGAR)
                .input(Ingredient.of(NVTags.Items.DUSTS_SULFUR))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(2100)
                .ticks(200)
                .minimumTier(3)
                .save(output, "intermediate_cutting_fluid_sigil");

        // Advanced Cutting Fluid with Water Sigil
        AlchemyTableRecipeBuilder.build(NVItems.ADVANCED_CUTTING_FLUID.get())
                .input(NVItems.TAU_OIL.get())
                .input(Ingredient.of(NVTags.Items.DUSTS_HELLFORGED))
                .input(Items.GLOW_BERRIES)
                .input(NVItems.SALTPETER.get())
                .input(Ingredient.of(NVTags.Items.DUSTS_SULFUR))
                .input(NVItems.SIGIL_WATER.get())
                .syphon(4100)
                .ticks(200)
                .minimumTier(4)
                .save(output, "advance_cutting_fluid_sigil");

        // === CORRUPTED DUST RECIPES ===
        // Corrupted Coal -> Coal Sand x3
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.COAL_SAND.get(), 3))
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .input(NVItems.CORRUPTED_DUST.get())
                .syphon(50)
                .ticks(50)
                .minimumTier(3)
                .save(output, "corrupted_coal");

        // Corrupted Copper -> Copper Gravel x2
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.COPPER_GRAVEL.get(), 2))
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_COPPER))
                .input(NVItems.CORRUPTED_DUST.get())
                .syphon(50)
                .ticks(50)
                .minimumTier(3)
                .save(output, "corrupted_copper");

        // Corrupted Gold -> Gold Gravel x2
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.GOLD_GRAVEL.get(), 2))
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_GOLD))
                .input(NVItems.CORRUPTED_DUST.get())
                .syphon(300)
                .ticks(50)
                .minimumTier(3)
                .save(output, "corrupted_gold");

        // Corrupted Iron -> Iron Gravel x2
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.IRON_GRAVEL.get(), 2))
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_IRON))
                .input(NVItems.CORRUPTED_DUST.get())
                .syphon(100)
                .ticks(50)
                .minimumTier(3)
                .save(output, "corrupted_iron");

        // Corrupted Netherite -> Netherite Gravel x2
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.NETHERITE_SCRAP_GRAVEL.get(), 2))
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_NETHERITE_SCRAP))
                .input(NVItems.CORRUPTED_DUST.get())
                .input(NVItems.CORRUPTED_DUST.get())
                .input(NVItems.CORRUPTED_DUST.get())
                .syphon(1000)
                .ticks(50)
                .minimumTier(3)
                .save(output, "corrupted_netherite");

        // === SAND RECIPES ===
        // Coal Sand from Coal
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.COAL_SAND.get(), 4))
                .input(Items.COAL)
                .input(Items.COAL)
                .input(Items.FLINT)
                .syphon(400)
                .ticks(200)
                .minimumTier(1)
                .save(output, "sand_coal");

        // Gold Sand from Gold Ore
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.GOLD_SAND.get(), 2))
                .input(Ingredient.of(Tags.Items.ORES_GOLD))
                .input(Ingredient.of(NVTags.Items.CUTTING_FLUIDS))
                .syphon(400)
                .ticks(200)
                .minimumTier(1)
                .save(output, "sand_gold");

        // Iron Sand from Iron Ore
        AlchemyTableRecipeBuilder.build(new ItemStack(NVItems.IRON_SAND.get(), 2))
                .input(Ingredient.of(Tags.Items.ORES_IRON))
                .input(Ingredient.of(NVTags.Items.CUTTING_FLUIDS))
                .syphon(400)
                .ticks(200)
                .minimumTier(1)
                .save(output, "sand_iron");

        // === OTHER UTILITY RECIPES ===
        // Cobweb from String
        AlchemyTableRecipeBuilder.build(Items.COBWEB)
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Ingredient.of(Tags.Items.STRINGS))
                .input(Ingredient.of(Tags.Items.STRINGS))
                .syphon(50)
                .ticks(50)
                .minimumTier(1)
                .save(output, "cobweb");

        // Explosive Cell (primitive)
        AlchemyTableRecipeBuilder.build(NVItems.PRIMITIVE_EXPLOSIVE_CELL.get())
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(NVBlocks.WEAK_TAU.item().get())
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(1000)
                .ticks(200)
                .minimumTier(3)
                .save(output, "explosive_cell");

        // Hellforged Explosive Cell
        AlchemyTableRecipeBuilder.build(NVItems.HELLFORGED_EXPLOSIVE_CELL.get())
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .input(Ingredient.of(NVTags.Items.DUSTS_SULFUR))
                .input(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
                .input(Ingredient.of(Tags.Items.GEMS_QUARTZ))
                .input(Ingredient.of(NVTags.Items.DUSTS_HELLFORGED))
                .input(Ingredient.of(NVTags.Items.DUSTS_COAL))
                .syphon(4000)
                .ticks(200)
                .minimumTier(4)
                .save(output, "hellforged_explosive_cell");

        // Gold Nuggets from Gilded Blackstone
        AlchemyTableRecipeBuilder.build(new ItemStack(Items.GOLD_NUGGET, 9))
                .input(Items.GILDED_BLACKSTONE)
                .syphon(200)
                .ticks(100)
                .minimumTier(2)
                .save(output, "gold_ore_from_gilded");

        // Grass Block from Dirt
        AlchemyTableRecipeBuilder.build(Items.GRASS_BLOCK)
                .input(Items.DIRT)
                .input(Items.BONE_MEAL)
                .input(Items.WHEAT_SEEDS)
                .syphon(200)
                .ticks(200)
                .minimumTier(1)
                .save(output, "grass_block");

        // Nether Wart from Nether Wart Block
        AlchemyTableRecipeBuilder.build(Items.NETHER_WART)
                .input(Items.NETHER_WART_BLOCK)
                .syphon(50)
                .ticks(40)
                .minimumTier(1)
                .save(output, "nether_wart_from_block");

        // Plant Oil from Potatoes
        AlchemyTableRecipeBuilder.build(NVItems.PLANT_OIL.get())
                .input(Items.POTATO)
                .input(Items.POTATO)
                .input(Items.BONE_MEAL)
                .syphon(100)
                .ticks(100)
                .minimumTier(1)
                .save(output, "plantoil_from_taters");

        // Reagent Fast Miner (alt recipe with tools)
        AlchemyTableRecipeBuilder.build(NVItems.REAGENT_FAST_MINER.get())
                .input(Items.IRON_PICKAXE)
                .input(Items.IRON_AXE)
                .input(Items.IRON_SHOVEL)
                .input(Ingredient.of(Tags.Items.GUNPOWDERS))
                .syphon(2000)
                .ticks(200)
                .minimumTier(2)
                .save(output, "reagent_fastminer");

        // Weak Filling Agent
        AlchemyTableRecipeBuilder.build(NVItems.WEAK_FILLING_AGENT.get())
                .input(NVItems.SIMPLE_CATALYST.get())
                .input(Items.SUGAR_CANE)
                .input(Items.CRIMSON_FUNGUS)
                .input(Items.WARPED_FUNGUS)
                .syphon(2000)
                .ticks(100)
                .minimumTier(2)
                .save(output, "weak_filling");
        // Alchemy Table recipe (crafting recipe for the table itself)
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, NVBlocks.ALCHEMY_TABLE.block().get())
                .pattern("sss")
                .pattern("wbw")
                .pattern("gog")
                .define('s', Tags.Items.STONES)
                .define('w', ItemTags.PLANKS)
                .define('b', Tags.Items.INGOTS_IRON)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('o', NVItems.SLATE_BLANK.get())
                .unlockedBy("has_blank_slate", has(NVItems.SLATE_BLANK.get()))
                .save(output, NeoVitae.rl("alchemy_table"));
    }

    private void addARCRecipes(RecipeOutput output) {
        // Iron processing chain
        // Ore -> Sand (3x) with cutting fluid
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.ORES_IRON))
                .guaranteedOutput(new ItemStack(NVItems.IRON_SAND.get(), 3))
                .save(output, NeoVitae.rl("dustsfrom_ore_iron"));

        // Raw material -> Fragment (2x + 25% extra) with explosive
        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Tags.Items.RAW_MATERIALS_IRON))
                .guaranteedOutput(new ItemStack(NVItems.IRON_FRAGMENT.get(), 2))
                .chancedOutput(new ItemStack(NVItems.IRON_FRAGMENT.get()), 0.25)
                .save(output, NeoVitae.rl("fragmentsiron"));

        // Ore -> Fragment (4x) with explosive
        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Tags.Items.ORES_IRON))
                .guaranteedOutput(new ItemStack(NVItems.IRON_FRAGMENT.get(), 4))
                .save(output, NeoVitae.rl("fragmentsfrom_ore_iron"));

        // Fragment -> Gravel (1x + 50% corrupted tinydust) with resonator
        ARCRecipeBuilder.build(NVTags.Items.RESONATOR)
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_IRON))
                .guaranteedOutput(new ItemStack(NVItems.IRON_GRAVEL.get()))
                .chancedOutput(new ItemStack(NVItems.CORRUPTED_DUST_TINY.get()), 0.5)
                .save(output, NeoVitae.rl("gravelsiron"));

        // Gravel -> Sand (1x) with cutting fluid
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(NVTags.Items.GRAVELS_IRON))
                .guaranteedOutput(new ItemStack(NVItems.IRON_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_gravel_iron"));

        // Ingot -> Sand (1x) with cutting fluid
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.INGOTS_IRON))
                .guaranteedOutput(new ItemStack(NVItems.IRON_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_ingot_iron"));

        // Raw material -> Sand (1x + 17% extra, 33% for 2nd extra) with cutting fluid
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.RAW_MATERIALS_IRON))
                .guaranteedOutput(new ItemStack(NVItems.IRON_SAND.get()))
                .chancedOutput(new ItemStack(NVItems.IRON_SAND.get()), 0.33)
                .save(output, NeoVitae.rl("dustsfrom_raw_iron"));

        // Gold processing chain
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.ORES_GOLD))
                .guaranteedOutput(new ItemStack(NVItems.GOLD_SAND.get(), 3))
                .save(output, NeoVitae.rl("dustsfrom_ore_gold"));

        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Tags.Items.RAW_MATERIALS_GOLD))
                .guaranteedOutput(new ItemStack(NVItems.GOLD_FRAGMENT.get(), 2))
                .chancedOutput(new ItemStack(NVItems.GOLD_FRAGMENT.get()), 0.25)
                .save(output, NeoVitae.rl("fragmentsgold"));

        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Tags.Items.ORES_GOLD))
                .guaranteedOutput(new ItemStack(NVItems.GOLD_FRAGMENT.get(), 4))
                .save(output, NeoVitae.rl("fragmentsfrom_ore_gold"));

        ARCRecipeBuilder.build(NVTags.Items.RESONATOR)
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_GOLD))
                .guaranteedOutput(new ItemStack(NVItems.GOLD_GRAVEL.get()))
                .chancedOutput(new ItemStack(NVItems.CORRUPTED_DUST_TINY.get()), 0.5)
                .save(output, NeoVitae.rl("gravelsgold"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(NVTags.Items.GRAVELS_GOLD))
                .guaranteedOutput(new ItemStack(NVItems.GOLD_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_gravel_gold"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.INGOTS_GOLD))
                .guaranteedOutput(new ItemStack(NVItems.GOLD_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_ingot_gold"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.RAW_MATERIALS_GOLD))
                .guaranteedOutput(new ItemStack(NVItems.GOLD_SAND.get()))
                .chancedOutput(new ItemStack(NVItems.GOLD_SAND.get()), 0.33)
                .save(output, NeoVitae.rl("dustsfrom_raw_gold"));

        // Copper processing chain
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.ORES_COPPER))
                .guaranteedOutput(new ItemStack(NVItems.COPPER_SAND.get(), 3))
                .save(output, NeoVitae.rl("dustsfrom_ore_copper"));

        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Tags.Items.RAW_MATERIALS_COPPER))
                .guaranteedOutput(new ItemStack(NVItems.COPPER_FRAGMENT.get(), 2))
                .chancedOutput(new ItemStack(NVItems.COPPER_FRAGMENT.get()), 0.25)
                .save(output, NeoVitae.rl("fragmentscopper"));

        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Tags.Items.ORES_COPPER))
                .guaranteedOutput(new ItemStack(NVItems.COPPER_FRAGMENT.get(), 4))
                .save(output, NeoVitae.rl("fragmentsfrom_ore_copper"));

        ARCRecipeBuilder.build(NVTags.Items.RESONATOR)
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_COPPER))
                .guaranteedOutput(new ItemStack(NVItems.COPPER_GRAVEL.get()))
                .chancedOutput(new ItemStack(NVItems.CORRUPTED_DUST_TINY.get()), 0.5)
                .save(output, NeoVitae.rl("gravelscopper"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(NVTags.Items.GRAVELS_COPPER))
                .guaranteedOutput(new ItemStack(NVItems.COPPER_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_gravel_copper"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.INGOTS_COPPER))
                .guaranteedOutput(new ItemStack(NVItems.COPPER_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_ingot_copper"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.RAW_MATERIALS_COPPER))
                .guaranteedOutput(new ItemStack(NVItems.COPPER_SAND.get()))
                .chancedOutput(new ItemStack(NVItems.COPPER_SAND.get()), 0.33)
                .save(output, NeoVitae.rl("dustsfrom_raw_copper"));

        // Netherite scrap processing chain
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Items.ANCIENT_DEBRIS))
                .guaranteedOutput(new ItemStack(NVItems.NETHERITE_SCRAP_SAND.get(), 2))
                .save(output, NeoVitae.rl("dustsfrom_ore_netherite_scrap"));

        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Items.NETHERITE_SCRAP))
                .guaranteedOutput(new ItemStack(NVItems.NETHERITE_SCRAP_FRAGMENT.get(), 3))
                .chancedOutput(new ItemStack(NVItems.NETHERITE_SCRAP_FRAGMENT.get()), 0.25)
                .save(output, NeoVitae.rl("fragmentsnetherite_scrap"));

        ARCRecipeBuilder.build(NVTags.Items.RESONATOR)
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_NETHERITE_SCRAP))
                .guaranteedOutput(new ItemStack(NVItems.NETHERITE_SCRAP_GRAVEL.get()))
                .chancedOutput(new ItemStack(NVItems.CORRUPTED_DUST_TINY.get()), 0.5)
                .save(output, NeoVitae.rl("gravelsnetherite_scrap"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(NVTags.Items.GRAVELS_NETHERITE_SCRAP))
                .guaranteedOutput(new ItemStack(NVItems.NETHERITE_SCRAP_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_gravel_netherite_scrap"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Items.NETHERITE_SCRAP))
                .guaranteedOutput(new ItemStack(NVItems.NETHERITE_SCRAP_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_ingot_netherite_scrap"));

        // Hellforged/Demonite processing (only gravel->sand, others need hellforged ore/ingot)
        ARCRecipeBuilder.build(NVTags.Items.RESONATOR)
                .input(Ingredient.of(NVTags.Items.FRAGMENTS_HELLFORGED))
                .guaranteedOutput(new ItemStack(NVItems.DEMONITE_GRAVEL.get()))
                .chancedOutput(new ItemStack(NVItems.CORRUPTED_DUST_TINY.get()), 0.5)
                .save(output, NeoVitae.rl("gravelshellforged"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(NVTags.Items.GRAVELS_HELLFORGED))
                .guaranteedOutput(new ItemStack(NVItems.HELLFORGED_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_gravel_hellforged"));

        // Hellforged ingot -> sand (uses explosives, not cutting fluid)
        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(NVTags.Items.INGOTS_HELLFORGED))
                .guaranteedOutput(new ItemStack(NVItems.HELLFORGED_SAND.get()))
                .save(output, NeoVitae.rl("dustsfrom_ingot_hellforged"));

        // Coal processing - coal -> coal sand with cutting fluid
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Tags.Items.ORES_COAL))
                .guaranteedOutput(new ItemStack(NVItems.COAL_SAND.get(), 6))
                .save(output, NeoVitae.rl("coalsand_from_ore"));

        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Items.COAL))
                .guaranteedOutput(new ItemStack(NVItems.COAL_SAND.get()))
                .save(output, NeoVitae.rl("coalsand_from_coal"));

        // Utility recipes - hydration (all require 200mB water)
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Tags.Items.SANDS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.CLAY_BALL, 1))
                .chancedOutput(new ItemStack(Items.CLAY_BALL, 1), 0.5)
                .save(output, NeoVitae.rl("clay_from_sand"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.TERRACOTTA))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.CLAY, 1))
                .save(output, NeoVitae.rl("clay_from_terracotta"));

        // Netherrack to sulfur
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(Items.NETHERRACK))
                .guaranteedOutput(new ItemStack(NVItems.SULFUR.get(), 2))
                .save(output, NeoVitae.rl("netherrack_to_sulfur"));

        // Weak blood shard from tau + life essence
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(NVBlocks.STRONG_TAU.item().get())
                .fluidInput(new FluidStack(NVFluids.LIFE_ESSENCE_SOURCE.get(), 3200))
                .guaranteedOutput(new ItemStack(NVItems.WEAK_BLOOD_SHARD.get()))
                .save(output, NeoVitae.rl("weakbloodshard_tau"));

        // Dirt to Mud
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.DIRT))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MUD, 1))
                .save(output, NeoVitae.rl("mud_from_dirt"));

        // === DYE WASHING RECIPES ===
        // Wool washing (any colored wool → white wool)
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(ItemTags.WOOL))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WHITE_WOOL, 1))
                .save(output, NeoVitae.rl("wash_wool"));

        // Carpet washing (any colored carpet → white carpet)
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(ItemTags.WOOL_CARPETS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WHITE_CARPET, 1))
                .save(output, NeoVitae.rl("wash_carpet"));

        // Bed washing (any colored bed → white bed)
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(ItemTags.BEDS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WHITE_BED, 1))
                .save(output, NeoVitae.rl("wash_bed"));

        // Stained glass washing → clear glass
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Tags.Items.GLASS_BLOCKS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.GLASS, 1))
                .save(output, NeoVitae.rl("wash_glass"));

        // Stained glass pane washing → clear glass pane
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Tags.Items.GLASS_PANES))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.GLASS_PANE, 1))
                .save(output, NeoVitae.rl("wash_glass_pane"));

        // === CONCRETE SOLIDIFICATION RECIPES ===
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WHITE_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WHITE_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_white_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.ORANGE_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.ORANGE_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_orange_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.MAGENTA_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MAGENTA_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_magenta_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.LIGHT_BLUE_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_light_blue_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.YELLOW_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.YELLOW_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_yellow_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.LIME_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.LIME_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_lime_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.PINK_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.PINK_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_pink_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.GRAY_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.GRAY_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_gray_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.LIGHT_GRAY_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_light_gray_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CYAN_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.CYAN_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_cyan_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.PURPLE_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.PURPLE_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_purple_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.BLUE_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.BLUE_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_blue_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.BROWN_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.BROWN_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_brown_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.GREEN_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.GREEN_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_green_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.RED_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.RED_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_red_concrete"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.BLACK_CONCRETE_POWDER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.BLACK_CONCRETE, 1))
                .save(output, NeoVitae.rl("solidify_black_concrete"));

        // === MOSS SPREADING RECIPES ===
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_COBBLESTONE, 1))
                .save(output, NeoVitae.rl("mossify_cobblestone"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE_STAIRS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_COBBLESTONE_STAIRS, 1))
                .save(output, NeoVitae.rl("mossify_cobblestone_stairs"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE_SLAB))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_COBBLESTONE_SLAB, 1))
                .save(output, NeoVitae.rl("mossify_cobblestone_slab"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COBBLESTONE_WALL))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_COBBLESTONE_WALL, 1))
                .save(output, NeoVitae.rl("mossify_cobblestone_wall"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICKS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_STONE_BRICKS, 1))
                .save(output, NeoVitae.rl("mossify_stone_bricks"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICK_STAIRS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_STONE_BRICK_STAIRS, 1))
                .save(output, NeoVitae.rl("mossify_stone_brick_stairs"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICK_SLAB))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_STONE_BRICK_SLAB, 1))
                .save(output, NeoVitae.rl("mossify_stone_brick_slab"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.STONE_BRICK_WALL))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.MOSSY_STONE_BRICK_WALL, 1))
                .save(output, NeoVitae.rl("mossify_stone_brick_wall"));

        // === COPPER OXIDATION RECIPES ===
        // Copper Block oxidation chain
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.COPPER_BLOCK))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.EXPOSED_COPPER, 1))
                .save(output, NeoVitae.rl("oxidize/copper_block_to_exposed_copper"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_COPPER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WEATHERED_COPPER, 1))
                .save(output, NeoVitae.rl("oxidize/exposed_copper_to_weathered_copper"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_COPPER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.OXIDIZED_COPPER, 1))
                .save(output, NeoVitae.rl("oxidize/weathered_copper_to_oxidized_copper"));

        // Cut Copper oxidation chain
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CUT_COPPER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.EXPOSED_CUT_COPPER, 1))
                .save(output, NeoVitae.rl("oxidize/cut_copper_to_exposed_cut_copper"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_CUT_COPPER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WEATHERED_CUT_COPPER, 1))
                .save(output, NeoVitae.rl("oxidize/exposed_cut_copper_to_weathered_cut_copper"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_CUT_COPPER))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.OXIDIZED_CUT_COPPER, 1))
                .save(output, NeoVitae.rl("oxidize/weathered_cut_copper_to_oxidized_cut_copper"));

        // Cut Copper Stairs oxidation chain
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CUT_COPPER_STAIRS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.EXPOSED_CUT_COPPER_STAIRS, 1))
                .save(output, NeoVitae.rl("oxidize/cut_copper_stairs_to_exposed_cut_copper_stairs"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_CUT_COPPER_STAIRS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WEATHERED_CUT_COPPER_STAIRS, 1))
                .save(output, NeoVitae.rl("oxidize/exposed_cut_copper_stairs_to_weathered_cut_copper_stairs"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_CUT_COPPER_STAIRS))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.OXIDIZED_CUT_COPPER_STAIRS, 1))
                .save(output, NeoVitae.rl("oxidize/weathered_cut_copper_stairs_to_oxidized_cut_copper_stairs"));

        // Cut Copper Slab oxidation chain
        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.CUT_COPPER_SLAB))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.EXPOSED_CUT_COPPER_SLAB, 1))
                .save(output, NeoVitae.rl("oxidize/cut_copper_slab_to_exposed_cut_copper_slab"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.EXPOSED_CUT_COPPER_SLAB))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.WEATHERED_CUT_COPPER_SLAB, 1))
                .save(output, NeoVitae.rl("oxidize/exposed_cut_copper_slab_to_weathered_cut_copper_slab"));

        ARCRecipeBuilder.build(NVTags.Items.HYDRATION)
                .input(Ingredient.of(Items.WEATHERED_CUT_COPPER_SLAB))
                .fluidInput(new FluidStack(Fluids.WATER, 200))
                .guaranteedOutput(new ItemStack(Items.OXIDIZED_CUT_COPPER_SLAB, 1))
                .save(output, NeoVitae.rl("oxidize/weathered_cut_copper_slab_to_oxidized_cut_copper_slab"));

        // === RUNE REVERSION RECIPES ===
        // Speed Rune 2 -> Speed Rune + hellforged parts + netherite scrap
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_SPEED.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_SPEED.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/speed"));

        // Acceleration Rune 2 -> Acceleration Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_ACCELERATION.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_ACCELERATION.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/acceleration"));

        // Capacity Rune 2 -> Capacity Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_CAPACITY.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_CAPACITY.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/capacity"));

        // Augmented Capacity Rune 2 -> Augmented Capacity Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_CAPACITY_AUGMENTED.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_CAPACITY_AUGMENTED.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/aug_capacity"));

        // Charging Rune 2 -> Charging Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_CHARGING.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_CHARGING.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/charging"));

        // Dislocation Rune 2 -> Dislocation Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_DISLOCATION.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_DISLOCATION.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/displacement"));

        // Orb Rune 2 -> Orb Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_ORB.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_ORB.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/orb_rune"));

        // Sacrifice Rune 2 -> Sacrifice Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_SACRIFICE.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_SACRIFICE.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/sac"));

        // Self-Sacrifice Rune 2 -> Self-Sacrifice Rune
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVBlocks.RUNE_2_SELF_SACRIFICE.item().get())
                .guaranteedOutput(new ItemStack(NVBlocks.RUNE_SELF_SACRIFICE.item().get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_PARTS.get()), 1.0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 1.0)
                .save(output, NeoVitae.rl("reversion/self_sac"));

        // === BLOOD ORB REVERSION ===
        // Weak Blood Orb -> Diamond
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_WEAK.get())
                .guaranteedOutput(new ItemStack(Items.DIAMOND))
                .save(output, NeoVitae.rl("reversion/weak_blood_orb"));

        // Apprentice Blood Orb -> Redstone Block
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_APPRENTICE.get())
                .guaranteedOutput(new ItemStack(Items.REDSTONE_BLOCK))
                .save(output, NeoVitae.rl("reversion/apprentice_blood_orb"));

        // Magician Blood Orb -> Gold Block
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_MAGICIAN.get())
                .guaranteedOutput(new ItemStack(Items.GOLD_BLOCK))
                .save(output, NeoVitae.rl("reversion/magician_blood_orb"));

        // Master Blood Orb -> Weak Blood Shard
        ARCRecipeBuilder.build(NVTags.Items.REVERTER)
                .input(NVItems.ORB_MASTER.get())
                .guaranteedOutput(new ItemStack(NVItems.WEAK_BLOOD_SHARD.get()))
                .save(output, NeoVitae.rl("reversion/master_blood_orb"));

        // === HELLFORGED PROCESSING ===
        // Hellforged Fragments from raw hellforged (explosive)
        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(NVTags.Items.RAW_MATERIALS_HELLFORGED))
                .guaranteedOutput(new ItemStack(NVItems.DEMONITE_FRAGMENT.get(), 2))
                .chancedOutput(new ItemStack(NVItems.DEMONITE_FRAGMENT.get()), 0.25)
                .save(output, NeoVitae.rl("fragmentshellforged"));

        // Hellforged Sand from raw hellforged (cutting fluid)
        ARCRecipeBuilder.build(NVTags.Items.CUTTING_FLUIDS)
                .input(Ingredient.of(NVTags.Items.RAW_MATERIALS_HELLFORGED))
                .guaranteedOutput(new ItemStack(NVItems.HELLFORGED_SAND.get()))
                .chancedOutput(new ItemStack(NVItems.HELLFORGED_SAND.get()), 0.33)
                .save(output, NeoVitae.rl("dustsfrom_raw_hellforged"));

        // === OTHER UTILITY ===
        // Netherrack to Sulfur + Lava
        ARCRecipeBuilder.build(NVTags.Items.EXPLOSIVES)
                .input(Ingredient.of(Tags.Items.NETHERRACKS))
                .guaranteedOutput(new ItemStack(NVItems.SULFUR.get()))
                .fluidOutput(new FluidStack(Fluids.LAVA, 50))
                .save(output, NeoVitae.rl("netherrack_to_sulfur"));

        // === POTION TRANSFER RECIPES ===
        // Tipped Amethyst Throwing Dagger - 8 amethyst daggers + lingering flask = 8 tipped daggers
        // The lingering flask's potion effects are transferred to the output daggers
        ARCPotionRecipeBuilder.build(NVTags.Items.LINGERING_FLASK)
                .input(Ingredient.of(NVItems.THROWING_DAGGER_AMETHYST.get()))
                .guaranteedOutput(new ItemStack(NVItems.THROWING_DAGGER_TIPPED.get(), 8))
                .save(output, NeoVitae.rl("tipped_throwing_dagger"));
    }

    /**
     * Meteor recipes for the Meteor Ritual.
     * Each recipe defines a catalyst item, LP cost, explosion radius, and layers of blocks.
     */
    private void addMeteorRecipes(RecipeOutput output) {
        String basePath = "meteor/";

        // Iron Block Meteor - basic ores with cobblestone shell
        MeteorRecipeBuilder.meteor(Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON), 1000000, 14)
                .addLayer(new MeteorLayer(4, 0, Blocks.IRON_ORE)
                        .addShellBlock(Blocks.COBBLESTONE)
                        .addWeightedBlock(Blocks.GOLD_ORE, 30)
                        .addWeightedBlock(Blocks.COPPER_ORE, 200)
                        .addWeightedBlock(Blocks.LAPIS_ORE, 60)
                        .addWeightedBlock(Blocks.REDSTONE_ORE, 100))
                .addLayer(new MeteorLayer(7, 100, Blocks.STONE)
                        .setMinWeight(1000)
                        .addWeightedBlock(Blocks.IRON_ORE, 400)
                        .addWeightedBlock(Blocks.GOLD_ORE, 30)
                        .addWeightedBlock(Blocks.COPPER_ORE, 200)
                        .addWeightedBlock(Blocks.LAPIS_ORE, 60)
                        .addWeightedBlock(Blocks.REDSTONE_ORE, 100))
                .save(output, NeoVitae.rl(basePath + "iron"));

        // Stone Meteor - large but mostly stone/coal
        MeteorRecipeBuilder.meteor(Ingredient.of(Tags.Items.STONES), 1000000, 30)
                .addLayer(new MeteorLayer(16, 0, Blocks.STONE)
                        .setMinWeight(400)
                        .addShellBlock(Blocks.COBBLESTONE)
                        .addWeightedBlock(Blocks.COAL_ORE, 150)
                        .addWeightedBlock(Blocks.IRON_ORE, 50))
                .save(output, NeoVitae.rl(basePath + "stone"));

        // Diamond Meteor - small but diamond-rich
        MeteorRecipeBuilder.meteor(Ingredient.of(Tags.Items.GEMS_DIAMOND), 1000000, 8)
                .addLayer(new MeteorLayer(2, 0, Blocks.DIAMOND_ORE))
                .addLayer(new MeteorLayer(5, 0, Blocks.COBBLESTONE)
                        .setMinWeight(1000)
                        .addWeightedBlock(Blocks.DIAMOND_ORE, 100)
                        .addWeightedBlock(Blocks.EMERALD_ORE, 75))
                .save(output, NeoVitae.rl(basePath + "diamond"));

        // Nether Meteor - nether materials including ancient debris
        MeteorRecipeBuilder.meteor(Ingredient.of(Tags.Items.DUSTS_GLOWSTONE), 1000000, 12)
                .addLayer(new MeteorLayer(8, 0, Blocks.NETHERRACK)
                        .setMinWeight(500)
                        .addWeightedBlock(Blocks.GLOWSTONE, 100)
                        .addWeightedBlock(Blocks.NETHER_QUARTZ_ORE, 150)
                        .addWeightedBlock(Blocks.NETHER_GOLD_ORE, 60))
                .addLayer(new MeteorLayer(5, 0, Blocks.BLACKSTONE)
                        .addShellBlock(Blocks.GLOWSTONE)
                        .addWeightedBlock(Blocks.ANCIENT_DEBRIS, 60)
                        .setMinWeight(1000)
                        .addWeightedBlock(Blocks.CHISELED_POLISHED_BLACKSTONE, 300)
                        .addWeightedBlock(Blocks.GILDED_BLACKSTONE, 200)
                        .addWeightedBlock(Blocks.POLISHED_BLACKSTONE, 400))
                .save(output, NeoVitae.rl(basePath + "nether"));
    }

    // ==================== Flask Recipes ====================

    private void addFlaskRecipes(RecipeOutput output) {
        // ==================== Effect Recipes ====================
        // Basic effects - add a new effect to a flask

        // Movement effects
        FlaskRecipeBuilder.effect(MobEffects.MOVEMENT_SPEED, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.SUGAR)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("speed_boost"));

        FlaskRecipeBuilder.effect(MobEffects.JUMP, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.RABBIT_FOOT)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("jump_boost"));

        FlaskRecipeBuilder.effect(MobEffects.SLOW_FALLING, 1800)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.PHANTOM_MEMBRANE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("slow_fall"));

        // Resistance effects
        FlaskRecipeBuilder.effect(MobEffects.FIRE_RESISTANCE, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.MAGMA_CREAM)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("fire_resist"));

        FlaskRecipeBuilder.effect(MobEffects.WATER_BREATHING, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.PUFFERFISH)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("water_breathing"));

        // Combat effects
        FlaskRecipeBuilder.effect(MobEffects.DAMAGE_BOOST, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.BLAZE_POWDER)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("strength"));

        FlaskRecipeBuilder.effect(MobEffects.REGENERATION, 900)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GHAST_TEAR)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("regen"));

        FlaskRecipeBuilder.effect(MobEffects.HEAL, 0)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GLISTERING_MELON_SLICE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("health"));

        // Debuff effects
        FlaskRecipeBuilder.effect(MobEffects.WEAKNESS, 1800)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("weakness"));

        FlaskRecipeBuilder.effect(MobEffects.POISON, 900)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("poison"));

        // Vision effects
        FlaskRecipeBuilder.effect(MobEffects.NIGHT_VISION, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GOLDEN_CARROT)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("night_vision"));

        // NeoVitae custom effects
        FlaskRecipeBuilder.effect(NVMobEffects.PASSIVITY, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.HONEYCOMB)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("passivity"));

        FlaskRecipeBuilder.effect(NVMobEffects.BOUNCE, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.SLIME_BALL)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("bounce"));

        FlaskRecipeBuilder.effect(NVMobEffects.HARD_CLOAK, 3600)
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.OBSIDIAN)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("hard_cloak"));

        // ==================== Effect Transform Recipes ====================
        // Transform one effect into another using fermented spider eye or other items

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.MOVEMENT_SPEED)
                .outputEffect(MobEffects.MOVEMENT_SLOWDOWN, 1800)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("speed_to_slow"));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.JUMP)
                .outputEffect(MobEffects.MOVEMENT_SLOWDOWN, 1800)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("jump_to_slow"));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.HEAL)
                .outputEffect(MobEffects.HARM, 0)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("health_to_harm"));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.POISON)
                .outputEffect(MobEffects.HARM, 0)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("poison_to_harm"));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.NIGHT_VISION)
                .outputEffect(MobEffects.INVISIBILITY, 3600)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("night_to_invis"));

        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.SLOW_FALLING)
                .outputEffect(MobEffects.LEVITATION, 3600)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("fall_to_levitation"));

        // Night vision to spectral sight
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.NIGHT_VISION)
                .outputEffect(NVMobEffects.SPECTRAL_SIGHT, 3600)
                .addIngredient(Items.GLOWSTONE_DUST)
                .syphon(500).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("night_to_spectral"));

        // Jump boost to grounded
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(MobEffects.JUMP)
                .outputEffect(NVMobEffects.GROUNDED, 1800)
                .addIngredient(Items.COBWEB)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("jump_to_grounded"));

        // Grounded + slow falling = gravity (requires combinational catalyst)
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.GROUNDED)
                .inputEffect(MobEffects.SLOW_FALLING)
                .outputEffect(NVMobEffects.GRAVITY, 1800)
                .addIngredient(NVItems.COMBINATIONAL_CATALYST.get())
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("gravity"));

        // Gravity to suspended
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.GRAVITY)
                .outputEffect(NVMobEffects.SUSPENDED, 1800)
                .addIngredient(Items.FERMENTED_SPIDER_EYE)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("gravity_to_suspended"));

        // Suspended + levitation = flight (requires combinational catalyst)
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.SUSPENDED)
                .inputEffect(MobEffects.LEVITATION)
                .outputEffect(NVMobEffects.FLIGHT, 3600)
                .addIngredient(NVItems.COMBINATIONAL_CATALYST.get())
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("suspended_to_flight"));

        // Gravity + heal = heavy heart (requires combinational catalyst)
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.GRAVITY)
                .inputEffect(MobEffects.HEAL)
                .outputEffect(NVMobEffects.HEAVY_HEART, 1800)
                .addIngredient(NVItems.COMBINATIONAL_CATALYST.get())
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("gravity_to_heart"));

        // Hard cloak to obsidian cloak
        FlaskRecipeBuilder.effectTransform()
                .inputEffect(NVMobEffects.HARD_CLOAK)
                .outputEffect(NVMobEffects.OBSIDIAN_CLOAK, 3600)
                .addIngredient(Tags.Items.GEMS_DIAMOND)
                .addIngredient(Items.CRYING_OBSIDIAN)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("hard_to_obsidian"));

        // ==================== Potency Recipes ====================
        // Increase the amplifier (potency) of effects

        addPotencyRecipes(output, MobEffects.MOVEMENT_SPEED, "speed_boost");
        addPotencyRecipes(output, MobEffects.JUMP, "jump_boost");
        addPotencyRecipes(output, MobEffects.DAMAGE_BOOST, "strength");
        addPotencyRecipes(output, MobEffects.WEAKNESS, "weakness");
        addPotencyRecipes(output, MobEffects.POISON, "poison");
        addPotencyRecipes(output, MobEffects.REGENERATION, "regen");
        addPotencyRecipes(output, MobEffects.LEVITATION, "levitation");
        addPotencyRecipes(output, MobEffects.MOVEMENT_SLOWDOWN, "slowness");
        addPotencyRecipes(output, NVMobEffects.HARD_CLOAK, "hard_cloak");
        addPotencyRecipes(output, NVMobEffects.HEAVY_HEART, "heavy_heart");
        addPotencyRecipes(output, NVMobEffects.OBSIDIAN_CLOAK, "obsidian_cloak");
        addPotencyRecipes(output, NVMobEffects.GRAVITY, "gravity");
        addPotencyRecipes(output, NVMobEffects.FLIGHT, "flight");
        addPotencyRecipes(output, NVMobEffects.SPECTRAL_SIGHT, "spectral_sight");

        // Instant effects (health/harm) have different modifiers
        FlaskRecipeBuilder.potency(MobEffects.HEAL, 1, 0.5)
                .addIngredient(NVItems.MUNDANE_POWER_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, NeoVitae.rl("potency_health"));

        FlaskRecipeBuilder.potency(MobEffects.HARM, 1, 0.5)
                .addIngredient(NVItems.MUNDANE_POWER_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, NeoVitae.rl("potency_harm"));

        FlaskRecipeBuilder.potency(MobEffects.HEAL, 2, 0.25)
                .addIngredient(NVItems.AVERAGE_POWER_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(3)
                .save(output, NeoVitae.rl("potency_average_health"));

        FlaskRecipeBuilder.potency(MobEffects.HARM, 2, 0.25)
                .addIngredient(NVItems.AVERAGE_POWER_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(3)
                .save(output, NeoVitae.rl("potency_average_harm"));

        // ==================== Length Recipes ====================
        // Increase the duration modifier of effects (for effects that don't have potency tiers)

        addLengthRecipes(output, MobEffects.MOVEMENT_SPEED, "speed_boost");
        addLengthRecipes(output, MobEffects.JUMP, "jump_boost");
        addLengthRecipes(output, MobEffects.DAMAGE_BOOST, "strength");
        addLengthRecipes(output, MobEffects.WEAKNESS, "weakness");
        addLengthRecipes(output, MobEffects.POISON, "poison");
        addLengthRecipes(output, MobEffects.REGENERATION, "regen");
        addLengthRecipes(output, MobEffects.LEVITATION, "levitation");
        addLengthRecipes(output, MobEffects.MOVEMENT_SLOWDOWN, "slowness");
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
                .save(output, NeoVitae.rl("fill_weak"));

        FlaskRecipeBuilder.fill(3)
                .addIngredient(NVItems.STANDARD_FILLING_AGENT.get())
                .syphon(3000).ticks(200).minimumTier(0)
                .save(output, NeoVitae.rl("fill_standard"));

        // ==================== Flask Transform Recipes ====================
        // Transform flask type (regular -> splash -> lingering)

        FlaskRecipeBuilder.itemTransform(NVItems.ALCHEMY_FLASK_THROWABLE.get())
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.GUNPOWDER)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("flask_splash"));

        FlaskRecipeBuilder.itemTransform(NVItems.ALCHEMY_FLASK_LINGERING.get())
                .addIngredient(NVItems.SIMPLE_CATALYST.get())
                .addIngredient(Items.DRAGON_BREATH)
                .syphon(1000).ticks(200).minimumTier(1)
                .save(output, NeoVitae.rl("flask_lingering"));

        // ==================== Cycle Recipe ====================
        // Cycle the order of effects in the flask

        FlaskRecipeBuilder.cycle(1)
                .addIngredient(NVItems.CYCLING_CATALYST.get())
                .syphon(500).ticks(50).minimumTier(1)
                .save(output, NeoVitae.rl("cycle_basic"));
    }

    /**
     * Helper method to add potency recipes for effects that can be amplified
     */
    private void addPotencyRecipes(RecipeOutput output, Holder<MobEffect> effect, String name) {
        // Mundane potency (amplifier +1, duration modifier 0.5)
        FlaskRecipeBuilder.potency(effect, 1, 0.5)
                .addIngredient(NVItems.MUNDANE_POWER_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, NeoVitae.rl("potency_" + name));

        // Average potency (amplifier +2, duration modifier 0.25)
        FlaskRecipeBuilder.potency(effect, 2, 0.25)
                .addIngredient(NVItems.AVERAGE_POWER_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(4)
                .save(output, NeoVitae.rl("potency_average_" + name));
    }

    /**
     * Helper method to add length recipes for effects that can have their duration extended
     */
    private void addLengthRecipes(RecipeOutput output, Holder<MobEffect> effect, String name) {
        // Mundane length (duration modifier 2.6667x)
        FlaskRecipeBuilder.length(effect, 2.6667)
                .addIngredient(NVItems.MUNDANE_LENGTHENING_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, NeoVitae.rl("length_" + name));

        // Average length (duration modifier 7.1112x)
        FlaskRecipeBuilder.length(effect, 7.1112)
                .addIngredient(NVItems.AVERAGE_LENGTHENING_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(4)
                .save(output, NeoVitae.rl("length_average_" + name));
    }

    /**
     * Helper method to add length-only recipes for effects that only have duration modifiers
     */
    private void addLengthOnlyRecipes(RecipeOutput output, Holder<MobEffect> effect, String name) {
        // Mundane length (duration modifier 2.6667x)
        FlaskRecipeBuilder.length(effect, 2.6667)
                .addIngredient(NVItems.MUNDANE_LENGTHENING_CATALYST.get())
                .syphon(200).ticks(100).minimumTier(1)
                .save(output, NeoVitae.rl("length_" + name));

        // Average length (duration modifier 7.1112x)
        FlaskRecipeBuilder.length(effect, 7.1112)
                .addIngredient(NVItems.AVERAGE_LENGTHENING_CATALYST.get())
                .syphon(500).ticks(100).minimumTier(4)
                .save(output, NeoVitae.rl("length_average_" + name));
    }

    // ==================== Living Downgrade Recipes ====================

    private void addLivingDowngradeRecipes(RecipeOutput output) {
        String basePath = "downgrade/";

        // Battle Hungry - increases hunger drain
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.ROTTEN_FLESH),
                NeoVitae.rl("battle_hungry"))
                .save(output, NeoVitae.rl(basePath + "battle_hungry"));

        // Melee Decrease - reduces melee damage
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.STONE_SWORD),
                NeoVitae.rl("melee_decrease"))
                .save(output, NeoVitae.rl(basePath + "melee_decrease"));

        // Quenched - reduces saturation
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.GLASS_BOTTLE),
                NeoVitae.rl("quenched"))
                .save(output, NeoVitae.rl(basePath + "quenched"));

        // Storm Trooper - reduces arrow accuracy
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.ARROW),
                NeoVitae.rl("storm_trooper"))
                .save(output, NeoVitae.rl(basePath + "storm_trooper"));

        // Dig Slowdown - reduces mining speed
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.STONE_PICKAXE),
                NeoVitae.rl("dig_slowdown"))
                .save(output, NeoVitae.rl(basePath + "dig_slowdown"));

        // Slow Heal - reduces healing effectiveness
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.GHAST_TEAR),
                NeoVitae.rl("slow_heal"))
                .save(output, NeoVitae.rl(basePath + "slow_heal"));

        // Swim Decrease - reduces swim speed
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.WATER_BUCKET),
                NeoVitae.rl("swim_decrease"))
                .save(output, NeoVitae.rl(basePath + "swim_decrease"));

        // Speed Decrease - reduces movement speed
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.SOUL_SAND),
                NeoVitae.rl("speed_decrease"))
                .save(output, NeoVitae.rl(basePath + "speed_decrease"));

        // Crippled Arm - reduces blocking effectiveness
        LivingDowngradeRecipeBuilder.downgrade(
                Ingredient.of(Items.SHIELD),
                NeoVitae.rl("crippled_arm"))
                .save(output, NeoVitae.rl(basePath + "crippled_arm"));
    }

}
