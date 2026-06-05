package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class RepairUpgradeEntry extends EntryProvider {

    public RepairUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repair");
        this.pageText("The armour develops a regenerative instinct, periodically mending a random piece of "
                + "worn equipment every 100 ticks.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Repairing the chestplate through any means, anvil, "
                + "[#](8B0000)Mending[#]() enchantment, or otherwise.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 1");
    }

    @Override
    protected String entryName() {
        return "Repair";
    }

    @Override
    protected String entryDescription() {
        return "The armour knits its own wounds shut, slowly restoring worn equipment.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ANVIL);
    }

    @Override
    protected String entryId() {
        return "upgrade_repair";
    }
}
