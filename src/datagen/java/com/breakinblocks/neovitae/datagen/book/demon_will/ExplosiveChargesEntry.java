package com.breakinblocks.neovitae.datagen.book.demon_will;

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
        this.pageText("Mining and foresting is all well and good for those who refuse to dream big, but what "
                + "self-respecting sanguimancer would ever stoop to that?\n\n"
                + "With this in mind, you have devised some devious little devices. Simply throw them at some "
                + "pesky trees, rocks, or whatever else you wish to cease to be and wait for them to self-ignite.");

        this.page("anointments", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Not only that, you can improve their functionality by **Anointing** them with certain "
                + "ingredients and a little Will, as described after each entry.\n\n"
                + "The currently available anointments are as follows:\n"
                + "- Fortunate - broken blocks are affected by **Fortune**.\n"
                + "- Heated Tool - Broken blocks are **Smelted**.\n"
                + "- Soft Touch - Broken blocks are affected by **Silk Touch**.\n"
                + "- Voiding - 'Junk' blocks are voided rather than dropping as items.");

        this.page("shaped_charge", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Shaped Charge");
        this.pageText("Craft the Shaped Charge in the Hellfire Forge.\n\n"
                + "The **Shaped Charge** will destroy a 5x5x5 cube facing whichever side of a block it lands "
                + "on, dropping all blocks as though mined with a pickaxe. It even works on Obsidian, and "
                + "provides a most satisfying KABOOM whilst doing so.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("aug_shaped_charge", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Augmented Shaped Charge");
        this.pageText("Craft the Augmented Shaped Charge in the Hellfire Forge.\n\n"
                + "The **Augmented Shaped Charge** is a direct upgrade from the standard Shaped Charge. It "
                + "will destroy a 7x7x7 cube facing whichever side of a block it lands on, dropping all blocks "
                + "as though mined with a pickaxe. It can also be anointed with **Fortune II**.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("tunnelling", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tunnelling Shaped Charge");
        this.pageText("Craft the Tunnelling Shaped Charge in the Hellfire Forge.\n\n"
                + "The **Tunnelling Shaped Charge** will destroy a 5x5x20 tunnel facing whichever side of a "
                + "block it lands on, dropping all blocks as though mined with a pickaxe. Good for mining.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("deforester", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deforester Charge");
        this.pageText("Craft the Deforester Charge in the Hellfire Forge.\n\n"
                + "The **Deforester Charge** is for felling trees. It can be used on logs or leaves, and will "
                + "fell all but the mightiest of trees, breaking up to **two stacks** of logs at a time (and "
                + "neatly stripping away any leaves it encounters in the process!) Even the giant trees of the "
                + "Jungle will fall in a matter of seconds.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("deforester2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deforester Charge II");
        this.pageText("Craft the Deforester Charge II in the Hellfire Forge.\n\n"
                + "The **Deforester Charge II**, much like the standard Deforester Charge, is for breaking wood "
                + "and trees. It can break up to 8x64 logs, clearing away any connecting leaves. Excellent for "
                + "making yourself a nice clearing in any Dark Oak Forest or Jungle you might happen to like the "
                + "look of.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("controlled", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Controlled Charge");
        this.pageText("Craft the Controlled Charge in the Hellfire Forge.\n\n"
                + "The **Controlled Charge** only destroys blocks **identical to the block it lands on**. It "
                + "will destroy up to 3 stacks of blocks, seeking out from the original point. As with other "
                + "charges, all mined blocks will be dropped.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("controlled2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Controlled Charge II");
        this.pageText("Craft the Controlled Charge II in the Hellfire Forge.\n\n"
                + "The **Controlled Charge II** behaves identically to the **Controlled Charge**, however it "
                + "will destroy up to 8 stacks of blocks at a time.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");

        this.page("fungal", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fungal Charge");
        this.pageText("Craft the Fungal Charge in the Hellfire Forge.\n\n"
                + "The **Fungal Charge** is for felling giant mushrooms, both in the Overworld and the Nether. "
                + "Although doubtful that you will ever reach this in a single charge, it can break up to "
                + "**three stacks of blocks**.\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate.");

        this.page("fungal2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fungal Charge II");
        this.pageText("Craft the Fungal Charge II in the Hellfire Forge.\n\n"
                + "The **Fungal Charge II** is for felling giant mushrooms, both in the Overworld and the "
                + "Nether. It can destroy up to 8 stacks of mushroomy goodness per charge.\n\n"
                + "Why would you ever need this?\n\n"
                + "Anointable variants: Voiding, Heated Tool, Soft Touch, Fortunate, Fortunate II.");
    }

    @Override
    protected String entryName() {
        return "Explosive Charges";
    }

    @Override
    protected String entryDescription() {
        return "Throwable demolition devices with anointment support.";
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
