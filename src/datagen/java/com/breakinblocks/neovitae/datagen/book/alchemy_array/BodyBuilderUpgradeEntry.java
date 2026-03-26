package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class BodyBuilderUpgradeEntry extends EntryProvider {

    public BodyBuilderUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Body Builder");
        this.pageText("The armour observes your habits of nourishment and responds in kind, hardening your "
                + "frame against impact. Grants [#](4A0080)Knockback Resistance[#]() up to 100%% and bonus "
                + "health up to 10 half-hearts.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Eating food.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Body Builder";
    }

    @Override
    protected String entryDescription() {
        return "Feed yourself well and the armour fortifies your body in turn.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.COOKED_BEEF);
    }

    @Override
    protected String entryId() {
        return "upgrade_body_builder";
    }
}
