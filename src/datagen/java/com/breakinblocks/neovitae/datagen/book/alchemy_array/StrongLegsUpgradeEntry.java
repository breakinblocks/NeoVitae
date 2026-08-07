package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class StrongLegsUpgradeEntry extends EntryProvider {

    public StrongLegsUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Strong Legs");
        this.pageText("The armor amplifies the power of your legs, propelling you higher with each leap. "
                + "Grants up to 7.5 additional blocks of jump height and 83%% fall resistance.\\\n\\\n"
                + "[#](2E8B57)Hold sneak while jumping to suppress the boost.[#]()\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Jumping.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 10");
    }

    @Override
    protected String entryName() {
        return "Strong Legs";
    }

    @Override
    protected String entryDescription() {
        return "The armor coils like a spring beneath you, launching you skyward.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DIAMOND_LEGGINGS);
    }

    @Override
    protected String entryId() {
        return "upgrade_strong_legs";
    }
}
