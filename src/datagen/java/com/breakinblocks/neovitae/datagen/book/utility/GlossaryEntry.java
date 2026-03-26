package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class GlossaryEntry extends EntryProvider {

    public GlossaryEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("key_terms", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Key Terms");
        this.pageText("Neo Vitae has a few key terms that are important to understand. "
                + "This glossary will help clarify the terminology used throughout the mod and this guide.");

        this.page("blood", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood");
        this.pageText("[#](8B0000)Blood[#]() is the raw, unrefined substance found in living creatures. "
                + "When you use a Sacrificial Knife or Dagger of Sacrifice, you are extracting this raw blood "
                + "and offering it to the Ara Vitae to be refined into something more useful.");

        this.page("life_essence", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Life Essence");
        this.pageText("[#](8B0000)Life Essence[#]() is refined blood that has been sanctified within the Ara Vitae. "
                + "It is a physical liquid that can be stored in the Altar's basin, transferred using Dislocation Runes, "
                + "collected in a bucket, or used for crafting recipes within the Altar.\\\n\\\n"
                + "When the guide refers to the Altar's capacity or contents, it is referring to Life Essence.");

        this.page("lp", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("LP (Life Points)");
        this.pageText("[#](8B0000)LP[#]() (Life Points) represents Life Essence that has been further purified through a Blood Orb "
                + "and stored within your Soul Network. Think of it as a third 'state' for blood - a pure distillation "
                + "of power that exists in an ethereal form.\\\n\\\n"
                + "LP is consumed when using Sigils, activating Rituals, and powering the Alchemy Table.");

        this.page("soul_network", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Soul Network");
        this.pageText("Your [#](8B0000)Soul Network[#]() is a personal, invisible reservoir that stores your LP. "
                + "Each player has their own unique network, and items bound to you will draw from (or add to) this network.\\\n\\\n"
                + "The capacity of your Soul Network is determined by your Blood Orb tier. "
                + "Higher tier orbs can store more LP.");

        this.page("refinement", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Refinement Process");
        this.pageText("To summarize the progression:\\\n\\\n"
                + "- [#](8B0000)Blood[#]() (raw) is extracted from living creatures\n"
                + "- [#](8B0000)Life Essence[#]() (refined) is created when blood is processed in the Ara Vitae\n"
                + "- [#](8B0000)LP[#]() (pure) is created when Life Essence is absorbed by a Blood Orb into your Soul Network\\\n\\\n"
                + "Each stage represents a further purification of the life force.");
    }

    @Override
    protected String entryName() {
        return "Glossary of Terms";
    }

    @Override
    protected String entryDescription() {
        return "Key terminology used throughout Neo Vitae.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BOOK);
    }

    @Override
    protected String entryId() {
        return "glossary";
    }
}
