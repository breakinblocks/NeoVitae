package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;

public class HollowHungerDowngradeEntry extends EntryProvider {

    public HollowHungerDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hollow Hunger");
        this.pageText("The armor feeds on you without pause, and no meal keeps the emptiness at bay "
                + "for long. Your hunger drains faster at every level, whether you fight, mine, or "
                + "simply stand still.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5\\\n\\\n"
                + "[#](4A0080)It is always hungry. Now, so are you.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/hollow_hunger"));
    }

    @Override
    protected String entryName() {
        return "Hollow Hunger";
    }

    @Override
    protected String entryDescription() {
        return "The armor feeds without pause, and so must you.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.HOLLOW_GUT.get());
    }

    @Override
    protected String entryId() {
        return "downgrade_hollow_hunger";
    }
}
