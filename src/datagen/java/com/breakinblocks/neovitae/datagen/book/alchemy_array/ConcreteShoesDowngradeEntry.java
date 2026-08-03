package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
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
        this.pageText("The armor grows dense and waterlogged, dragging you down through liquid like "
                + "an anchor. Reduces swim speed by up to 80%%.\\\n\\\n"
                + "[#](4A0080)The depths call, and the armor answers on your behalf.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/swim_decrease"));
    }

    @Override
    protected String entryName() {
        return "Concrete Shoes";
    }

    @Override
    protected String entryDescription() {
        return "The armor drags you down; water becomes a grave.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
