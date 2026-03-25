package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class SlownessFlaskEntry extends EntryProvider {

    public SlownessFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slowness");
        this.pageText("**Slowness** decreases the target's movement speed by 15% per level.\n\n"
                + "It's made from a potion of either **Speed** or **Jump Boost**.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Slowness from Speed** - neovitae:flask/speed_to_slow\n"
                + "- **Slowness from Jump Boost** - neovitae:flask/jump_to_slow\n"
                + "- **Slowness (8:00)** - neovitae:flask/length_slowness\n"
                + "- **Slowness II (1:30)** - neovitae:flask/potency_slowness");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Slowness III (0:45)** - neovitae:flask/potency_average_slowness\n"
                + "- **Slowness (21:20)** - neovitae:flask/length_average_slowness");
    }

    @Override
    protected String entryName() {
        return "Slowness";
    }

    @Override
    protected String entryDescription() {
        return "Decreases movement speed.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SOUL_SAND);
    }

    @Override
    protected String entryId() {
        return "flask_slowness";
    }
}
