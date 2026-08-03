package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class GiftOfIgnisUpgradeEntry extends EntryProvider {

    public GiftOfIgnisUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Gift of Ignis");
        this.pageText("The armor absorbs the memory of flame and learns to shield you from it. Grants "
                + "[#](4A0080)Fire Resistance[#](). Higher levels extend the duration and reduce the cooldown "
                + "between activations.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Being on fire. [#](2E8B57)A Potion of Fire Resistance is "
                + "highly recommended during training.[#]()\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Gift of Ignis";
    }

    @Override
    protected String entryDescription() {
        return "The armor remembers the flame and learns to refuse it.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BLAZE_POWDER);
    }

    @Override
    protected String entryId() {
        return "upgrade_gift_of_ignis";
    }
}
