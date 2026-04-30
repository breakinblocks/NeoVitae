package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.Identifier;
import com.mojang.datafixers.util.Pair;

public class TabulaVitaeEntry extends EntryProvider {

    public TabulaVitaeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tabula Vitae");
        this.pageText("The [#](8B0000)Tabula Vitae[#]() is a workbench of transmutation, drawing [#](4A0080)Essentia Vitae[#]() from your "
                + "[#](4A0080)Anima[#]() to reshape matter itself. The [#](4A0080)Anima[#]() tapped and the [#](B8860B)tier[#]() of recipes "
                + "available are governed by the [#](8B0000)Orb of Vitae[#]() socketed into its right-hand slot.\\\n\\\n"
                + "Through this instrument, the Vitaemancer transmutes base materials into reagents, "
                + "catalysts, and components essential to the art.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "tabula_vitae"))
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)When studying recipes in these pages or through JEI, hover over the arrow "
                + "marked with a EV label to reveal the drain cost, crafting time, and required tier.[#]()");

        this.page("errors", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Common Failures");
        this.pageText("Should the table refuse to work, the cause is almost certainly one of these:"
                + "\n\n- [#](8B0000)Orb Error[#](): No [#](8B0000)Orb of Vitae[#]() is present, or the orb's [#](B8860B)tier[#]() is insufficient "
                + "for the recipe you attempt."
                + "\n\n- [#](8B0000)Anima Error[#](): The orb is adequate, but it has not been bound to you (right-click to bind), "
                + "or your [#](4A0080)Anima[#]() lacks sufficient [#](4A0080)Essentia Vitae[#]() to fuel the transmutation."
                + "\\\n\\\nThe following pages catalogue several fundamental recipes available through the Tabula Vitae.");

        this.page("recipes1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mundane Transmutations");
        this.pageText("Even the simplest transmutations have their uses. The Tabula Vitae can produce:"
                + "\n\n- [#](8B0000)Grass Block[#]() from barren earth"
                + "\n\n- [#](8B0000)Leather[#]() purified from rotten flesh"
                + "\n\n- [#](8B0000)Bread[#]() from raw grain"
                + "\n\n- [#](8B0000)Clay[#]() drawn from sand"
                + "\n\n- [#](8B0000)String[#]() and [#](8B0000)Cobweb[#]()");

        this.page("recipes2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Further Transmutations");
        this.pageText("As your skill deepens, so does the table's repertoire:"
                + "\n\n- [#](8B0000)Plant Oil[#]() extracted from wheat, carrots, potatoes, or beets"
                + "\n\n- [#](8B0000)Coal Sand[#]() and [#](8B0000)Explosive Powder[#]()"
                + "\n\n- [#](8B0000)Flint[#]() coaxed from gravel"
                + "\n\n- [#](8B0000)Saltpeter[#]() and [#](8B0000)Gunpowder[#]()");

        this.page("sigil_recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A cunning Vitaemancer may place a [#](8B0000)Lava Sigil[#]() or [#](8B0000)Water Sigil[#]() within the "
                + "Tabula Vitae to conjure [#](8B0000)Lava Buckets[#]() or [#](8B0000)Water Buckets[#]() without consuming the sigil. "
                + "Furthermore, a [#](8B0000)Water Sigil[#]() may substitute for a [#](8B0000)Water Bucket[#]() in any recipe the "
                + "table accepts, a small convenience that saves many trips to the well.");
    }

    @Override
    protected String entryName() {
        return "Tabula Vitae";
    }

    @Override
    protected String entryDescription() {
        return "A transmutation workbench fuelled by EV from your Anima.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.TABULA_VITAE.asItem());
    }

    @Override
    protected String entryId() {
        return "tabula_vitae";
    }
}
