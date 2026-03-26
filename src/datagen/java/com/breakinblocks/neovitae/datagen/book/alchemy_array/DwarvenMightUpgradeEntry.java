package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DwarvenMightUpgradeEntry extends EntryProvider {

    public DwarvenMightUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dwarven Might");
        this.pageText("Effect: Increases mining speed while mining identical blocks. After a certain level, "
                + "gives a Haste buff after breaking blocks.\\\n\\\nTrained by: Mining.\\\n\\\nMaximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Dwarven Might";
    }

    @Override
    protected String entryDescription() {
        return "Increased mining speed from mining blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIAMOND_PICKAXE);
    }

    @Override
    protected String entryId() {
        return "upgrade_dwarven_might";
    }
}
