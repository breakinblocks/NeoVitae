package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

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
        this.pageText("Ritual circles demand precision; you cannot simply scatter inscriptions at random and expect the currents of [#](4A0080)Essentia Vitae[#]() to flow correctly. The [#](8B0000)Ritual Diviner[#]() is the master architect's tool, a wand that knows every pattern by heart."
                + "\\\n\\\n[#](2E8B57)Right-click while looking at empty air to open the ritual selector, then choose a pattern from the list. Only rituals the Diviner is capable of inscribing are shown.[#]()");

        this.page("direction", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("You can also change the facing of the completed ritual by [#](2E8B57)sneaking and right-clicking in the air[#](). This only matters for asymmetrical rituals such as the [#](8B0000)Ritual of Speed[#](); most patterns are perfectly symmetrical.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual_diviner"))
                .withText(this.context().pageText()));
        this.pageText("Tap Use while aiming at a [#](8B0000)Master Ritual Stone[#]() and the Diviner will construct the selected ritual, consuming [#](8B0000)Ritual Stones[#]() from your inventory as it works.");

        this.page("clearing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Diviner can clear soft obstructions like tall grass and snow, but not solid blocks. Ensure the area is unobstructed before you begin, or the [#](8B0000)Activation Crystal[#]() will find the circle incomplete."
                + "\\\n\\\nBreaking an inscribed stone returns a [#](8B0000)blank Ritual Stone[#](), so a dismantled circle feeds straight into the next one. The Diviner inscribes whatever it places, so blanks are all you need to carry."
                + "\\\n\\\nThe base Diviner can only inscribe simpler rituals. For the most advanced patterns, you must upgrade it with [#](8B0000)Tenebrae Inscription Tools[#]().");

        this.page("tenebrae_crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual_diviner_tenebrae"))
                .withText(this.context().pageText()));
        this.pageText("Unlike the [#](8B0000)Elemental Inscription Tools[#](), the Ritual Diviner and its [#](B8860B)Tenebrae[#]() variant are inexhaustible; they will never wear out.");

        this.page("inscription_tools", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Inscription Tools");
        this.pageText("The base Diviner requires one of each [#](8B0000)Elemental Inscription Tool[#](), and thus a [#](B8860B)Tier 2[#]() [#](8B0000)Ara Vitae[#]()."
                + "\\\n\\\nThe four elemental tools each cost [#](8B0000)1,000 EV[#]() to craft. The [#](B8860B)Tenebrae[#]() variant requires a [#](B8860B)Tier 3[#]() Altar and two [#](8B0000)Tenebrae Inscription Tools[#]() at [#](8B0000)2,000 EV[#]() each."
                + "\\\n\\\nWhile the Diviner handles functional ritual construction, the inscription tools remain useful for [#](2E8B57)decorative purposes[#](). "
                + "Use them to paint ritual markings onto stones by hand for aesthetic ritual circles, "
                + "thematic builds, or simply to mark areas of importance. They never break, so use them freely.");
    }

    @Override
    protected String entryName() {
        return "The Ritual Diviner";
    }

    @Override
    protected String entryDescription() {
        return "The architect's wand for inscribing ritual circles.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
