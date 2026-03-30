package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class EnvironmentArraysEntry extends EntryProvider {

    public EnvironmentArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Environmental Arrays");
        this.pageText("These arrays reshape the world around them.\\\n\\\n"
                + "The [#](8B0000)Tempest Array[#]() commands the weather, toggling rain on or off at a cost "
                + "of 500 EV from the owner's soul network. Like the time arrays, it is consumed on use.\\\n\\\n"
                + "The [#](8B0000)Growth Array[#]() coaxes nearby crops and plants to grow faster in a 2-block "
                + "radius, applying a gentle acceleration each second.");

        this.page("freeze", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Freeze Array[#]() reaches out to nearby water sources and converts them "
                + "to ice, while covering exposed solid ground in a thin layer of snow. The effect spreads "
                + "in a 3-block radius.\\\n\\\n"
                + "Once the array has frozen everything in reach, it dissipates.\\\n\\\n"
                + "[#](4A0080)The vitaemancer's will made manifest in frost and bloom.[#]()");
    }

    @Override
    protected String entryName() {
        return "Environmental Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Arrays that command weather, growth, and frost.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SNOWBALL);
    }

    @Override
    protected String entryId() {
        return "environment_arrays";
    }
}
