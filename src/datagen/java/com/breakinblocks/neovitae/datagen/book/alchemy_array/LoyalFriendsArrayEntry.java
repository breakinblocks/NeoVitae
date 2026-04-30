package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class LoyalFriendsArrayEntry extends EntryProvider {

    public LoyalFriendsArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Array of Loyal Friends");
        this.pageText("The [#](8B0000)Array of Loyal Friends[#]() is a powerful restorative circle that "
                + "calls all of your tamed companions to your side. Any living pet within range will be "
                + "teleported to the array and ordered to follow you.\\\n\\\n"
                + "More remarkably, the array reaches beyond death itself. Fallen pets are restored to "
                + "full health at the array's location, though they lose any items they were carrying. "
                + "The array costs [#](4A0080)5,000 EV[#]() from your soul network.");

        this.page("crafting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Inscribing the Array");
        this.pageText("Inscribe an [#](8B0000)Alchemy Array[#]() using a [#](4A0080)Lead[#]() as the base "
                + "reagent and a [#](4A0080)Tabula Robur[#]() as the catalyst. Step near the active array "
                + "to trigger the summoning. The nearest player within 8 blocks will be treated as the owner.");
    }

    @Override
    protected String entryName() {
        return "Array of Loyal Friends";
    }

    @Override
    protected String entryDescription() {
        return "Summon and revive your tamed companions.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.LEAD);
    }

    @Override
    protected String entryId() {
        return "loyal_friends_array";
    }
}
