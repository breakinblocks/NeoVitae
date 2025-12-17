package com.breakinblocks.neovitae.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.sigil.SigilType;

/**
 * Registry for sigil types loaded from datapacks.
 * Sigil types are defined in: data/<namespace>/bloodmagic/sigil_type/<name>.json
 */
public class SigilTypeRegistry {
    public static final ResourceKey<Registry<SigilType>> SIGIL_TYPE_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "sigil_type"));

    public static void register(IEventBus modBus) {
        modBus.addListener(SigilTypeRegistry::registerDatapackRegistry);
    }

    private static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                SIGIL_TYPE_KEY,
                SigilType.CODEC,
                SigilType.CLIENT_CODEC,
                builder -> builder.sync(true)
        );
    }

    /**
     * Creates a resource key for a sigil type.
     */
    public static ResourceKey<SigilType> key(ResourceLocation location) {
        return ResourceKey.create(SIGIL_TYPE_KEY, location);
    }

    /**
     * Creates a resource key for a sigil type in the bloodmagic namespace.
     */
    public static ResourceKey<SigilType> key(String path) {
        return key(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, path));
    }
}
