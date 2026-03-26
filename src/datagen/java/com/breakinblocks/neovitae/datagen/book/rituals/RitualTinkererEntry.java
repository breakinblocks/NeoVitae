package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualTinkererEntry extends EntryProvider {

    public RitualTinkererEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Tinkerer");
        this.pageText("The [#](8B0000)Ritual Tinkerer[#]() is an essential tool for the advanced sanguimancer who is looking for all they can get out of their [#](8B0000)Rituals[#](). It has three main modes, as described overleaf. You can cycle between them by pressing Sneak and Use.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual_reader")));

        this.page("modes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- [#](8B0000)Information[#](): Describes the function of the [#](8B0000)Ritual[#](), similar to the [#](8B0000)Ritual Diviner[#]()."
                + "\n- [#](8B0000)Set Will Consumed[#](): Tells the [#](8B0000)Ritual[#]() which kinds of [#](8B0000)Demon Will[#]() (if any) to consume from the Aura. Specify this by carrying [#](8B0000)Demon Will Crystals[#]() in your hotbar, one for each type of will you wish the [#](8B0000)Ritual[#]() to consume. Further information about the effects of [#](8B0000)Demon Will[#]() upon [#](8B0000)Rituals[#]() can be found on each [#](8B0000)Ritual[#]()'s respective page in this book.");

        this.page("define_area", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- [#](8B0000)Define Area[#](): Specifies the zone that the [#](8B0000)Ritual[#]() should work in, and displays the current zone. If multiple zones can be specified, pressing Sneak and Use on the [#](8B0000)Master Ritual Stone[#]() will cycle through them. Some [#](8B0000)Rituals[#]() can be expanded far beyond their default areas, but keep in mind that this will increase the [#](8B0000)LP[#]() cost to match...");
    }

    @Override
    protected String entryName() {
        return "Ritual Tinkerer";
    }

    @Override
    protected String entryDescription() {
        return "Configure and customize your rituals.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RITUAL_READER.get());
    }

    @Override
    protected String entryId() {
        return "ritual_tinkerer";
    }
}
