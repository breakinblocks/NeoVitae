package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class LeadenedPickDowngradeEntry extends EntryProvider {

    public LeadenedPickDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Leadened Pick");
        this.pageText("The armour weighs upon your arms, making every swing of the pick feel like moving "
                + "through tar. Reduces dig speed by up to 80%%.\\\n\\\n"
                + "[#](4A0080)The stone mocks your feeble blows.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/dig_slowdown"));
    }

    @Override
    protected String entryName() {
        return "Leadened Pick";
    }

    @Override
    protected String entryDescription() {
        return "The armour drags at your arms; mining becomes a crawl.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.STONE_PICKAXE);
    }

    @Override
    protected String entryId() {
        return "downgrade_leadened_pick";
    }
}
