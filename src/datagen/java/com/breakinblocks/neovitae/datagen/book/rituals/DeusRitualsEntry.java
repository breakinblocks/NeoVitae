package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;

public class DeusRitualsEntry extends EntryProvider {

    public DeusRitualsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Deus Rituals");
        this.pageText("The greatest workings are ringed in [#](8B0000)Deus Ritual Stones[#](). Each Deus ritual is a mightier "
                + "form of a ritual you already know, its circle wrapped in an outer crown of Deus stones.\\\n\\\n"
                + "Inscribe them with the [#](8B0000)Ritual Diviner [Deus][#]() and wake them with a "
                + "[#](8B0000)Divinus Activation Crystal[#](). They answer to the same Spiritus as their lesser forms.");

        this.page("deus_well_of_suffering", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_well_of_suffering"))
                .withMultiblockName("Deus Well of Suffering")
                .withText(this.context().pageText()));
        this.pageText("The Well's reach extends to [#](8B0000)32 blocks[#]() and every wound yields [#](8B0000)twice[#]() the Essentia Vitae.");

        this.page("deus_well_of_suffering_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_well_of_suffering")));

        this.page("deus_torment_nexus", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_torment_nexus"))
                .withMultiblockName("Deus Torment Nexus")
                .withText(this.context().pageText()));
        this.pageText("Spawners are found up to [#](8B0000)64 blocks[#]() away, each operation rolls [#](8B0000)three times[#]() the loot, and each simulated kill costs under half the EV.");

        this.page("deus_torment_nexus_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_torment_nexus")));

        this.page("deus_magnetism", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_magnetism"))
                .withMultiblockName("Deus Endless Quarry")
                .withText(this.context().pageText()));
        this.pageText("Moves [#](8B0000)four times[#]() the ore each pulse. In the Ritual Configurator, choose [#](8B0000)Silk Touch[#]() to keep whole ore blocks or [#](8B0000)Fortune III[#]() to mine them for drops.");

        this.page("deus_magnetism_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_magnetism")));

        this.page("deus_crystallum_fractura", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_crystallum_fractura"))
                .withMultiblockName("Deus Crystallum Fractura")
                .withText(this.context().pageText()));
        this.pageText("Crystals grow [#](8B0000)four times[#]() as fast across a 31-wide area, and every harvest strikes with [#](8B0000)Fortune III[#]() at no cost in Spiritus.");

        this.page("deus_crystallum_fractura_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_crystallum_fractura")));

        this.page("deus_green_grove", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_green_grove"))
                .withMultiblockName("Deus Overgrowth")
                .withText(this.context().pageText()));
        this.pageText("Tends [#](8B0000)three times[#]() the area and pulses [#](8B0000)twice[#]() as often before any Spiritus is added.");

        this.page("deus_green_grove_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_green_grove")));

        this.page("deus_placer", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_placer"))
                .withMultiblockName("Deus Mason")
                .withText(this.context().pageText()));
        this.pageText("Lays [#](8B0000)eight times[#]() as many blocks each pulse, across four times the volume and twice the reach.");

        this.page("deus_placer_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_placer")));

        this.page("deus_felling", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_felling"))
                .withMultiblockName("Deus Fallen Trees")
                .withText(this.context().pageText()));
        this.pageText("Fells up to [#](8B0000)512 blocks[#]() an operation across a 41-wide area, replanting from its drops or the chest.");

        this.page("deus_felling_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_felling")));

        this.page("deus_meteor", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_meteor"))
                .withMultiblockName("Deus Meteo")
                .withText(this.context().pageText()));
        this.pageText("Calls the same meteors as the Ritual of Meteo, but every outer layer is [#](8B0000)pure ore[#](), with no filler stone at all.");

        this.page("deus_meteor_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_meteor")));

        this.page("deus_armour_evolve", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/deus_armour_evolve"))
                .withMultiblockName("Deus Sentient Evolution")
                .withText(this.context().pageText()));
        this.pageText("Stand upon it in Sentient Armor already evolved to [#](B8860B)500 Upgrade Points[#]() and the armor ascends to [#](B8860B)600[#](). Every piece you wear is bound with [#](8B0000)Blood Mending[#]() in the same working.");

        this.page("deus_armour_evolve_stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("deus_armour_evolve")));
    }

    @Override
    protected String entryName() {
        return "The Deus Rituals";
    }

    @Override
    protected String entryDescription() {
        return "The mightiest forms of the rituals, crowned in Deus stone.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.DEUS_RITUAL_STONE.item().get());
    }

    @Override
    protected String entryId() {
        return "deus_rituals";
    }
}
