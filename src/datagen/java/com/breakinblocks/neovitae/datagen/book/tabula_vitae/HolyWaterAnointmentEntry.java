package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class HolyWaterAnointmentEntry extends EntryProvider {

    public HolyWaterAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Holy Water");
        this.pageText("[#](8B0000)Holy Water[#]() sears the profane. Blades anointed with it strike the undead with an "
                + "additional 5 points of searing damage -- a purifying flame they cannot endure."
                + "\\\n\\\nValid items: Tools, Swords.\\\n\\\nApplies: Holy Light (256 hits)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/holy_water_anointment")
                .withRecipeId2("neovitae:alchemytable/holy_water_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/holy_water_anointment_2")
                .withRecipeId2("neovitae:alchemytable/holy_water_anointment_xl"));
        this.page("recipe3", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/holy_water_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Holy Water";
    }

    @Override
    protected String entryDescription() {
        return "Sears the undead with purifying radiance.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.HOLY_WATER_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "holy_water_anointment";
    }
}
