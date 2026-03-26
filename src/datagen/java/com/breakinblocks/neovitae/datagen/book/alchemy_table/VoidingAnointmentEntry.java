package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyTableRecipePageModel;
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
        this.pageText("Craft **Void Essence** in the Alchemy Table. "
                + "Deletes simple blocks on mining, such as stone, dirt, and netherrack.\\\n\\\n"
                + "Valid items: Tools, Swords, Charges.\\\n\\\nApplies: Voiding I (256 blocks)");

        this.page("recipe1", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/voiding_anointment")
                .withRecipeId2("neovitae:alchemytable/voiding_anointment_l"));
        this.page("recipe2", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/voiding_anointment_xl"));
    }

    @Override
    protected String entryName() {
        return "Void Essence";
    }

    @Override
    protected String entryDescription() {
        return "Deletes simple blocks when mining.";
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
