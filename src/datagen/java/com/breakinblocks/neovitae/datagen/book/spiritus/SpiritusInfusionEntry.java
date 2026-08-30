package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class SpiritusInfusionEntry extends EntryProvider {

    public SpiritusInfusionEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Infusion");
        this.pageText("Through the [#](8B0000)Hellfire Forge[#](), a practitioner can bind a "
                + "[#](8B0000)Spiritus Gem[#]() directly into a piece of equipment, granting it the ability "
                + "to store [#](4A0080)Spiritus[#]() internally. The storage capacity matches the gem tier used: "
                + "a Petty Gem grants 64, Lesser 256, Common 1,024, Greater 4,096, and Grand 16,384.\\\n\\\n"
                + "Place a piece of wearable armor or a shield alongside a Spiritus Gem in the "
                + "[#](8B0000)Hellfire Forge[#]() to infuse it. The gem is consumed in the process. "
                + "Tools and weapons cannot hold an infusion, and an item can only be infused once.");

        this.page("usage", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Using Infused Equipment");
        this.pageText("Infused equipment stores [#](4A0080)Spiritus[#]() directly, eliminating the need to carry "
                + "separate gems. The stored spiritus is drawn upon by [#](8B0000)Sentient Tools[#]() and other spiritus-consuming "
                + "effects just as it would be from a gem in your inventory.\\\n\\\n"
                + "A colored bar at the top of the item icon indicates the current charge level. "
                + "The bar color reflects the spiritus type stored. Equipment can be recharged via a "
                + "[#](8B0000)Spiritus Crucible[#]() or by absorbing monster souls.");
    }

    @Override
    protected String entryName() {
        return "Spiritus Infusion";
    }

    @Override
    protected String entryDescription() {
        return "Bind spiritus storage directly into your equipment.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SPIRITUS_GEM_COMMON.get());
    }

    @Override
    protected String entryId() {
        return "spiritus_infusion";
    }
}
