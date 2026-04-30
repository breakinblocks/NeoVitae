package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class NecromancySigilEntry extends EntryProvider {

    public NecromancySigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sigil of Necromancy");
        this.pageText("The [#](8B0000)Sigil of Necromancy[#]() calls upon the latent death in the world "
                + "to raise an [#](4A0080)Undead Servant[#]() at the location you are aiming. Each use costs "
                + "[#](4A0080)2,000 EV[#]() and summons one of four undead variants at random: a zombie, husk, "
                + "skeleton, or stray.\\\n\\\n"
                + "Servants last for 5 minutes before crumbling to dust, and will disappear if they stray "
                + "too far from their master.");

        this.page("behavior", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Servant Behavior");
        this.pageText("Summoned undead are loyal to their creator. They will follow you when idle and "
                + "automatically attack whatever target you last struck. They cannot harm their master, "
                + "drop no loot or experience, and are immune to sunlight.\\\n\\\n"
                + "To dismiss a servant early, [#](8B0000)shift right-click[#]() it while holding the sigil.");

        this.page("crafting", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crafting");
        this.pageText("Unlike most sigils, the [#](8B0000)Sigil of Necromancy[#]() is forged in the "
                + "[#](8B0000)Athanor[#]() rather than inscribed via alchemy array. Place a "
                + "[#](4A0080)Tabula Robur[#](), [#](4A0080)Rotten Flesh[#](), and [#](4A0080)Bone[#]() "
                + "in the input slots with a [#](8B0000)Resonator[#]() tool. Supply [#](4A0080)1,000 mB of "
                + "Essentia Vitae[#]() and a minimum of [#](8B0000)20 Raw Spiritus[#]().");
    }

    @Override
    protected String entryName() {
        return "Sigil of Necromancy";
    }

    @Override
    protected String entryDescription() {
        return "Raise undead servants to fight alongside you.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIGIL_NECROMANCY.get());
    }

    @Override
    protected String entryId() {
        return "sigil_necromancy";
    }
}
