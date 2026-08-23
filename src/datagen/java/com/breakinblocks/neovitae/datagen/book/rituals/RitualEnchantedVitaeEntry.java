package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import com.breakinblocks.neovitae.ritual.types.RitualEnchantedVitae;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualEnchantedVitaeEntry extends EntryProvider {

    public RitualEnchantedVitaeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/" + RitualEnchantedVitae.NAME))
                .withMultiblockName("Ritual of Enchanted Vitae")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats(RitualEnchantedVitae.NAME)));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Enchanted Vitae");
        this.pageText("Throw a single item onto the master stone along with as many [#](8B0000)enchanted books[#]() as you care to offer. The rite reads each book in turn and binds every enchantment the item is able to carry, then hands the books back untouched, unless the pack you are playing has set them to be spent."
                + "\n\nOnly [#](B8860B)one[#]() item may lie in the zone at a time. Books stack freely beside it.");

        this.page("rules", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("What Binds");
        this.pageText("An enchantment is bound when the item can carry it and the offered level beats what the item already holds. The rite keeps no ceiling of its own: whatever level a book names is the level it binds, however far past the ordinary limit that reaches."
                + "\n\nWhere two books offer the same enchantment, the higher level wins. An enchantment the item cannot carry at all is simply passed over.");

        this.page("conflict", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("What Refuses");
        this.pageText("Two enchantments that cannot share an item will not be reconciled for you. The pentacle stops, a word of warning names the offending pair, and the books that carry them [#](B8860B)shine for ten seconds[#]() so you can pick them out of the pile. A book may also quarrel with something already inscribed on the item itself."
                + "\n\nA halted rite will not start again on its own, however many books you take away. Lift the [#](B8860B)item[#]() from the stone and offer it once more; only then does the pentacle turn, and it names what it is about to bind before it does, so a book swept up by mistake is caught before anything is spent.");

        this.page("cost", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Toll");
        this.pageText("Each bound level draws [#](4A0080)2,000 Essentia Vitae[#](), multiplied by how scarce the enchantment is in the world; the rarest command five times the toll."
                + "\n\nThe whole sum is taken at once when the rite completes. If your [#](8B0000)Anima[#]() cannot cover it the pentacle stalls and nothing is bound.");

        this.page("closes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Rite Closes");
        this.pageText("The pentacle goes dark of its own accord once the binding is done. It is no standing work; it turns once, spends itself, and stops."
                + "\n\nTo enchant a second item, activate the rite afresh with an [#](8B0000)Activation Crystal[#](). Its [#](4A0080)10,000 Essentia Vitae[#]() of activation is paid again every turning, on top of the toll for the levels bound.");
    }

    @Override
    protected String entryName() {
        return "Enchanted Vitae";
    }

    @Override
    protected String entryDescription() {
        return "Binds the enchantments of offered books onto a single item, at any level, without spending the books.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ENCHANTED_BOOK);
    }

    @Override
    protected String entryId() {
        return "ritual_enchanted_vitae";
    }
}
