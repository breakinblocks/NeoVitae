package com.breakinblocks.neovitae.common.recipe.aravitae;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import com.breakinblocks.neovitae.api.recipe.AraVitaeInput;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

public class AraVitaeRecipe extends com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe {

    public static final MapCodec<com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe::getInput),
            ItemStack.CODEC.fieldOf("output").forGetter(com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe::getResult),
            Codec.INT.fieldOf("minTier").forGetter(com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe::getMinTier),
            Codec.INT.fieldOf("bloodNeeded").forGetter(com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe::getTotalBlood),
            Codec.INT.fieldOf("craftSpeed").forGetter(com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe::getCraftSpeed),
            Codec.INT.fieldOf("drainSpeed").forGetter(com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe::getDrainSpeed),
            Codec.BOOL.optionalFieldOf("copyInputComponents", false).forGetter(com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe::shouldCopyInputComponents)
    ).apply(instance, AraVitaeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe decode(RegistryFriendlyByteBuf buf) {
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    int minTier = buf.readInt();
                    int bloodNeeded = buf.readInt();
                    int craftSpeed = buf.readInt();
                    int drainSpeed = buf.readInt();
                    boolean copyInputComponents = buf.readBoolean();
                    return new AraVitaeRecipe(input, output, minTier, bloodNeeded, craftSpeed, drainSpeed, copyInputComponents);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe recipe) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getInput());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
                    buf.writeInt(recipe.getMinTier());
                    buf.writeInt(recipe.getTotalBlood());
                    buf.writeInt(recipe.getCraftSpeed());
                    buf.writeInt(recipe.getDrainSpeed());
                    buf.writeBoolean(recipe.shouldCopyInputComponents());
                }
            };

    public AraVitaeRecipe(Ingredient input, ItemStack result, int minTier, int totalBlood, int craftSpeed, int drainSpeed) {
        super(input, result, minTier, totalBlood, craftSpeed, drainSpeed);
    }

    public AraVitaeRecipe(Ingredient input, ItemStack result, int minTier, int totalBlood, int craftSpeed, int drainSpeed, boolean copyInputComponents) {
        super(input, result, minTier, totalBlood, craftSpeed, drainSpeed, copyInputComponents);
    }

    @Override
    public RecipeSerializer<? extends Recipe<AraVitaeInput>> getSerializer() {
        return NVRecipes.ARA_VITAE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<AraVitaeInput>> getType() {
        return NVRecipes.ARA_VITAE_TYPE.get();
    }
}
