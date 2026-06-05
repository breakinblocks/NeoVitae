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
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualUpgradeRemoveEntry extends EntryProvider {

    public RitualUpgradeRemoveEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/upgrade_remove"))
                .withMultiblockName("Tabula Rasa")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("upgrade_remove")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Purification of Form");
        this.pageText("Stand on the [#](8B0000)Master Ritual Stone[#]() while wearing [#](8B0000)Sentient Armor[#](). "
                + "The ritual wipes [#](2E8B57)every upgrade[#]() from every Sentient piece you have equipped and "
                + "resets your used [#](B8860B)Upgrade Points[#]() to zero, giving you a clean slate.\\\n\\\n"
                + "Nothing is preserved. If you wish to keep your upgrades as reusable [#](8B0000)Tomes[#](), use "
                + "[#](8B0000)Sentient Extraction[#]() instead, which extracts upgrades from a thrown "
                + "piece of armor without destroying their accumulated knowledge.");
    }

    @Override
    protected String entryName() {
        return "Tabula Rasa";
    }

    @Override
    protected String entryDescription() {
        return "Wipes all upgrades from worn Sentient Armor, returning a clean slate.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.UPGRADE_TOME.get());
    }

    @Override
    protected String entryId() {
        return "ritual_upgrade_remove";
    }
}
