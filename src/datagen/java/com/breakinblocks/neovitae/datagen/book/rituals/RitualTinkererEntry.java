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
        this.pageText("The **Ritual Tinkerer** is an essential tool for the advanced sanguimancer who is looking for all they can get out of their **Rituals**. It has three main modes, as described overleaf. You can cycle between them by pressing Sneak and Use.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual_reader")));

        this.page("modes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- **Information**: Describes the function of the **Ritual**, similar to the **Ritual Diviner**."
                + "\n- **Set Will Consumed**: Tells the **Ritual** which kinds of **Demon Will** (if any) to consume from the Aura. Specify this by carrying **Demon Will Crystals** in your hotbar, one for each type of will you wish the **Ritual** to consume. Further information about the effects of **Demon Will** upon **Rituals** can be found on each **Ritual**'s respective page in this book.");

        this.page("define_area", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- **Define Area**: Specifies the zone that the **Ritual** should work in, and displays the current zone. If multiple zones can be specified, pressing Sneak and Use on the **Master Ritual Stone** will cycle through them. Some **Rituals** can be expanded far beyond their default areas, but keep in mind that this will increase the **LP** cost to match...");
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
