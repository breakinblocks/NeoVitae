package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class UtilityArraysEntry extends EntryProvider {

    public UtilityArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Utility Arrays");
        this.pageText("Not all arrays are weapons. These workings serve the practical needs of a vitaemancer's workshop.\\\n\\\n"
                + "The [#](8B0000)Collection Array[#]() draws dropped items within 2 blocks toward its center. "
                + "Place it atop a chest and collected items will be deposited directly inside.\\\n\\\n"
                + "The [#](8B0000)Light Array[#]() radiates illumination from invisible sources above the array, "
                + "keeping an area well-lit without cluttering it with torches.");

        this.page("furnace", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Furnace Array[#]() transmutes raw materials dropped nearby into their "
                + "smelted forms, following standard furnace recipes. It costs 10 EV per stack smelted "
                + "from the owner's soul network, and items within its radius will not despawn while "
                + "awaiting processing.\\\n\\\n"
                + "Cook time matches a standard furnace, but the array processes all valid stacks simultaneously.\\\n\\\n"
                + "[#](4A0080)The floor itself becomes the forge.[#]()");
    }

    @Override
    protected String entryName() {
        return "Utility Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Practical arrays for collection, light, and smelting.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.HOPPER);
    }

    @Override
    protected String entryId() {
        return "utility_arrays";
    }
}
