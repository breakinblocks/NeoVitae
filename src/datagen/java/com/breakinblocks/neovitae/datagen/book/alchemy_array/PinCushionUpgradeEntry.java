package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class PinCushionUpgradeEntry extends EntryProvider {

    public PinCushionUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Pin Cushion");
        this.pageText("The armor hardens against projectile impacts, blunting the force of arrows and "
                + "other ranged attacks. Each level absorbs more of the blow, reducing incoming projectile "
                + "damage by up to [#](4A0080)80%%[#]() at maximum.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Being struck by projectiles.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Pin Cushion";
    }

    @Override
    protected String entryDescription() {
        return "Every arrow absorbed teaches the armor to blunt the next.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ARROW);
    }

    @Override
    protected String entryId() {
        return "upgrade_pin_cushion";
    }
}
