package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;

public class ExplosiveChargesEntry extends EntryProvider {

    public ExplosiveChargesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Explosive Charges");
        this.pageText("Mining by hand and felling trees one swing at a time; such drudgery is beneath a "
                + "practitioner of [#](4A0080)Vitaemancy[#](). You have devised something far more elegant: compact "
                + "charges of compressed Spiritus that detonate on contact. Hurl them at offending stone, timber, "
                + "or anything else you wish unmade, and wait for the satisfying ignition.");

        this.page("anointments", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each charge can be further enhanced by [#](8B0000)Anointing[#]() it with certain reagents and "
                + "a measure of Spiritus:\n\n"
                + "- [#](B8860B)Fortunate[#](): broken blocks yield Fortune loot.\n\n"
                + "- [#](B8860B)Heated Tool[#](): broken blocks are smelted on the spot.\n\n"
                + "- [#](B8860B)Soft Touch[#](): broken blocks are affected by Silk Touch.\n\n"
                + "- [#](B8860B)Voiding[#](): worthless debris is annihilated rather than dropped.");

        this.page("shaped_charge", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Shaped Charge");
        this.pageText("Obliterates a [#](B8860B)5x5x5[#]() cube on the face it strikes, dropping all blocks as though "
                + "mined with a pickaxe. It cuts through Obsidian with ease and provides a most gratifying "
                + "concussion.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("aug_shaped_charge", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Augmented Shaped Charge");
        this.pageText("A direct improvement. Destroys a [#](B8860B)7x7x7[#]() cube on the impacted face, and may "
                + "be anointed with [#](B8860B)Fortune II[#]() as well.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("tunnelling", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tunnelling Shaped Charge");
        this.pageText("Bores a [#](B8860B)5x5x20[#]() corridor straight through whatever it strikes. An invaluable "
                + "companion for deep excavation.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("deforester", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deforester Charge");
        this.pageText("Purpose-built for timber. Strike a log or leaf block, and up to [#](B8860B)two stacks[#]() of wood "
                + "come crashing down, with leaves stripped away in the process. Even the mightiest jungle "
                + "giants fall in moments.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("deforester2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deforester Charge II");
        this.pageText("The greater variant. Fells up to [#](B8860B)8 stacks[#]() of logs, shearing away all connecting "
                + "leaves. Ideal for carving clearings in Dark Oak forests or dense jungles.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("controlled", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Controlled Charge");
        this.pageText("Destroys only blocks [#](B8860B)identical to the block it strikes[#](), seeking outward from "
                + "the point of impact. Consumes up to 3 stacks of matching blocks.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("controlled2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Controlled Charge II");
        this.pageText("Functions identically to the standard Controlled Charge, but devours up to "
                + "[#](B8860B)8 stacks[#]() of matching blocks.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("fungal", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fungal Charge");
        this.pageText("Designed for the great mushrooms of the Overworld and the Nether. Capable of consuming "
                + "up to [#](B8860B)three stacks[#]() of fungal material, though you would be hard-pressed to find a "
                + "specimen that large.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("fungal2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fungal Charge II");
        this.pageText("The greater fungal variant. Annihilates up to [#](B8860B)8 stacks[#]() of mushroom material "
                + "in a single detonation.\\\n\\\n"
                + "Why you would ever need such overkill is a question best left to the practitioner's "
                + "conscience.\\\n\\\n"
                + "Anointable: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");
    }

    @Override
    protected String entryName() {
        return "Explosive Charges";
    }

    @Override
    protected String entryDescription() {
        return "Compact demolition devices fueled by Spiritus and enhanced by anointment.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.SHAPED_CHARGE.asItem());
    }

    @Override
    protected String entryId() {
        return "explosive_charges";
    }
}
