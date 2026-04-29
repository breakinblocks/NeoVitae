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

public class RitualSpeedEntry extends EntryProvider {

    public RitualSpeedEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/speed"))
                .withMultiblockName("Ritual of Speed")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("speed")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Quickened Blood");
        this.pageText("The circle hums with kinetic potential, infusing all practitioners within its reach with unnatural swiftness. Your blood runs hot and your limbs move as though unburdened by mortal weight.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Heightens the swiftness granted."
                + "\n\n- [#](8B0000)Spiritus Ruina[#](): Afflicts hostile creatures with Slowness."
                + "\n\n- [#](8B0000)Spiritus Vindicta[#](): Bestows Haste upon practitioners."
                + "\n\n- [#](8B0000)Spiritus Nihilum[#](): Amplifies the effect at higher spiritus concentrations."
                + "\n\n- [#](8B0000)Spiritus Invictus[#](): Grants resistance to knockback.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Speed";
    }

    @Override
    protected String entryDescription() {
        return "Infuses practitioners with unnatural swiftness.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SUGAR);
    }

    @Override
    protected String entryId() {
        return "ritual_speed";
    }
}
