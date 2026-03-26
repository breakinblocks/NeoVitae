package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class DungeonAlternatorEntry extends EntryProvider {

    public DungeonAlternatorEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "alternator"))
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Dungeon Alternator[#]() emits a constantly changing redstone signal, alternating between "
                + "powered and unpowered at set intervals, similar to a [#](8B0000)Redstone Clock[#]().");
    }

    @Override
    protected String entryName() {
        return "Dungeon Alternator";
    }

    @Override
    protected String entryDescription() {
        return "A redstone clock block found in dungeons.";
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
