package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class TrainingBraceletEntry extends EntryProvider {

    public TrainingBraceletEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Training Bracelet");
        this.pageText("The armor's undirected growth can be maddening. To impose order upon it, you "
                + "have devised the [#](8B0000)Training Bracelet[#](), a focus that allows you to dictate "
                + "which abilities the armor should pursue and which it should ignore.\\\n\\\n"
                + "Press [Use] to open its configuration menu.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Training Bracelet[#]() is forged in an [#](8B0000)Alchemy Array[#]().\\\n\\\n"
                + "Only one bracelet is active at a time. Priority order: off-hand, Curios slots "
                + "(if available), main inventory, then any add-on inventories.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The bracelet lists every upgrade your worn armor can learn by training, beside the level it "
                + "has reached. Use [#](8B0000)-[#]() and [#](8B0000)+[#]() to set how far each may go; once the armor "
                + "reaches that cap, it stops training the skill. [#](B8860B)Off[#]() blocks it entirely and "
                + "[#](B8860B)Max[#]() leaves it free.\\\n\\\n"
                + "[#](2E8B57)Shift-click to jump straight to Off or Max. Every change is kept at once.[#]()");
    }

    @Override
    protected String entryName() {
        return "Training Bracelet";
    }

    @Override
    protected String entryDescription() {
        return "A focus of discipline, control what your sentient armor learns.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.TRAINING_BRACELET.get());
    }

    @Override
    protected String entryId() {
        return "training_bracelet";
    }
}
