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

public class RitualCrystalHarvestEntry extends EntryProvider {

    public RitualCrystalHarvestEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crystal_harvest"))
                .withMultiblockName("Crack of the Fractured Crystal")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("crystal_harvest")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystalline Reaping");
        this.pageText("This ritual carefully shears excess spires from [#](8B0000)Spiritus Crystal Clusters[#]() within its reach, collecting the harvested shards. It works with all [#](4A0080)aspects[#]() of will, raw, corrosive, destructive, vengeful, and steadfast alike.");
    }

    @Override
    protected String entryName() {
        return "Crack of the Fractured Crystal";
    }

    @Override
    protected String entryDescription() {
        return "Reaps crystallized will from mature clusters.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_CRYSTAL.get());
    }

    @Override
    protected String entryId() {
        return "ritual_crystal_harvest";
    }
}
