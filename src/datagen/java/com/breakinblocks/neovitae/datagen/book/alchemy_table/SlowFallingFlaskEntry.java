package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookFlaskRecipePageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class SlowFallingFlaskEntry extends EntryProvider {

    public SlowFallingFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slow Falling");
        this.pageText("**Slow Falling** causes the target to fall slowly and take no fall damage, akin to a chicken.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/slow_fall")
                .withRecipeId2("neovitae:flask/length_slow_fall"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_slow_fall"));
    }

    @Override
    protected String entryName() {
        return "Slow Falling";
    }

    @Override
    protected String entryDescription() {
        return "Fall slowly with no fall damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.PHANTOM_MEMBRANE);
    }

    @Override
    protected String entryId() {
        return "flask_slow_falling";
    }
}
