package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class AthanorEntry extends EntryProvider {

    public AthanorEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Athanor");
        this.pageText("The [#](8B0000)Athanor[#]() is the alchemist's furnace, "
                + "a vessel of transmutation that transcends mere smelting. Within its crucible you may triple ore yields, "
                + "revert Orbs of Vitae, unmake Netherite and Reinforced Runes, and - most critically - distill "
                + "[#](8B0000)Weak Blood Shards[#]() from [#](8B0000)Saturated Tau[#]().\\\n\\\n"
                + "[#](2E8B57)Most tools placed in the Athanor degrade over time, but Unbreaking or Mending "
                + "enchantments will slow or halt their decay.[#]()");

        this.page("recipe_arc", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "athanor_block")));

        this.page("sanguine_reverter", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Sanguine Reverter");
        this.pageText("Forged within the Hellfire Forge, the [#](8B0000)Sanguine Reverter[#]() is a tool of "
                + "[#](4A0080)unmaking[#](). It peels away the enchantments of creation, reducing Orbs of Vitae, "
                + "Netherite, and Reinforced Runes back to their constituent materials. It is also the instrument "
                + "by which Weak Blood Shards are born from Saturated Tau.");

        this.page("athanor_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Recipes of Reversion");
        this.pageText("With the Sanguine Reverter, the Athanor can unmake:\n\n"
                + "- [#](8B0000)Novicius Orb of Vitae[#]()\n"
                + "- [#](8B0000)Discipulus Orb of Vitae[#]()\n"
                + "- [#](8B0000)Veneficus Orb of Vitae[#]()\n"
                + "- [#](8B0000)Magus Orb of Vitae[#]()\n"
                + "- Netherite Ingot\n"
                + "- Any Reinforced Rune (to base materials)");

        this.page("reversion", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Reclaiming Runes");
        this.pageText("Every Reinforced Rune placed in the Athanor alongside the Sanguine Reverter surrenders "
                + "its form and returns to base materials. This is invaluable when restructuring your altar, "
                + "apprentice - no investment need be permanent.");

        this.page("automation", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Automating the Athanor");
        this.pageText("[#](2E8B57)The Athanor is sided, much like a common furnace. "
                + "Tools may only enter or exit from the top. Inputs are accepted from the sides. "
                + "Outputs emerge from the bottom.[#]()\\\n\\\n"
                + "Keep this geometry in mind when placing your Hoppers or Routing Nodes. "
                + "An automated Athanor is the heart of any serious ore-processing chain.");
    }

    @Override
    protected String entryName() {
        return "The Athanor";
    }

    @Override
    protected String entryDescription() {
        return "The alchemist's crucible: smelter, ore tripler, and instrument of unmaking.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.ATHANOR_BLOCK.asItem());
    }

    @Override
    protected String entryId() {
        return "athanor";
    }
}
