package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class ExperiencedUpgradeEntry extends EntryProvider {

    public ExperiencedUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Experienced");
        this.pageText("The armor learns to amplify the resonance of slain creatures, drawing forth "
                + "greater experience from each kill. Increases XP drops up to 150%%.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Collecting experience orbs.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Experienced";
    }

    @Override
    protected String entryDescription() {
        return "The armor draws deeper knowledge from each fallen foe.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.EXPERIENCE_BOTTLE);
    }

    @Override
    protected String entryId() {
        return "upgrade_experienced";
    }
}
