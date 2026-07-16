package com.breakinblocks.neovitae.datagen.provider;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NVSpriteSourceProvider extends SpriteSourceProvider {

    private static final List<String> TRIM_PATTERNS = List.of(
            "coast", "sentry", "dune", "wild", "ward", "eye", "vex", "tide", "snout",
            "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host", "flow", "bolt");

    public NVSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, NeoVitae.MODID);
    }

    @Override
    protected void gather() {
        atlas(AtlasIds.BLOCKS).addSource(new DirectoryLister("models/alchemyarrays", "models/alchemyarrays/"));

        Identifier paletteKey = Identifier.withDefaultNamespace("trims/color_palettes/trim_palette");
        Map<String, Identifier> permutations = Map.of(
                "neovitae_demonite", NeoVitae.rl("trims/color_palettes/neovitae_demonite"));

        List<Identifier> armorTrimTextures = new ArrayList<>();
        for (String pattern : TRIM_PATTERNS) {
            armorTrimTextures.add(Identifier.withDefaultNamespace("trims/entity/humanoid/" + pattern));
            armorTrimTextures.add(Identifier.withDefaultNamespace("trims/entity/humanoid_leggings/" + pattern));
        }
        atlas(AtlasIds.ARMOR_TRIMS).addSource(new PalettedPermutations(armorTrimTextures, paletteKey, permutations));
    }
}
