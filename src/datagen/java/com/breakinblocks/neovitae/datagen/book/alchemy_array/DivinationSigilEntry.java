package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class DivinationSigilEntry extends EntryProvider {

    public DivinationSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Divination Sigil");
        this.pageText("The [#](8B0000)Divination Sigil[#]() is the first lens through which a Vitaemancer peers "
                + "into the invisible currents of [#](4A0080)Essentia Vitae[#]() that sustain all vitaemantic works. "
                + "Forged in an [#](8B0000)Alchemy Array[#]() from humble components, it reveals what the naked "
                + "eye cannot.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Inscribe the array with [#](8B0000)Redstone Dust[#]() as the base and a "
                + "[#](8B0000)Blank Slate[#]() as the catalyst. The sigil that emerges thrums faintly with "
                + "awareness.\\\n\\\n[#](4A0080)To see is to begin understanding.[#]()");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The sigil serves two vital purposes:\n\n"
                + "- Activate it toward open air to reveal the [#](4A0080)Essentia Vitae[#]() stored within your "
                + "[#](8B0000)Anima[#](), the measure of your reserves.\n\n"
                + "- Aim it at an [#](8B0000)Ara Vitae[#]() to reveal the altar's current tier, the volume of "
                + "[#](4A0080)Essentia Vitae[#]() within, and its maximum capacity.");

        this.page("activation_intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil Activation");
        this.pageText("Sigils fall into two schools of invocation:\n\n"
                + "- [#](8B0000)Use-on-cast[#]() sigils such as the [#](8B0000)Water[#](), [#](8B0000)Lava[#](), "
                + "[#](8B0000)Void[#](), and [#](8B0000)Blood Lamp Sigils[#]() perform a single act each time you "
                + "invoke them, drawing a fixed measure of [#](4A0080)Essentia Vitae[#]() per use.\n\n"
                + "- [#](8B0000)Toggleable[#]() sigils maintain a continuous effect while held active. They draw "
                + "[#](4A0080)EV[#]() at a steady upkeep so long as their power flows.");

        this.page("activation_toggle", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To toggle a sigil on or off, hold [#](8B0000)Shift[#]() and right-click into open air. "
                + "Aiming at a block or creature will perform that sigil's normal action instead, so the shift "
                + "requirement protects against accidental toggles in the heat of work.\n\n"
                + "An active toggleable sigil bears a faint enchantment shimmer, and its tooltip lists the "
                + "[#](4A0080)EV[#]() it drains per interval. When your [#](8B0000)Anima[#]() runs dry, the "
                + "sigil quietly deactivates until you refill and reignite it.");

        this.page("activation_list", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The toggleable sigils are:\n\n"
                + "- [#](8B0000)Sigil of the Green Grove[#]()\n\n"
                + "- [#](8B0000)Sigil of the Fast Miner[#]()\n\n"
                + "- [#](8B0000)Sigil of Magnetism[#]()\n\n"
                + "- [#](8B0000)Sigil of the Frozen Lake[#]()\n\n"
                + "- [#](8B0000)Sigil of Suppression[#]()\n\n"
                + "- [#](8B0000)Sigil of the Phantom Bridge[#]()");

        this.page("gui_editing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Hold sneak and press [Use] to open a configuration interface. Here you may "
                + "reposition the various [#](4A0080)Vitaemancy[#]() display elements on your field of vision. "
                + "Click and drag to move them, then select Save to preserve the arrangement, or Default "
                + "to restore the original positions.");

        this.page("hud_elements", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The configurable displays are:\n\n"
                + "- The Incense Altar (light grey)\n\n"
                + "- The Seer's Sigil (purple)\n\n"
                + "- The Divination Sigil (lavender)\n\n"
                + "- The Spiritus Aura Gauge (orange)\n\n"
                + "- The Sigil of Holding (green)");
    }

    @Override
    protected String entryName() {
        return "Divination Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A lens into the unseen, reveals the state of your Anima and Ara Vitae.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_DIVINATION.get());
    }

    @Override
    protected String entryId() {
        return "sigil_divination";
    }
}
