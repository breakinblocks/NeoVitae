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
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
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
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.HELLFIRE_FORGE.block().get()), SoulForgeRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.BLOOD_ALTAR.block().get()), BloodAltarRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVItems.ARCANE_ASHES.get()), AlchemyArrayCraftingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.ALCHEMY_TABLE.block().get()), AlchemyTableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.ARC_BLOCK.block().get()), ARCRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVItems.ALCHEMY_FLASK.get()), FlaskRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.ALCHEMY_TABLE.block().get()), FlaskRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()), ImperfectRitualRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.MASTER_RITUAL_STONE.block().get()), RitualRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);

        List<ForgeRecipe> forgeRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.SOUL_FORGE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(SoulForgeRecipeCategory.RECIPE_TYPE, forgeRecipes);

        List<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe> altarRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.BLOOD_ALTAR_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(BloodAltarRecipeCategory.RECIPE_TYPE, altarRecipes);

        List<AlchemyArrayRecipe> arrayRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.ALCHEMY_ARRAY_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AlchemyArrayCraftingCategory.RECIPE_TYPE, arrayRecipes);

        List<AlchemyTableRecipe> tableRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.ALCHEMY_TABLE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AlchemyTableRecipeCategory.RECIPE_TYPE, tableRecipes);

        List<MeteorRecipe> meteorRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.METEOR_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(MeteorRecipeCategory.RECIPE_TYPE, meteorRecipes);

        List<ARCRecipe> arcRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.ARC_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(ARCRecipeCategory.RECIPE_TYPE, arcRecipes);

        List<FlaskRecipe> flaskRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.FLASK_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(FlaskRecipeCategory.RECIPE_TYPE, flaskRecipes);

        List<ImperfectRitualJEIRecipe> imperfectRitualRecipes = createImperfectRitualRecipes();
        registration.addRecipes(ImperfectRitualRecipeCategory.RECIPE_TYPE, imperfectRitualRecipes);

        List<RitualJEIRecipe> ritualRecipes = createRitualRecipes();
        registration.addRecipes(RitualRecipeCategory.RECIPE_TYPE, ritualRecipes);
    }

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

            Holder<ImperfectRitual> holder = registry.wrapAsHolder(ritual);
            ImperfectRitualStats stats = holder.getData(NVDataMaps.IMPERFECT_RITUAL_STATS);

            List<ItemStack> catalystBlocks = new ArrayList<>();
            int activationCost = ritual.getActivationCost();
            boolean consumesBlock = false;

            if (stats != null) {
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

            if (catalystBlocks.isEmpty()) {
                LOGGER.warn("No catalyst blocks found for ritual: {}, skipping", ritualId);
                continue;
            }

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

    private ItemStack getDisplayItemForBlock(Block block) {
        if (block == Blocks.WATER) {
            return new ItemStack(Items.WATER_BUCKET);
        }
        if (block == Blocks.LAVA) {
            return new ItemStack(Items.LAVA_BUCKET);
        }

        if (block instanceof LiquidBlock liquidBlock) {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof BucketItem bucketItem) {
                    if (bucketItem.content.isSame(liquidBlock.fluid)) {
                        return new ItemStack(bucketItem);
                    }
                }
            }
            LOGGER.warn("No bucket found for fluid block: {}", BuiltInRegistries.BLOCK.getKey(block));
        }

        return new ItemStack(block);
    }

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

            Holder<Ritual> holder = registry.wrapAsHolder(ritual);
            RitualStats stats = holder.getData(NVDataMaps.RITUAL_STATS);
            if (stats != null && !stats.enabled()) {
                LOGGER.debug("Skipping disabled ritual: {}", ritualId);
                continue;
            }

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
