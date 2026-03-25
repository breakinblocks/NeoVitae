package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class StrongLegsUpgradeEntry extends EntryProvider {

    public StrongLegsUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Strong Legs");
        this.pageText("Effect: Increases jump height and reduces fall damage, up to a maximum of an "
                + "additional 7.5 blocks and 83% fall resistance. Can be negated by holding sneak while "
                + "jumping.\n\nTrained by: Jumping around.\n\nMaximum level: 10");
    }

    @Override
    protected String entryName() {
        return "Strong Legs";
    }

    @Override
    protected String entryDescription() {
        return "Increased jump height from jumping.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIAMOND_LEGGINGS);
    }

    @Override
    protected String entryId() {
        return "upgrade_strong_legs";
    }
}
