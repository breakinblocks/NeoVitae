package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookFlaskRecipePageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class FlightFlaskEntry extends EntryProvider {

    public FlightFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Flight");
        this.pageText("**Flight** gives the target a temporary dose of creative-style Flight. Higher levels "
                + "increase flight speed.\\\n\\\n"
                + "It's created from a flask containing both the **Suspended** and **Levitation** effects.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/suspended_to_flight")
                .withRecipeId2("neovitae:flask/length_flight"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_flight")
                .withRecipeId2("neovitae:flask/potency_average_flight"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_flight"));
    }

    @Override
    protected String entryName() {
        return "Flight";
    }

    @Override
    protected String entryDescription() {
        return "Creative-style flight from a flask.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FEATHER);
    }

    @Override
    protected String entryId() {
        return "flask_flight";
    }
}
