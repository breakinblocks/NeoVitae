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

public class RitualCrystalCatalystEntry extends EntryProvider {

    public RitualCrystalCatalystEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/forsaken_soul"))
                .withMultiblockName("Gathering of the Forsaken Souls")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("forsaken_soul")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Souls Unmoored");
        this.pageText("When creatures perish within this ritual's domain, their departing essence is intercepted, not the vital spark, but the lingering [#](4A0080)demonic residue[#]() that clings to all mortal things. The circle distills this into [#](8B0000)Spiritus Catalysts[#](), potent reagents for cultivating and enhancing will crystals. The resulting catalysts are deposited into a nearby chest.");
    }

    @Override
    protected String entryName() {
        return "Gathering of the Forsaken Souls";
    }

    @Override
    protected String entryDescription() {
        return "Distills forsaken essence into will catalysts.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_CRYSTAL_CATALYST.get());
    }

    @Override
    protected String entryId() {
        return "ritual_crystal_catalyst";
    }
}
