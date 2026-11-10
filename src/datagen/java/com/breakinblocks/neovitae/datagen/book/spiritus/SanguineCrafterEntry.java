package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;

public class SanguineCrafterEntry extends EntryProvider {

    public SanguineCrafterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sanguine Crafter");
        this.pageText("The [#](8B0000)Sanguine Crafter[#]() is a tireless workshop of stone and blood. It works "
                + "[#](8B0000)crafting table[#](), [#](8B0000)Tabula Vitae[#]() and [#](8B0000)Hellfire Forge[#]() "
                + "recipes without the station beside it, pulling ingredients from a buffer you can fill by any means.\\\n\\\n"
                + "Every recipe still pays its true price: the orb, the gem and the Essentia Vitae its own station would ask.");

        this.page("structure", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "sanguine_crafter"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Workshop");
        this.pageText("A 3x3 floor of [#](8B0000)Sanguine Casing[#]() with a ring of casings on top, and the "
                + "[#](8B0000)Sanguine Crafter[#]() in the middle of one side of the ring, facing out. The block in the center, "
                + "right behind the crafter, can be anything, so a pipe can reach in. Once whole, the casings join into one shell.");

        this.page("recipes", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "sanguine_crafter"))
                .withRecipeId2(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "sanguine_casing")));

        this.page("template", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Template");
        this.pageText("The grid on the left of the crafter's menu is a [#](8B0000)template[#](). Click items into it, "
                + "or press the [#](8B0000)+[#]() button on a recipe in JEI, and the crafter learns that recipe. "
                + "The mode button chooses whether the template is read as a crafting table, Tabula Vitae or Hellfire Forge recipe.\\\n\\\n"
                + "[#](8B0000)Auto[#]() mode needs no template: it crafts whichever Tabula Vitae or Hellfire Forge recipe "
                + "the input buffer holds exactly.");

        this.page("feeding", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Feeding the Crafter");
        this.pageText("Ingredients go into the [#](8B0000)input buffer[#]() in any order, and results gather in the "
                + "[#](8B0000)output buffer[#](). Every casing and the crafter itself accept hoppers, pipes and "
                + "[#](4A0080)Routing Nodes[#]().\\\n\\\n"
                + "Click an empty slot to choose which sides reach its group. With the output buffer chosen, "
                + "right-click a side to push results out of it into whatever waits there.");

        this.page("controls", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The redstone button sets whether a signal on the crafter starts or stops it. The "
                + "[#](8B0000)limit[#]() halts crafting while the output buffer holds that many of the result; "
                + "scroll over it to change it.\\\n\\\n"
                + "Hover over the arrow or the status line to see why the crafter waits, how many crafts it runs at "
                + "once and whether it keeps its chunks loaded.");

        this.page("costs", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Paying the Price");
        this.pageText("Each craft draws a little [#](4A0080)EV[#]() from the network of whoever built the crafter.\\\n\\\n"
                + "Tabula Vitae recipes need a bound [#](8B0000)Blood Orb[#]() of their tier in the orb slot, "
                + "and that orb's network pays the recipe's EV. Hellfire Forge recipes draw on the "
                + "[#](8B0000)Spiritus Gem[#]() in the gem slot, which drinks from the local Aura like the forge's own.");

        this.page("upgrades", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Quickening the Workshop");
        this.pageText("Any casing may be swapped for an upgraded one.\\\n\\\n"
                + "Each [#](8B0000)Hellforged Sanguine Casing[#]() lets the crafter finish one more craft at a time, "
                + "up to sixteen by default. Each [#](8B0000)Swift Sanguine Casing[#]() makes every craft 25%% faster by default.");

        this.page("upgrade_recipes", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "hellforged_sanguine_casing"))
                .withRecipeId2(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "swift_sanguine_casing")));

        this.page("storage_networks", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Storage Networks");
        this.pageText("The crafter serves an AE2 pattern provider or Refined Storage autocrafter like any machine. "
                + "Leave it in [#](8B0000)Auto[#]() so one crafter takes every pattern, keep the fuel slots off the "
                + "provider's side, and push results back out of that side.\\\n\\\n"
                + "Turn on the provider's [#](8B0000)blocking mode[#](): two recipes pushed at once would stall it.");

        this.page("chunk_loading", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Workshop That Never Sleeps");
        this.pageText("While whole and running, the crafter keeps the chunks it stands in loaded so your work goes on "
                + "when you wander off. By default this lasts only while its builder is online, and each player may "
                + "keep ten crafters working this way. The eye beside the input buffer turns it off for one crafter. "
                + "Server owners may change or disable this.");

        this.page("packing", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Packing and Unpacking");
        this.pageText("Packing and Unpacking need no template. Packing combines 4 or 9 identical items; Unpacking separates one packed item. Both modes require existing crafting recipes in both directions that restore the exact original items and quantity. One-way recipes are skipped. Mixed materials are processed separately at normal crafting cost and speed.");
    }

    @Override
    protected String entryName() {
        return "Sanguine Crafter";
    }

    @Override
    protected String entryDescription() {
        return "A workshop that crafts while you are away.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.SANGUINE_CRAFTER.asItem());
    }

    @Override
    protected String entryId() {
        return "sanguine_crafter";
    }
}
