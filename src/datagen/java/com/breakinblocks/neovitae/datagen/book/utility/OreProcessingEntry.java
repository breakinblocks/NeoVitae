package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;

public class OreProcessingEntry extends EntryProvider {

    public OreProcessingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Alchemy of Ore");
        this.pageText("Blood has many uses, and among the most practical is the multiplication of mineral wealth. "
                + "The [#](8B0000)Tabula Vitae[#]() doubles your ore, while the [#](8B0000)Athanor[#]() "
                + "can yield 2.5 ingots per raw ore, or 4.5 ingots per ore block.\\\n\\\n"
                + "Every vein you mine becomes a bounty when viewed through the lens of [#](4A0080)Vitaemancy[#]().");

        this.page("basic_cutting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Cutting Fluid");
        this.pageText("[#](8B0000)Cutting Fluid[#]() is prepared in the Tabula Vitae and serves as the penultimate "
                + "reagent in all ore processing chains.\\\n\\\n"
                + "Within the Athanor, it dissolves an ore block into 3 portions of Metal Dust, "
                + "or a raw ore into 1.5 on average. [#](2E8B57)A simple Bottle of Water may substitute "
                + "for the Water Sigil in the recipe.[#]()");

        this.page("intermediate_cutting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Intermediate Cutting Fluid");
        this.pageText("A refined variant that endures eight times as long and hastens the Athanor's work "
                + "by 50%%. The [#](8B0000)Tau Oil[#]() it demands, however, can only be found by those "
                + "brave enough to [#](4A0080)delve into the demon realm[#]().");

        this.page("advanced_cutting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Advanced Cutting Fluid");
        this.pageText("The apex of dissolution. This fluid persists sixteen times longer, doubles crafting speed, "
                + "and doubles the probability of bonus yields. The [#](8B0000)Hellforged Dust[#]() "
                + "it requires lies buried in the deepest reaches of the [#](4A0080)Demon Realm[#]().");

        this.page("dust_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Metal Dust");
        this.pageText("The Tabula Vitae can grind raw ore into metallic dust, effectively doubling your yield:\n\n"
                + "- Iron Dust from Raw Iron\n"
                + "- Gold Dust from Raw Gold");

        this.page("athanor_ore", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Athanor's Yield");
        this.pageText("With access to the Athanor and a Cutting Fluid, every mined ore block yields three "
                + "portions of dust. This dust is then smelted into ingots as normal.\\\n\\\n"
                + "The discipline rewards patience, apprentice. Refine in stages, and the earth gives up "
                + "far more than it would to a common furnace.");

        this.page("smelting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Smelting Metal Dust");
        this.pageText("Metal Dust yields ingots in a standard furnace or blast furnace:\n\n"
                + "- Iron Dust smelts into Iron Ingots\n"
                + "- Gold Dust smelts into Gold Ingots\n"
                + "- Copper Dust smelts into Copper Ingots");

        this.page("explosive_powder", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Explosive Powder");
        this.pageText("[#](8B0000)Explosive Powder[#](), prepared in the Tabula Vitae, shatters ore into fragments "
                + "within the Athanor: 4.5 fragments per ore block, 2.25 per raw ore on average. "
                + "It can also reduce ingots to their dust form.\\\n\\\n"
                + "Netherrack, too, crumbles before it, yielding Sulfur and 50mb of Lava. "
                + "Two improved variants exist.");

        this.page("explosive_cells", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Explosive Cells");
        this.pageText("The [#](8B0000)Reinforced[#]() and [#](8B0000)Hellforged Explosive Cells[#]() are "
                + "superior variants prepared in the Tabula Vitae. Each outlasts and outpaces the "
                + "humble Explosive Powder, making them essential for sustained processing operations.");

        this.page("fragments", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fragments and Yields");
        this.pageText("Athanor recipes using Explosive Powder:\n\n"
                + "- Raw Ore to Ore Fragments (2.25 average)\n"
                + "- Ore Block to Ore Fragments (4.5 average)\n"
                + "- Netherrack to Sulfur + 50mb Lava");

        this.page("resonator", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Resonator");
        this.pageText("Forged in the Hellfire Forge, the [#](8B0000)Resonator[#]() vibrates ore fragments into "
                + "gravel for further refinement, producing [#](8B0000)Tiny Corrupted Dust[#]() as a byproduct. "
                + "The [#](B8860B)Reinforced[#]() variant is far more resilient, and the "
                + "[#](B8860B)Hellforged[#]() variant is nearly indestructible with doubled bonus outputs.");

        this.page("gravel_sand", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Refinement Chain");
        this.pageText("The full processing chain within the Athanor:\n\n"
                + "- Ore Fragment to Ore Gravel (using Resonator)\n"
                + "- Ore Gravel to Ore Dust (using Cutting Fluid)");

        this.page("corrupted_dust", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)Tiny Corrupted Dust[#]() can be combined into full "
                + "[#](8B0000)Corrupted Dust[#](), a potent catalyst that further amplifies ore yields. "
                + "Consult JEI for the combination recipe.");

        this.page("corrupted_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Corrupted Catalysts");
        this.pageText("Corrupted Dust merges with various materials in the Tabula Vitae to produce:\n\n"
                + "- [#](8B0000)Corrupted Coal[#]()\n"
                + "- [#](8B0000)Corrupted Iron[#]()\n"
                + "- [#](8B0000)Corrupted Debris[#]()");

        this.page("fuel_cell", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "furnacecell_primitive"))
                .withText(this.context().pageText()));
        this.pageText("The Athanor functions as a furnace, but accepts only two fuel sources: the "
                + "[#](8B0000)Primitive Fuel Cell[#]() or a [#](8B0000)Lava Crystal[#]().");

        this.page("fuel_info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("On Fuel Efficiency");
        this.pageText("The Primitive Fuel Cell endures for 128 individual operations, more than double what "
                + "the Block of Coal in its recipe would yield in a furnace. Better still, it wears down "
                + "only upon completing a craft, wasting nothing.\\\n\\\n"
                + "[#](2E8B57)An efficient Vitaemancer wastes neither blood nor fuel.[#]()");

        this.page("custom_materials", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Custom Materials");
        this.pageText("Neo Vitae's ore processing supports any material from any mod. "
                + "On first launch, the mod scans for installed ores and auto-generates processing entries.\\\n\\\n"
                + "Modpack makers can run [#](8B0000)/nvgenerate[#]() to re-scan after adding new mods, "
                + "or manually edit [#](8B0000)config/neovitae/materials.json[#]() to add, remove, "
                + "or customize materials. A restart is required for changes to take effect.");
    }

    @Override
    protected String entryName() {
        return "The Alchemy of Ore";
    }

    @Override
    protected String entryDescription() {
        return "Multiply your mineral wealth through cutting fluids, resonators, and the Athanor.";
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
