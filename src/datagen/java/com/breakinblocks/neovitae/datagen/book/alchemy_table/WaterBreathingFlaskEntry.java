package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class WaterBreathingFlaskEntry extends EntryProvider {

    public WaterBreathingFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Water Breathing");
        this.pageText("**Water Breathing** allows the target to breathe underwater.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Water Breathing (3:00)** - neovitae:flask/water_breathing\n"
                + "- **Water Breathing (8:00)** - neovitae:flask/length_water_breathing\n"
                + "- **Water Breathing (21:20)** - neovitae:flask/length_average_water_breathing");
    }

    @Override
    protected String entryName() {
        return "Water Breathing";
    }

    @Override
    protected String entryDescription() {
        return "Breathe underwater.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.PUFFERFISH);
    }

    @Override
    protected String entryId() {
        return "flask_water_breathing";
    }
}
