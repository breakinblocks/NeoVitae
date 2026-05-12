package com.breakinblocks.neovitae.common.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.sentient.SentientEffectComponents;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.sentient.SentientValueEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.breakinblocks.neovitae.ritual.RitualLayout;

public class NVRegistries {
    public static class Keys {
        public static final ResourceKey<Registry<AltarTier>> ALTAR_TIER_KEY = ResourceKey.createRegistryKey(bm("altar_tier"));
        public static final ResourceKey<Registry<RitualLayout>> RITUAL_LAYOUT_KEY = ResourceKey.createRegistryKey(bm("ritual_layout"));

        public static final ResourceKey<Registry<SentientUpgrade>> SENTIENT_UPGRADES = ResourceKey.createRegistryKey(bm("sentient_upgrades"));
        public static final ResourceKey<Registry<DataComponentType<?>>> SENTIENT_EFFECT_COMPONENTS = ResourceKey.createRegistryKey(bm("sentient_effect_component"));
        public static final ResourceKey<Registry<MapCodec<? extends SentientValueEffect>>> VALUE_BASED_EFFECT_TYPE = ResourceKey.createRegistryKey(bm("value_based_effect_type"));
        public static final ResourceKey<Registry<MapCodec<? extends SentientEntityEffect>>> ENTITY_EFFECT_TYPE = ResourceKey.createRegistryKey(bm("entity_effect_type"));
    }

    private static void registerPack(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Keys.ALTAR_TIER_KEY, AltarTier.CODEC);
        event.dataPackRegistry(
                Keys.RITUAL_LAYOUT_KEY,
                RitualLayout.CODEC,
                RitualLayout.CODEC,
                builder -> builder.sync(true)
        );
        event.dataPackRegistry(
                Keys.SENTIENT_UPGRADES,
                SentientUpgrade.CODEC,
                SentientUpgrade.CLIENT_CODEC,
                builder -> builder.sync(true)
        );
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(NVRegistries::registerPack);

        SentientEffectComponents.register(modBus);
        SentientValueEffect.register(modBus);
        SentientEntityEffect.register(modBus);

        SigilEffectRegistry.register(modBus);
        SigilTypeRegistry.register(modBus);
    }

    private static Identifier bm(String path) {
        return Identifier.fromNamespaceAndPath(NeoVitae.MODID, path);
    }
}
