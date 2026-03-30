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

public class RitualAnimalGrowthEntry extends EntryProvider {

    public RitualAnimalGrowthEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/animal_growth"))
                .withMultiblockName("Ritual of the Shepherd")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("animal_growth")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Nurturing Pulse");
        this.pageText("The circle radiates a warm vitality that seeps into the bones of young creatures, hastening their maturation. Baby animals within its influence grow to adulthood at an accelerated rate, as though seasons passed in mere moments.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Further accelerates the growth of young creatures."
                + "\n\n- [#](8B0000)Vengeful Spiritus[#](): Compels adult animals to breed without intervention."
                + "\n\n- [#](8B0000)Destructive Spiritus[#](): Increases the frequency of growth pulses."
                + "\n\n- [#](8B0000)Steadfast Spiritus[#](): Collects products from sheared or milked animals into a nearby chest.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Shepherd";
    }

    @Override
    protected String entryDescription() {
        return "Hastens the maturation of young creatures.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WHITE_WOOL);
    }

    @Override
    protected String entryId() {
        return "ritual_animal_growth";
    }
}
