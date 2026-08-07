package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DulledBladeDowngradeEntry extends EntryProvider {

    public DulledBladeDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dulled Blade");
        this.pageText("The armor saps the force behind your strikes, blunting every blow. "
                + "Reduces melee damage by up to 80%%.\\\n\\\n"
                + "[#](4A0080)Your sword arm weakens as the armor feeds elsewhere.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/melee_decrease"));
    }

    @Override
    protected String entryName() {
        return "Dulled Blade";
    }

    @Override
    protected String entryDescription() {
        return "The armor bleeds your strength; every strike lands softer.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.STONE_SWORD);
    }

    @Override
    protected String entryId() {
        return "downgrade_dulled_blade";
    }
}
