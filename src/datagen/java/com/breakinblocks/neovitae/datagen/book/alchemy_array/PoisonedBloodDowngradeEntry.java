package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;

public class PoisonedBloodDowngradeEntry extends EntryProvider {

    public PoisonedBloodDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Poisoned Blood");
        this.pageText("The armor's living tissue turns venomous, periodically flooding your veins "
                + "with poison. Higher levels shorten the reprieve between doses and worsen the "
                + "venom itself.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 3\\\n\\\n"
                + "[#](4A0080)Your blood is no longer entirely your own.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/poisoned_blood"));
    }

    @Override
    protected String entryName() {
        return "Poisoned Blood";
    }

    @Override
    protected String entryDescription() {
        return "The armor's tissue turns venomous; poison courses on its schedule.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.VENOMGLAND_SAC.get());
    }

    @Override
    protected String entryId() {
        return "downgrade_poisoned_blood";
    }
}
