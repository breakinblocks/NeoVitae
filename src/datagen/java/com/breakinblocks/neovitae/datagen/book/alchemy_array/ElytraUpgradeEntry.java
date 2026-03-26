package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class ElytraUpgradeEntry extends EntryProvider {

    public ElytraUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Elytra");
        this.pageText("Adds an Elytra to your Living Armor. Rather than being trained, the [#](8B0000)Upgrade Tome[#]() "
                + "must be crafted.\\\n\\\n"
                + "This Elytra does drain durability from the Chestplate, but at half the speed of a normal "
                + "Elytra.\\\n\\\nIt also looks pretty neat.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)Elytra Tome[#](): Created in an Alchemy Array. Apply it to your Living Chestplate "
                + "to gain flight capabilities.");
    }

    @Override
    protected String entryName() {
        return "Elytra";
    }

    @Override
    protected String entryDescription() {
        return "Adds Elytra flight to Living Armor.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
