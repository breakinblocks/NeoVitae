package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualHarvestEntry extends EntryProvider {

    public RitualHarvestEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/harvest"))
                .withMultiblockName("Ritual of Harvest")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Tenebrae] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("harvest")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Reaper's Bounty");
        this.pageText("This ritual commands an unseen sickle across your fields, harvesting mature crops and replanting them in a single sweep. Its reach spans eight blocks above and below the Master Ritual Stone, so stacked or sunken farms are gathered all at once. Place a [#](8B0000)chest[#]() atop the Master Ritual Stone and the bounty is funneled straight into it; without one it falls to the ground where it grew. Combined with the [#](8B0000)Ritual of Overgrowth[#](), you have the makings of a self-sustaining farm.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Harvest";
    }

    @Override
    protected String entryDescription() {
        return "Reaps and replants mature crops with an unseen sickle.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WHEAT);
    }

    @Override
    protected String entryId() {
        return "ritual_harvest";
    }
}
