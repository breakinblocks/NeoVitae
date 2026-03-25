package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RegenerationFlaskEntry extends EntryProvider {

    public RegenerationFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Regeneration");
        this.pageText("**Regeneration** heals the target over time. Higher levels increase the rate of healing.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Regeneration (3:00)** - neovitae:flask/regen\n"
                + "- **Regeneration (8:00)** - neovitae:flask/length_regen\n"
                + "- **Regeneration II (1:30)** - neovitae:flask/potency_regen");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\\\n\\\n"
                + "- **Regeneration III (0:45)** - neovitae:flask/potency_average_regen\n"
                + "- **Regeneration (21:20)** - neovitae:flask/length_average_regen");
    }

    @Override
    protected String entryName() {
        return "Regeneration";
    }

    @Override
    protected String entryDescription() {
        return "Heals over time.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GHAST_TEAR);
    }

    @Override
    protected String entryId() {
        return "flask_regeneration";
    }
}
