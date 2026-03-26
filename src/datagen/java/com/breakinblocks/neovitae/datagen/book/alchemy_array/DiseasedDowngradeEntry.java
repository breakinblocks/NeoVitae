package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingDowngradeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DiseasedDowngradeEntry extends EntryProvider {

    public DiseasedDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Diseased");
        this.pageText("Reduces the effectiveness of all healing sources. Caps out at 80%%, so a source "
                + "that would normally heal 10 hearts will only heal 2.");

        this.page("recipe", () -> BookLivingDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/slow_heal"));
    }

    @Override
    protected String entryName() {
        return "Diseased";
    }

    @Override
    protected String entryDescription() {
        return "Downgrade: reduced healing effectiveness.";
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
        return "downgrade_diseased";
    }
}
