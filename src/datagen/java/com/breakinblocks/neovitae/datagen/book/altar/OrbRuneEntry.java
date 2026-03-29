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

public class OrbRuneEntry extends EntryProvider {

    public OrbRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune of the Orb");
        this.pageText("The [#](8B0000)Rune of the Orb[#]() resonates with the [#](8B0000)Orb of Vitae[#]() resting within the "
                + "altar, stretching the boundaries of your [#](4A0080)Anima[#](). While the orb sits in the basin, "
                + "each rune increases its capacity by [#](8B0000)+2%%[#]() additively. The orb must remain within "
                + "the altar to benefit; remove it, and the expansion fades.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_orb")));

    }

    @Override
    protected String entryName() {
        return "Rune of the Orb";
    }

    @Override
    protected String entryDescription() {
        return "Stretches the Orb of Vitae's capacity while it rests in the altar.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_ORB.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_orb";
    }
}
