package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class QuickDrawAnointmentEntry extends EntryProvider {

    public QuickDrawAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dexterity Alkahest");
        this.pageText("[#](8B0000)Dexterity Alkahest[#]() loosens the mechanism and limbs of ranged weapons, reducing "
                + "draw time by 33%%. In the heat of battle, those fractions of a second are the difference "
                + "between life and death.\\\n\\\n"
                + "Valid items: Bows, Crossbows.\\\n\\\nApplies: Deft Hands I (256 shots)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/quick_draw_anointment")
                .withRecipeId2("neovitae:alchemytable/quick_draw_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/quick_draw_anointment_2")
                .withRecipeId2("neovitae:alchemytable/quick_draw_anointment_xl"));
        this.page("recipe3", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/quick_draw_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Dexterity Alkahest";
    }

    @Override
    protected String entryDescription() {
        return "Hastens the draw of ranged weapons.";
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
