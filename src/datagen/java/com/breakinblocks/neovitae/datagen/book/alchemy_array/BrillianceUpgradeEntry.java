package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class BrillianceUpgradeEntry extends EntryProvider {

    public BrillianceUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Brilliance");
        this.pageText("The armour responds to the knowledge you inscribe upon [#](8B0000)Upgrade Tomes[#](), hardening "
                + "its lattice with each lesson. Grants up to +5 [#](4A0080)Armour[#]() and +8 "
                + "[#](4A0080)Toughness[#]().\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Crafting an [#](8B0000)Upgrade Tome[#]() in the Tabula Vitae. Each tome grants "
                + "1 level.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Brilliance";
    }

    @Override
    protected String entryDescription() {
        return "Inscribe knowledge upon tomes, and the armour crystallizes into diamond-like hardness.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIAMOND);
    }

    @Override
    protected String entryId() {
        return "upgrade_brilliance";
    }
}
