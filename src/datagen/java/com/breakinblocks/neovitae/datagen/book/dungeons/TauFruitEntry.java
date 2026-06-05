package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class TauFruitEntry extends EntryProvider {

    public TauFruitEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tau Fruit");
        this.pageText("[#](8B0000)Tau Fruit[#]() is first encountered growing wild in the [#](4A0080)Simple Dungeons[#]() "
                + "of the Demon Realm. Once harvested, it can be cultivated, though as a native growth of "
                + "that accursed place, its lifecycle is far more demanding than any mundane crop.\\\n\\\n"
                + "Left to mature under ordinary conditions, it ripens into common [#](8B0000)Tau Fruit[#](), "
                + "which can be refined into [#](8B0000)Tau Oil[#]().");

        this.page("tau_oil", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tau Oil");
        this.pageText("[#](8B0000)Tau Oil[#]() is rendered from its fruit upon the [#](8B0000)Tabula Vitae[#](). "
                + "It carries a scent reminiscent of blood oranges, though with an unmistakable undertone "
                + "of something decidedly otherworldly.");

        this.page("tau_oil_uses", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Tau Oil serves two principal purposes in Vitaemancy:\n\n"
                + "- It extends the duration of any [#](8B0000)anointment[#]() by a factor of four.\n\n"
                + "- It is a key ingredient in [#](8B0000)Intermediate Cutting Fluid[#](), which functions "
                + "like its basic counterpart but endures eight times longer and provides a 25%% speed boost.");

        this.page("saturated_tau", () -> BookSpotlightPageModel.create()
                .withItem(NVBlocks.STRONG_TAU.asItem())
                .withTitle("Saturated Tau")
                .withText(this.context().pageText()));
        this.pageText("There exists a darker method of cultivation. If a [#](4A0080)living creature[#]() stands "
                + "atop the plant as it matures, the Tau vine will draw upon the creature's vitality to "
                + "feed its own growth. The result is [#](8B0000)Saturated Tau[#](), a far more potent variant "
                + "born from stolen life.");
    }

    @Override
    protected String entryName() {
        return "Tau Fruit";
    }

    @Override
    protected String entryDescription() {
        return "A parasitic fruit of the Demon Realm, nourished by the living.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.WEAK_TAU.asItem());
    }

    @Override
    protected String entryId() {
        return "tau_fruit";
    }
}
