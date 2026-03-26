package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BowPowerAnointmentEntry extends EntryProvider {

    public BowPowerAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Tip");
        this.pageText("[#](8B0000)Iron Tip[#]() increases the damage of fired arrows by 25%%. Also stacks with Vanilla enchantments."
                + "\\\n\\\nValid items: Bows, Crossbows.\\\n\\\nApplies: Heavy Shot (256 shots)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/bow_power_anointment")
                .withRecipeId2("neovitae:alchemytable/bow_power_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/bow_power_anointment_2")
                .withRecipeId2("neovitae:alchemytable/bow_power_anointment_xl"));
        this.page("recipe3", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/bow_power_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Iron Tip";
    }

    @Override
    protected String entryDescription() {
        return "Increases arrow damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.BOW_POWER_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "bow_power_anointment";
    }
}
