package com.breakinblocks.neovitae.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableRecipe;
import com.breakinblocks.neovitae.common.recipe.arc.ARCRecipe;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.compat.jei.alchemytable.AlchemyTableRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.altar.BloodAltarRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.arc.ARCRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.array.AlchemyArrayCraftingCategory;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.forge.SoulForgeRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.imperfectritual.ImperfectRitualJEIRecipe;
import com.breakinblocks.neovitae.compat.jei.imperfectritual.ImperfectRitualRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.meteor.MeteorRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.ritual.RitualJEIRecipe;
import com.breakinblocks.neovitae.compat.jei.ritual.RitualRecipeCategory;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class NeoVitaeJEIPlugin implements IModPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoVitaeJEIPlugin.class);
    public static IJeiHelpers jeiHelper;
    private static final ResourceLocation ID = NeoVitae.rl("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelper = registration.getJeiHelpers();
        registration.addRecipeCategories(new SoulForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new BloodAltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlchemyArrayCraftingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlchemyTableRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MeteorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ARCRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FlaskRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ImperfectRitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BMBlocks.HELLFIRE_FORGE.block().get()), SoulForgeRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BMBlocks.BLOOD_ALTAR.block().get()), BloodAltarRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BMItems.ARCANE_ASHES.get()), AlchemyArrayCraftingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BMBlocks.ALCHEMY_TABLE.block().get()), AlchemyTableRecipeCategory.RECIPE_TYPE);
        // Meteor recipes are accessed via the catalyst item (U key on the ore/item), not the ritual stone
        registration.addRecipeCatalyst(new ItemStack(BMBlocks.ARC_BLOCK.block().get()), ARCRecipeCategory.RECIPE_TYPE);
        // Flask recipes use the alchemy table with a flask
        registration.addRecipeCatalyst(new ItemStack(BMItems.ALCHEMY_FLASK.get()), FlaskRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BMBlocks.ALCHEMY_TABLE.block().get()), FlaskRecipeCategory.RECIPE_TYPE);
        // Imperfect ritual stone
        registration.addRecipeCatalyst(new ItemStack(BMBlocks.IMPERFECT_RITUAL_STONE.block().get()), ImperfectRitualRecipeCategory.RECIPE_TYPE);
        // Master ritual stone for ritual recipes
        registration.addRecipeCatalyst(new ItemStack(BMBlocks.MASTER_RITUAL_STONE.block().get()), RitualRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);

        // Soul Forge recipes
        List<ForgeRecipe> forgeRecipes = world.getRecipeManager()
                .getAllRecipesFor(BMRecipes.SOUL_FORGE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(SoulForgeRecipeCategory.RECIPE_TYPE, forgeRecipes);

        // Blood Altar recipes
        List<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe> altarRecipes = world.getRecipeManager()
                .getAllRecipesFor(BMRecipes.BLOOD_ALTAR_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(BloodAltarRecipeCategory.RECIPE_TYPE, altarRecipes);

        // Alchemy Array recipes
        List<AlchemyArrayRecipe> arrayRecipes = world.getRecipeManager()
                .getAllRecipesFor(BMRecipes.ALCHEMY_ARRAY_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AlchemyArrayCraftingCategory.RECIPE_TYPE, arrayRecipes);

        // Alchemy Table recipes
        List<AlchemyTableRecipe> tableRecipes = world.getRecipeManager()
                .getAllRecipesFor(BMRecipes.ALCHEMY_TABLE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AlchemyTableRecipeCategory.RECIPE_TYPE, tableRecipes);

        // Meteor recipes
        List<MeteorRecipe> meteorRecipes = world.getRecipeManager()
                .getAllRecipesFor(BMRecipes.METEOR_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(MeteorRecipeCategory.RECIPE_TYPE, meteorRecipes);

        // ARC recipes
        List<ARCRecipe> arcRecipes = world.getRecipeManager()
                .getAllRecipesFor(BMRecipes.ARC_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(ARCRecipeCategory.RECIPE_TYPE, arcRecipes);

        // Flask recipes
        List<FlaskRecipe> flaskRecipes = world.getRecipeManager()
                .getAllRecipesFor(BMRecipes.FLASK_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(FlaskRecipeCategory.RECIPE_TYPE, flaskRecipes);

        // Imperfect Ritual recipes
        List<ImperfectRitualJEIRecipe> imperfectRitualRecipes = createImperfectRitualRecipes();
        registration.addRecipes(ImperfectRitualRecipeCategory.RECIPE_TYPE, imperfectRitualRecipes);

        // Ritual recipes (full rituals)
        List<RitualJEIRecipe> ritualRecipes = createRitualRecipes();
        registration.addRecipes(RitualRecipeCategory.RECIPE_TYPE, ritualRecipes);
    }

    /**
     * Creates JEI recipe entries from registered imperfect rituals.
     * Reads block requirements from DataMaps when available.
     */
    private List<ImperfectRitualJEIRecipe> createImperfectRitualRecipes() {
        List<ImperfectRitualJEIRecipe> recipes = new ArrayList<>();
        Registry<ImperfectRitual> registry = RitualRegistry.getImperfectRitualRegistry();

        if (registry == null) {
            LOGGER.warn("Imperfect ritual registry is null - cannot create JEI recipes");
            return recipes;
        }

        LOGGER.info("Creating imperfect ritual JEI recipes. Registry size: {}", registry.size());

        for (ImperfectRitual ritual : registry) {
            ResourceLocation ritualId = registry.getKey(ritual);
            if (ritualId == null) {
                LOGGER.warn("Ritual has null ID, skipping");
                continue;
            }

            LOGGER.debug("Processing imperfect ritual: {}", ritualId);

            // Get stats from DataMap
            Holder<ImperfectRitual> holder = registry.wrapAsHolder(ritual);
            ImperfectRitualStats stats = holder.getData(BMDataMaps.IMPERFECT_RITUAL_STATS);

            // Determine catalyst block(s)
            List<ItemStack> catalystBlocks = new ArrayList<>();
            int activationCost = ritual.getActivationCost();
            boolean consumesBlock = false;

            if (stats != null) {
                // Skip disabled rituals in JEI
                if (!stats.enabled()) {
                    LOGGER.debug("Skipping disabled imperfect ritual: {}", ritualId);
                    continue;
                }

                LOGGER.debug("Found stats for {}: cost={}, block={}, blockTag={}",
                        ritualId, stats.activationCost(), stats.block(), stats.blockTag());
                activationCost = stats.activationCost();
                consumesBlock = stats.consumeBlock();

                if (stats.block().isPresent()) {
                    ItemStack displayStack = getDisplayItemForBlock(stats.block().get());
                    catalystBlocks.add(displayStack);
                } else if (stats.blockTag().isPresent()) {
                    // Get all blocks from the tag
                    TagKey<Block> tag = stats.blockTag().get();
                    BuiltInRegistries.BLOCK.getTag(tag).ifPresent(holders -> {
                        for (Holder<Block> blockHolder : holders) {
                            ItemStack displayStack = getDisplayItemForBlock(blockHolder.value());
                            catalystBlocks.add(displayStack);
                        }
                    });
                }
            } else {
                LOGGER.warn("No stats found in DataMap for ritual: {}", ritualId);
            }

            // If no catalyst determined from stats, skip (we can't display without knowing the block)
            if (catalystBlocks.isEmpty()) {
                LOGGER.warn("No catalyst blocks found for ritual: {}, skipping", ritualId);
                continue;
            }

            // Create description from translation key
            Component description = Component.translatable(ritual.getTranslationKey() + ".desc");

            recipes.add(new ImperfectRitualJEIRecipe(
                    ritualId,
                    catalystBlocks,
                    activationCost,
                    description,
                    consumesBlock
            ));
            LOGGER.debug("Added JEI recipe for imperfect ritual: {}", ritualId);
        }

        LOGGER.info("Created {} imperfect ritual JEI recipes", recipes.size());
        return recipes;
    }

    /**
     * Gets the appropriate display item for a block in JEI.
     * Converts fluid source blocks (water, lava) to their bucket items since
     * fluid blocks don't have proper item representations.
     */
    private ItemStack getDisplayItemForBlock(Block block) {
        // Handle fluid source blocks - show buckets instead
        if (block == Blocks.WATER) {
            return new ItemStack(Items.WATER_BUCKET);
        }
        if (block == Blocks.LAVA) {
            return new ItemStack(Items.LAVA_BUCKET);
        }

        // For other liquid blocks, try to find the corresponding bucket
        if (block instanceof LiquidBlock liquidBlock) {
            // Search for a bucket item that contains this fluid
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof BucketItem bucketItem) {
                    if (bucketItem.content.isSame(liquidBlock.fluid)) {
                        return new ItemStack(bucketItem);
                    }
                }
            }
            // Fallback - fluid block with no bucket, just show empty (shouldn't happen often)
            LOGGER.warn("No bucket found for fluid block: {}", BuiltInRegistries.BLOCK.getKey(block));
        }

        // Normal block - return its item form
        return new ItemStack(block);
    }

    /**
     * Creates JEI recipe entries from registered rituals.
     */
    private List<RitualJEIRecipe> createRitualRecipes() {
        List<RitualJEIRecipe> recipes = new ArrayList<>();
        Registry<Ritual> registry = RitualRegistry.getRitualRegistry();

        if (registry == null) {
            LOGGER.warn("Ritual registry is null - cannot create JEI recipes");
            return recipes;
        }

        LOGGER.info("Creating ritual JEI recipes. Registry size: {}", registry.size());

        for (Ritual ritual : registry) {
            ResourceLocation ritualId = registry.getKey(ritual);
            if (ritualId == null) {
                LOGGER.warn("Ritual has null ID, skipping");
                continue;
            }

            LOGGER.debug("Processing ritual: {}", ritualId);

            // Check if ritual is disabled via DataMap
            Holder<Ritual> holder = registry.wrapAsHolder(ritual);
            RitualStats stats = holder.getData(BMDataMaps.RITUAL_STATS);
            if (stats != null && !stats.enabled()) {
                LOGGER.debug("Skipping disabled ritual: {}", ritualId);
                continue;
            }

            // Gather ritual components
            List<RitualComponent> components = new ArrayList<>();
            ritual.gatherComponents(components::add);

            recipes.add(RitualJEIRecipe.create(
                    ritualId,
                    ritual.getTranslationKey(),
                    ritual.getActivationCost(),
                    ritual.getRefreshCost(),
                    ritual.getCrystalLevel(),
                    components
            ));
            LOGGER.debug("Added JEI recipe for ritual: {} with {} components", ritualId, components.size());
        }

        LOGGER.info("Created {} ritual JEI recipes", recipes.size());
        return recipes;
    }
}
