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

public class AugCapacityRuneEntry extends EntryProvider {

    public AugCapacityRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune of Aug. Capacity");
        this.pageText("Where the standard [#](8B0000)Rune of Capacity[#]() widens the basin, the "
                + "[#](8B0000)Rune of Augmented Capacity[#]() reshapes the altar's very nature. Each rune "
                + "multiplies the total reservoir by [#](8B0000)+7.5%%[#](), applied after all additive Capacity "
                + "Runes have taken effect. The difference between the two is subtle at first, but "
                + "devastating at scale.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_capacity_augmented"))
                .withTitle1("Rune of Aug. Capacity"));

    }

    @Override
    protected String entryName() {
        return "Rune of Aug. Capacity";
    }

    @Override
    protected String entryDescription() {
        return "Multiplicatively expands the altar's reservoir beyond what additive runes can achieve.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_CAPACITY_AUGMENTED.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_aug_capacity";
    }
}
