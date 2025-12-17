package com.breakinblocks.neovitae.common.crafting;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registry for custom Blood Magic ingredient types.
 */
public class BMIngredientTypes {

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, NeoVitae.MODID);

    /**
     * Orb tier ingredient - matches any Blood Orb with tier >= specified tier.
     * Data-driven through the blood_orb_stats data map.
     */
    public static final DeferredHolder<IngredientType<?>, IngredientType<OrbTierIngredient>> ORB_TIER =
            INGREDIENT_TYPES.register("orb_tier",
                    () -> new IngredientType<>(OrbTierIngredient.CODEC, OrbTierIngredient.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        INGREDIENT_TYPES.register(modBus);
    }
}
