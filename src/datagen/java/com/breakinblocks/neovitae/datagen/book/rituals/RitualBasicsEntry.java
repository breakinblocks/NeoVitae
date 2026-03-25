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
        this.pageTitle("Rituals - Getting Started");
        this.pageText("Once you have gotten your **Blood Altar** to Tier 3, you can delve into the wonderful world of Rituals."
                + "\\\n\\\nFor working with rituals, you will require the following:"
                + "\n- An **Activation Crystal**. At tier 3 only the **Weak Crystal** is available."
                + "\n- A **Master Ritual Stone**. Every ritual requires exactly one of these at its centre.");

        this.page("requirements", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- Enough Ritual Stones to build the Ritual."
                + "\n- (Recommended) A **Ritual Diviner**. Although not required, it will make ritual construction significantly easier."
                + "\\\n\\\nBuilding a ritual is relatively straightforward. Simply press Sneak + Use with the Ritual Diviner in hand until it displays the name of the desired ritual. Check the number of runes required by mousing over it in your inventory while sneaking.");

        this.page("building", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Place down a Master Ritual Stone, and hold Use until all the stones have been placed and painted with the correct element.\\\n\\\n"
                + "Finally, press Use on the Master Ritual Stone with your Activation Crystal in hand. If you've done everything right, you should get a message saying 'A rush of energy flows through the ritual!'. The ritual is now active.");

        this.page("troubleshooting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If this does not occur, a few things may have gone wrong. If you instead get text saying 'You feel a push, but are too weak to complete this ritual', then you do not have enough **LP** in your **Soul Network** to activate the ritual."
                + "\\\n\\\nIf the message reads 'You feel that these runes are not configured correctly...', then something has gone wrong in the placement of the runes. Check the area for any blockages and try assembling the ritual again.");

        this.page("binding", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Remember that some rituals extend several blocks above and below the Master Ritual Stone. If you get no error, ensure that the activation crystal is bound to your soul network - this can be accomplished by pressing Use while holding it."
                + "\\\n\\\nIt is important to note that the crystal does not have to be bound to YOUR network - if you can get a hold of another player's crystal, you can activate rituals using their **LP**. Guard yours well!");

        this.page("redstone", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("One last note; all rituals respond to a **redstone** signal, so sticking a lever on the side of the Master Ritual Stone is a good way to deactivate it. You can combine this knowledge with some redstone automation to ensure your rituals shut down automatically long before your **Soul Network** runs dry.");
    }

    @Override
    protected String entryName() {
        return "Rituals - Getting Started";
    }

    @Override
    protected String entryDescription() {
        return "An introduction to the ritual system.";
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
