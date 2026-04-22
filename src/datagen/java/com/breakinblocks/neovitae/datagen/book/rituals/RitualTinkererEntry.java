package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.resources.Identifier;
import com.mojang.datafixers.util.Pair;

public class RitualTinkererEntry extends EntryProvider {

    public RitualTinkererEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Tinkerer");
        this.pageText("The [#](8B0000)Ritual Tinkerer[#]() is an essential instrument for the practitioner who demands mastery over their [#](4A0080)ritual circles[#](). It offers three modes of interaction, each granting deeper control. Cycle between them by pressing Sneak and Use.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual_reader")));

        this.page("modes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- [#](8B0000)Information[#](): Reveals the purpose of the selected [#](4A0080)ritual[#](), much like the [#](8B0000)Ritual Diviner[#]()."
                + "\n\n- [#](8B0000)Set Spiritus Consumed[#](): Attunes the ritual to consume specific types of [#](8B0000)Spiritus[#]() from the [#](4A0080)Aura[#](). Carry the desired [#](8B0000)Spiritus Crystals[#]() in your hotbar, one for each aspect you wish the ritual to draw upon. The effects of each spiritus type are detailed on individual ritual pages.");

        this.page("define_area", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- [#](8B0000)Define Area[#](): Specifies the zone in which the ritual operates, and displays the current boundaries. If multiple zones exist, pressing Sneak and Use on the [#](8B0000)Master Ritual Stone[#]() cycles between them."
                + "\\\n\\\n[#](2E8B57)Some rituals can be expanded far beyond their default range, but the EV cost scales to match. Tread carefully with your reserves.[#]()");
    }

    @Override
    protected String entryName() {
        return "Ritual Tinkerer";
    }

    @Override
    protected String entryDescription() {
        return "Fine-tune the reach and resonance of your circles.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RITUAL_READER.get());
    }

    @Override
    protected String entryId() {
        return "ritual_tinkerer";
    }
}
