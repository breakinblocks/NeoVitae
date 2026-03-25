package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualCraftingEntry extends EntryProvider {

    public RitualCraftingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crafting"))
                .withMultiblockName("Rhythm of the Beating Anvil")
                .withText(this.context().pageText()));
        this.pageText("Use a **Ritual Diviner [Dusk]** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("The **Rhythm of the Beating Anvil** is a powerful and versatile ritual, allowing you to autocraft standard crafting recipes alongside recipes using your **Alchemy Table** or **Hellfire Forge** (if properly augmented). However, it can be a little complex, so what follows is a tutorial for setting it up."
                + "\n\nEach ritual can handle exactly one recipe.");

        this.page("filter_setup", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Setting the Recipe");
        this.pageText("First, we need to specify the recipe using an **Item Filter**. Multiple types of Item Filter can be used, however the ritual will only ever accept one filter at a time."
                + "\n\nThe following filters are accepted:"
                + "\n- **Standard Item Filter**: specifies exactly which item to use in each slot."
                + "\n- **Tag Item Filter**: Uses Tags to specify what items to use."
                + "\n- **Mod Item Filter**: Tries to use any item from the specified mod in this slot.");

        this.page("placement", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Item Filter must be placed on the ritual using an **Item Frame** or placed in a chest. If multiple filters are in the chest, only the first one will be used."
                + "\n\nBy default, the Input chest and Output chest are in the same place; however, this can be changed with the **Ritual Tinkerer**.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- **Steadfast Will**: The ritual will instead try to autocraft with a linked **Hellfire Forge**."
                + "\n- **Corrosive Will**: The ritual will instead try to autocraft with a linked **Alchemy Table**."
                + "\n\nThese recipes are all shapeless, but if you need 2 of an item, you'll have to specify it twice in the filter.");
    }

    @Override
    protected String entryName() {
        return "Rhythm of the Beating Anvil";
    }

    @Override
    protected String entryDescription() {
        return "Autocrafts recipes using item filters.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CRAFTING_TABLE);
    }

    @Override
    protected String entryId() {
        return "ritual_crafting";
    }
}
