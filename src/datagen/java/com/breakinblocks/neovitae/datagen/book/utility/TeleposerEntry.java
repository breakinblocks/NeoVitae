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
import net.minecraft.resources.Identifier;

public class TeleposerEntry extends EntryProvider {

    public TeleposerEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Teleposer");
        this.pageText("[#](8B0000)Teleposers[#]() bend space through the power of blood, swapping everything "
                + "above two linked points - blocks, items, entities, even you - in the blink of an eye.\\\n\\\n"
                + "Craft a [#](8B0000)Teleposition Focus[#](), bind it to a target Teleposer, "
                + "place it in a second, and apply a redstone signal. The exchange is instantaneous.");

        this.page("recipe_teleposer", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "teleposer"))
                .withText(this.context().pageText()));
        this.pageText("Nothing in [#](4A0080)Vitaemancy[#]() is free. Each block or entity transported costs "
                + "1 [#](4A0080)Essentia Vitae[#]() per two blocks of distance, up to 1,000 per object "
                + "and 10,000 total per activation.");

        this.page("focus", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Teleposition Focus");
        this.pageText("The basic [#](8B0000)Teleposition Focus[#]() is crafted in the Ara Vitae. "
                + "It commands a modest 1x1x1 exchange zone directly above each Teleposer - enough to "
                + "transport a single entity or block with precision.");

        this.page("enhanced_focus", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Enhanced Focus");
        this.pageText("The [#](8B0000)Enhanced Teleposition Focus[#](), also forged in the Ara Vitae, "
                + "widens the exchange to a 3x3x3 volume centred above the Teleposer. "
                + "Entire small structures can be relocated in a single pulse.");

        this.page("reinforced_focus", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "ara_vitae/enhanced_teleposer_focus"))
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Reinforced Teleposition Focus[#]() commands a 5x5x5 volume - "
                + "a formidable displacement field.");

        this.page("linking", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Topologies of Transport");
        this.pageText("Teleposers may be linked in many configurations: one-way (the receiving end stays inert), "
                + "two-way (a Focus in each, swapping on either signal), or chained in sequence - "
                + "A to B to C and back to A.\\\n\\\n"
                + "From vertical elevators threading your tower to labyrinthine transit networks beneath the earth, "
                + "the possibilities are boundless. Be creative, apprentice.");

        this.page("redstone", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Note on Redstone");
        this.pageText("[#](2E8B57)The Teleposer requires strong redstone power to activate. A redstone block "
                + "placed adjacent will not suffice - you need redstone dust or a repeater pointing into "
                + "its side, or a lever or button placed directly upon it.[#]()\\\n\\\n"
                + "[#](4A0080)Essentia Vitae[#]() is not free, and the Teleposer is deliberately designed "
                + "to minimize accidental misfires. Precision in all things.");

        this.page("spawners", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Relocating Spawners");
        this.pageText("[#](8B0000)Mob spawners and trial spawners[#]() count as ordinary blocks to the "
                + "Teleposer; their settings, current state, and any chained behaviour ride along with them. "
                + "Build your perfect chamber elsewhere, then transpose the spawner into it.\\\n\\\n"
                + "Bedrock, end portals, command blocks, and similar [#](4A0080)protected blocks[#]() are "
                + "rejected by the exchange. The full list is governed by the "
                + "[#](2E8B57)#neovitae:telepose_blacklist[#]() block tag - pack authors can extend or "
                + "shorten it to taste.");
    }

    @Override
    protected String entryName() {
        return "Teleposers";
    }

    @Override
    protected String entryDescription() {
        return "Bend space with blood - redstone-controlled spatial exchange.";
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
