package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualCrystalSplitEntry extends EntryProvider {

    public RitualCrystalSplitEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crystal_split"))
                .withMultiblockName("Resonance of the Faceted Crystal")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner [Dusk][#]() for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual takes a well grown (at least 5 spires) [#](8B0000)Raw Crystal Cluster[#]() located 2 blocks above the [#](8B0000)Master Ritual Stone[#](), and splits it into new single spires of each [#](8B0000)Aspected Will[#]() Crystal Cluster located directly above the 4 elemental Ritual Stones.");

        this.page("setup", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("This spacing is designed to accommodate a [#](8B0000)Demon Crystallizer[#]() on top of the [#](8B0000)Master Ritual Stone[#]()."
                + "\\\n\\\nFor information on growing Demon Will Crystals, please see the Crystallized Will entry in the Demon Will section of this book.");
    }

    @Override
    protected String entryName() {
        return "Resonance of the Faceted Crystal";
    }

    @Override
    protected String entryDescription() {
        return "Splits raw crystals into aspected crystals.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.VENGEFUL_CRYSTAL.get());
    }

    @Override
    protected String entryId() {
        return "ritual_crystal_split";
    }
}
