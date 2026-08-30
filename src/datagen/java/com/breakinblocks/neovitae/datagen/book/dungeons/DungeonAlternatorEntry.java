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
        this.pageText("The [#](8B0000)Dungeon Alternator[#]() is a [#](4A0080)demonic clockwork[#]() "
                + "timer. Right-click it to set a pulse delay in ticks; it then emits a redstone pulse "
                + "from every side on that rhythm. A delay of 1 makes it a constant signal source, and a "
                + "freshly placed Alternator sits dormant until given a delay. While it is set to stop on "
                + "redstone, a hard redstone signal pauses it until the signal is removed.\\\n\\\n"
                + "[#](2E8B57)Sneak + right-click it with a Node Router, then sneak + right-click up to "
                + "8 other blocks within 256 blocks, and they will pulse in step with it, as if a "
                + "redstone signal were applied directly to each. Consult JEI for the crafting recipe.[#]()");
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
