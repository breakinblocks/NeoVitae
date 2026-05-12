package com.breakinblocks.neovitae.ritual;

import com.breakinblocks.neovitae.common.registry.NVRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Lookup for a ritual's effective rune layout. Returns the datapack-loaded
 * {@link RitualLayout} when present, otherwise the ritual's hardcoded
 * {@link Ritual#gatherComponents} fallback. Use this everywhere a caller
 * previously invoked {@code ritual.gatherComponents} so pack overrides are
 * honoured uniformly.
 */
public final class RitualLayouts {

    private RitualLayouts() {
    }

    public static List<RitualComponent> get(Level level, Ritual ritual) {
        if (level != null) {
            RitualLayout fromData = lookup(level.registryAccess(), ritual);
            if (fromData != null) {
                return fromData.components();
            }
        }
        return defaultComponents(ritual);
    }

    public static List<RitualComponent> get(RegistryAccess registries, Ritual ritual) {
        if (registries != null) {
            RitualLayout fromData = lookup(registries, ritual);
            if (fromData != null) {
                return fromData.components();
            }
        }
        return defaultComponents(ritual);
    }

    public static List<RitualComponent> defaultComponents(Ritual ritual) {
        List<RitualComponent> out = new ArrayList<>();
        ritual.gatherComponents(out::add);
        return out;
    }

    private static RitualLayout lookup(RegistryAccess registries, Ritual ritual) {
        ResourceLocation id = RitualRegistry.getId(ritual);
        if (id == null) return null;
        Registry<RitualLayout> registry = registries.registry(NVRegistries.Keys.RITUAL_LAYOUT_KEY).orElse(null);
        if (registry == null) return null;
        return registry.get(id);
    }
}
