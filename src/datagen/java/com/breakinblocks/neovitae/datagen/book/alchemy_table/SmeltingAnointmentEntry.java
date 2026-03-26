package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyTableRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SmeltingAnointmentEntry extends EntryProvider {

    public SmeltingAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slow-burning Oil");
        this.pageText("Craft [#](8B0000)Slow-burning Oil[#]() in the Alchemy Table. "
                + "Uses heat to smelt harvested blocks.\\\n\\\n"
                + "Valid items: Tools, Swords, Charges.\\\n\\\nApplies: Heated Tool I (256 blocks)");

        this.page("recipe1", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/smelting_anointment")
                .withRecipeId2("neovitae:alchemytable/smelting_anointment_l"));
        this.page("recipe2", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/smelting_anointment_xl"));
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
