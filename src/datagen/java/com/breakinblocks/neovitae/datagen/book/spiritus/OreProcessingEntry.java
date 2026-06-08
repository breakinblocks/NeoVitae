package com.breakinblocks.neovitae.datagen.book.spiritus;

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
        this.pageTitle("The Alchemy of Ore");
        this.pageText("Blood has many uses, and among the most practical is the multiplication of mineral wealth. "
                + "The [#](8B0000)Tabula Vitae[#]() doubles your ore, while the [#](8B0000)Athanor[#]() "
                + "yields 3 ingots per raw ore, or 5 ingots per ore block.\\\n\\\n"
                + "Both Athanor yields rise still further when [#](4A0080)Raw Spiritus[#]() saturates the chunk; "
                + "see the [#](8B0000)Spiritus Boost[#]() page that follows. Every vein you mine becomes a bounty "
                + "when viewed through the lens of [#](4A0080)Vitaemancy[#]().");

        this.page("basic_cutting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Cutting Fluid");
        this.pageText("[#](8B0000)Cutting Fluid[#]() is prepared in the Tabula Vitae and serves as the universal "
                + "reagent in every Athanor ore-processing step.\\\n\\\n"
                + "[#](2E8B57)A simple Bottle of Water may substitute for the Water Sigil in the recipe.[#]()");

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
        this.pageText("The apex of dissolution. This fluid persists sixty-four times longer, doubles crafting speed, "
                + "and doubles the probability of bonus yields. The [#](8B0000)Hellforged Dust[#]() "
                + "it requires lies buried in the deepest reaches of the [#](4A0080)Demon Realm[#]().");

        this.page("tabula_dust", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Metal Dust at the Tabula Vitae");
        this.pageText("The Tabula Vitae can grind ore directly into metallic dust, doubling your yield in a single "
                + "step. This is the fastest path; it lacks the rich multiplier of the Athanor but rewards no "
                + "fuel or chain refinement.\n\n"
                + "- Iron Dust from Iron Ore (2 dust per block)\n"
                + "- Gold Dust from Gold Ore (2 dust per block)\n"
                + "- Copper Dust from Copper Ore (2 dust per block)");

        this.page("athanor_ore", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Athanor Path");
        this.pageText("Every Athanor ore-processing step uses [#](8B0000)Cutting Fluid[#](). The full chain is:\n\n"
                + "1. Ore block becomes 5 [#](8B0000)Ore Fragments[#](), or raw ore becomes 3.\n"
                + "2. Each fragment becomes 1 [#](8B0000)Ore Gravel[#]() via the Resonator (with a 50%% chance "
                + "of Tiny Corrupted Dust on the side).\n"
                + "3. Each gravel becomes 1 [#](8B0000)Metal Dust[#]().\n"
                + "4. Each dust smelts to 1 ingot in any furnace.\n\n"
                + "Patient refinement rewards the practitioner. Five ingots per silk-touched block, three per raw, "
                + "before any bonus from the chunk's [#](4A0080)Raw Spiritus[#]().");

        this.page("spiritus_boost", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Spiritus Boost");
        this.pageText("When [#](4A0080)Raw Spiritus[#]() saturates the chunk in which the Athanor sits, each "
                + "ore-to-fragment craft has a chance to yield one extra fragment.\\\n\\\n"
                + "- Below 5 Raw Spiritus, no bonus.\n"
                + "- At 5 Raw Spiritus, the bonus chance is 33%%.\n"
                + "- The chance rises linearly with chunk saturation, reaching 100%% at 100 Raw Spiritus.\n\n"
                + "Each successful bonus has a small 2.5%% probability of consuming 1 Raw Spiritus from the chunk. "
                + "Place your Athanor in a chunk steeped in slain blood and the yields climb to "
                + "6 per ore block and 4 per raw ore at saturation.");

        this.page("smelting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Smelting Metal Dust");
        this.pageText("Metal Dust yields ingots in a standard furnace or blast furnace:\n\n"
                + "- Iron Dust smelts into Iron Ingots\n"
                + "- Gold Dust smelts into Gold Ingots\n"
                + "- Copper Dust smelts into Copper Ingots");

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
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "furnacecell_primitive"))
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
