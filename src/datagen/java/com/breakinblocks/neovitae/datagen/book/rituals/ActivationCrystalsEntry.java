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
        this.pageText("Your rituals require more than simply the correct arrangement of blocks and Sigils. An effort of will is required to open a channel from your Soul Network to the ritual, and the [#](8B0000)Activation Crystal[#]() will allow you to focus yourself enough to activate your rituals.");

        this.page("weak_recipe", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ara_vitae/weak_activation_crystal"))
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Weak Activation Crystal[#]() is crafted in the Ara Vitae from a Lava Crystal. Simply press Use with a bound Activation Crystal on a Master Ritual Stone to activate the ritual.");

        this.page("awakened_recipe", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ara_vitae/awakened_activation_crystal"))
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Awakened Activation Crystal[#]() is a more powerful crystal required to activate advanced rituals. It is crafted in the Ara Vitae from a Weak Activation Crystal.");
    }

    @Override
    protected String entryName() {
        return "Activation Crystals";
    }

    @Override
    protected String entryDescription() {
        return "Focus your will to activate rituals.";
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
