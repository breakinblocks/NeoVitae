package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class QuickDrawAnointmentEntry extends EntryProvider {

    public QuickDrawAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dexterity Alkahest");
        this.pageText("Craft **Dexterity Alkahest** in the Alchemy Table (recipe: neovitae:alchemytable/quick_draw_anointment). "
                + "Lowers the draw time of bows and crossbows by 33%.\n\n"
                + "Valid items: Bows, Crossbows.\n\nApplies: Deft Hands I (256 shots)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dexterity Alkahest L");
        this.pageText("Craft **Dexterity Alkahest L** in the Alchemy Table (recipe: neovitae:alchemytable/quick_draw_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\n\nApplies: Deft Hands I (1024 shots)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dexterity Alkahest II");
        this.pageText("Craft **Dexterity Alkahest II** in the Alchemy Table (recipe: neovitae:alchemytable/quick_draw_anointment_2). "
                + "This upgraded version of the anointment lowers the draw time of bows and crossbows by 50%.\n\n"
                + "Applies: Deft Hands II (256 shots)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dexterity Alkahest XL");
        this.pageText("Craft **Dexterity Alkahest XL** in the Alchemy Table (recipe: neovitae:alchemytable/quick_draw_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\n\nApplies: Deft Hands I (4096 shots)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dexterity Alkahest III");
        this.pageText("Craft **Dexterity Alkahest III** in the Alchemy Table (recipe: neovitae:alchemytable/quick_draw_anointment_3). "
                + "This upgraded version of the anointment lowers the draw time of bows and crossbows by 67%.\n\n"
                + "Applies: Deft Hands III (256 shots)");
    }

    @Override
    protected String entryName() {
        return "Dexterity Alkahest";
    }

    @Override
    protected String entryDescription() {
        return "Reduces bow and crossbow draw time.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.QUICK_DRAW_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "quick_draw_anointment";
    }
}
