package com.breakinblocks.neovitae.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
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
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.enchantment.ItemEnchantments;
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
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.compat.jei.crystal.CrystalGrowthCategory;
import com.breakinblocks.neovitae.compat.jei.crystal.CrystalGrowthJEIRecipe;
import com.breakinblocks.neovitae.compat.jei.tabulavitae.TabulaVitaeRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.altar.AraVitaeRecipeCategory;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import com.breakinblocks.neovitae.common.menu.AthanorMenu;
import com.breakinblocks.neovitae.common.menu.HellfireForgeMenu;
import com.breakinblocks.neovitae.common.menu.NVMenus;
import com.breakinblocks.neovitae.compat.jei.athanor.AthanorRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.athanor.DisenchantCategory;
import com.breakinblocks.neovitae.compat.jei.athanor.DisenchantJEIRecipe;
import com.breakinblocks.neovitae.compat.jei.array.AlchemyArrayCraftingCategory;
import com.breakinblocks.neovitae.compat.jei.array.AlchemyArrayEffectCategory;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskCombinationCategory;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskCombinationJEIRecipe;
import com.breakinblocks.neovitae.compat.jei.flask.FlaskRecipeCategory;
import com.breakinblocks.neovitae.compat.jei.bloodtank.BloodTankSubtypeInterpreter;
import com.breakinblocks.neovitae.compat.jei.bloodtank.BloodTankUpgradeCategory;
import com.breakinblocks.neovitae.compat.jei.bloodtank.BloodTankUpgradeJEIRecipe;
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
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.BLANK_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "blank rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.AIR_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "air rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.WATER_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "water rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.FIRE_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "fire rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.EARTH_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "earth rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.TENEBRAE_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "tenebrae rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.DEUS_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "deus rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.MASTER_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "master rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.INVERTED_MASTER_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "inverted rune"));
        registration.addAliases(VanillaTypes.ITEM_STACK, new ItemStack(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()), List.of("rune", "ritual rune", "imperfect rune"));
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
        registration.addRecipeCategories(new BloodTankUpgradeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new DisenchantCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CrystalGrowthCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(HellfireForgeMenu.class, NVMenus.HELLFIRE_FORGE.get(),
                HellfireForgeRecipeCategory.RECIPE_TYPE, 0, 4, 6, 36);

        int athanorInventoryStart = 1 + AthanorBlockEntity.NUM_INPUTS + 2 + AthanorBlockEntity.NUM_OUTPUTS;
        registration.addRecipeTransferHandler(AthanorMenu.class, NVMenus.ARC.get(),
                AthanorRecipeCategory.RECIPE_TYPE, AthanorBlockEntity.INPUT_START, AthanorBlockEntity.NUM_INPUTS,
                athanorInventoryStart, 36);
    }

    private static List<CrystalGrowthJEIRecipe> buildCrystalGrowthRecipes() {
        double toForm = NeoVitae.SERVER_CONFIG.CRYSTAL_SPIRITUS_TO_FORM.get();
        int formTicks = (int) Math.round(NeoVitae.SERVER_CONFIG.CRYSTAL_FORMATION_TIME.get());
        double perSegment = NeoVitae.SERVER_CONFIG.CRYSTAL_SAME_SPIRITUS_RATE.get();
        int maxSegments = NeoVitae.SERVER_CONFIG.CRYSTAL_MAX_COUNT.get();
        int harvest = BlockSpiritusCrystal.HARVEST_SPIRITUS_REQUIRED;

        List<CrystalGrowthJEIRecipe> recipes = new ArrayList<>();
        recipes.add(new CrystalGrowthJEIRecipe(SpiritusType.RAW,
                new ItemStack(NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get()),
                new ItemStack(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get()),
                toForm, formTicks, perSegment, maxSegments, harvest));
        recipes.add(new CrystalGrowthJEIRecipe(SpiritusType.RUINA,
                new ItemStack(NVBlocks.SPIRITUS_RUINA_CRYSTAL.block().get()),
                new ItemStack(NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get()),
                toForm, formTicks, perSegment, maxSegments, harvest));
        recipes.add(new CrystalGrowthJEIRecipe(SpiritusType.NIHILUM,
                new ItemStack(NVBlocks.SPIRITUS_NIHILUM_CRYSTAL.block().get()),
                new ItemStack(NVItems.SPIRITUS_NIHILUM_CRYSTAL_ITEM.get()),
                toForm, formTicks, perSegment, maxSegments, harvest));
        recipes.add(new CrystalGrowthJEIRecipe(SpiritusType.VINDICTA,
                new ItemStack(NVBlocks.SPIRITUS_VINDICTA_CRYSTAL.block().get()),
                new ItemStack(NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get()),
                toForm, formTicks, perSegment, maxSegments, harvest));
        recipes.add(new CrystalGrowthJEIRecipe(SpiritusType.INVICTUS,
                new ItemStack(NVBlocks.SPIRITUS_INVICTUS_CRYSTAL.block().get()),
                new ItemStack(NVItems.SPIRITUS_INVICTUS_CRYSTAL_ITEM.get()),
                toForm, formTicks, perSegment, maxSegments, harvest));
        return recipes;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(HellfireForgeRecipeCategory.RECIPE_TYPE, new ItemStack(NVBlocks.HELLFIRE_FORGE.block().get()));
        registration.addCraftingStation(CrystalGrowthCategory.RECIPE_TYPE, new ItemStack(NVBlocks.CRYSTALLARIUM_MALEFICUM.block().get()));
        registration.addCraftingStation(CrystalGrowthCategory.RECIPE_TYPE, new ItemStack(NVBlocks.VAS_MALEFICUM.block().get()));
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
        registration.addCraftingStation(BloodTankUpgradeCategory.RECIPE_TYPE, new ItemStack(Items.CRAFTING_TABLE));
        registration.addCraftingStation(DisenchantCategory.RECIPE_TYPE, new ItemStack(NVItems.SANGUINE_REVERTER.get()));
    }

    private static ItemStack bloodTankStack(int tier) {
        ItemStack stack = new ItemStack(NVBlocks.BLOOD_TANK.block().get());
        stack.set(NVDataComponents.CONTAINER_TIER, tier);
        return stack;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        NVJeiRecipeIds.clear();
        RecipeMap syncedRecipes = ClientRecipeCache.get();

        List<ForgeRecipe> allForgeRecipes = NVJeiRecipeIds.track(syncedRecipes.byType(NVRecipes.HELLFIRE_FORGE_TYPE.get()));
        List<ForgeRecipe> upgradeRecipes = allForgeRecipes.stream()
                .filter(r -> r instanceof ForgeUpgradeRecipe || r instanceof ForgeTransformRecipe || r instanceof ForgeSpiritusInfusionRecipe)
                .toList();
        List<ForgeRecipe> forgeRecipes = allForgeRecipes.stream()
                .filter(r -> !(r instanceof ForgeUpgradeRecipe) && !(r instanceof ForgeTransformRecipe) && !(r instanceof ForgeSpiritusInfusionRecipe))
                .toList();
        registration.addRecipes(HellfireForgeRecipeCategory.RECIPE_TYPE, forgeRecipes);
        registration.addRecipes(ForgeUpgradeRecipeCategory.RECIPE_TYPE, upgradeRecipes);

        List<AraVitaeRecipe> altarRecipes = NVJeiRecipeIds.track(syncedRecipes.byType(NVRecipes.ARA_VITAE_TYPE.get()));
        registration.addRecipes(AraVitaeRecipeCategory.RECIPE_TYPE, altarRecipes);

        List<AlchemyArrayRecipe> allArrayRecipes = NVJeiRecipeIds.track(syncedRecipes.byType(NVRecipes.ALCHEMY_ARRAY_TYPE.get()));
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

        List<TabulaVitaeRecipe> tableRecipes = NVJeiRecipeIds.track(syncedRecipes.byType(NVRecipes.TABULA_VITAE_TYPE.get()));
        registration.addRecipes(TabulaVitaeRecipeCategory.RECIPE_TYPE, tableRecipes);

        List<MeteorRecipe> meteorRecipes = NVJeiRecipeIds.track(syncedRecipes.byType(NVRecipes.METEOR_TYPE.get()));
        registration.addRecipes(MeteorRecipeCategory.RECIPE_TYPE, meteorRecipes);

        List<AthanorRecipe> arcRecipes = NVJeiRecipeIds.track(syncedRecipes.byType(NVRecipes.ATHANOR_TYPE.get()));
        registration.addRecipes(AthanorRecipeCategory.RECIPE_TYPE, arcRecipes);

        List<FlaskRecipe> flaskRecipes = NVJeiRecipeIds.track(syncedRecipes.byType(NVRecipes.FLASK_TYPE.get()));
        registration.addRecipes(FlaskRecipeCategory.RECIPE_TYPE, flaskRecipes);

        List<FlaskCombinationJEIRecipe> combinationRecipes = createFlaskCombinationRecipes(flaskRecipes);
        for (int i = 0; i < combinationRecipes.size(); i++) {
            NVJeiRecipeIds.put(combinationRecipes.get(i), NeoVitae.rl("flask_combination/" + i));
        }
        registration.addRecipes(FlaskCombinationCategory.RECIPE_TYPE, combinationRecipes);

        List<ImperfectRitualJEIRecipe> imperfectRitualRecipes = createImperfectRitualRecipes();
        for (ImperfectRitualJEIRecipe recipe : imperfectRitualRecipes) {
            NVJeiRecipeIds.put(recipe, recipe.ritualId());
        }
        registration.addRecipes(ImperfectRitualRecipeCategory.RECIPE_TYPE, imperfectRitualRecipes);

        List<RitualJEIRecipe> ritualRecipes = createRitualRecipes();
        for (RitualJEIRecipe recipe : ritualRecipes) {
            NVJeiRecipeIds.put(recipe, recipe.ritualId());
        }
        registration.addRecipes(RitualRecipeCategory.RECIPE_TYPE, ritualRecipes);

        // Blood tank upgrade recipes, one per tier transition
        List<BloodTankUpgradeJEIRecipe> tankUpgrades = new ArrayList<>();
        for (int tier = 2; tier <= 16; tier++) {
            BloodTankUpgradeJEIRecipe recipe = new BloodTankUpgradeJEIRecipe(tier, bloodTankStack(tier - 1), bloodTankStack(tier));
            NVJeiRecipeIds.put(recipe, NeoVitae.rl("blood_tank_upgrade/" + tier));
            tankUpgrades.add(recipe);
        }
        registration.addRecipes(BloodTankUpgradeCategory.RECIPE_TYPE, tankUpgrades);

        List<ItemStack> orbStacks = List.of(
                new ItemStack(NVItems.ORB_WEAK.get()), new ItemStack(NVItems.ORB_APPRENTICE.get()),
                new ItemStack(NVItems.ORB_MAGICIAN.get()), new ItemStack(NVItems.ORB_MASTER.get()),
                new ItemStack(NVItems.ORB_ARCHMAGE.get()), new ItemStack(NVItems.ORB_TRANSCENDENT.get()));
        registration.addIngredientInfo(orbStacks, VanillaTypes.ITEM_STACK,
                Component.translatable("jei.neovitae.orb.info"));

        registration.addIngredientInfo(List.of(new ItemStack(NVItems.SANGUINE_REVERTER.get())), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.neovitae.disenchant.info"));

        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            HolderLookup.RegistryLookup<SentientUpgrade> upgradeRegistry = world.registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES);
            addTomeInfo(registration, upgradeRegistry, NVTags.Sentient.IS_SCRAPPABLE);
            addTomeInfo(registration, upgradeRegistry, NVTags.Sentient.IS_DOWNGRADE);

            List<ItemStack> allEnchantedBooks = new ArrayList<>();
            world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().forEach(holder -> {
                for (int lvl = holder.value().getMinLevel(); lvl <= holder.value().getMaxLevel(); lvl++) {
                    ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                    ItemEnchantments.Mutable mut = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    mut.set(holder, lvl);
                    book.set(DataComponents.STORED_ENCHANTMENTS, mut.toImmutable());
                    allEnchantedBooks.add(book);
                }
            });
            DisenchantJEIRecipe disenchantRecipe = new DisenchantJEIRecipe(allEnchantedBooks);
            NVJeiRecipeIds.put(disenchantRecipe, NeoVitae.rl("disenchant"));
            registration.addRecipes(DisenchantCategory.RECIPE_TYPE, List.of(disenchantRecipe));
        }

        registration.addRecipes(CrystalGrowthCategory.RECIPE_TYPE, buildCrystalGrowthRecipes());

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
