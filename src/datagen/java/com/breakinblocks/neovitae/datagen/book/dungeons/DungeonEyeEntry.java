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
        this.pageText("Set into the walls of the [#](4A0080)Endless Realm[#](), the [#](8B0000)Dungeon Eye[#]() "
                + "emits a pale, unwavering light. Whether these serve as sentinels or mere decoration "
                + "is unclear; what is certain is that they illuminate the darkness with no fuel or flame.\\\n\\\n"
                + "Variants exist tinted by [#](4A0080)Raw[#](), [#](4A0080)Corrosive[#](), "
                + "[#](4A0080)Destructive[#](), [#](4A0080)Steadfast[#](), and [#](4A0080)Vengeful[#]() Will. "
                + "Consult JEI for crafting recipes.");
    }

    @Override
    protected String entryName() {
        return "Dungeon Eyes";
    }

    @Override
    protected String entryDescription() {
        return "Luminous sentinels embedded in the walls of the Demon Realm.";
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
