package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class FireResistanceFlaskEntry extends EntryProvider {

    public FireResistanceFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fire Resistance");
        this.pageText("**Fire Resistance** makes the target immune to most fire-based damage.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Fire Resistance (3:00)** - neovitae:flask/fire_resist\n"
                + "- **Fire Resistance (8:00)** - neovitae:flask/length_fire_resist\n"
                + "- **Fire Resistance (21:20)** - neovitae:flask/length_average_fire_resist");
    }

    @Override
    protected String entryName() {
        return "Fire Resistance";
    }

    @Override
    protected String entryDescription() {
        return "Immunity to fire damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.MAGMA_CREAM);
    }

    @Override
    protected String entryId() {
        return "flask_fire_resistance";
    }
}
