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

public class RitualCondorEntry extends EntryProvider {

    public RitualCondorEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/condor"))
                .withMultiblockName("Soaring Skies")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("condor")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Wings of the Condor");
        this.pageText("The circle bestows the gift of true flight upon all practitioners within its reach, not the crude hop of enchanted boots, but the unfettered freedom of a soaring bird. You may fly as freely as though gravity itself has been revoked.\\\n\\\n"
                + "[#](2E8B57)The flight range can be configured with the Ritual Configurator.[#]()");
    }

    @Override
    protected String entryName() {
        return "Soaring Skies";
    }

    @Override
    protected String entryDescription() {
        return "Bestows true flight upon all within the circle.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FEATHER);
    }

    @Override
    protected String entryId() {
        return "ritual_condor";
    }
}
