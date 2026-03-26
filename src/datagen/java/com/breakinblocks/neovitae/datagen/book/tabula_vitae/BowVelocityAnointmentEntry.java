package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BowVelocityAnointmentEntry extends EntryProvider {

    public BowVelocityAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Archer's Polish");
        this.pageText("[#](8B0000)Archer's Polish[#]() increases the velocity of fired arrows by 50%%. This also increases the damage dealt by your "
                + "arrows proportionally. Also stacks with Vanilla enchantments."
                + "\\\n\\\nValid items: Bows, Crossbows.\\\n\\\nApplies: Sniping (256 shots)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/bow_velocity_anointment")
                .withRecipeId2("neovitae:alchemytable/bow_velocity_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/bow_velocity_anointment_2")
                .withRecipeId2("neovitae:alchemytable/bow_velocity_anointment_xl"));
        this.page("recipe3", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/bow_velocity_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Archer's Polish";
    }

    @Override
    protected String entryDescription() {
        return "Increases arrow velocity.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.BOW_VELOCITY_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "bow_velocity_anointment";
    }
}
