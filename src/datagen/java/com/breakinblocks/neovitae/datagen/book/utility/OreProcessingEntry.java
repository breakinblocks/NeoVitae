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

public class OreProcessingEntry extends EntryProvider {

    public OreProcessingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ore Processing");
        this.pageText("The Tabula Vitae can be used, amongst other things, for ore doubling, whilst the "
                + "Athanor can give you 2.5 ingots per piece of Raw Ore, or 4.5 ingots "
                + "per Ore Block. Get more ore out of your mining expeditions with the power of blood!");

        this.page("basic_cutting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Cutting Fluid");
        this.pageText("Cutting Fluid is crafted in the Tabula Vitae. It is the penultimate step in all forms of "
                + "Ore Processing.\\\n\\\n"
                + "It can be used in the Athanor to get 3 Ore Sand from one "
                + "Ore Block, or 1.5 Ore Sand from one Raw Ore (on average). While a Water Sigil is used in the "
                + "recipe, a simple Bottle of Water may be used.");

        this.page("intermediate_cutting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Intermediate Cutting Fluid");
        this.pageText("Intermediate Cutting Fluid is an improved version that lasts eight times as long and "
                + "increases crafting speed by 50%%. You'll have to go Dungeon Delving for the Tau Oil, though.");

        this.page("advanced_cutting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Advanced Cutting Fluid");
        this.pageText("Advanced Cutting Fluid lasts sixteen times as long, doubles crafting speed, and doubles "
                + "the chance of getting bonus outputs from any recipes it's used in. You'll have to do some deep "
                + "Dungeon Delving for the Hellforged Sand it needs.");

        this.page("sand_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Metal Sand");
        this.pageText("The Tabula Vitae can produce Metal Sand from Raw Ore for ore doubling:\n"
                + "- Iron Sand from Raw Iron\n"
                + "- Gold Sand from Raw Gold");

        this.page("athanor_ore", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Athanor Ore Processing");
        this.pageText("Once you have access to the Athanor, you can get 3 sand from every "
                + "ore you mine using a Cutting Fluid. Metal Sand can then be smelted into ingots.");

        this.page("smelting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Smelting Metal Sand");
        this.pageText("Metal Sand can be smelted in a furnace to produce ingots:\n"
                + "- Iron Sand smelts into Iron Ingots\n"
                + "- Gold Sand smelts into Gold Ingots");

        this.page("explosive_powder", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Explosive Powder");
        this.pageText("Explosive Powder is crafted in the Tabula Vitae. In the Athanor, it is used to turn Ores into "
                + "4.5 Ore Fragments on average, or Raw Ores into 2.25 fragments on average, or turn Ingots into "
                + "their Sand variant.\\\n\\\n"
                + "It can also turn Netherrack into Sulfur and 50mb of Lava. It has 2 improved variants.");

        this.page("explosive_cells", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Explosive Cells");
        this.pageText("The Reinforced and Hellforged Explosive Cells are improved versions crafted in the Tabula Vitae. "
                + "They last longer and craft faster than the basic Explosive Powder variant.");

        this.page("fragments", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fragments and Processing");
        this.pageText("Athanor recipes with Explosive Powder:\n"
                + "- Raw Ore to Ore Fragments (2.25 average)\n"
                + "- Ore Block to Ore Fragments (4.5 average)\n"
                + "- Netherrack to Sulfur + 50mb Lava");

        this.page("resonator", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Resonator");
        this.pageText("The Resonator is crafted in the Hellfire Forge. It is used to turn Ore Fragments into the "
                + "relevant Gravel for continued ore processing, and creates Tiny Corrupted Dust. The Reinforced "
                + "Resonator has 4x durability, and the Hellforged Resonator has 16x durability and doubles any "
                + "bonus outputs.");

        this.page("gravel_sand", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fragment to Sand");
        this.pageText("Athanor recipes for further processing:\n"
                + "- Ore Fragment to Ore Gravel (using Resonator)\n"
                + "- Ore Gravel to Ore Sand (using Cutting Fluid)");

        this.page("corrupted_dust", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Tiny Corrupted Dust can be combined into Corrupted Dust, which can be used to further "
                + "boost the yield of other ores. View this recipe in JEI.");

        this.page("corrupted_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Corrupted Recipes");
        this.pageText("Corrupted Dust can be combined in the Tabula Vitae with various materials:\n"
                + "- Corrupted Coal\n"
                + "- Corrupted Iron\n"
                + "- Corrupted Debris");

        this.page("fuel_cell", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "furnacecell_primitive"))
                .withText(this.context().pageText()));
        this.pageText("The Athanor also functions as a Furnace, but the only fuel sources it accepts are the "
                + "Primitive Fuel Cell or a Lava Crystal.");

        this.page("fuel_info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fuel Cell Details");
        this.pageText("The Primitive Fuel Cell is good for 128 individual uses. That's more than the Block of Coal "
                + "used to craft it (60 items), and since it only loses durability when the crafting is finished "
                + "it will not waste fuel.");
    }

    @Override
    protected String entryName() {
        return "Ore Processing";
    }

    @Override
    protected String entryDescription() {
        return "Multiply your ore yields with cutting fluids and the Athanor.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.BASIC_CUTTING_FLUID.get());
    }

    @Override
    protected String entryId() {
        return "ore_processing";
    }
}
