package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class RitualDivinerEntry extends EntryProvider {

    public RitualDivinerEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Ritual Diviner");
        this.pageText("Crafting rituals is an intricate business; even if you have the correct [#](8B0000)Inscription Tools[#](), you can't just slap runic inscriptions down any old how and expect things to happen. Luckily, the [#](8B0000)Ritual Diviner[#]() is here to help."
                + "\\\n\\\nHold Sneak and press Use or Attack while looking at empty air to cycle through the available rituals in either direction.");

        this.page("direction", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("You can also change the direction that the completed ritual will face by pressing Use. This will only affect a small number of rituals, such as the [#](8B0000)Ritual of Speed[#](), as most rituals are symmetrical.");

        this.page("crafting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Simply tap Use while looking at a [#](8B0000)Master Ritual Stone[#]() to make the Diviner build the currently selected ritual out of any [#](8B0000)Ritual Stones[#]() you may be carrying. It's almost like magic! View the Ritual Diviner recipe in JEI.");

        this.page("clearing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Ritual Diviner can break replaceable blocks (like tall grass, snow, etc), but not solid ones such as stone or dirt, so make sure the area is clear before you commence construction, or the [#](8B0000)Activation Crystal[#]() will be unable to do its job."
                + "\\\n\\\nIt's also worth noting that the base ritual diviner can only create some of the more basic rituals. If you want the most out of your diviner, you'll have to upgrade it with [#](8B0000)Dusk Inscription Tools[#]().");

        this.page("dusk_crafting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Unlike the Elemental Inscription Tools, the Ritual Diviner and Ritual Diviner [Dusk] will never run out. View the Ritual Diviner [Dusk] recipe in JEI.");

        this.page("inscription_tools", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The base Ritual Diviner requires one of each [#](8B0000)Elemental Inscription Tool[#]() for its construction, and thus a tier 3 [#](8B0000)Ara Vitae[#]()."
                + "\\\n\\\nThe four base Elemental Inscription Tools can be crafted in your Altar for [#](8B0000)1,000 Life Essence[#]() each. The Ritual Diviner [Dusk] similarly requires a Tier 4 Altar to make two Dusk Elemental Inscription Tools, at a cost of [#](8B0000)2,000 Life Essence[#]() each."
                + "\\\n\\\nYou can also use these tools to inscribe runes by hand, but this should only seriously be used for decorative purposes, as it is both slow and inaccurate.");
    }

    @Override
    protected String entryName() {
        return "The Ritual Diviner";
    }

    @Override
    protected String entryDescription() {
        return "A tool for building rituals automatically.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RITUAL_DIVINER.get());
    }

    @Override
    protected String entryId() {
        return "ritual_diviner";
    }
}
