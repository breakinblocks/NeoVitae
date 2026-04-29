package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualGreenGroveEntry extends EntryProvider {

    public RitualGreenGroveEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/green_grove"))
                .withMultiblockName("Ritual of the Green Grove")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("green_grove")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Verdant Awakening");
        this.pageText("Blood is the wellspring of all life, and this ritual proves it. The circle suffuses the surrounding soil with [#](4A0080)Essentia Vitae[#](), accelerating the growth of crops and plants within its reach as though seasons passed in moments.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Hastens the growth rate further."
                + "\n\n- [#](8B0000)Spiritus Ruina[#](): Hydrates nearby farmland, ensuring fertile soil."
                + "\n\n- [#](8B0000)Spiritus Vindicta[#](): Causes nearby plants to spread and propagate."
                + "\n\n- [#](8B0000)Spiritus Nihilum[#](): Intensifies the effect at higher spiritus concentrations."
                + "\n\n- [#](8B0000)Spiritus Invictus[#](): Coaxes nearby saplings into full-grown trees.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Green Grove";
    }

    @Override
    protected String entryDescription() {
        return "Suffuses the earth with life, hastening all growth.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BONE_MEAL);
    }

    @Override
    protected String entryId() {
        return "ritual_green_grove";
    }
}
