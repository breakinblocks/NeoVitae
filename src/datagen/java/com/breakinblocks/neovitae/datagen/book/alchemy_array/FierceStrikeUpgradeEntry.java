package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class FierceStrikeUpgradeEntry extends EntryProvider {

    public FierceStrikeUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fierce Strike");
        this.pageText("The armour studies each blow you deliver and sharpens your killing edge in response. "
                + "Melee attacks deal progressively greater damage.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Performing melee attacks.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Fierce Strike";
    }

    @Override
    protected String entryDescription() {
        return "Each blow teaches the armour to sharpen the next.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GOLDEN_SWORD);
    }

    @Override
    protected String entryId() {
        return "upgrade_fierce_strike";
    }
}
