package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookAraVitaeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class ActivationCrystalsEntry extends EntryProvider {

    public ActivationCrystalsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Activation Crystals");
        this.pageText("A properly inscribed circle is inert without the spark of will to awaken it. The [#](8B0000)Activation Crystal[#]() serves as that spark, a focus through which you open a conduit between your [#](4A0080)Anima[#]() and the waiting runes, breathing purpose into stone.");

        this.page("weak_recipe", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ara_vitae/weak_activation_crystal"))
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Weak Activation Crystal[#]() is forged within the [#](8B0000)Ara Vitae[#]() from a [#](8B0000)Lava Crystal[#](). Press Use with a bound crystal upon the [#](8B0000)Master Ritual Stone[#]() to ignite the circle.");

        this.page("awakened_recipe", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ara_vitae/awakened_activation_crystal"))
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Awakened Activation Crystal[#]() resonates with deeper currents of power, required to activate [#](B8860B)advanced rituals[#]() beyond the Weak Crystal's reach. It is forged within the [#](8B0000)Ara Vitae[#]() from its lesser counterpart.");
    }

    @Override
    protected String entryName() {
        return "Activation Crystals";
    }

    @Override
    protected String entryDescription() {
        return "The spark that awakens sleeping runes.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ACTIVATION_CRYSTAL_WEAK.get());
    }

    @Override
    protected String entryId() {
        return "activation_crystals";
    }
}
