package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
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
        this.pageText("The armour's living tissue sickens, suppressing your body's ability to mend. "
                + "All healing is reduced by up to 80%%; a wound that should restore ten hearts "
                + "barely returns two.\\\n\\\n"
                + "[#](4A0080)What lives can also sicken.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/slow_heal"));
    }

    @Override
    protected String entryName() {
        return "Diseased";
    }

    @Override
    protected String entryDescription() {
        return "The armour festers, choking your body's ability to heal.";
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
