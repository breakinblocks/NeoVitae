package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import com.mojang.datafixers.util.Pair;

public class RitualCrystalSplitEntry extends EntryProvider {

    public RitualCrystalSplitEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crystal_split"))
                .withMultiblockName("Resonance of the Faceted Crystal")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("crystal_split")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Prismatic Fracture");
        this.pageText("This ritual shatters a mature [#](8B0000)Raw Crystal Cluster[#]() (at least 5 spires) positioned 2 blocks above the [#](8B0000)Master Ritual Stone[#](), refining it into individual spires of each [#](8B0000)Aspected Spiritus[#]() Crystal Cluster. These new crystals appear above the four elemental [#](8B0000)Ritual Stones[#]().");

        this.page("setup", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The spacing is designed to accommodate a [#](8B0000)Crystallarium Maleficum[#]() atop the [#](8B0000)Master Ritual Stone[#]()."
                + "\\\n\\\n[#](2E8B57)For details on cultivating Spiritus Crystals, consult the Crystallized Spiritus entry in the Spiritus chapter.[#]()");
    }

    @Override
    protected String entryName() {
        return "Resonance of the Faceted Crystal";
    }

    @Override
    protected String entryDescription() {
        return "Fractures raw Spiritus into its aspected components.";
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
