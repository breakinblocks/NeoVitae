package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class KeysEntry extends EntryProvider {

    public KeysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dungeon Keys");
        this.pageText("The [#](4A0080)Demon Realm[#]() does not yield its secrets freely. Its sealed doors "
                + "demand [#](8B0000)Keys[#](), forged or found. The humblest is the "
                + "[#](8B0000)Simple Dungeon Key[#](), recovered from [#](8B0000)Antechamber[#]() and dungeon "
                + "chests or forged by your own hand; sturdier rooms call instead for a "
                + "[#](8B0000)Standard Dungeon Key[#](). Each key opens only the kinds of door it was cut for.");

        this.page("distortions", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("As you delve deeper, you may encounter [#](4A0080)Spatial Distortions[#](), anomalies "
                + "that conceal either a unique, uncraftable key or a door that only such a key can open. "
                + "These fractures manifest only within the Endless Realm proper; you will never find one "
                + "in the Antechamber.");

        this.page("mines_key", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.MINE_ENTRANCE_KEY.get())
                .withTitle("The Mines")
                .withText(this.context().pageText()));
        this.pageText("The passage to [#](8B0000)The Mines[#]() opens only to the "
                + "[#](8B0000)Mine Entrance Key[#](). Forge it in the [#](8B0000)Hellfire Forge[#]() from a "
                + "[#](8B0000)Tabula Animata[#](), an [#](8B0000)Echo Shard[#](), a [#](8B0000)Diamond[#](), "
                + "and a [#](8B0000)Simple Dungeon Key[#]() - or recover one from the chests of standard "
                + "dungeons. Beyond that threshold broods the [#](8B0000)Foreman[#](); fell him and he "
                + "surrenders a clutch of [#](8B0000)Mine Dungeon Keys[#](), the only keys that breach the "
                + "deeper workings, where [#](8B0000)Demonite Ore[#]() and rare plunder await.");

        this.page("simple_key", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Simple Dungeon Key");
        this.pageText("A [#](8B0000)Simple Dungeon Key[#]() is forged in the [#](8B0000)Hellfire Forge[#]() "
                + "from a pair of [#](8B0000)Iron Ingots[#]() and a pinch of [#](8B0000)Corrupted Dust[#](). "
                + "One wonders why the demons never thought to simply pick the locks.");

        this.page("mine_key", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mine Dungeon Key");
        this.pageText("[#](8B0000)Mine Dungeon Keys[#]() cannot be forged; they are wrenched only from the "
                + "[#](8B0000)Foreman[#]() who guards the mine, who yields several at once. Best him and the "
                + "deeper doors are yours. Steel your nerves, practitioner.");
    }

    @Override
    protected String entryName() {
        return "Dungeon Keys";
    }

    @Override
    protected String entryDescription() {
        return "The means by which the sealed corridors of the Demon Realm are opened.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIMPLE_KEY.get());
    }

    @Override
    protected String entryId() {
        return "keys";
    }
}
