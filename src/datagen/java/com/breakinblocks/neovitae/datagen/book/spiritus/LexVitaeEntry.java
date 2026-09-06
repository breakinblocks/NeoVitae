package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class LexVitaeEntry extends EntryProvider {

    public LexVitaeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lex Vitae");
        this.pageText("The [#](8B0000)Lex Vitae[#]() is a sentient multitool that chops, mines, digs, and tills, drawing on "
                + "the [#](8B0000)Spiritus[#]() you carry to do its work. Like other Sentient equipment it grows stronger as "
                + "you feed it Spiritus, and may be repaired with a Binding Reagent in an Anvil."
                + "\\\n\\\n[#](2E8B57)Sneak + right-click to toggle the Lex Vitae between dormant and active. It only works "
                + "while active.[#]()");

        this.page("beam", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Beam");
        this.pageText("While active, hold the [#](8B0000)Fire Beam[#]() key to project a lance of will from your hand that "
                + "reaches far beyond your normal grasp. Whatever it mines is sent straight into your inventory, so nothing is "
                + "lost across the distance."
                + "\\\n\\\nSneak + scroll to widen the beam's [#](8B0000)mining radius[#](), carving out a larger area with "
                + "each strike."
                + "\\\n\\\n[#](B8860B)The Fire Beam key is unbound by default; assign it in Controls under the Neo Vitae "
                + "category.[#]()");

        this.page("modes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Beam Modes");
        this.pageText("Press the [#](8B0000)Cycle Mode[#]() key (L by default) to change what the beam affects:"
                + "\\\n\\\n- [#](8B0000)Mining & Damage[#](): mines blocks and strikes creatures. The default."
                + "\n\n- [#](8B0000)Mining[#](): only mines blocks; the beam passes harmlessly through creatures."
                + "\n\n- [#](8B0000)Damage[#](): only strikes creatures; the beam ignores blocks."
                + "\\\n\\\nThe current mode is shown in the Lex Vitae's tooltip.");
    }

    @Override
    protected String entryName() {
        return "Lex Vitae";
    }

    @Override
    protected String entryDescription() {
        return "A sentient multitool whose ranged beam mines straight to your inventory.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.LEX_VITAE.get());
    }

    @Override
    protected String entryId() {
        return "lex_vitae";
    }
}
