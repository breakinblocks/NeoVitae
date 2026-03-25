package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class PassiveFlaskEntry extends EntryProvider {

    public PassiveFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Passive");
        this.pageText("**Passive** prevents the affected target from attacking. Players are unaffected, however.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Passive (3:00)** - neovitae:flask/passivity\n"
                + "- **Passive (8:00)** - neovitae:flask/length_passivity\n"
                + "- **Passive (21:20)** - neovitae:flask/length_average_passivity");
    }

    @Override
    protected String entryName() {
        return "Passive";
    }

    @Override
    protected String entryDescription() {
        return "Prevents mobs from attacking.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.HONEYCOMB);
    }

    @Override
    protected String entryId() {
        return "flask_passive";
    }
}
