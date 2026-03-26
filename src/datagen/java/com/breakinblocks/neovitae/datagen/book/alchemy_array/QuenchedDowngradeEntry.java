package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingDowngradeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class QuenchedDowngradeEntry extends EntryProvider {

    public QuenchedDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Quenched");
        this.pageText("The armour rejects all liquid sustenance, clamping your throat shut against any "
                + "potion you attempt to consume. No healing draughts, no buffs, no alchemical aids.\\\n\\\n"
                + "[#](4A0080)The armour alone shall sustain you -- or so it believes.[#]()");

        this.page("recipe", () -> BookLivingDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/quenched"));
    }

    @Override
    protected String entryName() {
        return "Quenched";
    }

    @Override
    protected String entryDescription() {
        return "The armour seals your throat -- no potion shall pass your lips.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GLASS_BOTTLE);
    }

    @Override
    protected String entryId() {
        return "downgrade_quenched";
    }
}
