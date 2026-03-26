package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

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

public class TabulaVitaeEntry extends EntryProvider {

    public TabulaVitaeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tabula Vitae");
        this.pageText("The [#](8B0000)Tabula Vitae[#]() crafts items using [#](8B0000)LP[#]() from a [#](8B0000)Soul Network[#](). "
                + "The [#](8B0000)Soul Network[#]() used and Tier of recipes available are determined by the [#](8B0000)Blood Orb[#]() "
                + "inserted on the right side of the GUI.\\\n\\\n"
                + "The [#](8B0000)Tabula Vitae[#]() is used to craft a handful of [#](8B0000)Neo Vitae[#]() components, "
                + "and provides alternate recipes for some vanilla items as well.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "tabula_vitae"))
                .withText(this.context().pageText()));
        this.pageText("When looking at [#](8B0000)Tabula Vitae[#]() recipes in this book (or in [#](8B0000)JEI[#]()), "
                + "please hover over the arrow with an \"LP\" label to view Time, [#](8B0000)LP[#]() drain, and Tier information.");

        this.page("gui", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tabula Vitae GUI");
        this.pageText("The [#](8B0000)Tabula Vitae[#]() has a number of buttons on its right hand side. "
                + "These are, in order, [#](8B0000)D[#]()own, [#](8B0000)U[#]()p, [#](8B0000)N[#]()orth, [#](8B0000)S[#]()outh, [#](8B0000)W[#]()est, and [#](8B0000)E[#]()ast.\\\n\\\n"
                + "To use them, first click on any slot in the Tabula Vitae. Next, click on one of these "
                + "six buttons to toggle whether or not the [#](8B0000)Tabula Vitae[#]() should allow [#](8B0000)Hoppers[#](), pipes, "
                + "or other such external interference from this face. To return to the table's normal function, "
                + "simply click the slot again to deselect it.");

        this.page("gui2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The icon displayed in the slot shows whether it is accepting inputs or providing outputs.");

        this.page("errors", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tabula Vitae Errors");
        this.pageText("The two most common reasons for a recipe to not work in the Tabula Vitae are as follows:"
                + "\n\n- [#](8B0000)Orb Error[#](): Either there is no Blood Orb present in the relevant slot, or the Orb "
                + "you are trying to use is not a high enough level for this recipe."
                + "\n\n- [#](8B0000)Soul Network Error[#](): The orb is of a valid level, but either you have not bound it to "
                + "yourself (right click while holding it), or your Soul Network does not have enough LP in it."
                + "\\\n\\\nThe following pages document a number of recipes currently available for the Tabula Vitae.");

        this.page("recipes1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Recipes");
        this.pageText("The Tabula Vitae provides the following basic recipes:"
                + "\n\n- [#](8B0000)Grass Block[#]() (neovitae:alchemytable/grass_block)"
                + "\n\n- [#](8B0000)Leather[#]() from Rotten Flesh (neovitae:alchemytable/leather_from_flesh)"
                + "\n\n- [#](8B0000)Bread[#]() (neovitae:alchemytable/bread)"
                + "\n\n- [#](8B0000)Clay[#]() from Sand (neovitae:alchemytable/clay_from_sand)"
                + "\n\n- [#](8B0000)String[#]() (neovitae:alchemytable/string)"
                + "\n\n- [#](8B0000)Cobweb[#]() (neovitae:alchemytable/cobweb)");

        this.page("recipes2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("More Recipes");
        this.pageText("Additional Tabula Vitae recipes include:"
                + "\n\n- [#](8B0000)Plant Oil[#]() from Wheat, Carrots, Potatoes, or Beets"
                + "\n\n- [#](8B0000)Coal Sand[#]() (neovitae:alchemytable/sand_coal)"
                + "\n\n- [#](8B0000)Explosive Powder[#]() (neovitae:alchemytable/explosive_powder)"
                + "\n\n- [#](8B0000)Flint[#]() from Gravel (neovitae:alchemytable/flint_from_gravel)"
                + "\n\n- [#](8B0000)Saltpeter[#]() (neovitae:alchemytable/saltpeter)"
                + "\n\n- [#](8B0000)Gunpowder[#]() (neovitae:alchemytable/gunpowder)");

        this.page("sigil_recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Lava Sigil[#]() and [#](8B0000)Water Sigil[#]() can be used in the Tabula Vitae to produce "
                + "[#](8B0000)Lava Buckets[#]() and [#](8B0000)Water Buckets[#]() respectively. The Sigils are not consumed in these "
                + "recipes. What's more, the [#](8B0000)Water Sigil[#]() can be used in place of a [#](8B0000)Water Bucket[#]() in "
                + "any Tabula Vitae recipe.");
    }

    @Override
    protected String entryName() {
        return "Tabula Vitae";
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
        return BookIconModel.create(NVBlocks.TABULA_VITAE.asItem());
    }

    @Override
    protected String entryId() {
        return "tabula_vitae";
    }
}
