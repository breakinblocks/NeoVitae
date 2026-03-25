package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class GravityFlaskEntry extends EntryProvider {

    public GravityFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Gravity");
        this.pageText("**Gravity** causes the target to fall faster and take more damage on landing. "
                + "It does not reduce jump height, however.\n\n"
                + "It's created from a flask containing both the **Grounded** and **Slow Falling** effects.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Gravity (3:00)** - neovitae:flask/gravity\n"
                + "- **Gravity (8:00)** - neovitae:flask/length_gravity\n"
                + "- **Gravity II (1:30)** - neovitae:flask/potency_gravity");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Gravity III (0:45)** - neovitae:flask/potency_average_gravity\n"
                + "- **Gravity (21:20)** - neovitae:flask/length_average_gravity");
    }

    @Override
    protected String entryName() {
        return "Gravity";
    }

    @Override
    protected String entryDescription() {
        return "Fall faster and take more fall damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.TROPICAL_FISH);
    }

    @Override
    protected String entryId() {
        return "flask_gravity";
    }
}
