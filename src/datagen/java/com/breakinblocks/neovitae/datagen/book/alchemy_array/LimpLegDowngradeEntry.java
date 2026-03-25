package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class LimpLegDowngradeEntry extends EntryProvider {

    public LimpLegDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Limp Leg");
        this.pageText("Effect: Reduces your movement speed significantly. Caps out at a 70% reduction.\\\n\\\n"
                + "Maximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Limp Leg";
    }

    @Override
    protected String entryDescription() {
        return "Downgrade: reduced movement speed.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SOUL_SAND);
    }

    @Override
    protected String entryId() {
        return "downgrade_limp_leg";
    }
}
