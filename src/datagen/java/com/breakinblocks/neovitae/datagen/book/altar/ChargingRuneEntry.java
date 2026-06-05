package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.Identifier;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class ChargingRuneEntry extends EntryProvider {

    public ChargingRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Charging Rune");
        this.pageText("The [#](8B0000)Charging Rune[#]() inscribes a pattern of [#](4A0080)anticipation[#]() into the altar. "
                + "When the [#](8B0000)Ara Vitae[#]() lies idle, neither crafting nor filling an Orb of Vitae, "
                + "it siphons [#](4A0080)Essentia Vitae[#]() into a hidden reserve within the rune itself. The moment an "
                + "item is placed upon the altar, this stored charge floods into the transmutation instantly, "
                + "at a perfect 1:1 ratio.");

        this.page("formula", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The altar pulses once every 20 ticks, reduced by 1 per [#](8B0000)Acceleration Rune[#]()."
                + "\\\n\\\nEach pulse stores: [[#](8B0000)10 EV[#]() x [#](8B0000)Charging Runes[#]() x (1 + [#](8B0000)Speed Runes[#]()/10)]"
                + "\\\n\\\nMaximum stored charge: [#](8B0000)1,000 EV[#]() per [#](8B0000)Charging Rune[#](), multiplied by "
                + "[altar capacity / 20,000] if that ratio exceeds 1.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "rune_charging")));

    }

    @Override
    protected String entryName() {
        return "Charging Rune";
    }

    @Override
    protected String entryDescription() {
        return "Stores Essentia Vitae during idle moments for instant transmutation.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_CHARGING.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_charging";
    }
}
