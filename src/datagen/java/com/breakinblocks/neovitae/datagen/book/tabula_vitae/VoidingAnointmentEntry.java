package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class VoidingAnointmentEntry extends EntryProvider {

    public VoidingAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Void Essence");
        this.pageText("[#](8B0000)Void Essence[#]() consigns worthless materials to oblivion the instant they are broken, "
                + "stone, dirt, netherrack, and other mundane refuse simply cease to exist.\\\n\\\n"
                + "Valid items: Tools, Swords, Charges.\\\n\\\nApplies: Voiding I (256 blocks)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/voiding_anointment")
                .withRecipeId2("neovitae:alchemytable/voiding_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/voiding_anointment_xl"));
    }

    @Override
    protected String entryName() {
        return "Void Essence";
    }

    @Override
    protected String entryDescription() {
        return "Consigns worthless blocks to oblivion.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.VOIDING_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "voiding_anointment";
    }
}
