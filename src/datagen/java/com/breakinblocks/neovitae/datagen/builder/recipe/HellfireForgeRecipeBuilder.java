package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStackTemplate;

public class HellfireForgeRecipeBuilder extends BaseRecipeBuilder {
    public static final int MAX_INGREDIENTS = 4;

    protected double minSpiritus;
    protected double drainedSpiritus;
    protected List<Ingredient> ingredients = new ArrayList<>();
    protected boolean requireSpiritusType = false;
    protected Optional<SpiritusType> spiritusType = Optional.empty();
    private final DataComponentPatch.Builder patchBuilder = DataComponentPatch.builder();

    protected HellfireForgeRecipeBuilder(ItemLike result, int count) {
        super(result, count);
        if (result == null) {
            throw new IllegalArgumentException("ForgeRecipe result cannot be null");
        }
    }

    public static HellfireForgeRecipeBuilder build(ItemLike result) {
        return new HellfireForgeRecipeBuilder(result, 1);
    }

    public static HellfireForgeRecipeBuilder build(ItemLike result, int count) {
        return new HellfireForgeRecipeBuilder(result, count);
    }

    public HellfireForgeRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(ingredientOf(tag));
    }

    public HellfireForgeRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public HellfireForgeRecipeBuilder requires(ItemLike item, int quantity) {
        this.requires(Ingredient.of(item), quantity);
        return this;
    }

    public HellfireForgeRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public HellfireForgeRecipeBuilder requires(Ingredient ingredient, int quantity) {
        if (ingredients.size() + quantity > MAX_INGREDIENTS) {
            throw new IllegalStateException("ForgeRecipe cannot have more than " + MAX_INGREDIENTS + " ingredients");
        }
        for (int i = 0; i < quantity; i++) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public HellfireForgeRecipeBuilder minSpiritus(double minSpiritus) {
        if (minSpiritus < 0) {
            throw new IllegalArgumentException("minSpiritus cannot be negative");
        }
        this.minSpiritus = minSpiritus;
        return this;
    }

    public HellfireForgeRecipeBuilder drain(double drain) {
        if (drain < 0) {
            throw new IllegalArgumentException("drain cannot be negative");
        }
        this.drainedSpiritus = drain;
        return this;
    }

    public HellfireForgeRecipeBuilder requiredSpiritusType(SpiritusType type) {
        this.spiritusType = Optional.of(type);
        return this;
    }

    public HellfireForgeRecipeBuilder withAnointment(String key, int level, int maxDamage) {
        this.patchBuilder.set(NVDataComponents.ANOINTMENT_HOLDER.get(), AnointmentHolder.single(key, level, maxDamage));
        return this;
    }

    public <T> HellfireForgeRecipeBuilder withComponent(DataComponentType<T> component, T value) {
        this.patchBuilder.set(component, value);
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("ForgeRecipe must have at least one ingredient");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        DataComponentPatch patch = patchBuilder.build();
        ItemStackTemplate resultTpl = patch.isEmpty()
                ? resultTemplate()
                : new ItemStackTemplate(resultItem.asItem(), patch)
                        .withCount(Math.max(resultCount, 1));
        ForgeRecipe recipe = new ForgeRecipe(minSpiritus, drainedSpiritus, ingredients, resultTpl, spiritusType);
        output.accept(ResourceKey.create(Registries.RECIPE, id.identifier().withPrefix("hellfire_forge/")), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
