package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class RitualBasicsEntry extends EntryProvider {

    public RitualBasicsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Art of Ritual");
        this.pageText("When your [#](8B0000)Ara Vitae[#]() reaches [#](B8860B)Tier 2[#](), the boundaries of [#](4A0080)Vitaemancy[#]() expand dramatically. You gain access to [#](4A0080)Rituals[#](), vast sigil-circles that channel your [#](4A0080)Anima[#]() into sustained, powerful effects upon the world itself."
                + "\\\n\\\nTo perform a ritual, you require:"
                + "\n\n- An [#](8B0000)Activation Crystal[#](). At [#](B8860B)Tier 2[#](), only the [#](8B0000)Weak Crystal[#]() is available."
                + "\n\n- A [#](8B0000)Master Ritual Stone[#](). Every ritual demands exactly one at its heart.");

        this.page("requirements", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- Sufficient [#](8B0000)Ritual Stones[#]() to form the pattern."
                + "\n\n- A [#](8B0000)Ritual Diviner[#]() (strongly recommended). While not strictly necessary, it simplifies construction immensely."
                + "\\\n\\\n[#](2E8B57)Press Sneak + Use with the Diviner in hand to cycle through available rituals. Check rune requirements by hovering over it in your inventory while sneaking.[#]()");

        this.page("building", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Place a [#](8B0000)Master Ritual Stone[#](), then hold Use with the Diviner aimed at it until every stone has been placed and inscribed with the correct element.\\\n\\\n"
                + "Finally, press Use on the [#](8B0000)Master Ritual Stone[#]() with your [#](8B0000)Activation Crystal[#]() in hand. If the circle is complete, you will feel a rush of energy; the ritual awakens.");

        this.page("troubleshooting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If instead you feel a push but are too weak, your [#](4A0080)Anima[#]() lacks the [#](8B0000)Essentia Vitae[#]() needed for activation. Fill your reserves and try again."
                + "\\\n\\\nIf the runes feel misconfigured, something obstructs or misaligns the pattern. Clear the area of blockages and reassemble the circle with care.");

        this.page("binding", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Remember that some rituals extend several blocks above and below the [#](8B0000)Master Ritual Stone[#](). If nothing happens at all, ensure the crystal is bound to an [#](4A0080)Anima[#](); press Use while holding it to bind it to yours."
                + "\\\n\\\n[#](2E8B57)A crystal need not be bound to your own Anima. If you acquire another Vitaemancer's crystal, you can activate rituals using their EV. Guard yours well.[#]()");

        this.page("redstone", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("One final note: all rituals respond to a [#](8B0000)redstone[#]() signal. A lever on the [#](8B0000)Master Ritual Stone[#]() provides a simple means of silencing it. Combine this with redstone automation to ensure your rituals shut down before your [#](4A0080)Anima[#]() runs dry.");
    }

    @Override
    protected String entryName() {
        return "The Art of Ritual";
    }

    @Override
    protected String entryDescription() {
        return "Inscribing circles of power upon the world.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ACTIVATION_CRYSTAL_CREATIVE.get());
    }

    @Override
    protected String entryId() {
        return "ritual_basics";
    }
}
