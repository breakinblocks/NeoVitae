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
        this.pageTitle("Athanor");
        this.pageText("The [#](8B0000)Athanor[#]() can function as a Furnace, "
                + "offers a form of Ore-Tripling, can revert Blood Orbs, Netherite and Reinforced Runes, "
                + "and is currently the only way to get Weak Blood Shards, specifically from a Saturated Tau.\\\n\\\n"
                + "Most tools used within the Athanor degrade over time, but can be enchanted with unbreaking or "
                + "mending to compensate.");

        this.page("recipe_arc", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "athanor_block")));

        this.page("sanguine_reverter", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sanguine Reverter");
        this.pageText("The Sanguine Reverter is crafted in the Hellfire Forge. It is used to create Weak Blood Shards, "
                + "and revert Blood Orbs, Netherite and Reinforced Runes to their input crafting item.");

        this.page("athanor_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Athanor Recipes");
        this.pageText("The Athanor can produce Weak Blood Shards from Saturated Tau, and revert various items:\n"
                + "- Weak Blood Orb\n"
                + "- Apprentice Blood Orb\n"
                + "- Magician Blood Orb\n"
                + "- Master Blood Orb\n"
                + "- Netherite Ingot\n"
                + "- Any Reinforced Rune (back to base materials)");

        this.page("reversion", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune Reversion");
        this.pageText("All Reinforced Runes can be reverted in the Athanor with a Sanguine Reverter. "
                + "This is useful if you want to reclaim the materials from runes you no longer need.");

        this.page("automation", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Automation");
        this.pageText("Should you wish to automate the Athanor, perhaps as part of an Ore-Tripling chain, "
                + "you may want to know that it is sided, much like a furnace. Tools can only be inserted or "
                + "extracted from the top, inputs from the sides, and outputs from the bottom. Keep this in mind "
                + "when placing your Hoppers or Routing Nodes.");
    }

    @Override
    protected String entryName() {
        return "Athanor";
    }

    @Override
    protected String entryDescription() {
        return "The Athanor: furnace, ore tripler, and item reverter.";
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
