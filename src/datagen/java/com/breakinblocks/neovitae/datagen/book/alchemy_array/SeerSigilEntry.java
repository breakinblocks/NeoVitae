package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SeerSigilEntry extends EntryProvider {

    public SeerSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Seer's Sigil");
        this.pageText("Where the [#](8B0000)Divination Sigil[#]() offers a glimpse, the [#](8B0000)Seer's Sigil[#]() "
                + "opens your perception wide. It reveals not only the [#](4A0080)Essentia Vitae[#]() within your "
                + "[#](8B0000)Anima[#](), but lays bare the inner workings of the [#](8B0000)Ara Vitae[#]() itself "
                + ", crafting progress, consumption rates, and more.\\\n\\\n"
                + "It also provides the same display configuration as its lesser counterpart.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("First, forge the [#](8B0000)Sight Reagent[#]() in the [#](8B0000)Tabula Vitae[#](). "
                + "Then inscribe an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as "
                + "catalyst.\\\n\\\n[#](4A0080)When mere sight is no longer sufficient.[#]()");

        this.page("hud", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When aimed at an [#](8B0000)Ara Vitae[#](), the Seer's display reveals, from top to bottom:\n\n"
                + "- The altar's current [#](B8860B)Tier[#]()\n\n"
                + "- Current [#](4A0080)EV[#]() volume and total capacity\n\n"
                + "- Active crafting progress, if any\n\n"
                + "- [#](4A0080)Essentia Vitae[#]() consumed per tick during crafting\n\n"
                + "- Current [#](4A0080)Essentia Vitae[#]() reserve held by Charging Runes");

        this.page("ritual_area", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Seer also lays bare the reach of your [#](4A0080)rituals[#](). Look upon an active "
                + "[#](8B0000)Master Ritual Stone[#]() and the ritual's working area is traced in the world, a "
                + "colored boundary drawn around each of its ranges, exactly the zones the "
                + "[#](8B0000)Ritual Configurator[#]() defines.\\\n\\\n"
                + "[#](2E8B57)The sigil need only be on your person: in your pack, tucked within a Sigil of Holding, or worn as a charm.[#]()");
    }

    @Override
    protected String entryName() {
        return "Seer's Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A deeper sight, reveals the full workings of the Ara Vitae.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_SEER.get());
    }

    @Override
    protected String entryId() {
        return "sigil_seer";
    }
}
