package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class HeavyHeartFlaskEntry extends EntryProvider {

    public HeavyHeartFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Heavy Heart");
        this.pageText("**Heavy Heart** drags the target steadily downwards, making flying and swimming "
                + "significantly more difficult.\\\n\\\n"
                + "It's created from a flask containing both the **Gravity** and **Instant Health** effects.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Heavy Heart (3:00)** - neovitae:flask/gravity_to_heart\n"
                + "- **Heavy Heart (8:00)** - neovitae:flask/length_heavy_heart\n"
                + "- **Heavy Heart II (1:30)** - neovitae:flask/potency_heavy_heart");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\\\n\\\n"
                + "- **Heavy Heart III (0:45)** - neovitae:flask/potency_average_heavy_heart\n"
                + "- **Heavy Heart (21:20)** - neovitae:flask/length_average_heavy_heart");
    }

    @Override
    protected String entryName() {
        return "Heavy Heart";
    }

    @Override
    protected String entryDescription() {
        return "Drags the target downwards.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SLIME_BALL);
    }

    @Override
    protected String entryId() {
        return "flask_heavy_heart";
    }
}
