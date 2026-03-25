package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class GroundedFlaskEntry extends EntryProvider {

    public GroundedFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Grounded");
        this.pageText("**Grounded** prevents the target from jumping. Swimming is unaffected, however.\\\n\\\n"
                + "It's made from a potion of **Jump Boost**.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Grounded (3:00)** - neovitae:flask/jump_to_grounded\n"
                + "- **Grounded (8:00)** - neovitae:flask/length_grounded\n"
                + "- **Grounded (21:20)** - neovitae:flask/length_average_grounded");
    }

    @Override
    protected String entryName() {
        return "Grounded";
    }

    @Override
    protected String entryDescription() {
        return "Prevents the target from jumping.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.STRING);
    }

    @Override
    protected String entryId() {
        return "flask_grounded";
    }
}
