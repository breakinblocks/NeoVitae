package com.breakinblocks.neovitae.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import com.mojang.datafixers.util.Pair;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.screen.RoutingNodeScreen;
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
import com.breakinblocks.neovitae.common.recipe.forge.ForgeSpiritusInfusionRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeTransformRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeUpgradeRecipe;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.altar.AraVitaeRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.athanor.AthanorRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.array.AlchemyArrayCraftingCategory;
import com.breakinblocks.neovitae.compat.jei.array.AlchemyArrayEffectCategory;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskCombinationCategory;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskCombinationJEIRecipe;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.bloodtank.BloodTankSubtypeInterpreter;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskSubtypeInterpreter;
import com.breakinblocks.neovitae.compat.jei.tome.UpgradeTomeSubtypeInterpreter;
import com.breakinblocks.neovitae.compat.jei.forge.ForgeUpgradeRecipeCategory;
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

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@JeiPlugin
public class NeoVitaeJEIPlugin implements IModPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoVitaeJEIPlugin.class);
    public static IJeiHelpers jeiHelper;
    public static IJeiRuntime jeiRuntime;
    private static final Identifier ID = NeoVitae.rl("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(RoutingNodeScreen.class, new RoutingNodeGhostHandler());
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAliases(
                VanillaTypes.ITEM_STACK,
                new ItemStack(NVItems.GUIDE_BOOK.get()),
                List.of("guide", "guidebook", "manual", "wiki", "scriptura", "scriptura vitae", "neovitae")
        );
        registration.addAliases(
                VanillaTypes.ITEM_STACK,
                new ItemStack(NVBlocks.ARA_VITAE.block().get()),
                List.of("altar", "blood altar", "vitae altar")
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(NVItems.ALCHEMY_FLASK.get(), FlaskSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(NVItems.ALCHEMY_FLASK_THROWABLE.get(), FlaskSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(NVItems.ALCHEMY_FLASK_LINGERING.get(), FlaskSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(NVBlocks.BLOOD_TANK.item().get(), BloodTankSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(NVItems.UPGRADE_TOME.get(), UpgradeTomeSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelper = registration.getJeiHelpers();
        registration.addRecipeCategories(new HellfireForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ForgeUpgradeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AraVitaeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlchemyArrayCraftingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AlchemyArrayEffectCategory(registration.getJeiHelpers().getGuiHelper()));
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
        registration.addCraftingStation(HellfireForgeRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.HELLFIRE_FORGE.block().get()));
        registration.addCraftingStation(ForgeUpgradeRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.HELLFIRE_FORGE.block().get()));
        registration.addCraftingStation(AraVitaeRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.ARA_VITAE.block().get()));
        registration.addCraftingStation(AlchemyArrayCraftingCategory.RECIPE_TYPE, new ItemStack(NVItems.ARCANE_SCRIBE_TOOL.get()));
        registration.addCraftingStation(AlchemyArrayEffectCategory.RECIPE_TYPE, new ItemStack(NVItems.ARCANE_SCRIBE_TOOL.get()));
        registration.addCraftingStation(TabulaVitaeRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.TABULA_VITAE.block().get()));
        registration.addCraftingStation(AthanorRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.ATHANOR_BLOCK.block().get()));
        registration.addCraftingStation(FlaskRecipeCategory.RECIPE_TYPE, new ItemStack(NVItems.ALCHEMY_FLASK.get()));
        registration.addCraftingStation(FlaskRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.TABULA_VITAE.block().get()));
        registration.addCraftingStation(FlaskCombinationCategory.RECIPE_TYPE, new ItemStack(NVItems.ALCHEMY_FLASK.get()));
        registration.addCraftingStation(FlaskCombinationCategory.RECIPE_TYPE, new ItemStack(NVBlocks.TABULA_VITAE.block().get()));
        registration.addCraftingStation(ImperfectRitualRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()));
        registration.addCraftingStation(RitualRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.MASTER_RITUAL_STONE.block().get()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeMap syncedRecipes = ClientRecipeCache.get();

        List<ForgeRecipe> allForgeRecipes = syncedRecipes.byType(NVRecipes.HELLFIRE_FORGE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        List<ForgeRecipe> upgradeRecipes = allForgeRecipes.stream()
                .filter(r -> r instanceof ForgeUpgradeRecipe || r instanceof ForgeTransformRecipe || r instanceof ForgeSpiritusInfusionRecipe)
                .toList();
        List<ForgeRecipe> forgeRecipes = allForgeRecipes.stream()
                .filter(r -> !(r instanceof ForgeUpgradeRecipe) && !(r instanceof ForgeTransformRecipe) && !(r instanceof ForgeSpiritusInfusionRecipe))
                .toList();
        registration.addRecipes(HellfireForgeRecipeCategory.RECIPE_TYPE, forgeRecipes);
        registration.addRecipes(ForgeUpgradeRecipeCategory.RECIPE_TYPE, upgradeRecipes);

        List<AraVitaeRecipe> altarRecipes = syncedRecipes.byType(NVRecipes.ARA_VITAE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(AraVitaeRecipeCategory.RECIPE_TYPE, altarRecipes);

        List<AlchemyArrayRecipe> allArrayRecipes = syncedRecipes.byType(NVRecipes.ALCHEMY_ARRAY_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        List<AlchemyArrayRecipe> arrayCraftingRecipes = allArrayRecipes.stream()
                .filter(r -> r.getEffectType() == AlchemyArrayEffectType.CRAFTING
                        || r.getEffectType() == AlchemyArrayEffectType.BINDING)
                .toList();
        List<AlchemyArrayRecipe> arrayEffectRecipes = allArrayRecipes.stream()
                .filter(r -> r.getEffectType() != AlchemyArrayEffectType.CRAFTING
                        && r.getEffectType() != AlchemyArrayEffectType.BINDING)
                .toList();
        registration.addRecipes(AlchemyArrayCraftingCategory.RECIPE_TYPE, arrayCraftingRecipes);
        registration.addRecipes(AlchemyArrayEffectCategory.RECIPE_TYPE, arrayEffectRecipes);

        List<TabulaVitaeRecipe> tableRecipes = syncedRecipes.byType(NVRecipes.TABULA_VITAE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(TabulaVitaeRecipeCategory.RECIPE_TYPE, tableRecipes);

        List<MeteorRecipe> meteorRecipes = syncedRecipes.byType(NVRecipes.METEOR_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(MeteorRecipeCategory.RECIPE_TYPE, meteorRecipes);

        List<AthanorRecipe> arcRecipes = syncedRecipes.byType(NVRecipes.ATHANOR_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(AthanorRecipeCategory.RECIPE_TYPE, arcRecipes);

        List<FlaskRecipe> flaskRecipes = syncedRecipes.byType(NVRecipes.FLASK_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(FlaskRecipeCategory.RECIPE_TYPE, flaskRecipes);

        List<FlaskCombinationJEIRecipe> combinationRecipes = createFlaskCombinationRecipes(flaskRecipes);
        registration.addRecipes(FlaskCombinationCategory.RECIPE_TYPE, combinationRecipes);

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

        List<ItemStack> orbStacks = List.of(
                new ItemStack(NVItems.ORB_WEAK.get()), new ItemStack(NVItems.ORB_APPRENTICE.get()),
                new ItemStack(NVItems.ORB_MAGICIAN.get()), new ItemStack(NVItems.ORB_MASTER.get()),
                new ItemStack(NVItems.ORB_ARCHMAGE.get()), new ItemStack(NVItems.ORB_TRANSCENDENT.get()));
        registration.addIngredientInfo(orbStacks, VanillaTypes.ITEM_STACK,
                Component.translatable("jei.neovitae.orb.info"));

        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            HolderLookup.RegistryLookup<SentientUpgrade> upgradeRegistry = world.registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES);
            addTomeInfo(registration, upgradeRegistry, NVTags.Sentient.IS_SCRAPPABLE);
            addTomeInfo(registration, upgradeRegistry, NVTags.Sentient.IS_DOWNGRADE);
        }

        List<RecipeHolder<CraftingRecipe>> scribeDyeRecipes = new ArrayList<>();
        for (DyeColor color : DyeColor.values()) {
            Item dyeItem = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color.getSerializedName() + "_dye"));
            if (dyeItem == null || dyeItem == Items.AIR) continue;
            ItemStack result = new ItemStack(NVItems.ARCANE_SCRIBE_TOOL.get());
            result.set(NVDataComponents.ALCHEMY_ARRAY_COLOR.get(), color);
            List<Ingredient> ingredients = List.of(
                    Ingredient.of(NVItems.ARCANE_SCRIBE_TOOL.get()),
                    Ingredient.of(dyeItem));
            ShapelessRecipe recipe = new ShapelessRecipe(
                    new Recipe.CommonInfo(true),
                    new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "neovitae"),
                    ItemStackTemplate.fromNonEmptyStack(result),
                    ingredients);
            ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, NeoVitae.rl("arcane_scribe_dye_" + color.getSerializedName()));
            scribeDyeRecipes.add(new RecipeHolder<>(key, recipe));
        }
        registration.addRecipes(RecipeTypes.CRAFTING, scribeDyeRecipes);
    }

    private void addTomeInfo(IRecipeRegistration registration, HolderLookup.RegistryLookup<SentientUpgrade> registry, TagKey<SentientUpgrade> tag) {
        registry.get(tag).ifPresent(set -> set.forEach(holder -> holder.unwrapKey().ifPresent(key -> {
            ItemStack tome = new ItemStack(NVItems.UPGRADE_TOME.get());
            tome.set(NVDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(holder, 0f));
            registration.addIngredientInfo(tome, VanillaTypes.ITEM_STACK,
                    Component.translatable("jei.neovitae.upgrade_tome." + key.identifier().getPath() + ".info"));
        })));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;

        runtime.getIngredientManager().removeIngredientsAtRuntime(
                VanillaTypes.ITEM_STACK,
                List.of(
                        new ItemStack(NVItems.BOSS_KEY.get()),
                        new ItemStack(NVBlocks.SPATIAL_RIFT.item().get()),
                        new ItemStack(NVItems.RAW_SPIRITUS.get()),
                        new ItemStack(NVItems.MONSTER_SOUL_RAW.get()),
                        new ItemStack(NVItems.MONSTER_SOUL_RUINA.get()),
                        new ItemStack(NVItems.MONSTER_SOUL_NIHILUM.get()),
                        new ItemStack(NVItems.MONSTER_SOUL_VINDICTA.get()),
                        new ItemStack(NVItems.MONSTER_SOUL_INVICTUS.get())
                )
        );
    }

    private List<FlaskCombinationJEIRecipe> createFlaskCombinationRecipes(List<FlaskRecipe> allFlaskRecipes) {
        List<FlaskCombinationJEIRecipe> combinations = new ArrayList<>();

        List<FlaskEffectRecipe> effectRecipes = allFlaskRecipes.stream()
                .filter(r -> r instanceof FlaskEffectRecipe)
                .map(r -> (FlaskEffectRecipe) r)
                .toList();

        // Collect all effects that can exist in a flask from all recipe types
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
            Identifier ritualId = registry.getKey(ritual);
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
                    for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
                        ItemStack displayStack = getDisplayItemForBlock(blockHolder.value());
                        catalystBlocks.add(displayStack);
                    }
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
            Identifier ritualId = registry.getKey(ritual);
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

            Minecraft mc = Minecraft.getInstance();
            Level level = mc != null ? mc.level : null;
            List<RitualComponent> components = RitualLayouts.get(level, ritual);

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
