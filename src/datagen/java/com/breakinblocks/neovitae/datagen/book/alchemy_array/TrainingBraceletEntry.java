package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

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
        this.pageText("This new equipment is an incredible help, but its undirected growth can sometimes be "
                + "frustrating. To this end, you have devised a form of **Training Bracelet** to assist you "
                + "in your endeavours. Once crafted, a simple press of [Use] will activate its menu and allow "
                + "you to specify which abilities to focus your attention on... or which ones to avoid.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("**Training Bracelet**: Created in an Alchemy Array.\\\n\\\n"
                + "*Insert Rocky Training Montage here*\\\n\\\n"
                + "Only one of these bracelets will work at a time. Off-hand > Curios (if available) > "
                + "Main Inventory (including main hand) > add-on inventories.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The bracelet can specify a limit for any given upgrade (assuming you have a copy of "
                + "the **Tome** to hand). For example, you could tell it to limit Strong Legs to level 3 - "
                + "once you reach this level, Strong Legs will no longer gain experience.\\\n\\\n"
                + "It can also prevent or allow the training of all other skills that you haven't otherwise "
                + "specified. If you want to allow all upgrades except one, you can add that one to the "
                + "bracelet in 'allow others' mode and set its level cap to 0.");
    }

    @Override
    protected String entryName() {
        return "Training Bracelet";
    }

    @Override
    protected String entryDescription() {
        return "A bracelet that controls which upgrades your Living Equipment trains.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
