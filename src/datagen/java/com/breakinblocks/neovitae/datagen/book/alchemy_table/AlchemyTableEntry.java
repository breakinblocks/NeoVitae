package com.breakinblocks.neovitae.datagen.book.alchemy_table;

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

public class AlchemyTableEntry extends EntryProvider {

    public AlchemyTableEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Alchemy Table");
        this.pageText("The **Alchemy Table** crafts items using **LP** from a **Soul Network**. "
                + "The **Soul Network** used and Tier of recipes available are determined by the **Blood Orb** "
                + "inserted on the right side of the GUI.\n\n"
                + "The **Alchemy Table** is used to craft a handful of **Neo Vitae** components, "
                + "and provides alternate recipes for some vanilla items as well.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "alchemy_table"))
                .withText(this.context().pageText()));
        this.pageText("When looking at **Alchemy Table** recipes in this book (or in **JEI**), "
                + "please hover over the arrow with an \"LP\" label to view Time, **LP** drain, and Tier information.");

        this.page("gui", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Alchemy Table GUI");
        this.pageText("The **Alchemy Table** has a number of buttons on its right hand side. "
                + "These are, in order, **D**own, **U**p, **N**orth, **S**outh, **W**est, and **E**ast. "
                + "To use them, first click on any slot in the Alchemy Table. Next, click on one of these "
                + "six buttons to toggle whether or not the **Alchemy Table** should allow **Hoppers**, pipes, "
                + "or other such external interference from this face. To return to the table's normal function, "
                + "simply click the slot again to deselect it.");

        this.page("gui2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The icon displayed in the slot shows whether it is accepting inputs or providing outputs.");

        this.page("errors", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Alchemy Table Errors");
        this.pageText("The two most common reasons for a recipe to not work in the Alchemy Table are as follows:"
                + "\n- **Orb Error**: Either there is no Blood Orb present in the relevant slot, or the Orb "
                + "you are trying to use is not a high enough level for this recipe."
                + "\n- **Soul Network Error**: The orb is of a valid level, but either you have not bound it to "
                + "yourself (right click while holding it), or your Soul Network does not have enough LP in it."
                + "\n\nThe following pages document a number of recipes currently available for the Alchemy Table.");

        this.page("recipes1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Recipes");
        this.pageText("The Alchemy Table provides the following basic recipes:"
                + "\n- **Grass Block** (neovitae:alchemytable/grass_block)"
                + "\n- **Leather** from Rotten Flesh (neovitae:alchemytable/leather_from_flesh)"
                + "\n- **Bread** (neovitae:alchemytable/bread)"
                + "\n- **Clay** from Sand (neovitae:alchemytable/clay_from_sand)"
                + "\n- **String** (neovitae:alchemytable/string)"
                + "\n- **Cobweb** (neovitae:alchemytable/cobweb)");

        this.page("recipes2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("More Recipes");
        this.pageText("Additional Alchemy Table recipes include:"
                + "\n- **Plant Oil** from Wheat, Carrots, Potatoes, or Beets"
                + "\n- **Coal Sand** (neovitae:alchemytable/sand_coal)"
                + "\n- **Explosive Powder** (neovitae:alchemytable/explosive_powder)"
                + "\n- **Flint** from Gravel (neovitae:alchemytable/flint_from_gravel)"
                + "\n- **Saltpeter** (neovitae:alchemytable/saltpeter)"
                + "\n- **Gunpowder** (neovitae:alchemytable/gunpowder)");

        this.page("sigil_recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The **Lava Sigil** and **Water Sigil** can be used in the Alchemy Table to produce "
                + "**Lava Buckets** and **Water Buckets** respectively. The Sigils are not consumed in these "
                + "recipes. What's more, the **Water Sigil** can be used in place of a **Water Bucket** in "
                + "any Alchemy Table recipe.");
    }

    @Override
    protected String entryName() {
        return "Alchemy Table";
    }

    @Override
    protected String entryDescription() {
        return "A crafting station that uses LP from your Soul Network.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.ALCHEMY_TABLE.asItem());
    }

    @Override
    protected String entryId() {
        return "alchemy_table";
    }
}
