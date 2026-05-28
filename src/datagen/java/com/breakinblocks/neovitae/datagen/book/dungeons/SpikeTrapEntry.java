package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.mojang.datafixers.util.Pair;

public class SpikeTrapEntry extends EntryProvider {

    public SpikeTrapEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)Spike Traps[#]() sit dormant and deceptively innocuous until a redstone signal "
                + "reaches them, whereupon serrated blades erupt from their housing with lethal intent. "
                + "Whatever [#](4A0080)intelligence[#]() designed these was clearly determined to discourage "
                + "uninvited guests.\\\n\\\n"
                + "[#](2E8B57)Clever practitioners have repurposed these for their own defenses. "
                + "Consult JEI for the crafting recipe.[#]()");

        this.page("harvester", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Lesser Tithe");
        this.pageText("When a powered spike trap [#](8B0000)slays a creature[#](), the spilled vitality is not lost. "
                + "Place an [#](8B0000)Ara Vitae[#]() within sixteen blocks and the kill will feed it directly, mimicking "
                + "the [#](4A0080)Well of Suffering[#]() at roughly [#](8B0000)one third[#]() of its yield. The trap remembers the "
                + "nearest altar after the first kill and re-scans only when that altar is broken or relocated.\\\n\\\n"
                + "[#](2E8B57)Players are immune to this harvest[#]() - the spikes will wound you, but they will not feed "
                + "your altar from your blood, nor will their kills be credited to any practitioner. No experience, no "
                + "player-only drops. A reliable [#](8B0000)pre-ritual mob farm[#]() for vitaemancers who have not yet "
                + "mastered the true ritual circles.");
    }

    @Override
    protected String entryName() {
        return "Spike Trap";
    }

    @Override
    protected String entryDescription() {
        return "Dormant blades that awaken at a redstone whisper.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.SPIKE_TRAP.asItem());
    }

    @Override
    protected String entryId() {
        return "spike_trap";
    }
}
