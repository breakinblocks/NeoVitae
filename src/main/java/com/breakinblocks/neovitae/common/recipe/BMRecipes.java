package com.breakinblocks.neovitae.common.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.recipe.arc.ARCRecipe;
import com.breakinblocks.neovitae.common.recipe.arc.ARCSerializer;
import com.breakinblocks.neovitae.common.recipe.arc.ARCPotionRecipe;
import com.breakinblocks.neovitae.common.recipe.arc.ARCPotionSerializer;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArraySerializer;
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableRecipe;
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableSerializer;
import com.breakinblocks.neovitae.common.recipe.bloodaltar.BloodAltarRecipe;
import com.breakinblocks.neovitae.common.recipe.bloodaltar.BloodAltarRecipeSerializer;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeSerializer;
import com.breakinblocks.neovitae.common.recipe.flask.*;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorSerializer;
import com.breakinblocks.neovitae.common.recipe.tiered.FluidTieredRecipe;
import com.breakinblocks.neovitae.common.recipe.tiered.FluidTieredSerializer;
import com.breakinblocks.neovitae.common.recipe.livingdowngrade.LivingDowngradeRecipe;
import com.breakinblocks.neovitae.common.recipe.livingdowngrade.LivingDowngradeSerializer;

public class BMRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, NeoVitae.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ForgeRecipe>> SOUL_FORGE_TYPE = TYPES.register(ForgeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(ForgeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgeRecipe>> SOUL_FORGE_SERIALIZER = SERIALIZERS.register(ForgeRecipe.RECIPE_TYPE_NAME, ForgeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe>> BLOOD_ALTAR_TYPE = TYPES.register(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe>> BLOOD_ALTAR_SERIALIZER = SERIALIZERS.register(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe.RECIPE_TYPE_NAME, BloodAltarRecipeSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ARCRecipe>> ARC_TYPE = TYPES.register(ARCRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(ARCRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ARCRecipe>> ARC_SERIALIZER = SERIALIZERS.register(ARCRecipe.RECIPE_TYPE_NAME, ARCSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ARCPotionRecipe>> ARC_POTION_SERIALIZER = SERIALIZERS.register("arc_potion", ARCPotionSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidTieredRecipe>> FLUID_TIERED_TYPE = TYPES.register(FluidTieredRecipe.NAME, () -> RecipeType.simple(bm(FluidTieredRecipe.NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidTieredRecipe>> FLUID_TIERED_SERIALIZER = SERIALIZERS.register(FluidTieredRecipe.NAME, FluidTieredSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyArrayRecipe>> ALCHEMY_ARRAY_TYPE = TYPES.register(AlchemyArrayRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(AlchemyArrayRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyArrayRecipe>> ALCHEMY_ARRAY_SERIALIZER = SERIALIZERS.register(AlchemyArrayRecipe.RECIPE_TYPE_NAME, AlchemyArraySerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyTableRecipe>> ALCHEMY_TABLE_TYPE = TYPES.register(AlchemyTableRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(AlchemyTableRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyTableRecipe>> ALCHEMY_TABLE_SERIALIZER = SERIALIZERS.register(AlchemyTableRecipe.RECIPE_TYPE_NAME, AlchemyTableSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MeteorRecipe>> METEOR_TYPE = TYPES.register(MeteorRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(MeteorRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MeteorRecipe>> METEOR_SERIALIZER = SERIALIZERS.register(MeteorRecipe.RECIPE_TYPE_NAME, MeteorSerializer::new);

    // Flask recipe type (alchemy table flask modifications)
    public static final DeferredHolder<RecipeType<?>, RecipeType<FlaskRecipe>> FLASK_TYPE = TYPES.register(FlaskRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(FlaskRecipe.RECIPE_TYPE_NAME)));

    // Flask recipe serializers
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskEffectRecipe>> FLASK_EFFECT_SERIALIZER = SERIALIZERS.register("flask_effect", FlaskEffectSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskFillRecipe>> FLASK_FILL_SERIALIZER = SERIALIZERS.register("flask_fill", FlaskFillSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskCycleRecipe>> FLASK_CYCLE_SERIALIZER = SERIALIZERS.register("flask_cycle", FlaskCycleSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskItemTransformRecipe>> FLASK_ITEM_TRANSFORM_SERIALIZER = SERIALIZERS.register("flask_item_transform", FlaskItemTransformSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskLengthRecipe>> FLASK_LENGTH_SERIALIZER = SERIALIZERS.register("flask_length", FlaskLengthSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskPotencyRecipe>> FLASK_POTENCY_SERIALIZER = SERIALIZERS.register("flask_potency", FlaskPotencySerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskEffectTransformRecipe>> FLASK_EFFECT_TRANSFORM_SERIALIZER = SERIALIZERS.register("flask_effect_transform", FlaskEffectTransformSerializer::new);

    // Living Downgrade recipes
    public static final DeferredHolder<RecipeType<?>, RecipeType<LivingDowngradeRecipe>> LIVING_DOWNGRADE_TYPE = TYPES.register(LivingDowngradeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(LivingDowngradeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LivingDowngradeRecipe>> LIVING_DOWNGRADE_SERIALIZER = SERIALIZERS.register(LivingDowngradeRecipe.RECIPE_TYPE_NAME, LivingDowngradeSerializer::new);

    // Special crafting recipes
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UpgradeTomeCombineRecipe>> UPGRADE_TOME_COMBINE_SERIALIZER = SERIALIZERS.register("upgrade_tome_combine", UpgradeTomeCombineSerializer::new);

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
        TYPES.register(modBus);
    }

    private static ResourceLocation bm(String path) {
        return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, path);
    }
}
