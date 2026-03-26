package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookAraVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class TeleposerEntry extends EntryProvider {

    public TeleposerEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Teleposers");
        this.pageText("[#](8B0000)Teleposers[#]() allow for a form of redstone-controlled teleportation. Simply craft a "
                + "Teleposition Focus (overleaf), bind it to your target teleposer, place it in another teleposer, "
                + "and apply a redstone signal to the teleposer with a focus in it. Anything - blocks, items, "
                + "entities, players - in a defined area above the two teleposers will be swapped.");

        this.page("recipe_teleposer", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "teleposer"))
                .withText(this.context().pageText()));
        this.pageText("Nothing comes for free, however; transporting blocks or entities via the teleposer will cost "
                + "1 LP each for every two blocks traversed, to a maximum of 1,000 LP per block/entity, "
                + "or 10,000 LP total.");

        this.page("focus", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Teleposition Focus");
        this.pageText("The basic Teleposer Focus is crafted in the Ara Vitae. "
                + "It will swap anything in a 1x1x1 block area above the two teleposers.");

        this.page("enhanced_focus", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Enhanced Focus");
        this.pageText("The Enhanced Teleposer Focus is also crafted in the Ara Vitae. "
                + "It will swap anything in a 3x3x3 block area centred directly above the two teleposers.");

        this.page("reinforced_focus", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "ara_vitae/enhanced_teleposer_focus"))
                .withText(this.context().pageText()));
        this.pageText("The Reinforced Teleposer Focus will swap anything in a 5x5x5 block area centred "
                + "directly above the two teleposers.");

        this.page("linking", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Linking Teleposers");
        this.pageText("Teleposers can be linked one-way (such that a redstone signal to the exiting Teleposer "
                + "does nothing), two-way (such that each Teleposer has a Focus in it linking to the other, "
                + "making you teleport back and forth at a redstone signal from either end), or they can even "
                + "be chained - A to B to C and back to A. From base-traversing elevator systems to complex "
                + "underground labyrinths, go nuts!");

        this.page("redstone", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Redstone Power");
        this.pageText("If you're having difficulty getting your Teleposer to work, make sure that it is being "
                + "strongly redstone powered. This means that placing a redstone block next to the block won't "
                + "power it - you'll need redstone dust or a repeater pointing into the side of the Teleposer, "
                + "or a lever or button directly on it. Life Essence isn't free after all, so the Teleposer is "
                + "designed to minimize accidental misfires.");
    }

    @Override
    protected String entryName() {
        return "Teleposers";
    }

    @Override
    protected String entryDescription() {
        return "Redstone-controlled teleportation via block swapping.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.TELEPOSER_FOCUS.get());
    }

    @Override
    protected String entryId() {
        return "teleposer";
    }
}
