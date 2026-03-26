package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class BrillianceUpgradeEntry extends EntryProvider {

    public BrillianceUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Brilliance");
        this.pageText("Effect: Improves your armour defence. Caps out at +5 Armour and +8 Toughness.\\\n\\\n"
                + "Trained by: Crafting a living tome in the Alchemy Table. Each tome adds 1 level of "
                + "Brilliance.\\\n\\\nMaximum level: 5");
    }

    @Override
    protected String entryName() {
        return "Brilliance";
    }

    @Override
    protected String entryDescription() {
        return "Improved armour defence from crafting tomes.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
