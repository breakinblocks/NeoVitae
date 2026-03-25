package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SmeltingAnointmentEntry extends EntryProvider {

    public SmeltingAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slow-burning Oil");
        this.pageText("Craft **Slow-burning Oil** in the Alchemy Table (recipe: neovitae:alchemytable/smelting_anointment). "
                + "Uses heat to smelt harvested blocks.\n\n"
                + "Valid items: Tools, Swords, Charges.\n\nApplies: Heated Tool I (256 blocks)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slow-burning Oil L");
        this.pageText("Craft **Slow-burning Oil L** in the Alchemy Table (recipe: neovitae:alchemytable/smelting_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\n\nApplies: Heated Tool I (1024 blocks)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slow-burning Oil XL");
        this.pageText("Craft **Slow-burning Oil XL** in the Alchemy Table (recipe: neovitae:alchemytable/smelting_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\n\nApplies: Heated Tool I (4096 blocks)");
    }

    @Override
    protected String entryName() {
        return "Slow-burning Oil";
    }

    @Override
    protected String entryDescription() {
        return "Smelts harvested blocks automatically.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SMELTING_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "smelting_anointment";
    }
}
