package com.breakinblocks.neovitae.common.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorPotionRecipe;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeSpiritusInfusionRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeTransformRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeUpgradeRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.*;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.common.recipe.tiered.FluidTieredRecipe;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;

public class NVRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, NeoVitae.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ForgeRecipe>> HELLFIRE_FORGE_TYPE = TYPES.register(ForgeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(ForgeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgeRecipe>> HELLFIRE_FORGE_SERIALIZER = SERIALIZERS.register(ForgeRecipe.RECIPE_TYPE_NAME, () -> new RecipeSerializer<>(ForgeRecipe.CODEC, ForgeRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgeUpgradeRecipe>> HELLFIRE_FORGE_UPGRADE_SERIALIZER = SERIALIZERS.register("hellfire_forge_upgrade", () -> new RecipeSerializer<>(ForgeUpgradeRecipe.CODEC, ForgeUpgradeRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgeTransformRecipe>> HELLFIRE_FORGE_TRANSFORM_SERIALIZER = SERIALIZERS.register("hellfire_forge_transform", () -> new RecipeSerializer<>(ForgeTransformRecipe.CODEC, ForgeTransformRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgeSpiritusInfusionRecipe>> HELLFIRE_FORGE_SPIRITUS_INFUSION_SERIALIZER = SERIALIZERS.register("hellfire_forge_spiritus_infusion", () -> new RecipeSerializer<>(ForgeSpiritusInfusionRecipe.CODEC, ForgeSpiritusInfusionRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AraVitaeRecipe>> ARA_VITAE_TYPE = TYPES.register(AraVitaeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(AraVitaeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AraVitaeRecipe>> ARA_VITAE_SERIALIZER = SERIALIZERS.register(AraVitaeRecipe.RECIPE_TYPE_NAME, () -> new RecipeSerializer<>(com.breakinblocks.neovitae.common.recipe.aravitae.AraVitaeRecipe.CODEC, com.breakinblocks.neovitae.common.recipe.aravitae.AraVitaeRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AthanorRecipe>> ATHANOR_TYPE = TYPES.register(AthanorRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(AthanorRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AthanorRecipe>> ATHANOR_SERIALIZER = SERIALIZERS.register(AthanorRecipe.RECIPE_TYPE_NAME, () -> new RecipeSerializer<>(AthanorRecipe.CODEC, AthanorRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AthanorPotionRecipe>> ATHANOR_POTION_SERIALIZER = SERIALIZERS.register("athanor_potion", () -> new RecipeSerializer<>(AthanorPotionRecipe.CODEC, AthanorPotionRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<FluidTieredRecipe>> FLUID_TIERED_TYPE = TYPES.register(FluidTieredRecipe.NAME, () -> RecipeType.simple(bm(FluidTieredRecipe.NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidTieredRecipe>> FLUID_TIERED_SERIALIZER = SERIALIZERS.register(FluidTieredRecipe.NAME, () -> new RecipeSerializer<>(FluidTieredRecipe.CODEC, FluidTieredRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemyArrayRecipe>> ALCHEMY_ARRAY_TYPE = TYPES.register(AlchemyArrayRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(AlchemyArrayRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyArrayRecipe>> ALCHEMY_ARRAY_SERIALIZER = SERIALIZERS.register(AlchemyArrayRecipe.RECIPE_TYPE_NAME, () -> new RecipeSerializer<>(AlchemyArrayRecipe.CODEC, AlchemyArrayRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<TabulaVitaeRecipe>> TABULA_VITAE_TYPE = TYPES.register(TabulaVitaeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(TabulaVitaeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TabulaVitaeRecipe>> TABULA_VITAE_SERIALIZER = SERIALIZERS.register(TabulaVitaeRecipe.RECIPE_TYPE_NAME, () -> new RecipeSerializer<>(TabulaVitaeRecipe.CODEC, TabulaVitaeRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<MeteorRecipe>> METEOR_TYPE = TYPES.register(MeteorRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(MeteorRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MeteorRecipe>> METEOR_SERIALIZER = SERIALIZERS.register(MeteorRecipe.RECIPE_TYPE_NAME, () -> new RecipeSerializer<>(MeteorRecipe.CODEC, MeteorRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<FlaskRecipe>> FLASK_TYPE = TYPES.register(FlaskRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(FlaskRecipe.RECIPE_TYPE_NAME)));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskEffectRecipe>> FLASK_EFFECT_SERIALIZER = SERIALIZERS.register("flask_effect", () -> new RecipeSerializer<>(FlaskEffectRecipe.CODEC, FlaskEffectRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskFillRecipe>> FLASK_FILL_SERIALIZER = SERIALIZERS.register("flask_fill", () -> new RecipeSerializer<>(FlaskFillRecipe.CODEC, FlaskFillRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskCycleRecipe>> FLASK_CYCLE_SERIALIZER = SERIALIZERS.register("flask_cycle", () -> new RecipeSerializer<>(FlaskCycleRecipe.CODEC, FlaskCycleRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskItemTransformRecipe>> FLASK_ITEM_TRANSFORM_SERIALIZER = SERIALIZERS.register("flask_item_transform", () -> new RecipeSerializer<>(FlaskItemTransformRecipe.CODEC, FlaskItemTransformRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskLengthRecipe>> FLASK_LENGTH_SERIALIZER = SERIALIZERS.register("flask_length", () -> new RecipeSerializer<>(FlaskLengthRecipe.CODEC, FlaskLengthRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskPotencyRecipe>> FLASK_POTENCY_SERIALIZER = SERIALIZERS.register("flask_potency", () -> new RecipeSerializer<>(FlaskPotencyRecipe.CODEC, FlaskPotencyRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FlaskEffectTransformRecipe>> FLASK_EFFECT_TRANSFORM_SERIALIZER = SERIALIZERS.register("flask_effect_transform", () -> new RecipeSerializer<>(FlaskEffectTransformRecipe.CODEC, FlaskEffectTransformRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeType<?>, RecipeType<SentientDowngradeRecipe>> SENTIENT_DOWNGRADE_TYPE = TYPES.register(SentientDowngradeRecipe.RECIPE_TYPE_NAME, () -> RecipeType.simple(bm(SentientDowngradeRecipe.RECIPE_TYPE_NAME)));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SentientDowngradeRecipe>> SENTIENT_DOWNGRADE_SERIALIZER = SERIALIZERS.register(SentientDowngradeRecipe.RECIPE_TYPE_NAME, () -> new RecipeSerializer<>(SentientDowngradeRecipe.CODEC, SentientDowngradeRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UpgradeTomeCombineRecipe>> UPGRADE_TOME_COMBINE_SERIALIZER = SERIALIZERS.register("upgrade_tome_combine", () -> new RecipeSerializer<>(UpgradeTomeCombineRecipe.CODEC, UpgradeTomeCombineRecipe.STREAM_CODEC));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SigilDyeRecipe>> SIGIL_DYE_SERIALIZER = SERIALIZERS.register("sigil_dye", () -> new RecipeSerializer<>(SigilDyeRecipe.CODEC, SigilDyeRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ArcaneScribeDyeRecipe>> ARCANE_SCRIBE_DYE_SERIALIZER = SERIALIZERS.register("arcane_scribe_dye", () -> new RecipeSerializer<>(ArcaneScribeDyeRecipe.CODEC, ArcaneScribeDyeRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SigilColorResetRecipe>> SIGIL_COLOR_RESET_SERIALIZER = SERIALIZERS.register("sigil_color_reset", () -> new RecipeSerializer<>(SigilColorResetRecipe.CODEC, SigilColorResetRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SigilRainbowRecipe>> SIGIL_RAINBOW_SERIALIZER = SERIALIZERS.register("sigil_rainbow", () -> new RecipeSerializer<>(SigilRainbowRecipe.CODEC, SigilRainbowRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnointmentApplyRecipe>> ANOINTMENT_APPLY_SERIALIZER = SERIALIZERS.register("anointment_apply", () -> new RecipeSerializer<>(AnointmentApplyRecipe.CODEC, AnointmentApplyRecipe.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
        TYPES.register(modBus);
    }

    private static Identifier bm(String path) {
        return Identifier.fromNamespaceAndPath(NeoVitae.MODID, path);
    }
}
