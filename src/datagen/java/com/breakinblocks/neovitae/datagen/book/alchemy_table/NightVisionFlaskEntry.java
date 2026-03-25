package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class NightVisionFlaskEntry extends EntryProvider {

    public NightVisionFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Night Vision");
        this.pageText("**Night Vision** increases the target's ability to see in darkness and underwater.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Night Vision (3:00)** - neovitae:flask/night_vision\n"
                + "- **Night Vision (8:00)** - neovitae:flask/length_night_vision\n"
                + "- **Night Vision (21:20)** - neovitae:flask/length_average_night_vision");
    }

    @Override
    protected String entryName() {
        return "Night Vision";
    }

    @Override
    protected String entryDescription() {
        return "See in darkness and underwater.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GOLDEN_CARROT);
    }

    @Override
    protected String entryId() {
        return "flask_night_vision";
    }
}
