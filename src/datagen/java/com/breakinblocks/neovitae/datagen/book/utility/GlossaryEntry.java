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
        this.pageTitle("Lexicon of the Art");
        this.pageText("Vitaemancy has its own language, forged over centuries by practitioners who understood "
                + "that naming a thing grants power over it. Study these terms well, apprentice - "
                + "you will encounter them throughout this grimoire and in practice.");

        this.page("blood", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood");
        this.pageText("[#](8B0000)Blood[#]() is the raw, unrefined substance found coursing through every living creature. "
                + "When you draw a blade across your flesh, or when the [#](8B0000)Dagger of Sacrifice[#]() claims a life, "
                + "it is this crude vitality you offer to the Ara Vitae for purification.");

        this.page("essentia_vitae", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Essentia Vitae");
        this.pageText("[#](4A0080)Essentia Vitae[#]() is blood that has been sanctified within the Ara Vitae - "
                + "refined into a luminous crimson fluid of considerable power. It pools in the altar's basin, "
                + "may be siphoned with Dislocation Runes, drawn into a bucket, or consumed in altar crafting.\\\n\\\n"
                + "When this grimoire speaks of the altar's capacity or contents, it refers to Essentia Vitae.");

        this.page("lp", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("EV (Abbreviation)");
        this.pageText("Throughout this grimoire and in conversation among practitioners, "
                + "[#](4A0080)Essentia Vitae[#]() is often shortened to [#](4A0080)EV[#](). "
                + "Whether it pools in the basin of your Ara Vitae or flows invisibly through your "
                + "[#](4A0080)Anima[#](), it is the same substance, the refined essence of life.\\\n\\\n"
                + "EV is consumed when activating Sigils, empowering Rituals, and fueling the Tabula Vitae.");

        this.page("anima", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Anima");
        this.pageText("Your [#](4A0080)Anima[#]() is the invisible reservoir woven into the fabric of your very soul. "
                + "Every Vitaemancer possesses one, unique and untouchable by others. Items bound to you "
                + "draw from and replenish this wellspring.\\\n\\\n"
                + "The capacity of your Anima is determined by the tier of your [#](8B0000)Blood Orb[#](). "
                + "Greater orbs hold vastly more EV.");

        this.page("refinement", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Path of Refinement");
        this.pageText("To distill the essence of this lexicon:\\\n\\\n"
                + "- [#](8B0000)Blood[#]() (raw): torn from living flesh by blade or sacrifice\n"
                + "- [#](4A0080)Essentia Vitae[#]() (refined): sanctified within the [#](8B0000)Ara Vitae[#](), "
                + "pooling as luminous crimson fluid\\\n\\\n"
                + "The Ara Vitae transmutes blood into EV. A [#](8B0000)Blood Orb[#]() placed within draws that EV "
                + "into your [#](4A0080)Anima[#](), where it fuels your instruments across any distance.");
    }

    @Override
    protected String entryName() {
        return "Lexicon of the Art";
    }

    @Override
    protected String entryDescription() {
        return "The language of Vitaemancy - terms every practitioner must know.";
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
