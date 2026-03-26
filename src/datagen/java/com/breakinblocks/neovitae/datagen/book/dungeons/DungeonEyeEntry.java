package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.mojang.datafixers.util.Pair;

public class DungeonEyeEntry extends EntryProvider {

    public DungeonEyeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A [#](8B0000)Dungeon Eye[#]() is a light-emitting block often found within the depths of the [#](8B0000)Endless Realm[#](). "
                + "Dungeon Eyes come in Raw, Corrosive, Destructive, Steadfast, and Vengeful variants. View these recipes in JEI.");
    }

    @Override
    protected String entryName() {
        return "Dungeon Eyes";
    }

    @Override
    protected String entryDescription() {
        return "Light-emitting blocks from the Demon Realm.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.DUNGEON_EYE.get(DungeonVariant.RAW).asItem());
    }

    @Override
    protected String entryId() {
        return "dungeon_eye";
    }
}
