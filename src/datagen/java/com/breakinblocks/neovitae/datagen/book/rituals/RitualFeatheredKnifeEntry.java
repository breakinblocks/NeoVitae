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
        this.pageText("Where the Well of Suffering takes from unwilling victims, this ritual draws from the practitioner's own vitality, a more honorable, if painful, path. Every second it takes half a heart from each blood mage near the Master Ritual Stone and deposits the [#](8B0000)Essentia Vitae[#]() into a nearby [#](8B0000)Ara Vitae[#](), stopping once you fall to 30%% of your health. Efficiency improves with [#](8B0000)Runes of Self Sacrifice[#]() and the [#](8B0000)Tough Palms[#]() upgrade. An [#](8B0000)Incense Altar[#]() only helps when the ritual is fed [#](8B0000)Spiritus Ruina[#]().");

        this.page("spiritus_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Halves the time between drains, so the ritual takes blood twice as often."
                + "\n\n- [#](8B0000)Spiritus Ruina[#](): While you carry an [#](8B0000)Incense Bonus[#](), drains you to the safety floor in one stroke and multiplies the [#](8B0000)Essentia Vitae[#]() by that bonus. The incense is used up and you are left with [#](8B0000)Soul Fray[#]()."
                + "\n\n- [#](8B0000)Spiritus Vindicta[#](): Lowers the safety floor from 30%% to 10%% of your health. Has no effect alongside [#](8B0000)Invictus[#]()."
                + "\n\n- [#](8B0000)Spiritus Nihilum[#](): Increases the [#](8B0000)Essentia Vitae[#]() gained from each drain, scaling with the Nihilum present."
                + "\n\n- [#](8B0000)Spiritus Invictus[#](): Raises the safety floor from 30%% to 70%% of your health.");
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
