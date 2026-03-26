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

public class ChargingRuneEntry extends EntryProvider {

    public ChargingRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Charging Rune");
        this.pageText("The [#](8B0000)Charging Rune[#]() is a unique Rune upgrade. When the [#](8B0000)Ara Vitae[#]() is not crafting "
                + "nor filling a [#](8B0000)Blood Orb[#](), it will syphon [#](8B0000)LP[#]() from the Altar to charge an internal buffer. "
                + "When an item is next placed inside of the Altar, it will instantaneously consume the stored "
                + "charge and apply it to the crafting of the item at a 1:1 ratio.");

        this.page("formula", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Ara Vitae does a charging tick once per 20 in-game ticks, which is reduced by 1 "
                + "per [#](8B0000)Acceleration Rune[#]().\\\n\\\nThe speed that the Ara Vitae charges at per charging tick is: "
                + "[[#](8B0000)10LP[#]() x [#](8B0000)Charging Runes[#]() x (1 + [#](8B0000)Speed Runes[#]()/10)]\\\n\\\nThe maximum charge that a Blood "
                + "Altar can hold is [#](8B0000)1000 LP[#]() per [#](8B0000)Charging Rune[#](), which is then multiplied by: "
                + "[(capacity of the main Ara Vitae tank)/20000] if that value is above 1.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_charging")));

    }

    @Override
    protected String entryName() {
        return "Charging Rune";
    }

    @Override
    protected String entryDescription() {
        return "Pre-charges LP for instant crafting when items are inserted.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_CHARGING.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_charging";
    }
}
