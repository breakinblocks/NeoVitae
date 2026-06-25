package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualButcheringEntry extends EntryProvider {

    public RitualButcheringEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/butchering"))
                .withMultiblockName("Ritual of Butchering")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Built from standard runes; any Ritual Diviner will serve.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("butchering")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Culling");
        this.pageText("The circle reaps the grown beasts within its bounds and gathers their spoils into a chest set above the Master Ritual Stone. It is no wanton slaughter, however: it counts the herd by species and stays its hand while too few of a kind remain, so a breeding stock always survives.\\\n\\\n"
                + "Set how many of each species to spare with the [#](8B0000)Ritual Configurator[#](); right-click the stone and adjust the [#](8B0000)Keep per species[#]() dial.");

        this.page("synergy", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Tended Harvest");
        this.pageText("Lay this circle over the same ground as a [#](8B0000)Ritual of the Shepherd[#]() and the two sustain one another endlessly. The Shepherd grows the young and breeds the grown; the Butchering reaps the surplus and spares your chosen breeding stock. Stock the chest, walk away, and return to a full larder of meat, leather, and more, with the flock none the smaller for it.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Butchering";
    }

    @Override
    protected String entryDescription() {
        return "Reaps grown animals while sparing a breeding stock.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_AXE);
    }

    @Override
    protected String entryId() {
        return "ritual_butchering";
    }
}
