package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class ElytraUpgradeEntry extends EntryProvider {

    public ElytraUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Elytra");
        this.pageText("The armour sprouts membranes of living tissue, granting true flight. This upgrade "
                + "cannot be trained; it must be inscribed via [#](8B0000)Upgrade Tome[#]().\\\n\\\n"
                + "The wings draw upon the chestplate's vitality, wearing it down at half the rate of conventional "
                + "elytra. The aesthetic is, admittedly, rather striking.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Elytra Tome[#]() is forged in an [#](8B0000)Alchemy Array[#](). Apply it "
                + "to your [#](8B0000)Sentient Chestplate[#]() to unfurl the wings.\\\n\\\n"
                + "[#](4A0080)Blood gave you life. Now it gives you the sky.[#]()");
    }

    @Override
    protected String entryName() {
        return "Elytra";
    }

    @Override
    protected String entryDescription() {
        return "Wings of living tissue unfurl from the chestplate, take to the sky.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ELYTRA);
    }

    @Override
    protected String entryId() {
        return "upgrade_elytra";
    }
}
