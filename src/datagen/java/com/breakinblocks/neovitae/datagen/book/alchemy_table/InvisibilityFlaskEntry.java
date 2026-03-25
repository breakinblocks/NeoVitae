package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class InvisibilityFlaskEntry extends EntryProvider {

    public InvisibilityFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Invisibility");
        this.pageText("**Invisibility** causes the target to disappear from view, making it harder for them "
                + "to be spotted. This effect does not extend to any **Armour** or held items, any of which "
                + "may be a give-away as to the target's location.\n\n"
                + "It's made from a potion of **Night Vision**.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Invisibility (3:00)** - neovitae:flask/night_to_invis\n"
                + "- **Invisibility (8:00)** - neovitae:flask/length_invisibility\n"
                + "- **Invisibility (21:20)** - neovitae:flask/length_average_invisibility");
    }

    @Override
    protected String entryName() {
        return "Invisibility";
    }

    @Override
    protected String entryDescription() {
        return "Disappear from view.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GLASS_PANE);
    }

    @Override
    protected String entryId() {
        return "flask_invisibility";
    }
}
