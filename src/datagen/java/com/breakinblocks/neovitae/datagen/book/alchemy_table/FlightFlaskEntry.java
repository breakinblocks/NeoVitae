package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
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

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Flight (3:00)** - neovitae:flask/suspended_to_flight\n"
                + "- **Flight (8:00)** - neovitae:flask/length_flight\n"
                + "- **Flight II (1:30)** - neovitae:flask/potency_flight");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\\\n\\\n"
                + "- **Flight III (0:45)** - neovitae:flask/potency_average_flight\n"
                + "- **Flight (21:20)** - neovitae:flask/length_average_flight");
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
