package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class RepairUpgradeEntry extends EntryProvider {

    public RepairUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repair");
        this.pageText("Effect: Repairs a random piece of worn armour every 100 ticks.\\\n\\\n"
                + "Trained by: Repairing your chestplate (in an anvil, with the **Mending** enchantment, "
                + "etc).\\\n\\\nMaximum level: 1");
    }

    @Override
    protected String entryName() {
        return "Repair";
    }

    @Override
    protected String entryDescription() {
        return "Auto-repairs worn armour periodically.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
