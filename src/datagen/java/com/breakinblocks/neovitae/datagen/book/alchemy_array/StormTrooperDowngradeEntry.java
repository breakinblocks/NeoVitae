package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class StormTrooperDowngradeEntry extends EntryProvider {

    public StormTrooperDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Storm Trooper");
        this.pageText("The armor interferes with your aim, introducing wild inaccuracy to every arrow "
                + "and bolt you loose. Bows and crossbows become exercises in frustration.\\\n\\\n"
                + "[#](4A0080)Your hands are steady. The armor is not.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/storm_trooper"));
    }

    @Override
    protected String entryName() {
        return "Storm Trooper";
    }

    @Override
    protected String entryDescription() {
        return "The armor ruins your aim; every shot goes astray.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ARROW);
    }

    @Override
    protected String entryId() {
        return "downgrade_storm_trooper";
    }
}
