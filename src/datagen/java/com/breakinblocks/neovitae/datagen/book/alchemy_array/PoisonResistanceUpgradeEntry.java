package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class PoisonResistanceUpgradeEntry extends EntryProvider {

    public PoisonResistanceUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Poison Resistance");
        this.pageText("The armor develops an antivenom response, automatically purging Poison from your "
                + "blood. Higher levels shorten the cooldown between cures.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Being poisoned.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Poison Resistance";
    }

    @Override
    protected String entryDescription() {
        return "The armor learns to purge venom from your blood.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.MILK_BUCKET);
    }

    @Override
    protected String entryId() {
        return "upgrade_poison_resistance";
    }
}
