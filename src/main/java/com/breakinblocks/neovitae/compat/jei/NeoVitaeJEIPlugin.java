package com.breakinblocks.neovitae.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.constants.VanillaTypes;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
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
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskEffectRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskEffectTransformRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.altar.AraVitaeRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.athanor.AthanorRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.array.AlchemyArrayCraftingCategory;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskCombinationCategory;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskCombinationJEIRecipe;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.bloodtank.BloodTankSubtypeInterpreter;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskSubtypeInterpreter;
import com.breakinblocks.neovitae.compat.jei.forge.HellfireForgeRecipeCategory;
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
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(NVItems.ALCHEMY_FLASK.get(), FlaskSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(NVItems.ALCHEMY_FLASK_THROWABLE.get(), FlaskSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(NVItems.ALCHEMY_FLASK_LINGERING.get(), FlaskSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(NVBlocks.BLOOD_TANK.item().get(), BloodTankSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelper = registration.getJeiHelpers();
        registration.addRecipeCategories(new HellfireForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AraVitaeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlchemyArrayCraftingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new TabulaVitaeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MeteorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AthanorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FlaskRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FlaskCombinationCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ImperfectRitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.HELLFIRE_FORGE.block().get()), HellfireForgeRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.ARA_VITAE.block().get()), AraVitaeRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVItems.ARCANE_SCRIBE_TOOL.get()), AlchemyArrayCraftingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.TABULA_VITAE.block().get()), TabulaVitaeRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.ATHANOR_BLOCK.block().get()), AthanorRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVItems.ALCHEMY_FLASK.get()), FlaskRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.TABULA_VITAE.block().get()), FlaskRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVItems.ALCHEMY_FLASK.get()), FlaskCombinationCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.TABULA_VITAE.block().get()), FlaskCombinationCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()), ImperfectRitualRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NVBlocks.MASTER_RITUAL_STONE.block().get()), RitualRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);

        List<ForgeRecipe> forgeRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.HELLFIRE_FORGE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(HellfireForgeRecipeCategory.RECIPE_TYPE, forgeRecipes);

        List<com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe> altarRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.ARA_VITAE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AraVitaeRecipeCategory.RECIPE_TYPE, altarRecipes);

        List<AlchemyArrayRecipe> arrayRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.ALCHEMY_ARRAY_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AlchemyArrayCraftingCategory.RECIPE_TYPE, arrayRecipes);

        List<TabulaVitaeRecipe> tableRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.TABULA_VITAE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(TabulaVitaeRecipeCategory.RECIPE_TYPE, tableRecipes);

        List<MeteorRecipe> meteorRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.METEOR_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(MeteorRecipeCategory.RECIPE_TYPE, meteorRecipes);

        List<AthanorRecipe> arcRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.ATHANOR_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AthanorRecipeCategory.RECIPE_TYPE, arcRecipes);

        List<FlaskRecipe> flaskRecipes = world.getRecipeManager()
                .getAllRecipesFor(NVRecipes.FLASK_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(FlaskRecipeCategory.RECIPE_TYPE, flaskRecipes);

        LOGGER.info("Registered {} flask recipes with JEI", flaskRecipes.size());

        List<FlaskCombinationJEIRecipe> combinationRecipes = createFlaskCombinationRecipes(flaskRecipes);
        registration.addRecipes(FlaskCombinationCategory.RECIPE_TYPE, combinationRecipes);
        LOGGER.info("Registered {} flask combination recipes with JEI", combinationRecipes.size());

        List<ImperfectRitualJEIRecipe> imperfectRitualRecipes = createImperfectRitualRecipes();
        registration.addRecipes(ImperfectRitualRecipeCategory.RECIPE_TYPE, imperfectRitualRecipes);

        List<RitualJEIRecipe> ritualRecipes = createRitualRecipes();
        registration.addRecipes(RitualRecipeCategory.RECIPE_TYPE, ritualRecipes);

        // Blood tank upgrade info
        List<ItemStack> bloodTankStacks = new ArrayList<>();
        for (int tier = 1; tier <= 16; tier++) {
            ItemStack stack = new ItemStack(NVBlocks.BLOOD_TANK.block().get());
            stack.set(NVDataComponents.CONTAINER_TIER, tier);
            bloodTankStacks.add(stack);
        }
        registration.addIngredientInfo(bloodTankStacks, VanillaTypes.ITEM_STACK,
                Component.translatable("jei.neovitae.blood_tank.upgrade_info"));

        List<ItemStack> orbStacks = java.util.List.of(
                new ItemStack(NVItems.ORB_WEAK.get()), new ItemStack(NVItems.ORB_APPRENTICE.get()),
                new ItemStack(NVItems.ORB_MAGICIAN.get()), new ItemStack(NVItems.ORB_MASTER.get()),
                new ItemStack(NVItems.ORB_ARCHMAGE.get()), new ItemStack(NVItems.ORB_TRANSCENDENT.get()));
        registration.addIngredientInfo(orbStacks, VanillaTypes.ITEM_STACK,
                Component.translatable("jei.neovitae.orb.info"));
    }

    private List<FlaskCombinationJEIRecipe> createFlaskCombinationRecipes(List<FlaskRecipe> allFlaskRecipes) {
        List<FlaskCombinationJEIRecipe> combinations = new ArrayList<>();

        List<FlaskEffectRecipe> effectRecipes = allFlaskRecipes.stream()
                .filter(r -> r instanceof FlaskEffectRecipe)
                .map(r -> (FlaskEffectRecipe) r)
                .toList();

        // Collect all effects that can exist in a flask from all recipe types
        java.util.Set<Holder<MobEffect>> allEffects = new java.util.LinkedHashSet<>();
        for (FlaskRecipe recipe : allFlaskRecipes) {
            if (recipe instanceof FlaskEffectRecipe er) {
                allEffects.add(er.getOutputEffect());
            } else if (recipe instanceof FlaskEffectTransformRecipe tr) {
                for (com.mojang.datafixers.util.Pair<Holder<MobEffect>, Integer> pair : tr.getOutputEffects()) {
                    allEffects.add(pair.getFirst());
                }
            }
        }

        for (FlaskEffectRecipe recipe : effectRecipes) {
            for (Holder<MobEffect> baseEffect : allEffects) {
                if (baseEffect.equals(recipe.getOutputEffect())) continue;

                EffectHolder baseHolder = EffectHolder.create(baseEffect, 3600, 0);

                ItemStack inputFlask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
                ItemAlchemyFlask.setFlaskEffects(inputFlask, new FlaskEffects(List.of(baseHolder)));

                List<EffectHolder> combinedEffects = new ArrayList<>();
                combinedEffects.add(baseHolder);
                combinedEffects.add(EffectHolder.create(recipe.getOutputEffect(), recipe.getBaseDuration(), 0));
                ItemStack outputFlask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
                ItemAlchemyFlask.setFlaskEffects(outputFlask, new FlaskEffects(combinedEffects));

                combinations.add(new FlaskCombinationJEIRecipe(
                        inputFlask,
                        recipe.getInput(),
                        outputFlask,
                        recipe.getSyphon(),
                        recipe.getTicks(),
                        recipe.getMinimumTier()
                ));
            }
        }

        return combinations;
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
