package com.breakinblocks.neovitae.common.recipe.bloodaltar;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Serializer for Blood Altar recipes. Note that the generic type uses the API's abstract
 * BloodAltarRecipe class so that other mods can work with altar recipes through the API.
 */
public class BloodAltarRecipeSerializer implements RecipeSerializer<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe> {

    public static final MapCodec<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe::getInput),
            ItemStack.CODEC.fieldOf("output").forGetter(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe::getResult),
            Codec.INT.fieldOf("minTier").forGetter(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe::getMinTier),
            Codec.INT.fieldOf("bloodNeeded").forGetter(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe::getTotalBlood),
            Codec.INT.fieldOf("craftSpeed").forGetter(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe::getCraftSpeed),
            Codec.INT.fieldOf("drainSpeed").forGetter(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe::getDrainSpeed),
            Codec.BOOL.optionalFieldOf("copyInputComponents", false).forGetter(com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe::shouldCopyInputComponents)
    ).apply(instance, BloodAltarRecipe::new));  // Creates concrete implementation

    public static final StreamCodec<RegistryFriendlyByteBuf, com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe decode(RegistryFriendlyByteBuf buf) {
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    int minTier = buf.readInt();
                    int bloodNeeded = buf.readInt();
                    int craftSpeed = buf.readInt();
                    int drainSpeed = buf.readInt();
                    boolean copyInputComponents = buf.readBoolean();
                    return new BloodAltarRecipe(input, output, minTier, bloodNeeded, craftSpeed, drainSpeed, copyInputComponents);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe recipe) {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getInput());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.getResult());
                    buf.writeInt(recipe.getMinTier());
                    buf.writeInt(recipe.getTotalBlood());
                    buf.writeInt(recipe.getCraftSpeed());
                    buf.writeInt(recipe.getDrainSpeed());
                    buf.writeBoolean(recipe.shouldCopyInputComponents());
                }
            };

    @Override
    public MapCodec<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
