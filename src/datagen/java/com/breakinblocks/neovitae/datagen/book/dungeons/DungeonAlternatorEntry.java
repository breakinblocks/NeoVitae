package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.mojang.datafixers.util.Pair;

public class DungeonAlternatorEntry extends EntryProvider {

    public DungeonAlternatorEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Dungeon Alternator[#]() pulses with a ceaseless rhythm, emitting a "
                + "redstone signal that oscillates between powered and dormant states at fixed intervals. "
                + "It is the heartbeat of the realm's trap mechanisms, a [#](4A0080)demonic clockwork[#]() "
                + "device of elegant simplicity.\\\n\\\n"
                + "[#](2E8B57)These can be repurposed as compact redstone clocks in your own constructions. "
                + "Consult JEI for the crafting recipe.[#]()");
    }

    @Override
    protected String entryName() {
        return "Dungeon Alternator";
    }

    @Override
    protected String entryDescription() {
        return "A tireless redstone pulse, born of demonic clockwork.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.ALTERNATOR.asItem());
    }

    @Override
    protected String entryId() {
        return "dungeon_alternator";
    }
}
