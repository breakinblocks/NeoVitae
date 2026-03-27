package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SpiritusGemsEntry extends EntryProvider {

    public SpiritusGemsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Gems");
        this.pageText("The fragments of [#](8B0000)Spiritus[#]() you have gathered thus far are potent, yet unwieldy. "
                + "What you require is a vessel, a crystalline prison to contain and compress that malice into "
                + "something manageable. The [#](8B0000)Spiritus Gem[#]() is precisely such an artifact.\\\n\\\n"
                + "Better still, the gem hungers. Drop loose Will upon the ground nearby, and the gem will "
                + "devour it of its own accord.");

        this.page("petty", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Petty Spiritus Gem");
        this.pageText("Your first vessel holds a modest [#](B8860B)64 Will[#](), crude, but far more practical than "
                + "loose fragments rattling about your pack.\\\n\\\n"
                + "[#](2E8B57)To transfer Will between gems, right-click while holding the gem you wish to empty. "
                + "Its contents flow into the first valid gem in your inventory.[#]()");

        this.page("lesser_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Your [#](8B0000)Petty Spiritus Gem[#]() has served its purpose, but its capacity chafes against "
                + "your ambitions. By working it with [#](8B0000)Diamond[#](), [#](8B0000)Lapis[#](), and [#](8B0000)Redstone[#]() within "
                + "the Forge, you have discovered a method to quadruple its containment.");

        this.page("lesser", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lesser Spiritus Gem");
        this.pageText("This reinforced gem holds up to [#](B8860B)256 Will[#]().\\\n\\\n"
                + "[#](2E8B57)When upgrading, the Forge draws Will from the gem being crafted before tapping "
                + "the gem in its Gem Slot. The newly forged gem retains any leftover Will from the "
                + "process.[#]()");

        this.page("common_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Your [#](8B0000)Lesser Spiritus Gem[#]() is a noted improvement, yet once more you press against "
                + "its limits. Further refinement demands an [#](8B0000)Imbued Slate[#]() from the [#](8B0000)Ara Vitae[#](), "
                + "combined with another [#](8B0000)Diamond[#]() and a [#](8B0000)Block of Gold[#](), quadrupling capacity once again.");

        this.page("common", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Common Spiritus Gem");
        this.pageText("This intricate gem holds an impressive [#](B8860B)1,024 Will[#](). A worthy vessel for a "
                + "practitioner of growing renown.");

        this.page("greater_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Common Spiritus Gem[#]() is a fine achievement, yet you sense the limits of mortal "
                + "craft closing in. To break through demands the culmination of all you have learned; "
                + "a [#](8B0000)Demonic Slate[#](), a [#](8B0000)Weak Blood Shard[#](), and a [#](8B0000)Spiritus Crystal[#]().\\\n\\\n"
                + "The rewards, however, shall be commensurate. Your [#](8B0000)Sentient Tools[#]() will reach potencies "
                + "you have only dreamed of...");

        this.page("greater", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Greater Spiritus Gem");
        this.pageText("This masterwork of [#](4A0080)vitaemantic[#]() artifice holds an astounding [#](B8860B)4,096 Will[#](). "
                + "Few vessels in existence can rival its containment.");
    }

    @Override
    protected String entryName() {
        return "Spiritus Gems";
    }

    @Override
    protected String entryDescription() {
        return "Crystalline vessels that imprison and compress Spiritus.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SPIRITUS_GEM_GREATER.get());
    }

    @Override
    protected String entryId() {
        return "spiritus_gems";
    }
}
