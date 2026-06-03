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
        this.pageText("The [#](4A0080)Demon Realm[#]() does not yield its secrets freely. Passage through "
                + "its sealed corridors demands [#](8B0000)Keys[#](), forged or found. The simplest variety, "
                + "[#](8B0000)Iron Keys[#](), can be discovered in the [#](8B0000)Antechamber[#]() chests or "
                + "scattered throughout the nearer chambers of the Endless Realm. They can also be forged "
                + "by your own hand.");

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
                + "[#](8B0000)Mine Entrance Key[#](), which may be forged in the [#](8B0000)Hellfire "
                + "Forge[#]() or unearthed from a [#](8B0000)Spirit Cache[#]() by the fortunate. Beyond "
                + "that threshold broods the [#](8B0000)Foreman[#](); fell him and he surrenders a clutch "
                + "of [#](8B0000)Mine Keys[#](), the only keys that breach the deeper workings. The mines "
                + "offer [#](8B0000)Demonite Ore[#]() and rare plunder, though the dangers there are "
                + "commensurate with the rewards.");

        this.page("iron_key", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Key");
        this.pageText("Iron Keys can be forged in the [#](8B0000)Hellfire Forge[#](). One wonders why the "
                + "demons never thought to simply pick the locks.");

        this.page("miners_key", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mine Key");
        this.pageText("Mine Keys cannot be forged; they are wrenched only from the [#](8B0000)Foreman[#]() "
                + "who guards the mine's mouth. Best him and the deeper doors are yours. Steel your nerves, "
                + "practitioner.");
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
