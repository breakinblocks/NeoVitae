package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class WeaknessFlaskEntry extends EntryProvider {

    public WeaknessFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Weakness");
        this.pageText("**Weakness** decreases the target's attack damage by 4 points per level.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Weakness (3:00)** - neovitae:flask/weakness\n"
                + "- **Weakness (8:00)** - neovitae:flask/length_weakness\n"
                + "- **Weakness II (1:30)** - neovitae:flask/potency_weakness");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Weakness III (0:45)** - neovitae:flask/potency_average_weakness\n"
                + "- **Weakness (21:20)** - neovitae:flask/length_average_weakness");
    }

    @Override
    protected String entryName() {
        return "Weakness";
    }

    @Override
    protected String entryDescription() {
        return "Decreases attack damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FERMENTED_SPIDER_EYE);
    }

    @Override
    protected String entryId() {
        return "flask_weakness";
    }
}
