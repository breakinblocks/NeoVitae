package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class ToughUpgradeEntry extends EntryProvider {

    public ToughUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tough");
        this.pageText("The armour hardens against melee strikes, explosions, and all forms of close-range "
                + "violence. Protection against non-projectile harm increases with each wound endured.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Taking damage from sources other than projectiles.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Tough";
    }

    @Override
    protected String entryDescription() {
        return "Pain is the teacher; each blow hardens the armour's resolve.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SHIELD);
    }

    @Override
    protected String entryId() {
        return "upgrade_tough";
    }
}
