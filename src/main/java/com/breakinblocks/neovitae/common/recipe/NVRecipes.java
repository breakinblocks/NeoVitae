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
import com.breakinblocks.neovitae.common.recipe.arc.ARCPotionRecipe;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.*;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.tiered.FluidTieredRecipe;
import com.breakinblocks.neovitae.common.recipe.livingdowngrade.LivingDowngradeRecipe;

public class NVRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, NeoVitae.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ForgeRecipe>> SOUL_FORGE_TYPE = TYPES.register(ForgeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(ForgeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgeRecipe>> SOUL_FORGE_SERIALIZER = SERIALIZERS.register(ForgeRecipe.RECIPE_TYPE_NAME, () -> new NVRecipeSerializer<>(ForgeRecipe.CODEC, ForgeRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe>> BLOOD_ALTAR_TYPE = TYPES.register(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe>> BLOOD_ALTAR_SERIALIZER = SERIALIZERS.register(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe.RECIPE_TYPE_NAME, () -> new NVRecipeSerializer<>(com.breakinblocks.neovitae.common.recipe.bloodaltar.BloodAltarRecipe.CODEC, com.breakinblocks.neovitae.common.recipe.bloodaltar.BloodAltarRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<ARCRecipe>> ARC_TYPE = TYPES.register(ARCRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(ARCRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ARCRecipe>> ARC_SERIALIZER = SERIALIZERS.register(ARCRecipe.RECIPE_TYPE_NAME, () -> new NVRecipeSerializer<>(ARCRecipe.CODEC, ARCRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ARCPotionRecipe>> ARC_POTION_SERIALIZER = SERIALIZERS.register("arc_potion", () -> new NVRecipeSerializer<>(ARCPotionRecipe.CODEC, ARCPotionRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidTieredRecipe>> FLUID_TIERED_TYPE = TYPES.register(FluidTieredRecipe.NAME, () -> RecipeType.simple(bm(FluidTieredRecipe.NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidTieredRecipe>> FLUID_TIERED_SERIALIZER = SERIALIZERS.register(FluidTieredRecipe.NAME, () -> new NVRecipeSerializer<>(FluidTieredRecipe.CODEC, FluidTieredRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyArrayRecipe>> ALCHEMY_ARRAY_TYPE = TYPES.register(AlchemyArrayRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(AlchemyArrayRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyArrayRecipe>> ALCHEMY_ARRAY_SERIALIZER = SERIALIZERS.register(AlchemyArrayRecipe.RECIPE_TYPE_NAME, () -> new NVRecipeSerializer<>(AlchemyArrayRecipe.CODEC, AlchemyArrayRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyTableRecipe>> ALCHEMY_TABLE_TYPE = TYPES.register(AlchemyTableRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(AlchemyTableRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyTableRecipe>> ALCHEMY_TABLE_SERIALIZER = SERIALIZERS.register(AlchemyTableRecipe.RECIPE_TYPE_NAME, () -> new NVRecipeSerializer<>(AlchemyTableRecipe.CODEC, AlchemyTableRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<MeteorRecipe>> METEOR_TYPE = TYPES.register(MeteorRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(MeteorRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MeteorRecipe>> METEOR_SERIALIZER = SERIALIZERS.register(MeteorRecipe.RECIPE_TYPE_NAME, () -> new NVRecipeSerializer<>(MeteorRecipe.CODEC, MeteorRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<FlaskRecipe>> FLASK_TYPE = TYPES.register(FlaskRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(FlaskRecipe.RECIPE_TYPE_NAME)));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskEffectRecipe>> FLASK_EFFECT_SERIALIZER = SERIALIZERS.register("flask_effect", () -> new NVRecipeSerializer<>(FlaskEffectRecipe.CODEC, FlaskEffectRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskFillRecipe>> FLASK_FILL_SERIALIZER = SERIALIZERS.register("flask_fill", () -> new NVRecipeSerializer<>(FlaskFillRecipe.CODEC, FlaskFillRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskCycleRecipe>> FLASK_CYCLE_SERIALIZER = SERIALIZERS.register("flask_cycle", () -> new NVRecipeSerializer<>(FlaskCycleRecipe.CODEC, FlaskCycleRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskItemTransformRecipe>> FLASK_ITEM_TRANSFORM_SERIALIZER = SERIALIZERS.register("flask_item_transform", () -> new NVRecipeSerializer<>(FlaskItemTransformRecipe.CODEC, FlaskItemTransformRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskLengthRecipe>> FLASK_LENGTH_SERIALIZER = SERIALIZERS.register("flask_length", () -> new NVRecipeSerializer<>(FlaskLengthRecipe.CODEC, FlaskLengthRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskPotencyRecipe>> FLASK_POTENCY_SERIALIZER = SERIALIZERS.register("flask_potency", () -> new NVRecipeSerializer<>(FlaskPotencyRecipe.CODEC, FlaskPotencyRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskEffectTransformRecipe>> FLASK_EFFECT_TRANSFORM_SERIALIZER = SERIALIZERS.register("flask_effect_transform", () -> new NVRecipeSerializer<>(FlaskEffectTransformRecipe.CODEC, FlaskEffectTransformRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<LivingDowngradeRecipe>> LIVING_DOWNGRADE_TYPE = TYPES.register(LivingDowngradeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(LivingDowngradeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LivingDowngradeRecipe>> LIVING_DOWNGRADE_SERIALIZER = SERIALIZERS.register(LivingDowngradeRecipe.RECIPE_TYPE_NAME, () -> new NVRecipeSerializer<>(LivingDowngradeRecipe.CODEC, LivingDowngradeRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UpgradeTomeCombineRecipe>> UPGRADE_TOME_COMBINE_SERIALIZER = SERIALIZERS.register("upgrade_tome_combine", () -> new NVRecipeSerializer<>(UpgradeTomeCombineRecipe.CODEC, UpgradeTomeCombineRecipe.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
        TYPES.register(modBus);
    }

    private static ResourceLocation bm(String path) {
        return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, path);
    }
}
