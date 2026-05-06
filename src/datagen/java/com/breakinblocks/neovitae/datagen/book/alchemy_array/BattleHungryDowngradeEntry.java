package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientDowngradeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class BattleHungryDowngradeEntry extends EntryProvider {

    public BattleHungryDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Battle Hungry");
        this.pageText("The armour develops a bloodlust that seeps into your belly. If you fail to attack "
                + "something recently, gnawing hunger overtakes you. Higher levels shorten the grace period "
                + "and intensify the starvation.\\\n\\\n"
                + "[#](4A0080)The armour feeds on violence. Deny it, and it feeds on you instead.[#]()");

        this.page("recipe", () -> BookSentientDowngradeRecipePageModel.create()
                .withRecipeId1("neovitae:downgrade/battle_hungry"));
    }

    @Override
    protected String entryName() {
        return "Battle Hungry";
    }

    @Override
    protected String entryDescription() {
        return "The armour craves combat; starve it of violence, and it starves you.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ROTTEN_FLESH);
    }

    @Override
    protected String entryId() {
        return "downgrade_battle_hungry";
    }
}
