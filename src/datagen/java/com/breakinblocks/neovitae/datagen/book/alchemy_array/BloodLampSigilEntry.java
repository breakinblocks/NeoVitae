package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BloodLampSigilEntry extends EntryProvider {

    public BloodLampSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of the Blood Lamp");
        this.pageText("The [#](8B0000)Sigil of the Blood Lamp[#]() launches a mote of crystallized "
                + "[#](4A0080)Essentia Vitae[#]() in the direction you face. When it strikes a surface, it "
                + "anchors itself as a permanent floating light source at a cost of merely 10 "
                + "[#](4A0080)Essentia Vitae[#]().\\\n\\\n"
                + "Alternatively, right-clicking a nearby surface places the light directly without "
                + "launching a projectile.");

        this.page("brightness", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Brightness Control");
        this.pageText("The sigil remembers a [#](8B0000)brightness level[#]() from 1 to 15. "
                + "Left-click the air to cycle the brightness upward. Sneak and left-click to "
                + "cycle it downward. The current setting is shown in the sigil's tooltip.\\\n\\\n"
                + "Once a blood light has been placed, you can also adjust it directly. "
                + "Right-click an existing blood light with an empty hand to increase its brightness, "
                + "or sneak and right-click to dim it.");

        this.page("redstone", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Redstone Control");
        this.pageText("Right-click a blood light with [#](8B0000)Redstone Dust[#]() to toggle "
                + "redstone sensitivity. When enabled, the light will only emit light and particles "
                + "while receiving a redstone signal. Without a signal, it goes dark.\\\n\\\n"
                + "Right-click with redstone again to disable redstone control and return the light "
                + "to its always-on state.");

        this.page("dyeing", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Color Dyeing");
        this.pageText("Blood lights are not limited to their natural crimson. Combine the sigil with "
                + "any vanilla [#](8B0000)dye[#]() in a shapeless craft to change its color. All other "
                + "data on the sigil, including its binding and brightness setting, is preserved.\\\n\\\n"
                + "Lights placed after dyeing will glow in the chosen color. To restore the default "
                + "red, simply place the sigil alone in a crafting grid.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Blood Lamp Reagent[#]() in the [#](8B0000)Tabula Vitae[#](), then inscribe "
                + "an [#](8B0000)Alchemy Array[#]() with the reagent as base and a slate as catalyst.\\\n\\\n"
                + "[#](4A0080)A small light to banish the deepest dark, in any color you desire.[#]()");
    }

    @Override
    protected String entryName() {
        return "Sigil of the Blood Lamp";
    }

    @Override
    protected String entryDescription() {
        return "Cast colored motes of blood-light into the darkness ahead of you.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_BLOOD_LIGHT.get());
    }

    @Override
    protected String entryId() {
        return "sigil_blood_lamp";
    }
}
