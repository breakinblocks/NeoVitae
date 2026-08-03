package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookHellfireForgeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class BloodMendingEntry extends EntryProvider {

    public BloodMendingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood Mending");
        this.pageText("Through the [#](8B0000)Hellfire Forge[#](), a practitioner can bind the restorative "
                + "power of [#](4A0080)Essentia Vitae[#]() directly into their equipment. An item imbued with "
                + "[#](8B0000)Blood Mending[#]() will slowly knit itself back together, drawing "
                + "[#](4A0080)Essentia Vitae[#]() from the wielder's [#](8B0000)Soul Network[#]() each second.\\\n\\\n"
                + "The enchantment requires a bound [#](8B0000)Orb of Vitae[#]() somewhere in the practitioner's "
                + "inventory and sufficient reserves in their network to function. Only worn armor "
                + "and held items benefit from this effect.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Applying Blood Mending");
        this.pageText("Place the item to enchant in the [#](8B0000)Hellfire Forge[#]() alongside a "
                + "[#](4A0080)Tabula Robur[#](), [#](4A0080)Lapis Lazuli[#](), and [#](4A0080)Nether Wart[#](). "
                + "The forge requires a minimum of [#](8B0000)400 Spiritus[#]() and will consume "
                + "[#](8B0000)200 Spiritus[#]() in the process.\\\n\\\n"
                + "The item will retain all of its existing enchantments and properties.");
    }

    @Override
    protected String entryName() {
        return "Blood Mending";
    }

    @Override
    protected String entryDescription() {
        return "Repair equipment by drawing on the power of your Soul Network.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.TABULA_ROBUR.get());
    }

    @Override
    protected String entryId() {
        return "blood_mending";
    }
}
