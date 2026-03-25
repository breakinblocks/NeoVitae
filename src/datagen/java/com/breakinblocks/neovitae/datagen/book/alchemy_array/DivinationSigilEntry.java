package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class DivinationSigilEntry extends EntryProvider {

    public DivinationSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Divination Sigil");
        this.pageText("The **Divination Sigil** is probably the first of many sigils that you would like to "
                + "craft in Neo Vitae. In order to craft the sigil, you need to create an **Alchemy Array** "
                + "and use **Redstone Dust** and a **Blank Slate** as the base and catalyst items, respectively.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("**Divination Sigil**: Created in an Alchemy Array with **Redstone Dust** (base) and "
                + "**Blank Slate** (catalyst).\n\n*Peer into the soul.*");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Divination Sigil has two primary uses:\n"
                + "- When any player uses the sigil while aiming at the air, it will display the amount of "
                + "LP that is in the owner's **Soul Network**.\n"
                + "- When using the sigil on a **Blood Altar**, it will tell the player the altar's current "
                + "Tier, the amount of **Life Essence** stored in the altar, as well as its current max capacity.");

        this.page("gui_editing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When holding down the sneak key and pressing [Use], a new interface opens displaying "
                + "all available HUD elements from Neo Vitae. An element can be selected and moved by clicking "
                + "and dragging. Releasing the element and clicking \"Save\" will save its new location. "
                + "Selecting \"Default\" returns the elements to their default positions.");

        this.page("hud_elements", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The HUD elements are:\n"
                + "- The Incense Altar (light grey)\n"
                + "- The Seer's Sigil (purple)\n"
                + "- The Divination Sigil (lavender)\n"
                + "- The Demon Will Aura Gauge (orange)\n"
                + "- The Sigil of Holding (green)");
    }

    @Override
    protected String entryName() {
        return "Divination Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A sigil that reveals information about your Soul Network and Blood Altar.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_DIVINATION.get());
    }

    @Override
    protected String entryId() {
        return "sigil_divination";
    }
}
