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

public class RitualFeatheredKnifeEntry extends EntryProvider {

    public RitualFeatheredKnifeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/feathered_knife"))
                .withMultiblockName("Ritual of the Willing Sacrifice")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Tenebrae] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("feathered_knife")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood Freely Given");
        this.pageText("Where the Well of Suffering takes from unwilling victims, this ritual draws from the practitioner's own vitality, a more honorable, if painful, path. It drains health from nearby blood mages and converts it into [#](8B0000)Essentia Vitae[#](), depositing the [#](4A0080)Essentia Vitae[#]() into a nearby [#](8B0000)Ara Vitae[#](). Efficiency improves with [#](8B0000)Runes of Self Sacrifice[#]() and the [#](8B0000)Tough Palms[#]() upgrade.");

        this.page("spiritus_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Increases the [#](8B0000)Essentia Vitae[#]() gained per health sacrificed."
                + "\n\n- [#](8B0000)Spiritus Ruina[#](): Channels the [#](8B0000)Incense Bonus[#]() from a nearby [#](8B0000)Incense Altar[#]()."
                + "\n\n- [#](8B0000)Spiritus Vindicta[#](): When paired with [#](8B0000)Invictus[#](), increases the drain rate."
                + "\n\n- [#](8B0000)Spiritus Nihilum[#](): Raises the maximum vitality drained per tick."
                + "\n\n- [#](8B0000)Spiritus Invictus[#](): Prevents the ritual from draining you to death.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Willing Sacrifice";
    }

    @Override
    protected String entryDescription() {
        return "Converts the practitioner's own vitality into EV.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ORB_WEAK.get());
    }

    @Override
    protected String entryId() {
        return "ritual_feathered_knife";
    }
}
