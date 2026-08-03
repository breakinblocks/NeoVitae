package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
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
        this.pageText("[#](8B0000)Slow-burning Oil[#]() infuses the tool with a smoldering heat that smelts blocks "
                + "the instant they are broken. Ore becomes ingot in a single swing.\\\n\\\n"
                + "Valid items: anything in [#](8B0000)#neovitae:anointable/mining[#]().\\\n\\\nApplies: Heated Tool I (256 blocks)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/smelting_anointment")
                .withRecipeId2("neovitae:alchemytable/smelting_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/smelting_anointment_xl"));
    }

    @Override
    protected String entryName() {
        return "Slow-burning Oil";
    }

    @Override
    protected String entryDescription() {
        return "Smelts blocks on contact through residual heat.";
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
