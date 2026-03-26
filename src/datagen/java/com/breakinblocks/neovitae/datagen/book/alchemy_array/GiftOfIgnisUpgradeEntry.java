package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class GiftOfIgnisUpgradeEntry extends EntryProvider {

    public GiftOfIgnisUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Gift of Ignis");
        this.pageText("Effect: Provides Fire Resistance. Higher levels last longer and recharge faster.\\\n\\\n"
                + "Trained by: Being on Fire. (**Potions of Fire Resistance** may be your friend here.)\\\n\\\n"
                + "Maximum level: 5");
    }

    @Override
    protected String entryName() {
        return "Gift of Ignis";
    }

    @Override
    protected String entryDescription() {
        return "Fire resistance from being on fire.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BLAZE_POWDER);
    }

    @Override
    protected String entryId() {
        return "upgrade_gift_of_ignis";
    }
}
