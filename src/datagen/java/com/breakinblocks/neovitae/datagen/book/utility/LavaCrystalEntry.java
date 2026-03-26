package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class LavaCrystalEntry extends EntryProvider {

    public LavaCrystalEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lava Crystal");
        this.pageText("The [#](8B0000)Lava Crystal[#]() is a shard of imprisoned heat, born from molten rock "
                + "and sustained by the power of your [#](4A0080)Anima[#](). So long as "
                + "[#](4A0080)Essentia Vitae[#]() flows, it will never cool.\\\n\\\n"
                + "[#](2E8B57)Use on any block in the world to ignite it, at a cost of 100 EV.[#]()");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "lava_crystal"))
                .withText(this.context().pageText()));
        this.pageText("Place a Lava Crystal in the fuel slot of any furnace, and it becomes an "
                + "inexhaustible flame, drawing 50 [#](4A0080)Essentia Vitae[#]() per item smelted. "
                + "Ten seconds of burn time per operation - exactly enough for one item.");
    }

    @Override
    protected String entryName() {
        return "Lava Crystal";
    }

    @Override
    protected String entryDescription() {
        return "A shard of eternal heat, fueled by the Anima.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.LAVA_CRYSTAL.get());
    }

    @Override
    protected String entryId() {
        return "lava_crystal";
    }
}
