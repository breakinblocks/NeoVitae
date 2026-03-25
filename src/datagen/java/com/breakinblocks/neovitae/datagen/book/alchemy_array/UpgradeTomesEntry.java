package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class UpgradeTomesEntry extends EntryProvider {

    public UpgradeTomesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Upgrade Tomes");
        this.pageText("Upon activating the **Sound of the Cleansing Soul** ritual, a series of magical "
                + "**Tomes** will manifest around you.\n\n"
                + "By pressing [Use] whilst holding one of these, you can 'teach' your chestplate one level "
                + "of this skill (assuming it has enough spare points to learn it). Holding sneak and pressing "
                + "[Use] will instead consume as much XP from the tome as possible.");

        this.page("consumption", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Doing so will consume the **Tome** and apply all EXP from it to your chestplate - "
                + "unless there are not enough **Upgrade Points** available.\n\n"
                + "In this case, the Tome will apply as much EXP as it can and store the remainder - unless "
                + "there's less than 1 level's worth left, in which case the tome will be destroyed.");

        this.page("strategy", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("This is an excellent way to focus training on abilities you wish to see more of, "
                + "whilst conveniently forgetting ones you find less useful.\n\n"
                + "Alternately, you can teach different chestplates different skills, so you can have one "
                + "chestplate for mining, one for combat, and yet another for exploration.");
    }

    @Override
    protected String entryName() {
        return "Upgrade Tomes";
    }

    @Override
    protected String entryDescription() {
        return "Using tomes to train specific Living Equipment skills.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.UPGRADE_TOME.get());
    }

    @Override
    protected String entryId() {
        return "upgrade_tomes";
    }
}
