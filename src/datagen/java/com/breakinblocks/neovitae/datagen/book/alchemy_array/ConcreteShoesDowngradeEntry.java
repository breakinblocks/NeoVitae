package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingDowngradeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class ConcreteShoesDowngradeEntry extends EntryProvider {

    public ConcreteShoesDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Concrete Shoes");
        this.pageText("Reduces your swim speed significantly. Caps out at an 80%% reduction.");

        this.page("recipe", () -> BookLivingDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/swim_decrease"));
    }

    @Override
    protected String entryName() {
        return "Concrete Shoes";
    }

    @Override
    protected String entryDescription() {
        return "Downgrade: reduced swim speed.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WATER_BUCKET);
    }

    @Override
    protected String entryId() {
        return "downgrade_concrete_shoes";
    }
}
