package com.breakinblocks.neovitae.common.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.living.LivingEffectComponents;
import com.breakinblocks.neovitae.common.living.LivingEntityEffect;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.living.LivingValueEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

public class BMRegistries {
    public static class Keys {
        public static final ResourceKey<Registry<AltarTier>> ALTAR_TIER_KEY = ResourceKey.createRegistryKey(bm("altar_tier"));

        public static final ResourceKey<Registry<LivingUpgrade>> LIVING_UPGRADES = ResourceKey.createRegistryKey(bm("living_upgrades"));
        public static final ResourceKey<Registry<DataComponentType<?>>> LIVING_EFFECT_COMPONENTS = ResourceKey.createRegistryKey(bm("living_effect_component"));
        public static final ResourceKey<Registry<MapCodec<? extends LivingValueEffect>>> VALUE_BASED_EFFECT_TYPE = ResourceKey.createRegistryKey(bm("value_based_effect_type"));
        public static final ResourceKey<Registry<MapCodec<? extends LivingEntityEffect>>> ENTITY_EFFECT_TYPE = ResourceKey.createRegistryKey(bm("entity_effect_type"));
    }

    private static void registerPack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Keys.ALTAR_TIER_KEY, AltarTier.CODEC);
        event.dataPackRegistry(
                Keys.LIVING_UPGRADES,
                LivingUpgrade.CODEC,
                LivingUpgrade.CLIENT_CODEC,
                builder -> builder.sync(true)
        );
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(BMRegistries::registerPack);

        LivingEffectComponents.register(modBus);
        LivingValueEffect.register(modBus);
        LivingEntityEffect.register(modBus);

        // Sigil system registries
        SigilEffectRegistry.register(modBus);
        SigilTypeRegistry.register(modBus);
    }

    private static ResourceLocation bm(String path) {
        return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, path);
    }
}
