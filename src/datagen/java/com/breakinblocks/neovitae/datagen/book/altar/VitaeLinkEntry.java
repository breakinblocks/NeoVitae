package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.datagen.book.page.BookAraVitaeRecipePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class VitaeLinkEntry extends EntryProvider {

    public VitaeLinkEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Vitae Link");
        this.pageText("Left to its own devices, the [#](8B0000)Ara Vitae[#]() climbs the whole slate chain: "
                + "feed it deepslate and it will not stop until the basin holds the highest [#](8B0000)Tabula[#]() its "
                + "runes allow. The [#](8B0000)Vitae Link[#]() ends that tyranny. Bind one to a nearby altar and it "
                + "crafts on the altar's behalf, but only up to a [#](8B0000)tier you choose[#]().");

        this.page("recipe", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "ara_vitae/vitae_link"))
                .withText(this.context().pageText()));
        this.pageText("Place an [#](8B0000)Ara Vitae[#]() within another, larger altar holding at least "
                + "[#](4A0080)1,500 Essentia Vitae[#](), and the lesser altar is folded inward upon itself "
                + "to form the Link.");

        this.page("binding", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Binding");
        this.pageText("Set the Link within [#](8B0000)eight blocks[#]() of an altar and it reaches out, a crimson "
                + "thread snapping taut between the two. From then on it draws [#](4A0080)Essentia Vitae[#]() straight "
                + "from that altar's basin and borrows its [#](8B0000)rune bonuses[#]() for speed and efficiency. It "
                + "holds no essence of its own.\\\n\\\n[#](2E8B57)A Link with no altar in reach gives off thin smoke. "
                + "Glance at it with Jade to read its bond.[#]()");

        this.page("tier", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Choosing the Tier");
        this.pageText("[#](8B0000)Sneak and use[#]() the Link to cycle its craft tier. A Link can never reach the "
                + "altar it serves; it caps [#](8B0000)one tier below[#](). Bind to a tier-five altar and the Link "
                + "may climb to four, no further, then wraps back to zero.\\\n\\\nShould the altar lose a ring and "
                + "drop a tier, the Link follows it down. Should the altar grow, the Link stays where you set it.");

        this.page("usage", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Working the Link");
        this.pageText("[#](8B0000)Use[#]() the Link with an item to drop it in; [#](8B0000)use[#]() it empty-handed "
                + "to take the result back. Like the altar, it accepts [#](8B0000)one item at a time[#](). It crafts "
                + "in place, climbing the chain only as far as your chosen tier, then sets the finished piece in its "
                + "[#](8B0000)output[#]() to await collection.\\\n\\\n[#](2E8B57)Use it while it works to cancel the "
                + "craft and reclaim the item. The tier cannot be changed mid-craft.[#]()");

        this.page("automation", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Many Hands");
        this.pageText("Pipe into any side but the bottom to feed the [#](8B0000)input[#](); draw from the bottom to "
                + "empty the [#](8B0000)output[#](). Because input and output are kept apart, an automated line never "
                + "claws back its own ingredients.\\\n\\\nLay down as many Links as you like around one altar. Only "
                + "[#](8B0000)one crafts at a time[#](), and never while the altar itself is busy. When several wait, "
                + "the [#](8B0000)highest tier[#]() takes priority, then the rest in turn.");

        this.page("orb_link", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Orb Vitae Link");
        this.pageText("A cousin of the Link, the [#](8B0000)Orb Vitae Link[#]() does not craft; it [#](8B0000)charges[#](). "
                + "Seat a single [#](8B0000)Blood Orb[#]() within it (use with an orb in hand, use empty-handed to take it "
                + "back), and it pulls [#](4A0080)Essentia Vitae[#]() from its bound altar straight into the orb owner's "
                + "[#](8B0000)Anima[#](), at the altar's [#](8B0000)full tier[#]() and runes, until the network is full.\\\n\\\n"
                + "[#](2E8B57)It sits lowest in the chain: it draws only while the altar is idle and no Vitae Link is "
                + "crafting, sipping the leftover essence so it never starves a working.[#]()");

        this.page("orb_link_recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "orb_filling_link"))
                .withText(this.context().pageText()));
        this.pageText("Combine a [#](8B0000)Vitae Link[#]() with [#](8B0000)two iron ingots[#]() and a [#](8B0000)piece of "
                + "glass[#]() at a crafting table. Its [#](8B0000)comparator[#]() output rises with how full the owner's "
                + "Anima has become, ideal for switching off your blood engines once you are topped off.");
    }

    @Override
    protected String entryName() {
        return "Vitae Link";
    }

    @Override
    protected String entryDescription() {
        return "A bound relay that crafts at a capped tier and pipes in and out.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.VITAE_LINK.asItem());
    }

    @Override
    protected String entryId() {
        return "vitae_link";
    }
}
