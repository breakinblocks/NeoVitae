package com.breakinblocks.neovitae.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A custom ingredient that matches any Blood Orb with a tier >= the specified minimum tier.
 * This ingredient is data-driven through the blood_orb_stats data map, allowing modpack makers
 * to add custom orbs to any tier.
 */
public class OrbTierIngredient implements ICustomIngredient {

    private final int minimumTier;

    public static final MapCodec<OrbTierIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.INT.fieldOf("tier").forGetter(OrbTierIngredient::getMinimumTier)
    ).apply(instance, OrbTierIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OrbTierIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, OrbTierIngredient::getMinimumTier,
            OrbTierIngredient::new
    );

    public OrbTierIngredient(int minimumTier) {
        this.minimumTier = minimumTier;
    }

    public int getMinimumTier() {
        return minimumTier;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        BloodOrb orb = stack.getItemHolder().getData(BMDataMaps.BLOOD_ORB_STATS);
        return orb != null && orb.tier() >= minimumTier;
    }

    @Override
    public Stream<ItemStack> getItems() {
        // Return all items from the registry that have blood orb data with tier >= minimumTier
        List<ItemStack> matchingOrbs = new ArrayList<>();
        for (var item : BuiltInRegistries.ITEM) {
            BloodOrb orb = BuiltInRegistries.ITEM.wrapAsHolder(item).getData(BMDataMaps.BLOOD_ORB_STATS);
            if (orb != null && orb.tier() >= minimumTier) {
                matchingOrbs.add(new ItemStack(item));
            }
        }
        return matchingOrbs.stream();
    }

    @Override
    public boolean isSimple() {
        // Not simple since we check data map values
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return BMIngredientTypes.ORB_TIER.get();
    }

    /**
     * Helper method to create an Ingredient for use in recipes
     */
    public static Ingredient of(int minimumTier) {
        return new OrbTierIngredient(minimumTier).toVanilla();
    }
}
