package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class SpeedRuneEntry extends EntryProvider {

    public SpeedRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Speed Rune");
        this.pageText("The [#](8B0000)Speed Rune[#]() quickens the altar's heartbeat. Every transmutation the "
                + "[#](8B0000)Ara Vitae[#]() performs -- the consumption of [#](4A0080)Essentia Vitae[#](), the slow "
                + "reshaping of matter -- accelerates by [#](8B0000)+20%%[#]() per rune, stacking additively. "
                + "Be warned: an empty altar also loses progress faster with these runes in place.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_speed")));

    }

    @Override
    protected String entryName() {
        return "Speed Rune";
    }

    @Override
    protected String entryDescription() {
        return "Hastens the altar's transmutations, for those who cannot abide waiting.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_SPEED.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_speed";
    }
}
