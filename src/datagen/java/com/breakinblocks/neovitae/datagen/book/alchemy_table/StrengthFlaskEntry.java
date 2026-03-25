package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class StrengthFlaskEntry extends EntryProvider {

    public StrengthFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Strength");
        this.pageText("**Strength** increases the target's attack damage by 3 points per level.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Strength (3:00)** - neovitae:flask/strength\n"
                + "- **Strength (8:00)** - neovitae:flask/length_strength\n"
                + "- **Strength II (1:30)** - neovitae:flask/potency_strength");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Strength III (0:45)** - neovitae:flask/potency_average_strength\n"
                + "- **Strength (21:20)** - neovitae:flask/length_average_strength");
    }

    @Override
    protected String entryName() {
        return "Strength";
    }

    @Override
    protected String entryDescription() {
        return "Increases attack damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BLAZE_POWDER);
    }

    @Override
    protected String entryId() {
        return "flask_strength";
    }
}
