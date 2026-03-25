package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class BounceFlaskEntry extends EntryProvider {

    public BounceFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bounce");
        this.pageText("If the target would take fall damage, **Bounce** will instead cause them to spring "
                + "harmlessly off the ground. Whee!\\\n\\\nCrouching before impact will prevent you from bouncing.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Bounce (3:00)** - neovitae:flask/bounce\n"
                + "- **Bounce (8:00)** - neovitae:flask/length_bounce\n"
                + "- **Bounce (21:20)** - neovitae:flask/length_average_bounce");
    }

    @Override
    protected String entryName() {
        return "Bounce";
    }

    @Override
    protected String entryDescription() {
        return "Spring off the ground instead of taking fall damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SLIME_BALL);
    }

    @Override
    protected String entryId() {
        return "flask_bounce";
    }
}
