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

        this.page("light", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Light Array");
        this.pageText("The [#](8B0000)Light Array[#]() hangs [#](8B0000)25[#]() invisible lights in the air "
                + "above itself, filling every open space within [#](8B0000)3[#]() blocks of the array in a "
                + "diamond. Each burns at full strength, so the whole span is lit as brightly as daylight.\\\n\\\n"
                + "A [#](8B0000)redstone signal[#]() snuffs them out; cut the signal and they return. Break "
                + "the array and the lights go with it.\\\n\\\n"
                + "[#](2E8B57)Feed it a block of Glowstone and the working turns permanent: the lights it has "
                + "hung will remain even after the array is taken up.[#]()\\\n\\\n"
                + "Should you tire of them, scribe a fresh array on the same spot and break it; an array "
                + "gathers up any lights already standing in its reach.");

        this.page("furnace", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Furnace Array[#]() transmutes raw materials dropped nearby into their "
                + "smelted forms, following standard furnace recipes. It costs 10 EV per stack smelted "
                + "from the owner's soul network, and items within its radius will not despawn while "
                + "awaiting processing.\\\n\\\n"
                + "Cook time matches a standard furnace, but the array processes all valid stacks simultaneously.\\\n\\\n"
                + "[#](4A0080)The floor itself becomes the forge.[#]()");

        this.page("endless_fountain", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Endless Fountain Array[#]() is scribed with a [#](8B0000)Block of Lapis[#]() "
                + "and awakened by a [#](8B0000)Sea Pickle[#](). Once lit, it draws water from nowhere and pipes it "
                + "into every fluid container touching its six faces.\\\n\\\n"
                + "Each placement cycle (every 5 ticks) the array attempts to deposit [#](8B0000)up to 6 buckets[#]() "
                + "of water, spread evenly across its neighbors (one per face when all six are tanks). Only whole-bucket "
                + "fills are committed: if a tank has room for less than a full bucket the array skips it and tries the "
                + "next cached neighbor until all 6 buckets land or every tank refuses.");

        this.page("endless_fountain2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The array keeps a cache of its six adjacent tanks. The cache refreshes on a timer and also "
                + "reacts instantly whenever a neighboring block is placed or broken, so tanks can be swapped in or "
                + "out freely without waiting for the next scan. Neighbors in unloaded chunks are left alone until "
                + "their chunk ticks in again.\\\n\\\n"
                + "When every cached tank is full the array backs off progressively (like the [#](8B0000)Serenade of "
                + "the Nether[#]()). While backing off it emits a small puff of drowsy slate-gray particles so you "
                + "can spot a stalled fountain at a glance; the moment a tank opens up it snaps back to full cadence.\\\n\\\n"
                + "A [#](8B0000)redstone signal[#]() on any face parks the array completely, preserving its cache "
                + "and backoff state, making it easy to gate with a lever or comparator.");
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
