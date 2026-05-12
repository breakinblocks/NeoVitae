package com.breakinblocks.neovitae.datagen.content;

import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualLayout;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Emits the default {@code neovitae:ritual_layout} entries by collecting each
 * registered ritual's hardcoded {@link Ritual#gatherComponents} output and
 * serializing it through {@link RitualLayout#CODEC}. Pack authors override a
 * specific ritual by dropping their own JSON at
 * {@code data/<ns>/neovitae/ritual_layout/<ritual_path>.json}.
 */
public final class RitualLayoutsContent {

    private RitualLayoutsContent() {
    }

    public static void bootstrap(BootstrapContext<RitualLayout> context) {
        for (Ritual ritual : RitualRegistry.getAllRituals()) {
            Identifier id = RitualRegistry.getId(ritual);
            if (id == null) continue;
            List<RitualComponent> components = new ArrayList<>();
            ritual.gatherComponents(components::add);
            if (components.isEmpty()) continue;
            ResourceKey<RitualLayout> key = ResourceKey.create(NVRegistries.Keys.RITUAL_LAYOUT_KEY, id);
            context.register(key, new RitualLayout(List.copyOf(components)));
        }
    }
}
