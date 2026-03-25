package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class LevitationFlaskEntry extends EntryProvider {

    public LevitationFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Levitation");
        this.pageText("**Levitation** causes the target to float up into the air. Higher levels will make the "
                + "target levitate faster.\\\n\\\n"
                + "It's made from a potion of **Slow Falling**.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Levitation (3:00)** - neovitae:flask/fall_to_levitation\n"
                + "- **Levitation (8:00)** - neovitae:flask/length_levitation\n"
                + "- **Levitation II (1:30)** - neovitae:flask/potency_levitation");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\\\n\\\n"
                + "- **Levitation III (0:45)** - neovitae:flask/potency_average_levitation\n"
                + "- **Levitation (21:20)** - neovitae:flask/length_average_levitation");
    }

    @Override
    protected String entryName() {
        return "Levitation";
    }

    @Override
    protected String entryDescription() {
        return "Float up into the air.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SHULKER_SHELL);
    }

    @Override
    protected String entryId() {
        return "flask_levitation";
    }
}
