package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NVSpriteSourceProvider extends SpriteSourceProvider {

    private static final List<String> ROUTING_NODE_TEXTURES = List.of(
            "modelroutingnode", "modelinputroutingnode", "modeloutputroutingnode", "modelmasterroutingnode");

    private static final List<String> TRIM_PATTERNS = List.of(
            "coast", "sentry", "dune", "wild", "ward", "eye", "vex", "tide", "snout",
            "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host", "flow", "bolt");

    public NVSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NeoVitae.MODID, existingFileHelper);
    }

    @Override
    protected void gather() {
        ResourceLocation paletteKey = ResourceLocation.withDefaultNamespace("trims/color_palettes/trim_palette");
        Map<String, ResourceLocation> permutations = Map.of(
                "neovitae_demonite", NeoVitae.rl("trims/color_palettes/neovitae_demonite"));

        List<ResourceLocation> armorTrimTextures = new ArrayList<>();
        for (String pattern : TRIM_PATTERNS) {
            armorTrimTextures.add(ResourceLocation.withDefaultNamespace("trims/models/armor/" + pattern));
            armorTrimTextures.add(ResourceLocation.withDefaultNamespace("trims/models/armor/" + pattern + "_leggings"));
        }
        atlas(ResourceLocation.withDefaultNamespace("armor_trims"))
                .addSource(new PalettedPermutations(armorTrimTextures, paletteKey, permutations));

        List<ResourceLocation> itemTrimTextures = List.of(
                ResourceLocation.withDefaultNamespace("trims/items/leggings_trim"),
                ResourceLocation.withDefaultNamespace("trims/items/chestplate_trim"),
                ResourceLocation.withDefaultNamespace("trims/items/helmet_trim"),
                ResourceLocation.withDefaultNamespace("trims/items/boots_trim"));

        SourceList blocks = atlas(BLOCKS_ATLAS)
                .addSource(new DirectoryLister("models/alchemyarrays", "models/alchemyarrays/"));
        for (String node : ROUTING_NODE_TEXTURES) {
            blocks.addSource(new SingleFile(NeoVitae.rl("models/" + node), Optional.empty()));
        }
        blocks.addSource(new PalettedPermutations(itemTrimTextures, paletteKey, permutations));
    }
}
