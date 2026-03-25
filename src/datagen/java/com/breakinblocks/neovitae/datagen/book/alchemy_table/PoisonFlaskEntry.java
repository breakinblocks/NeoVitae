package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class PoisonFlaskEntry extends EntryProvider {

    public PoisonFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Poison");
        this.pageText("**Poison** deals damage over time to the target, but cannot kill them on its own. "
                + "Higher levels deal faster damage.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Poison (3:00)** - neovitae:flask/poison\n"
                + "- **Poison (8:00)** - neovitae:flask/length_poison\n"
                + "- **Poison II (1:30)** - neovitae:flask/potency_poison");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\\\n\\\n"
                + "- **Poison III (0:45)** - neovitae:flask/potency_average_poison\n"
                + "- **Poison (21:20)** - neovitae:flask/length_average_poison");
    }

    @Override
    protected String entryName() {
        return "Poison";
    }

    @Override
    protected String entryDescription() {
        return "Deals damage over time.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SPIDER_EYE);
    }

    @Override
    protected String entryId() {
        return "flask_poison";
    }
}
