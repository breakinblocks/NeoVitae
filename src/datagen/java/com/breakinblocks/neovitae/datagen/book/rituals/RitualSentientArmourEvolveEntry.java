package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualSentientArmourEvolveEntry extends EntryProvider {

    public RitualSentientArmourEvolveEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/armour_evolve"))
                .withMultiblockName("Ritual of Sentient Evolution")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("armour_evolve")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Crucible of Becoming");
        this.pageText("Stand upon the [#](8B0000)Master Ritual Stone[#]() while clad in [#](8B0000)Sentient Armor[#](), and the circle will pour its power into the symbiotic plates. The armor evolves; its capacity for upgrades deepens, its potential expands. This is the crucible through which your armor transcends its former limits.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Sentient Evolution";
    }

    @Override
    protected String entryDescription() {
        return "Evolves Sentient Armor beyond its former limits.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SENTIENT_PLATE.get());
    }

    @Override
    protected String entryId() {
        return "ritual_armour_evolve";
    }
}
